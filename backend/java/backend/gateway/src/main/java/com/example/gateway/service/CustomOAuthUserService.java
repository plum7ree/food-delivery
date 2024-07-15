//package com.example.gateway.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CustomOAuthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
//    private final SocialMemberRepository socialMemberRepository;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2UserService delegate = new DefaultOAuth2UserService();
//        OAuth2User oAuth2User = delegate.loadUser(userRequest);
//        log.info("oauth2user = {}",oAuth2User);
//        String email = oAuth2User.getAttribute("email");
//        String nickname = UUID.randomUUID().toString().substring(0,15);
//        String password = "default";
////        Role role = Role.ROLE_USER;
//
//
//        Optional<SocialMember> socialMember = socialMemberRepository.findByEmail(email);
//
//        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//
//        if(socialMember.isEmpty()){
//            SocialMember savedSocialMember = SocialMember.createSocialMember(email, nickname);
//            SaveMemberResponseDto savedResponse = socialMemberRepository.save(savedSocialMember);
//            authorities.add(new SimpleGrantedAuthority("ROLE_FIRST_JOIN"));
//            User savedUser = new User (String.valueOf(savedResponse.getId()),password,authorities);
//            return new CustomUserDetails(String.valueOf(savedResponse.getId()),authorities,savedUser,oAuth2User.getAttributes());
//        }
//        else{
//            authorities.add(new SimpleGrantedAuthority("ROLE_EXIST_USER"));
//            User savedUser = new User (String.valueOf(socialMember.get().getUserId()),password,authorities);
//            return new CustomUserDetails(savedUser.getUsername(),authorities,savedUser,oAuth2User.getAttributes());
//        }
//    }
//
//}