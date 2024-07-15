package com.example.user.mappers;

import com.example.user.data.dto.UserDto;
import com.example.user.data.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface UserMapper {
    @Mapping(target = "id", expression = "java(UUID.fromString(userDto.getId()))")
    Account userDtoToAccount(UserDto userDto);

    // @Mapping(source = "dateOfBirth", target = "birthDate")
    @Mapping(target = "id", expression = "java(account.getId().toString())")
    UserDto accountEntityToUser(Account account);
}
