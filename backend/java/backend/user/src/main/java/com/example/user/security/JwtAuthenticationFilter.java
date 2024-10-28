//package com.example.user.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.web.filter.OncePerRequestFilter;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//
//import java.io.IOException;
//
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final String jwtSecret = "your-jwt-secret";  // 실제 비밀 키 사용 필요
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            Claims claims = validateTokenAndGetClaims(token);
//
//            if (claims != null) {
//                String email = claims.get("sub", String.class);
//
//                // 인증 정보 설정
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                    new User(email, "", new ArrayList<>()), null, new ArrayList<>()
//                );
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//        chain.doFilter(request, response);
//    }
//
//    private Claims validateTokenAndGetClaims(String token) {
//        try {
//            return Jwts.parser()
//                    .setSigningKey(jwtSecret)
//                    .parseClaimsJws(token)
//                    .getBody();
//        } catch (Exception e) {
//            return null;  // 유효하지 않은 토큰일 경우 null 반환
//        }
//    }
//}
//
//
////package com.example.user.security;
////import org.springframework.security.core.context.SecurityContextHolder;
////import org.springframework.security.core.Authentication;
////import org.springframework.security.core.AuthenticationException;
////import org.springframework.security.web.authentication.WebAuthenticationFilter;
////
////import javax.servlet.FilterChain;
////import javax.servlet.http.HttpServletRequest;
////import javax.servlet.http.HttpServletResponse;
////
////public class JwtAuthenticationFilter extends WebAuthenticationFilter {
////
////    private final JwtProvider jwtProvider;
////
////    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
////        this.jwtProvider = jwtProvider;
////    }
////
////    @Override
////    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
////            throws ServletException, IOException {
////        String token = getTokenFromRequest(request);
////
////        if (token != null && jwtProvider.validateToken(token)) {
////            Authentication authentication = jwtProvider.getAuthentication(token);
////            SecurityContextHolder.getContext().setAuthentication(authentication);
////        }
////
////        chain.doFilter(request, response);
////    }
////
////    private String getTokenFromRequest(HttpServletRequest request) {
////        // Request 헤더에서 JWT 토큰을 가져오는 로직
////        String bearerToken = request.getHeader("Authorization");
////        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
////            return bearerToken.substring(7); // "Bearer " 부분을 제거하고 토큰 반환
////        }
////        return null;
////    }
////}
