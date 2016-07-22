package com.ge.dt.ptsc;

@FunctionalInterface
public interface IngestionJob {

    void doInSession(IngestionSession ingestionSession) throws PredixTimeSeriesClientException;

}
