package com.femmie.ecommerce.service.user;

import com.femmie.ecommerce.dto.UserDto;
import com.femmie.ecommerce.request.CreateUserRequest;
import com.femmie.ecommerce.request.UserUpdateRequest;

public interface IUserService {

    UserDto getUserById(Long userId);

    UserDto createUser(CreateUserRequest request);

    UserDto updateUser(UserUpdateRequest request, Long userId);

    void deleteUser(Long userId);

    boolean existsByEmail(String email);
}
