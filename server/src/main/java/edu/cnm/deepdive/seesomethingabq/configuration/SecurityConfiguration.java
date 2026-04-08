package edu.cnm.deepdive.seesomethingabq.configuration;

import edu.cnm.deepdive.seesomethingabq.service.repository.UserProfileRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for the {@code service} profile.
 *
 * <p>Configures JWT-based authentication/authorization, including an audience/issuer validator and a
 * filter enforcing {@code userEnabled} for authenticated users.</p>
 */
@Configuration
@EnableWebSecurity
@Profile("service")
public class SecurityConfiguration {

  private final Converter<Jwt, ? extends AbstractAuthenticationToken> converter;
  private final String issuerUri;
  private final String clientId;
  private final UserProfileRepository userProfileRepository;

  @Autowired
  SecurityConfiguration(
      Converter<Jwt, ? extends AbstractAuthenticationToken> converter,
      @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri,
      @Value("${spring.security.oauth2.resourceserver.jwt.client-id}") String clientId,
      UserProfileRepository userProfileRepository
  ) {
    this.converter = converter;
    this.issuerUri = issuerUri;
    this.clientId = clientId;
    this.userProfileRepository = userProfileRepository;
  }

  /**
   * Configures the {@link SecurityFilterChain} used by the server.
   *
   * @param httpsecurity HTTP security builder.
   * @return configured filter chain.
   * @throws Exception if Spring Security fails to build the chain.
   */
  @Bean
  public SecurityFilterChain provideSecurityFilterChain(HttpSecurity httpsecurity) {
    return httpsecurity
        .sessionManagement((configurer) ->
            configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests((auth) -> auth
            .requestMatchers("/manager/**").hasRole("MANAGER")
            .anyRequest().authenticated())
        .oauth2ResourceServer((customizer) ->
            customizer.jwt((jwt) -> jwt.jwtAuthenticationConverter(converter)))
        .addFilterAfter(new UserEnabledFilter(userProfileRepository), BearerTokenAuthenticationFilter.class)
        .build();
  }

  /**
   * Provides a {@link JwtDecoder} configured with audience and issuer validation.
   *
   * @return configured decoder.
   */
  @Bean
  public JwtDecoder provideDecoder() {
    NimbusJwtDecoder decoder = JwtDecoders.fromOidcIssuerLocation(issuerUri);
    OAuth2TokenValidator<Jwt> audienceValidator =
        new JwtClaimValidator<List<String>>(JwtClaimNames.AUD, (aud) -> aud.contains(clientId));
    OAuth2TokenValidator<Jwt> issuerValidator =
        JwtValidators.createDefaultWithIssuer(issuerUri);
    OAuth2TokenValidator<Jwt> combinedValidator =
        new DelegatingOAuth2TokenValidator<>(audienceValidator, issuerValidator);
    decoder.setJwtValidator(combinedValidator);
    return decoder;
  }
}
