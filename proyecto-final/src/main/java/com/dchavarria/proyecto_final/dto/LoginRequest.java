package com.dchavarria.proyecto_final.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}