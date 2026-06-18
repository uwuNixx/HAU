package com.hau.labhau.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RegisterRequest(
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 4, max = 100) String password,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @Size(max = 20) String phone,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 200) String street,
        @NotBlank @Size(max = 20) String houseNumber,
        @NotBlank @Size(max = 20) String apartmentNumber,
        Integer floor
) {
}