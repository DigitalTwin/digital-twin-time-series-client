package com.ge.dt.tsc;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class IngestionClient extends WebSocketAdapter {

    private static final Logger LOGGER = getLogger(IngestionClient.class);

    private final DigitalTwinTimeSeriesClientProperties digitalTwinTimeSeriesClientProperties;
    private final OAuth2RestTemplate oAuth2RestTemplate;
    private final WebSocketFactory webSocketFactory;

    @Autowired
    public IngestionClient(DigitalTwinTimeSeriesClientProperties digitalTwinTimeSeriesClientProperties,
            @Qualifier("timeseries-client") OAuth2RestTemplate oAuth2RestTemplate) {
        this.digitalTwinTimeSeriesClientProperties = digitalTwinTimeSeriesClientProperties;
        this.oAuth2RestTemplate = oAuth2RestTemplate;
        this.webSocketFactory = new WebSocketFactory();

        configureProxySettings();
    }

    public void doInSession(IngestionJob ingestionJob) throws DigitalTwinTimeSeriesClientException {
        final WebSocket webSocket;

        try {
            webSocket = createWebSocket().connect();
        } catch (WebSocketException e) {
            throw new DigitalTwinTimeSeriesClientException("Encountered exception when connecting web socket", e);
        }

        ingestionJob.doInSession(new IngestionSession(webSocket));
        webSocket.disconnect();
    }

    private WebSocket createWebSocket() throws DigitalTwinTimeSeriesClientException {
        try {
            return webSocketFactory.createSocket(digitalTwinTimeSeriesClientProperties.getIngestionEndpoint())
                    .addHeader("Authorization", "Bearer " + oAuth2RestTemplate.getAccessToken().getValue())
                    .addHeader("Predix-Zone-Id", digitalTwinTimeSeriesClientProperties.getZoneId().toString())
                    .addHeader("Origin", "http://predix.io")
                    .addListener(this);
        } catch (IOException e) {
            throw new DigitalTwinTimeSeriesClientException("Encountered exception when creating web socket", e);
        }
    }

    private void configureProxySettings() {
        if (digitalTwinTimeSeriesClientProperties.getProxyHost() != null)
            webSocketFactory.getProxySettings()
                    .setHost(digitalTwinTimeSeriesClientProperties.getProxyHost())
                    .setPort(digitalTwinTimeSeriesClientProperties.getProxyPort());
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) {
        LOGGER.error("Encountered web socket error", cause);
    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) {
        LOGGER.debug("Received text message from time series service: {}", text);
    }
}
