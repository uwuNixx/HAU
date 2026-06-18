package com.hau.labhau.dto.response;

import com.hau.labhau.entity.ApplicationPriority;
import com.hau.labhau.entity.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationResponse(
        UUID id,
        String title,
        String description,
        ApplicationStatus status,
        ApplicationPriority priority,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime completedAt,
        ServiceCategoryResponse category,
        ApartmentResponse apartment,
        UserBriefResponse author,
        UserBriefResponse assignee
) {
}
