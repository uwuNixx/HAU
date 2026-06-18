package com.hau.labhau.dto.response;

import java.util.UUID;

public record BuildingResponse(
        UUID id,
        String city,
        String street,
        String houseNumber,
        String managementCompany,
        String fullAddress
) {
}
