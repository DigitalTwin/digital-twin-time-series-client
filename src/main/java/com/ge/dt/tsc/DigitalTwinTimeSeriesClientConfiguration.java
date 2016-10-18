package com.ge.dt.tsc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

import static java.util.Collections.singletonList;

@Configuration
@ComponentScan
public class DigitalTwinTimeSeriesClientConfiguration {

    @Bean
    @Qualifier("timeseries-client")
    public OAuth2RestTemplate oAuth2RestTemplate(DigitalTwinTimeSeriesClientProperties digitalTwinTimeSeriesClientProperties) {
        final OAuth2RestTemplate newOAuth2RestTemplate = new OAuth2RestTemplate(oAuth2ProtectedResourceDetails(digitalTwinTimeSeriesClientProperties));
        newOAuth2RestTemplate.setMessageConverters(singletonList(new MappingJackson2HttpMessageConverter()));
        return newOAuth2RestTemplate;

    }

    @Bean
    @Qualifier("timeseries-client")
    public OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails(DigitalTwinTimeSeriesClientProperties digitalTwinTimeSeriesClientProperties) {
        final ClientCredentialsResourceDetails clientCredentialsResourceDetails = new ClientCredentialsResourceDetails();
        clientCredentialsResourceDetails.setAccessTokenUri(digitalTwinTimeSeriesClientProperties.getTokenEndpoint().toString());
        clientCredentialsResourceDetails.setClientId(digitalTwinTimeSeriesClientProperties.getClientId());
        clientCredentialsResourceDetails.setClientSecret(digitalTwinTimeSeriesClientProperties.getClientSecret());
        return clientCredentialsResourceDetails;
    }

}
