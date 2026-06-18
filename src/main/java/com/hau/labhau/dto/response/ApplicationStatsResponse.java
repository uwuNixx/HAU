package com.hau.labhau.dto.response;

import java.util.Map;

public record ApplicationStatsResponse(
        long total,
        Map<String, Long> byStatus
) {
}
