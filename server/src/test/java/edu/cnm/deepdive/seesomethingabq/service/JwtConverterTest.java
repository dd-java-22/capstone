/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.seesomethingabq.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class JwtConverterTest {

  @Mock
  private UserService userService;

  @Test
  void convertAddsManagerRoleWhenPersistedUserIsManager() throws Exception {
    UserProfile persisted = new UserProfile();
    persisted.setOauthKey("sub-123");
    persisted.setDisplayName("Manager User");
    persisted.setEmail("manager@example.com");
    persisted.setIsManager(true);

    when(userService.getOrCreate(anyString(), any(UserProfile.class))).thenReturn(persisted);

    JwtConverter converter = new JwtConverter(userService);
    Jwt jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .subject("sub-123")
        .claim("name", "Manager User")
        .claim("email", "manager@example.com")
        .claim("picture", new URL("https://example.com/avatar.png"))
        .build();

    UsernamePasswordAuthenticationToken token = converter.convert(jwt);

    Set<String> roles = token.getAuthorities()
        .stream()
        .map(Object::toString)
        .collect(Collectors.toSet());
    assertTrue(roles.contains("ROLE_USER"));
    assertTrue(roles.contains("ROLE_MANAGER"));
  }

}

