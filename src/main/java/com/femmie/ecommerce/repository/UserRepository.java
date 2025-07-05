package com.femmie.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.femmie.ecommerce.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
}
