package com.example.login.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // JWT 토큰 생성 로직 (필요한 경우)
        String token = generateToken(authentication);

        // 세션에 토큰 저장
        request.getSession().setAttribute("AUTH_TOKEN", token);
        // 보안 쿠키 설정
        Cookie cookie = new Cookie("AUTH_TOKEN", token);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // HTTPS에서만 사용
        cookie.setPath("/");
        cookie.setMaxAge(3600); // 1시간 유효
        response.addCookie(cookie);
        // 프론트엔드로 리다이렉트 (세션 ID는 쿠키로 자동 전송됨)
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:5173");

    }

    private String generateToken(Authentication authentication) {
        // JWT 토큰 생성 로직 구현
        // 예: return jwtTokenProvider.generateToken(authentication);
        return "sample-token";
    }
}

//package com.example.login.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class OAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
//    private final TokenProvider tokenProvider;
//    @Value("${jwt.domain}") private String domain;
//    @Value("${oauth-signup-uri}") private String signUpURI;
//    @Value("${oauth-signin-uri}") private String signInURI;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException {
//        String accessToken = tokenProvider.createToken(authentication);
//        if(request.getServerName().equals("localhost")){
//            String cookieValue = "accessToken=" + accessToken + "; Path=/; Domain=" + domain + "; Max-Age=1800; HttpOnly";
//            response.setHeader("Set-Cookie", cookieValue);
//            log.info("redirect url ={}",redirectUriByFirstJoinOrNot(authentication));
//            response.sendRedirect(redirectUriByFirstJoinOrNot(authentication));
//        }
//        else{
//            String cookieValue = "accessToken="+accessToken+"; "+"Path=/; "+"Domain="+domain+"; "+"Max-Age=1800; HttpOnly; SameSite=None; Secure";
//            response.setHeader("Set-Cookie",cookieValue);
//            response.sendRedirect(redirectUriByFirstJoinOrNot(authentication));
//        }
//    }
//
//    private String redirectUriByFirstJoinOrNot(Authentication authentication){
//        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
//        Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();
//        //사실 authority 가 ROLE_FIRST_JOIN인게 이상하긴함. 하지만 authentication 객체를 활용하기 위해서 해당 방법을 사용하였음.
//        //어차피 role은 우리 로직엔 사용되지 않기 때문임.
//        if(authorities.stream().filter(o -> o.getAuthority().equals("ROLE_FIRST_JOIN")).findAny().isPresent()){
//            return UriComponentsBuilder.fromHttpUrl(signUpURI)
//                    .path(authentication.getName())
//                    .build().toString();
//
//        }
//        else{
//            return UriComponentsBuilder.fromHttpUrl(signInURI)
//                    .build().toString();
//        }
//    }
//}