package com.ge.dt.ptsc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.springframework.http.HttpMethod.POST;

@Service
public class QueryClient {

    private final PredixTimeSeriesClientProperties predixTimeSeriesClientProperties;
    private final RestTemplate restTemplate;

    @Autowired
    public QueryClient(PredixTimeSeriesClientProperties predixTimeSeriesClientProperties,
            @Qualifier("timeseries-client") RestTemplate restTemplate) {
        this.predixTimeSeriesClientProperties = predixTimeSeriesClientProperties;
        this.restTemplate = restTemplate;
    }

    public QueryResponse query(QueryRequest queryRequest) {
        return restTemplate.execute(predixTimeSeriesClientProperties.getQueryEndpoint(), POST,
                (request) -> doWithRequest(request, queryRequest), this::extractData);
    }

    private void doWithRequest(ClientHttpRequest request, QueryRequest queryRequest) throws IOException {
        request.getHeaders().set("Predix-Zone-Id", predixTimeSeriesClientProperties.getZoneId().toString());
        request.getBody().write(new ObjectMapper().writeValueAsBytes(queryRequest));
    }

    private QueryResponse extractData(ClientHttpResponse response) throws IOException {
        return new ObjectMapper().readValue(response.getBody(), QueryResponse.class);
    }

}
