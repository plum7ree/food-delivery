package com.example.user.service;

import com.example.user.data.dto.AddressDto;
import com.example.user.data.dto.UserDto;
import com.example.user.data.entity.Account;
import com.example.user.data.entity.Address;
import com.example.user.data.repository.AccountRepository;
import com.example.user.data.repository.AddressRepository;
import com.example.user.exceptions.UserRegistrationException;
import com.example.user.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;
    private final UserMapper userMapper;

    public Optional<UserDto> getUserByOauth2Subject(String sub) {
        try {
            return accountRepository.findByOauth2Sub(sub).map(userMapper::accountEntityToUser
            );
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public Account register(UserDto userDto) throws Exception {
        Account account = userMapper.userDtoToAccount(userDto);
        try {
            return accountRepository.save(account);
        } catch (Exception e) {
            log.error("Error on registering user: {}", e.getMessage());
            throw new UserRegistrationException("Failed to register user", e); // 커스텀 예외 발생
        }
    }

    public Optional<List<UserDto>> findUsersByIds(List<UUID> ids) {
        try {
            return Optional.of(accountRepository.findAllById(ids)
                .stream()
                .map(userMapper::accountEntityToUser).collect(Collectors.toList()));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public Optional<List<AddressDto>> findAddressesByUserIds(List<UUID> ids) {
        try {
            List<Address> addresses = addressRepository.findAllByUserIds(ids);
            Assert.notEmpty(addresses, String.format("address is empty userIds: %s", ids));

            return Optional.of(addresses
                .stream()
                .map(address -> {
                    // 매핑 전 Address 엔티티 로그
                    log.info("Before mapping: {}", address);

                    // 매핑 수행
                    AddressDto addressDto = userMapper.addressEntityToAddress(address);

                    // 매핑 후 AddressDto 로그
                    log.info("After mapping: {}", addressDto);

                    return addressDto;
                }).collect(Collectors.toList()));
        } catch (Exception exception) {
            log.error("findAddressesByUserIds exception: {}", exception.toString());
            return Optional.empty();
        }
    }

    public Address registerAddress(AddressDto addressDto) throws Exception {
        try {
            Address address = userMapper.addressDtoToAddress(addressDto);
            return addressRepository.save(address);

        } catch (Exception e) {
            throw new Exception("registerAddress e: " + e);
        }
    }

    @Transactional
    public void registerUserWithAddress(UserDto userDto, AddressDto addressDto) throws Exception {
        Account account = register(userDto);
        Address address = registerAddress(addressDto);
        Assert.isTrue(account.getId().equals(address.getUserId()), String.format("account, address userId not equal: %s %s", account.getId(), address.getUserId()));
    }
}
