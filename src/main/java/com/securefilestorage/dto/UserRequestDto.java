package com.securefilestorage.dto;

import lombok.Data;

/**
 * Data Transfer Object for receiving user registration data.
 */
@Data
public class UserRequestDto {
    private String email;
    private String login;
    private String name;
    private String password;
}