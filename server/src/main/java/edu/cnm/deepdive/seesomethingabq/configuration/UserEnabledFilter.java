package edu.cnm.deepdive.seesomethingabq.configuration;

import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.repository.UserProfileRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Enforces {@link UserProfile#getUserEnabled()} for authenticated requests.
 *
 * <p>Business rule: {@code GET /users/me} is always allowed for authenticated users to support
 * first-login profile creation/bootstrap.</p>
 */
public class UserEnabledFilter extends OncePerRequestFilter {

  private final UserProfileRepository userProfileRepository;

  /**
   * Creates a filter that consults {@link UserProfileRepository} to determine whether an
   * authenticated principal is enabled.
   *
   * @param userProfileRepository repository used to resolve {@link UserProfile} records.
   */
  public UserEnabledFilter(UserProfileRepository userProfileRepository) {
    this.userProfileRepository = userProfileRepository;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String path = request.getRequestURI();
    String contextPath = request.getContextPath();
    if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
      path = path.substring(contextPath.length());
    }

    if ("GET".equalsIgnoreCase(request.getMethod()) && "/users/me".equals(path)) {
      filterChain.doFilter(request, response);
      return;
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      filterChain.doFilter(request, response);
      return;
    }

    if (!isEnabled(authentication)) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    filterChain.doFilter(request, response);
  }

  private boolean isEnabled(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof UserProfile profile) {
      return profile.getUserEnabled();
    }
    if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      String subject = jwtAuthenticationToken.getToken().getSubject();
      Optional<UserProfile> profile = userProfileRepository.findByOauthKey(subject);
      // If we can't resolve a profile for an authenticated principal, fail closed.
      return profile.map(UserProfile::getUserEnabled).orElse(false);
    }
    // For non-JWT authentication types (primarily test scaffolding), don't block.
    return true;
  }

}

