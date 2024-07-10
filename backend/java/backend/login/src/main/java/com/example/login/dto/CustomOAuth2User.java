package com.example.login.dto;

import com.example.login.data.entity.Account;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private String email;
    private String role;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            String email,
                            String role) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.role = role;
    }

    public static CustomOAuth2User of(Account account,
                                      Map<String, Object> attributes,
                                      String providerId) {
        return new CustomOAuth2User(
            AuthorityUtils.createAuthorityList(account.getRole()),
            attributes,
            providerId,
            account.getEmail(),
            account.getRole()
        );
    }
}