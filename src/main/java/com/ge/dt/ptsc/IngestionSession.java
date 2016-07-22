package com.ge.dt.ptsc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.ws.client.WebSocket;

public class IngestionSession {

    private final WebSocket webSocket;

    public IngestionSession(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public void ingest(IngestionRequest ingestionRequest) throws PredixTimeSeriesClientException {
        try {
            webSocket.sendText(new ObjectMapper().writeValueAsString(ingestionRequest));
        } catch (JsonProcessingException e) {
            throw new PredixTimeSeriesClientException("Encountered exception when generating request JSON", e);
        }
    }
}
