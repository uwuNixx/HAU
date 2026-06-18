package com.hau.labhau.dto.response;

import java.util.UUID;

public record ServiceCategoryResponse(
        UUID id,
        String name,
        String description
) {
}
