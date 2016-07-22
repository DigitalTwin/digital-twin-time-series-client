package com.ge.dt.ptsc;

public class PredixTimeSeriesClientException extends Exception {

    private static final long serialVersionUID = 6907983571078525638L;

    @SuppressWarnings("unused")
    public PredixTimeSeriesClientException(String message) {
        super(message);
    }

    public PredixTimeSeriesClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
