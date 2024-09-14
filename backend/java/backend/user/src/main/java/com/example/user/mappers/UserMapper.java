package com.example.user.mappers;

import com.example.user.data.dto.AddressDto;
import com.example.user.data.dto.UserDto;
import com.example.user.data.entity.Account;
import com.example.user.data.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface UserMapper {
    @Mapping(target = "id", expression = "java(UUID.fromString(userDto.getId()))")
    Account userDtoToAccount(UserDto userDto);

    @Mapping(target = "id", expression = "java(account.getId().toString())")
    UserDto accountEntityToUser(Account account);

    @Mapping(target = "id", expression = "java(address.getId().toString())")
    @Mapping(target = "userId", expression = "java(address.getUserId().toString())")
    AddressDto addressEntityToAddress(Address address);

    @Mapping(target = "id", expression = "java(UUID.fromString(addressDto.getId()))")
    @Mapping(target = "userId", expression = "java(UUID.fromString(addressDto.getUserId()))")
    Address addressDtoToAddress(AddressDto addressDto);
}
