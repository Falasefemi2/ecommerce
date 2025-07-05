package com.femmie.ecommerce.service.user;

import com.femmie.ecommerce.model.User;
import com.femmie.ecommerce.request.CreateUserRequest;
import com.femmie.ecommerce.request.UserUpdateRequest;

public interface IUserService {

    User getUserById(Long userId);

    User createUser(CreateUserRequest request);

    User updateUser(UserUpdateRequest request, Long userId);

    void deleteUser(Long userId);
}
