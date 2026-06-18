package com.hau.labhau.dto.request;

import com.hau.labhau.entity.ApplicationPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateApplicationRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 5000) String description,
        @NotNull ApplicationPriority priority,
        @NotNull UUID categoryId,
        UUID residentId
) {
}
