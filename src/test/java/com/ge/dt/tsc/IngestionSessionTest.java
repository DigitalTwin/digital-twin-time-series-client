package com.ge.dt.tsc;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.ws.client.WebSocket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IngestionSession.class)
public class IngestionSessionTest {

    private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    private IngestionSession ingestionSession;

    @Mock
    private WebSocket webSocket;

    @Before
    public void setup() {
        initMocks(this);
        ingestionSession = new IngestionSession(webSocket);
    }

    @Test
    public void sendsJsonIngestionRequestPayload() throws DigitalTwinTimeSeriesClientException {
        final IngestionRequest ingestionRequest = new IngestionRequest();
        ingestionRequest.addBody("foo").addDataPoint(new Date(123L), 234, 0);

        ingestionSession.ingest(ingestionRequest);
        verify(webSocket).sendText(matches("^\\{\"messageId\":\"" + UUID_REGEX + "\",\"" +
                "body\":\\[\\{\"attributes\":\\{\\},\"name\":\"foo\",\"datapoints\":\\[\\[123,234.0,0\\]\\]\\}\\]\\}$"));
    }

    @Test(expected = DigitalTwinTimeSeriesClientException.class)
    public void rethrowsExceptionWhenPerformingObjectMapping() throws Exception {
        final ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonMappingException("Boom"));
        whenNew(ObjectMapper.class).withAnyArguments().thenReturn(objectMapper);
        ingestionSession.ingest(new IngestionRequest());
    }
}
