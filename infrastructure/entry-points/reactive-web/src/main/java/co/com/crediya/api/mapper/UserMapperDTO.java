package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.CreateUserDTO;
import co.com.crediya.api.dto.UserDTO;
import co.com.crediya.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapperDTO {

    UserDTO toResponse(User user);

    User toModel(CreateUserDTO createUserDTO);
}
