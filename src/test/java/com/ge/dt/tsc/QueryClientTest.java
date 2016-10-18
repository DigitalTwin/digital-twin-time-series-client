package com.ge.dt.tsc;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.MockRestServiceServer.createServer;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class QueryClientTest {

    private static final URI QUERY_URI = URI.create("http://www.foo.com");
    private static final UUID ZONE_ID = randomUUID();

    private QueryClient queryClient;
    private MockRestServiceServer mockRestServiceServer;

    @Before
    public void setup() {
        final DigitalTwinTimeSeriesClientProperties digitalTwinTimeSeriesClientProperties = new DigitalTwinTimeSeriesClientProperties();
        digitalTwinTimeSeriesClientProperties.setQueryEndpoint(QUERY_URI);
        digitalTwinTimeSeriesClientProperties.setZoneId(ZONE_ID);

        final RestTemplate restTemplate = new RestTemplate();
        mockRestServiceServer = createServer(restTemplate);

        queryClient = new QueryClient(digitalTwinTimeSeriesClientProperties, restTemplate);
    }

    @Test
    public void postsRequest() {
        mockRestServiceServer.expect(requestTo(QUERY_URI.toString()))
                .andExpect(method(POST)).andExpect(content().string("{\"start\":123,\"tags\":[]}"))
                .andRespond(withSuccess().body("{}"));

        final QueryRequest queryRequest = new QueryRequest(123L);
        queryClient.query(queryRequest);

        mockRestServiceServer.verify();
    }

    @Test
    public void setsPredixZoneId() {
        mockRestServiceServer.expect(requestTo(QUERY_URI.toString()))
                .andExpect(method(POST)).andExpect(header("Predix-Zone-Id", ZONE_ID.toString()))
                .andRespond(withSuccess().body("{}"));

        final QueryRequest queryRequest = new QueryRequest(123L);
        queryClient.query(queryRequest);

        mockRestServiceServer.verify();
    }

}
