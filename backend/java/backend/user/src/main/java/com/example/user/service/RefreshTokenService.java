package com.example.user.service;

import com.example.user.data.dto.command.SaveRefreshTokenCommand;
import com.example.user.data.dto.command.SaveRefreshTokenEvent;
import com.example.user.data.entity.RefreshTokenEntity;
import com.example.user.data.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public SaveRefreshTokenEvent saveRefreshToken(SaveRefreshTokenCommand saveRefreshTokenCommand) {
        refreshTokenRepository.save(RefreshTokenEntity.builder()
            .email(saveRefreshTokenCommand.email())
            .value(saveRefreshTokenCommand.token())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build());
        return SaveRefreshTokenEvent.builder().build();
    }
}
