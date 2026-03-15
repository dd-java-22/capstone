package edu.cnm.deepdive.seesomethingabq;

import java.util.Collection;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtDecoder jwtDecoder) throws Exception {
    httpSecurity
        .authorizeHttpRequests(auth ->
            auth.anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 ->
            oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)))
        .csrf(AbstractHttpConfigurer::disable);
    return httpSecurity.build();
  }

  @Bean
  public JwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties) {
    NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(
        properties.getJwt().getIssuerUri()
    );
    OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<Collection<String>>(
        JwtClaimNames.AUD, aud -> aud != null && aud.contains(properties.getJwt().getAudiences())
    );
    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(
        properties.getJwt().getIssuerUri()
    );
    OAuth2TokenValidator<Jwt> combined = new DelegatingOAuth2TokenValidator<>(
        withIssuer, audienceValidator
    );
    jwtDecoder.setJwtValidator(combined);
    return jwtDecoder;
  }


}
