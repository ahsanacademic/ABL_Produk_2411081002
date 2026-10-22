package com.asan.produk.security;

import java.io.IOException;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiAuthorizationFilter extends OncePerRequestFilter {

  private final JwtTokenVerifier tokenVerifier;
  private final Pattern internalReadPath;

  public ApiAuthorizationFilter(JwtTokenVerifier tokenVerifier,
      @Value("${app.auth.internal-read-resource:produk}") String internalReadResource) {
    this.tokenVerifier = tokenVerifier;
    this.internalReadPath = Pattern.compile("^/api/" + Pattern.quote(internalReadResource) + "/[0-9]+$");
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return !"/api".equals(path) && !path.startsWith("/api/");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authorization = request.getHeader("Authorization");
    if (authorization == null || !authorization.startsWith("Bearer ") || authorization.length() <= 7) {
      writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token tidak ada atau formatnya salah");
      return;
    }

    Claims claims;
    try {
      claims = tokenVerifier.verify(authorization.substring(7).trim());
    } catch (JwtException | IllegalArgumentException exception) {
      writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token tidak valid atau kedaluwarsa");
      return;
    }

    String role = claims.get("role", String.class);
    if ("admin".equals(role) || isAllowedInternalRead(role, request)) {
      filterChain.doFilter(request, response);
      return;
    }

    writeError(response, HttpServletResponse.SC_FORBIDDEN, "Akses ditolak");
  }

  private boolean isAllowedInternalRead(String role, HttpServletRequest request) {
    return "service".equals(role)
        && "GET".equals(request.getMethod())
        && internalReadPath.matcher(request.getServletPath()).matches();
  }

  private void writeError(HttpServletResponse response, int status, String message) throws IOException {
    response.setStatus(status);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write("{\"error\":\"" + message + "\"}");
  }
}
