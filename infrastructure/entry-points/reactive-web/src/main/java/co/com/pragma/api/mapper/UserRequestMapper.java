package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.request.UserRequestDto;
import co.com.pragma.model.user.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserRequestMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "identityDocument", source = "identityDocument")
    @Mapping(target = "birthday", source = "birthday")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "salary", source = "salary")
    @Mapping(target = "password", source = "password")
    User toModel(UserRequestDto userRequestDto);
}
