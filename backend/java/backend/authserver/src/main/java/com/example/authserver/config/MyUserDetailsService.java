package com.example.authserver.config;

import com.example.authserver.data.entity.Account;
import com.example.authserver.data.repository.AccountRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new
            UsernameNotFoundException("User details not found for the user: " + email));
//        List<GrantedAuthority> authorities = account.getAuthorities().stream().map(authority -> new
//            SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());
        return new User(account.getEmail(),
            account.getPassword(),
            Collections.singleton(new SimpleGrantedAuthority("USER")));
    }

}
