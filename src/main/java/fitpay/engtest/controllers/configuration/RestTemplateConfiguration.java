package fitpay.engtest.controllers.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;

import java.io.UnsupportedEncodingException;

@Configuration
public class RestTemplateConfiguration {

    @Value("${clientId}")
    private String clientId;

    @Value("${clientSecret}")
    private String clientSecret;

    @Value("${accessTokenUri}")
    private String accessTokenUri;

    @Bean
    public OAuth2RestTemplate oauth2RestTemplate() throws UnsupportedEncodingException {
        ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
        resourceDetails.setClientId(clientId);
        resourceDetails.setClientSecret(clientSecret);
        resourceDetails.setClientAuthenticationScheme(AuthenticationScheme.header);
        resourceDetails.setAccessTokenUri(accessTokenUri);
        return new OAuth2RestTemplate(resourceDetails);
    }

}
