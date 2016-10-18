package com.ge.dt.tsc;

import com.neovisionaries.ws.client.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.io.IOException;
import java.net.URI;

import static com.neovisionaries.ws.client.WebSocketError.UNSUPPORTED_PROTOCOL;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.test.util.ReflectionTestUtils.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IngestionClient.class, LoggerFactory.class})
public class IngestionClientTest {

    private static Logger LOGGER;

    private IngestionClient ingestionClient;
    private DigitalTwinTimeSeriesClientProperties digitalTwinTimeSeriesClientProperties;

    @Mock
    private WebSocketFactory webSocketFactory;

    @Mock
    private WebSocket webSocket;

    @Mock
    private OAuth2RestTemplate oAuth2RestTemplate;

    @Mock
    private OAuth2AccessToken oAuth2AccessToken;

    static {
        spy(LoggerFactory.class);
        LOGGER = mock(Logger.class);
        when(getLogger(IngestionClient.class)).thenReturn(LOGGER);
    }

    @Before
    public void setup() throws IOException, WebSocketException {
        initMocks(this);
        reset(LOGGER);
        when(webSocketFactory.createSocket(any(URI.class))).thenReturn(webSocket);
        when(webSocket.addHeader(anyString(), anyString())).thenReturn(webSocket);
        when(webSocket.addListener(any(WebSocketListener.class))).thenReturn(webSocket);
        when(webSocket.connect()).thenReturn(webSocket);
        when(oAuth2RestTemplate.getAccessToken()).thenReturn(oAuth2AccessToken);
        when(oAuth2AccessToken.getValue()).thenReturn("foo");

        digitalTwinTimeSeriesClientProperties = new DigitalTwinTimeSeriesClientProperties();
        digitalTwinTimeSeriesClientProperties.setTokenEndpoint(URI.create("https://www.uaa.com"));
        digitalTwinTimeSeriesClientProperties.setClientId("clientId");
        digitalTwinTimeSeriesClientProperties.setClientSecret("clientSecret");
        digitalTwinTimeSeriesClientProperties.setIngestionEndpoint(URI.create("wss://localhost"));
        digitalTwinTimeSeriesClientProperties.setZoneId(randomUUID());

        ingestionClient = new IngestionClient(digitalTwinTimeSeriesClientProperties, oAuth2RestTemplate);
        setField(ingestionClient, "webSocketFactory", webSocketFactory);
    }

    @Test
    public void createsSocketToTimeSeriesService() throws IOException {
        invokeMethod(ingestionClient, "createWebSocket");
        verify(webSocketFactory).createSocket(digitalTwinTimeSeriesClientProperties.getIngestionEndpoint());
    }

    @Test
    public void addsBearerTokenToWebSocketHandshake() {
        invokeMethod(ingestionClient, "createWebSocket");
        verify(webSocket).addHeader("Authorization", "Bearer foo");
    }

    @Test
    public void addsZoneIdToWebSocketHandshake() {
        invokeMethod(ingestionClient, "createWebSocket");
        verify(webSocket).addHeader("Predix-Zone-Id", digitalTwinTimeSeriesClientProperties.getZoneId().toString());
    }

    @Test
    public void addsOriginToHandshake() {
        invokeMethod(ingestionClient, "createWebSocket");
        verify(webSocket).addHeader("Origin", "http://predix.io");
    }

    @Test
    public void noProxySettingsByDefault() {
        invokeMethod(ingestionClient, "configureProxySettings");
        verify(webSocketFactory, times(0)).getProxySettings();
    }

    @Test
    public void setsProxySettingsIfProvided() {
        setField(digitalTwinTimeSeriesClientProperties, "proxyHost", "proxy.foo.com");
        setField(digitalTwinTimeSeriesClientProperties, "proxyPort", 80);

        final ProxySettings proxySettings = mock(ProxySettings.class);
        when(proxySettings.setHost(anyString())).thenReturn(proxySettings);
        when(webSocketFactory.getProxySettings()).thenReturn(proxySettings);

        invokeMethod(ingestionClient, "configureProxySettings");
        verify(proxySettings).setHost(digitalTwinTimeSeriesClientProperties.getProxyHost());
        verify(proxySettings).setPort(digitalTwinTimeSeriesClientProperties.getProxyPort());
    }

    @Test
    public void connectsForSessionOperations() throws WebSocketException, DigitalTwinTimeSeriesClientException {
        final IngestionJob ingestionJob = mock(IngestionJob.class);
        ingestionClient.doInSession(ingestionJob);

        final InOrder sessionOperations = inOrder(webSocket, ingestionJob);
        sessionOperations.verify(webSocket).connect();
        sessionOperations.verify(ingestionJob).doInSession(any(IngestionSession.class));
    }

    @Test
    public void disconnectsAfterSessionOperations() throws DigitalTwinTimeSeriesClientException {
        final IngestionJob ingestionJob = mock(IngestionJob.class);
        ingestionClient.doInSession(ingestionJob);

        final InOrder sessionOperations = inOrder(webSocket, ingestionJob);
        sessionOperations.verify(ingestionJob).doInSession(any(IngestionSession.class));
        sessionOperations.verify(webSocket).disconnect();
    }

    @Test
    public void createSessionWithWebSocket() throws DigitalTwinTimeSeriesClientException {
        final IngestionJob ingestionJob = mock(IngestionJob.class);
        ingestionClient.doInSession(ingestionJob);

        final ArgumentCaptor<IngestionSession> sessionCaptor = ArgumentCaptor.forClass(IngestionSession.class);
        verify(ingestionJob).doInSession(sessionCaptor.capture());
        assertThat(getField(sessionCaptor.getValue(), "webSocket"), is(sameInstance(webSocket)));
    }

    @Test(expected = DigitalTwinTimeSeriesClientException.class)
    public void createWebSocketRethrowsException() throws IOException, DigitalTwinTimeSeriesClientException {
        when(webSocketFactory.createSocket(any(URI.class))).thenThrow(new IOException());
        ingestionClient.doInSession(mock(IngestionJob.class));
    }

    @Test(expected = DigitalTwinTimeSeriesClientException.class)
    public void doInSessionRethrowsConnectException() throws WebSocketException, DigitalTwinTimeSeriesClientException {
        when(webSocket.connect()).thenThrow(new WebSocketException(UNSUPPORTED_PROTOCOL));
        ingestionClient.doInSession(mock(IngestionJob.class));
    }

    @Test
    public void createWebSocketRegistersClientAsListener() {
        invokeMethod(ingestionClient, "createWebSocket");
        verify(webSocket).addListener(same(ingestionClient));
    }

    @Test
    public void logsWebSocketErrors() {
        final WebSocketException webSocketException = new WebSocketException(UNSUPPORTED_PROTOCOL);
        ingestionClient.onError(webSocket, webSocketException);
        verify(LOGGER).error(anyString(), same(webSocketException));
    }

    @Test
    public void logsWebSocketTextMessage() {
        ingestionClient.onTextMessage(webSocket, "foo");
        verify(LOGGER).debug(anyString(), eq("foo"));
    }
}
