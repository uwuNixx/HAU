package com.hau.labhau.dto.response;

import java.util.UUID;

public record ApartmentResponse(
        UUID id,
        String number,
        Integer floor,
        BuildingResponse building,
        UserBriefResponse resident
) {
}
