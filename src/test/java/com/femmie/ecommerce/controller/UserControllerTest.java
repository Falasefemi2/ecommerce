package com.femmie.ecommerce.controller;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.femmie.ecommerce.model.User;
import com.femmie.ecommerce.repository.UserRepository;
import com.femmie.ecommerce.request.CreateUserRequest;
import com.femmie.ecommerce.request.UserUpdateRequest;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();

        request.setFirstName("Femi");
        request.setLastName("Code");
        request.setEmail("femi@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.email").value("femi@example.com"));
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = userRepository.save(new User(null, "Femi", "Old", "update@me.com", "pass", null, new ArrayList<>()));

        UserUpdateRequest update = new UserUpdateRequest();
        update.setFirstName("Updated");
        update.setLastName("User");

        mockMvc.perform(put("/api/v1/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Updated"));
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User(null, "Femi", "Dev", "femi@dev.com", "secret", null, new ArrayList<>());
        User saved = userRepository.save(user);

        mockMvc.perform(get("/api/v1/users/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("femi@dev.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = userRepository.save(new User(null, "Femi", "Del", "delete@me.com", "pass", null, new ArrayList<>()));

        mockMvc.perform(delete("/api/v1/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }
}
