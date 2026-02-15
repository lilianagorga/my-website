package dev.lilianagorga.mywebsite.config;

import dev.lilianagorga.mywebsite.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
  @Order(1)
  public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
    String activeProfile = System.getProperty("spring.profiles.active", "dev");

    if ("test".equals(activeProfile)) {
      http.securityMatcher("/admin/**")
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    } else {
      http.securityMatcher("/admin/**")
              .authorizeHttpRequests(auth -> auth
                      .requestMatchers("/admin/login", "/admin/register").permitAll()
                      .requestMatchers("/admin/css/**", "/admin/js/**").permitAll()
                      .anyRequest().hasAuthority("ADMIN"))
              .formLogin(form -> form
                      .loginPage("/admin/login")
                      .loginProcessingUrl("/admin/login")
                      .defaultSuccessUrl("/admin/dashboard", true)
                      .failureUrl("/admin/login?error=true")
                      .permitAll())
              .logout(logout -> logout
                      .logoutUrl("/admin/logout")
                      .logoutSuccessUrl("/admin/login?logout=true")
                      .permitAll());
    }
    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    String activeProfile = System.getProperty("spring.profiles.active", "dev");

    if ("test".equals(activeProfile)) {
      http.cors(Customizer.withDefaults())
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    } else if ("dev".equals(activeProfile) || "prod".equals(activeProfile)) {
      http.cors(Customizer.withDefaults())
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(auth -> auth
                      .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                      .requestMatchers("/auth/**").permitAll()
                      .requestMatchers("/send-email").permitAll()
                      .requestMatchers("/").permitAll()
                      .requestMatchers("/projects/**").permitAll()
                      .requestMatchers("/contact").permitAll()
                      .requestMatchers("/messages/**").permitAll()
                      .requestMatchers("/update-ip").permitAll()
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
