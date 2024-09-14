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

    /**
     * websocket + oauth2:
     * ref: https://stackoverflow.com/questions/77094697/secure-websocket-connections-with-oauth-in-spring-boot-app
     * websocket 연결이 이루어 지기전, http/https handshake 을 수행한다.
     * 따라서, 이부분에서 jwt 을 보내면,
     * 서버에서 oauth2ResourceServer 함수로 체인 구성하고 있다면
     * 굳이 인터셉터에서 직접 검증 필요없을듯.
     *
     * @param http
     * @return
     */
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
                    "/eatssearch/**"

                ).authenticated()
                .pathMatchers(
                    "/login",
                    "/register",
                    "/contact",
                    "/register",
                    "/ws/**",
                    "/sockjs/**"
                ).permitAll()
                .anyExchange().authenticated())
            .httpBasic(Customizer.withDefaults())
            .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec
                .jwt(jwtSpec -> jwtSpec
                    .jwtAuthenticationConverter(grantedAuthoritiesExtractor())));


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
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:63342", "http://localhost:5173"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setExposedHeaders(Arrays.asList("Authorization")); // 추가된 부분
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/driver/**", configuration);
        source.registerCorsConfiguration("/route/**", configuration);
        source.registerCorsConfiguration("/user/**", configuration);
        source.registerCorsConfiguration("/eatssearch/**", configuration);
        return source;
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter =
            new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter
            (new KeycloakRoleConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }


}