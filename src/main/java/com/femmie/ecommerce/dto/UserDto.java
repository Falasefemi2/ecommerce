package com.femmie.ecommerce.dto;

import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    private Long cartId;
    private int orderCount;
}
