package dev.lilianagorga.mywebsite.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Profile({"dev", "prod"})
public class RateLimitingFilter extends OncePerRequestFilter {

  private static final Set<String> RATE_LIMITED_PATHS = Set.of(
      "/auth/login", "/auth/register", "/messages", "/send-email"
  );

  private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    String path = request.getRequestURI();
    String method = request.getMethod();

    boolean shouldLimit = RATE_LIMITED_PATHS.stream().anyMatch(p -> {
      if ("/messages".equals(p)) {
        return path.equals(p) && "POST".equalsIgnoreCase(method);
      }
      return path.equals(p);
    });

    if (shouldLimit) {
      String clientIp = getClientIp(request);
      Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createBucket());

      if (!bucket.tryConsume(1)) {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private Bucket createBucket() {
    return Bucket.builder()
        .addLimit(Bandwidth.simple(10, Duration.ofMinutes(1)))
        .build();
  }

  private String getClientIp(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}