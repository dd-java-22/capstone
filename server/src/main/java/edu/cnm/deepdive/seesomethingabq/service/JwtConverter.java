package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Converts an authenticated {@link Jwt} into a Spring Security {@link UsernamePasswordAuthenticationToken}
 * populated with a {@link UserProfile} principal.
 */
@Service
@Profile("service")
public class JwtConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

  private final UserService userService;

  /**
   * Creates a converter that resolves/creates user profiles via {@link UserService}.
   *
   * @param userService user service.
   */
  @Autowired
  JwtConverter(UserService userService) {
    this.userService = userService;
  }

  @Override
  public UsernamePasswordAuthenticationToken convert(Jwt source) {
    UserProfile user = new UserProfile();
    user.setDisplayName(source.getClaimAsString("name"));
    user.setAvatar(source.getClaimAsURL("picture"));
    user.setEmail(source.getClaimAsString("email"));
    String subject = source.getSubject();
    user.setOauthKey(subject);
    user.setUserEnabled(true);
    user = userService.getOrCreate(subject, user);
    Collection<SimpleGrantedAuthority> grants = new ArrayList<>();
    grants.add(new SimpleGrantedAuthority("ROLE_USER"));
    if (user.isManager()) {
      grants.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
    }
    return new UsernamePasswordAuthenticationToken(user, source.getTokenValue(), grants);
  }


}

