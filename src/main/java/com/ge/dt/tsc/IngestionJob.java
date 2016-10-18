package com.ge.dt.tsc;

@FunctionalInterface
public interface IngestionJob {

    void doInSession(IngestionSession ingestionSession) throws DigitalTwinTimeSeriesClientException;

}
