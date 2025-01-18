package dev.lilianagorga.mywebsite.config;

import dev.lilianagorga.mywebsite.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    String activeProfile = System.getProperty("spring.profiles.active", "dev");

    if ("test".equals(activeProfile)) {
      http.csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    } else if ("dev".equals(activeProfile) || "prod".equals(activeProfile)) {
      http.csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(auth -> auth
                      .requestMatchers("/auth/**").permitAll()
                      .requestMatchers("/send-email").permitAll()
                      .requestMatchers("/").permitAll()
                      .requestMatchers("/messages/**").permitAll()
                      .requestMatchers("/users/**").hasAnyAuthority("USER", "ADMIN")
                      .anyRequest().authenticated())
              .httpBasic(Customizer.withDefaults());
      http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}