package com.deundeunhaku.reliablekkuserver.common.security;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import com.deundeunhaku.reliablekkuserver.common.security.filter.JwtAuthenticationFilter;
import com.deundeunhaku.reliablekkuserver.jwt.util.JwtTokenUtils;
import com.deundeunhaku.reliablekkuserver.member.constant.Role;
import com.deundeunhaku.reliablekkuserver.member.repository.MemberRepository;
import com.deundeunhaku.reliablekkuserver.member.service.MemberDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final MemberDetailsService memberDetailsService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth ->
            auth
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers(antMatcher("/docs/**")).permitAll()
                .requestMatchers(antMatcher("/api/v1/login")).permitAll()
                .requestMatchers(antMatcher("/api/v1/token/**")).permitAll()
                .requestMatchers(antMatcher("/api/v1/auth/**")).permitAll()
                .requestMatchers(antMatcher("/api/v1/find-password/**")).permitAll()
                .requestMatchers(antMatcher("/api/v1/register")).permitAll()
                .requestMatchers(antMatcher("/api/v1/register/**")).permitAll()
                .requestMatchers(antMatcher("/api/v1/auth/admin")).permitAll()
                .requestMatchers(antMatcher("/api/v1/admin/**")).hasRole(Role.ADMIN.name())
                .requestMatchers(antMatcher("/api/v1/**")).hasRole(Role.USER.name())
                .anyRequest().authenticated()
        )
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http
        .authenticationProvider(authenticationProvider()).addFilterBefore(
            jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(memberDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

}
