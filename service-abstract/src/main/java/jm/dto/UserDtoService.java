package jm.dto;

import jm.model.User;

public interface UserDtoService {

    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);
}
