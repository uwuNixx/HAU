package com.hau.labhau.dto.response;

import java.util.UUID;

public record UserBriefResponse(
        UUID id,
        String email,
        String firstName,
        String lastName
) {
}
