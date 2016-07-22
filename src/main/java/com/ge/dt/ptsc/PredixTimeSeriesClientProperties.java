package com.ge.dt.ptsc;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.URI;
import java.util.UUID;

@Component
@ConfigurationProperties("com.ge.dt.ptsc")
@EnableAutoConfiguration
public class PredixTimeSeriesClientProperties {

    @NotNull
    private URI tokenEndpoint;

    @NotNull
    @Size(min = 1)
    private String clientId;

    @NotNull
    @Size(min = 1)
    private String clientSecret;

    @NotNull
    private URI ingestionEndpoint;

    @NotNull
    private URI queryEndpoint;

    @NotNull
    private UUID zoneId;

    @Size(min = 1)
    private String proxyHost;

    private int proxyPort;

    public URI getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(URI tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public URI getIngestionEndpoint() {
        return ingestionEndpoint;
    }

    public void setIngestionEndpoint(URI ingestionEndpoint) {
        this.ingestionEndpoint = ingestionEndpoint;
    }

    public URI getQueryEndpoint() {
        return queryEndpoint;
    }

    public void setQueryEndpoint(URI queryEndpoint) {
        this.queryEndpoint = queryEndpoint;
    }

    public UUID getZoneId() {
        return zoneId;
    }

    public void setZoneId(UUID zoneId) {
        this.zoneId = zoneId;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }
}
