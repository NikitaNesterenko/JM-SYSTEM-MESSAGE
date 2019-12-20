package jm.dto;

import jm.model.User;

import java.util.List;

public interface UserDtoService {

    UserDTO toDto(User user);

    List<UserDTO> toDto(List<User> users);

    User toEntity(UserDTO userDTO);

}
