package com.example.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        log.info(source.getClaims().toString());
        // {sub=..., email_verified=true,
        // iss=https://accounts.google.com,
        // given_name=..., picture=..., name=..., exp=2024-07-09T09:09:28Z,
        // family_name=..., iat=2024-07-09T08:09:28Z, email=..., jti=...}

        Collection<GrantedAuthority> returnValue = List.of(new SimpleGrantedAuthority("ROLE_USER"));
//            ((List<String>) realmAccess.get("roles"))
//                .stream().map(roleName -> "ROLE_USER")
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
        return returnValue;
    }

}
