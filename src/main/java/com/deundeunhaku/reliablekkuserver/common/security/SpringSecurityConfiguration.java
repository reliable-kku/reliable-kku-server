package com.deundeunhaku.reliablekkuserver.common.security;

import com.deundeunhaku.reliablekkuserver.common.security.filter.JwtAuthenticationFilter;
import com.deundeunhaku.reliablekkuserver.jwt.util.JwtTokenUtils;
import com.deundeunhaku.reliablekkuserver.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

  private final JwtTokenUtils jwtTokenUtils;
  private final MemberRepository memberRepository;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
    MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

    http
        .cors(AbstractHttpConfigurer::disable)
        .csrf(csrfConfigurer ->
            csrfConfigurer.ignoringRequestMatchers(mvcMatcherBuilder.pattern("/api/v1/**"),
                PathRequest.toH2Console()))
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth ->
        auth
            .requestMatchers(mvcMatcherBuilder.pattern("/api/v1/login")).permitAll()
            .requestMatchers(mvcMatcherBuilder.pattern("/api/v1/token/valid")).permitAll()
            .requestMatchers(mvcMatcherBuilder.pattern("/api/v1/token/update")).permitAll()
            .requestMatchers(PathRequest.toH2Console()).permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    http
        .addFilterBefore(
            new JwtAuthenticationFilter(jwtTokenUtils, memberRepository),
            UsernamePasswordAuthenticationFilter.class
        );

    return http.build();
  }

}
