package com.hau.labhau.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileInfoResponse {
    private UUID id;
    private String originalName;
    private String contentType;
    private Long size;
    private LocalDateTime createdAt;
    private UUID uploadedById;
}