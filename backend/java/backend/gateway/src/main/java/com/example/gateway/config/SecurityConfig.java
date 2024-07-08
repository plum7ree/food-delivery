package com.example.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
//                .cors(cors->cors.disable())
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/my-account",
                    "/driver/**",
                    "/route/**",
                    "/user/**",
                    "/eatssearch/**",
                    "/websocket/**",
                    "/sockjs/**"
                ).authenticated()
                .pathMatchers(
                    "/contact",
                    "/register"
                ).permitAll()
                .anyExchange().authenticated())
            .httpBasic(Customizer.withDefaults())
            .oauth2Login(Customizer.withDefaults())
            .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec
                .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));


        return http.build();
    }

    // from spring 3.2.2, need to implement MapReactiveUserDetailsService by my own.
    // https://github.com/spring-projects/spring-boot/issues/38713#issuecomment-1852289101
    // https://github.com/spring-projects/spring-boot/issues/39096
    @Bean
    MapReactiveUserDetailsService userDetailsService() {
        UserDetails userDetails = User.withUsername("admin").password("admin").roles("ADMIN").build();
        return new MapReactiveUserDetailsService(List.of(userDetails));
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(false);
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:63342"));
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter =
            new JwtAuthenticationConverter();
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }


//    @Bean
//    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
//        return (authorities) -> {
//            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
//
//            authorities.forEach(authority -> {
//                if (OidcUserAuthority.class.isInstance(authority)) {
//                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
//
//                    OidcIdToken idToken = oidcUserAuthority.getIdToken();
//                    OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();
//                    log.info("idToken: {}", idToken);
//                    log.info("userInfo: {}", userInfo);
//                    // Map the claims found in idToken and/or userInfo
//                    // to one or more GrantedAuthority's and add it to mappedAuthorities
//
//                } else if (OAuth2UserAuthority.class.isInstance(authority)) {
//                    OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;
//
//                    Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();
//
//                    // Map the attributes found in userAttributes
//                    // to one or more GrantedAuthority's and add it to mappedAuthorities
//
//                }
//            });
//
//            return mappedAuthorities;
//        };
//    }

}