package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.response.UserResponseDto;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "identityDocument", source = "identityDocument")
    @Mapping(target = "birthday", source = "birthday")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "email", source = "email")
    UserResponseDto toDto(User user);
}
