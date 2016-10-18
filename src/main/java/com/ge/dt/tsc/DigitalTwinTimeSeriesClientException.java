package com.ge.dt.tsc;

public class DigitalTwinTimeSeriesClientException extends Exception {

    private static final long serialVersionUID = 6907983571078525638L;

    @SuppressWarnings("unused")
    public DigitalTwinTimeSeriesClientException(String message) {
        super(message);
    }

    public DigitalTwinTimeSeriesClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
