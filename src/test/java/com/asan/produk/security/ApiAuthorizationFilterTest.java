package com.asan.produk.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class ApiAuthorizationFilterTest {

  private static final String SECRET = "only-a-test-key-not-a-production-secret-value";
  private final ApiAuthorizationFilter filter = new ApiAuthorizationFilter(new JwtTokenVerifier(SECRET), "produk");

  @Test
  void deniesMissingToken() throws Exception {
    MockHttpServletResponse response = invoke("GET", "/api/produk/1", null);

    assertEquals(401, response.getStatus());
  }

  @Test
  void permitsAdminToManageProductRoutes() throws Exception {
    MockFilterChain chain = new MockFilterChain();
    MockHttpServletResponse response = invoke("POST", "/api/produk", token("admin"), chain);

    assertEquals(200, response.getStatus());
    assertNotNull(chain.getRequest());
  }

  @Test
  void permitsServiceOnlyForSingleProductRead() throws Exception {
    MockFilterChain allowedChain = new MockFilterChain();
    MockHttpServletResponse allowed = invoke("GET", "/api/produk/1", token("service"), allowedChain);
    MockFilterChain deniedChain = new MockFilterChain();
    MockHttpServletResponse denied = invoke("GET", "/api/produk", token("service"), deniedChain);

    assertEquals(200, allowed.getStatus());
    assertNotNull(allowedChain.getRequest());
    assertEquals(403, denied.getStatus());
    assertEquals(null, deniedChain.getRequest());
  }

  @Test
  void rejectsCustomerRoleAndTamperedToken() throws Exception {
    MockHttpServletResponse customerResponse = invoke("GET", "/api/produk/1", token("pelanggan"));
    String tampered = token("admin").concat("x");
    MockHttpServletResponse tamperedResponse = invoke("GET", "/api/produk/1", tampered);

    assertEquals(403, customerResponse.getStatus());
    assertEquals(401, tamperedResponse.getStatus());
  }

  private MockHttpServletResponse invoke(String method, String path, String token) throws Exception {
    return invoke(method, path, token, new MockFilterChain());
  }

  private MockHttpServletResponse invoke(String method, String path, String token, MockFilterChain chain)
      throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest(method, path);
    request.setServletPath(path);
    if (token != null) {
      request.addHeader("Authorization", "Bearer " + token);
    }
    MockHttpServletResponse response = new MockHttpServletResponse();
    filter.doFilter(request, response, chain);
    return response;
  }

  private String token(String role) {
    byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);
    SecretKey key = Keys.hmacShaKeyFor(keyBytes);
    Instant now = Instant.now();
    return Jwts.builder()
        .issuer("abl-auth")
        .audience().add("abl-services").and()
        .subject("test-user")
        .claim("role", role)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(60)))
        .signWith(key)
        .compact();
  }
}
