package com.femmie.ecommerce.response;

import lombok.Data;

@Data
public class ApiResponse {
    public ApiResponse() {
    }

    public ApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    private String message;
    private Object data;
}
