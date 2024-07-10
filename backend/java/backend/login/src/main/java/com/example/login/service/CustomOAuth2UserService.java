package com.example.login.service;

import com.example.login.data.entity.Account;

import com.example.login.data.repository.AccountRepository;
import com.example.login.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException, IllegalArgumentException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String provider = userRequest.getClientRegistration()
            .getRegistrationId();
        String providerId = userRequest.getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        CommonOAuth2Provider providerType = CommonOAuth2Provider.valueOf(provider);

        // redirect a user into registration page if not exists
        //TODO 만약 이렇게 저장해놓고 registration 페이지에서 취소하거나 시간 만료되면
        //1. isRegistrationComplete 이 false 인 것들을 주기적으로 체크해야할듯.
        //2. registration 페이지에서 세션 만료 기능추가 해서 시간 지나면 취소하도록 어떻게?
        Account account = accountRepository.findByEmail((String) attributes.get("email"))
            .orElseGet(() ->
                accountRepository.save(Account.builder()
                    .id(UUID.randomUUID())
                    .username(UUID.randomUUID().toString())
                    .password("")
                    .email(((String) attributes.get("email")))
                    .provider(provider)
                    .providerId(providerId)
                    .role("ROLE_USER")
                    .isRegistrationComplete(false)
                    .build()));

        return CustomOAuth2User.of(account, attributes, providerId);

    }


}