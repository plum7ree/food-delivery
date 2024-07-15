package com.example.user.service;

import com.example.user.data.dto.UserDto;
import com.example.user.data.entity.Account;
import com.example.user.data.repository.AccountRepository;
import com.example.user.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserMapper userMapper;

    public Optional<UserDto> getUser(String email) {
        try {
            return accountRepository.findByEmail(email).map(userMapper::accountEntityToUser
            );
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public boolean register(UserDto userDto) {
        Account account = userMapper.userDtoToAccount(userDto);
        try {
            accountRepository.save(account);
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

}
