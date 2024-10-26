package com.example.user.controller;

import com.example.commondata.constants.ApplicationConstants;
import com.example.user.data.dto.command.SaveRefreshTokenCommand;
import com.example.user.data.dto.web.*;
import com.example.user.service.AccountService;
import com.example.user.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AccountService accountService;
    private final RefreshTokenService refreshTokenService;

    private final AuthenticationManager authenticationManager;
    private final Environment env;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto tokenRequestDto) {
        // refresh 객체에서 refreshToken 추출
        String refreshTokenSecret = env.getProperty(ApplicationConstants.JWT_REFRESH_TOKEN_SECRET_KEY,
            ApplicationConstants.JWT_REFRESH_TOKEN_SECRET_DEFAULT_VALUE);
        SecretKey refreshTokenSecretKey = Keys.hmacShaKeyFor(refreshTokenSecret.getBytes(StandardCharsets.UTF_8));


        try {
            Claims claims = Jwts.parser().verifyWith(refreshTokenSecretKey)
                .build()
                .parseSignedClaims(tokenRequestDto.refreshToken())
                .getPayload();
            //refresh 토큰의 만료시간이 지나지 않았을 경우, 새로운 access 토큰을 생성
            if (!claims.getExpiration().before(new Date())) {
                // 새로운 Access Token 생성 로직 추가
                String accessTokenSecret = env.getProperty(ApplicationConstants.JWT_ACCESS_TOKEN_SECRET_KEY,
                    ApplicationConstants.JWT_ACCESS_TOKEN_SECRET_DEFAULT_VALUE);
                SecretKey accessTokenSecretKey = Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));

                String newAccessToken = Jwts.builder()
                    .issuer(claims.getIssuer())
                    .subject(claims.getSubject())
                    .claim("email", claims.get("email"))
                    .claim("authorities", claims.get("authorities"))
                    .issuedAt(new java.util.Date())
                    .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))  // 1시간 유효한 Access Token
                    .signWith(accessTokenSecretKey)
                    .compact();

                // 성공적으로 토큰을 갱신하여 반환
                RefreshTokenResponseDto response = new RefreshTokenResponseDto(newAccessToken, "");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RefreshTokenResponseDto("", "Refresh token expired, please login again."));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            //refresh 토큰이 만료되었을 경우, 로그인이 필요합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new RefreshTokenResponseDto("", "Invalid refresh token, please login again."));

        }
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDTO loginRequest) throws Exception {
        // TODO username, password 를 노출시키지 말고 Authorization 헤더에 Basic <email : password encoded> 로 바꾸자.
        String accessToken = "";
        String refreshToken = "";
        Authentication authentication = UsernamePasswordAuthenticationToken
            .unauthenticated(loginRequest.email(), loginRequest.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);
        if (null != authenticationResponse && authenticationResponse.isAuthenticated()) {
            var email = authenticationResponse.getName();
            String accessTokenSecret = env.getProperty(ApplicationConstants.JWT_ACCESS_TOKEN_SECRET_KEY,
                ApplicationConstants.JWT_ACCESS_TOKEN_SECRET_DEFAULT_VALUE);
            SecretKey accessTokenSecretKey = Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));

            String refreshTokenSecret = env.getProperty(ApplicationConstants.JWT_REFRESH_TOKEN_SECRET_KEY,
                ApplicationConstants.JWT_REFRESH_TOKEN_SECRET_DEFAULT_VALUE);
            SecretKey refreshTokenSecretKey = Keys.hmacShaKeyFor(refreshTokenSecret.getBytes(StandardCharsets.UTF_8));

            accessToken = Jwts.builder().issuer("food-delivery").subject("jwt-token")
                .claim("email", email)
                .claim("authorities", authenticationResponse.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date((new java.util.Date()).getTime() + 60 * 60 * 1000L)) // 1시간
                .signWith(accessTokenSecretKey).compact();

            refreshToken = Jwts.builder().issuer("food-delivery").subject("jwt-token")
                .claim("email", email)
                .claim("authorities", authenticationResponse.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date((new java.util.Date()).getTime() + 3 * 24 * 60 * 60 * 1000L)) // 3일
                .signWith(refreshTokenSecretKey).compact();

            refreshTokenService.saveRefreshToken(new SaveRefreshTokenCommand(email, refreshToken));

        }


        return ResponseEntity.ok(LoginResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build());
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterUserDto registerUserDto) {
        Assert.hasLength(registerUserDto.getEmail(), "email is empty");
        Assert.hasLength(registerUserDto.getUsername(), "name is empty");
        Assert.hasLength(registerUserDto.getPassword(), "password is empty");
        Assert.hasLength(registerUserDto.getLat(), "getLat is empty");
        Assert.hasLength(registerUserDto.getLon(), "getLon is empty");
        Assert.hasLength(registerUserDto.getStreet(), "getStreet is empty");
        Assert.hasLength(registerUserDto.getCity(), "getCity is empty");
        Assert.hasLength(registerUserDto.getPostalCode(), "getPostalCode is empty");
        UserDto userDto = UserDto.builder().build();
        userDto.setId(UUID.randomUUID().toString());
        userDto.setEmail(registerUserDto.getEmail());
        userDto.setUsername(registerUserDto.getUsername());
        userDto.setEncryptedPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        userDto.setRole("USER");
        AddressDto addressDto = AddressDto.builder()
            .id(UUID.randomUUID().toString())
            .userId(userDto.getId()) // account 에서 id 직접 가져와야 foreign key error 안뜬다.
            .city(registerUserDto.getCity())
            .street(registerUserDto.getStreet())
            .postalCode(registerUserDto.getPostalCode())
            .lat(Double.parseDouble(registerUserDto.getLat()))
            .lon(Double.parseDouble(registerUserDto.getLon()))
            .build();

        try {
            //TODO transactional
            // ADDRESS 는 restaurant / user one to one mapping 으로 되어있나? 어느걸로 되어있지 ?
            accountService.registerUserWithAddress(userDto, addressDto);
            return ResponseEntity.ok("");
        } catch (Exception e) {
            log.error("User registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/oauth2/register")
    public ResponseEntity<String> register(@RequestHeader HttpHeaders headers, @RequestBody Oauth2RegisterUserDto registerUserDto) throws ParseException {
        Assert.hasLength(registerUserDto.getUsername(), "name is empty");
        Assert.hasLength(registerUserDto.getLat(), "getLat is empty");
        Assert.hasLength(registerUserDto.getLon(), "getLon is empty");
        Assert.hasLength(registerUserDto.getStreet(), "getStreet is empty");
        Assert.hasLength(registerUserDto.getCity(), "getCity is empty");
        Assert.hasLength(registerUserDto.getPostalCode(), "getPostalCode is empty");

        var email = Objects.requireNonNull(headers.get("X-Auth-User-Email")).get(0);
        var oauth2Sub = Objects.requireNonNull(headers.get("X-Auth-User-Sub")).get(0);
        var oauth2Provider = Objects.requireNonNull(headers.get("X-Auth-User-Provider")).get(0);
        var role = Objects.requireNonNull(headers.get("X-Auth-User-Roles")).get(0);

        UserDto userDto = UserDto.builder().build();
        userDto.setId(UUID.randomUUID().toString());
        userDto.setEmail(email);
        userDto.setRole(role);
        userDto.setOauth2Provider(oauth2Provider);
        userDto.setOauth2Sub(oauth2Sub);
        userDto.setUsername(registerUserDto.getUsername());

        AddressDto addressDto = AddressDto.builder()
            .id(UUID.randomUUID().toString())
            .userId(userDto.getId()) // account 에서 id 직접 가져와야 foreign key error 안뜬다.
            .city(registerUserDto.getCity())
            .street(registerUserDto.getStreet())
            .postalCode(registerUserDto.getPostalCode())
            .lat(Double.parseDouble(registerUserDto.getLat()))
            .lon(Double.parseDouble(registerUserDto.getLon()))
            .build();

        log.info("register userDto: {} ", userDto);
        log.info("register addressDto: {}", addressDto);

        try {
            //TODO transactional
            // ADDRESS 는 restaurant / user one to one mapping 으로 되어있나? 어느걸로 되어있지 ?
            accountService.registerUserWithAddress(userDto, addressDto);
            return ResponseEntity.ok("");
        } catch (Exception e) {
            log.error("User registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}