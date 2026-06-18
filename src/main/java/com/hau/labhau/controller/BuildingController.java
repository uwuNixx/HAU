package com.hau.labhau.controller;

import com.hau.labhau.dto.request.ApplicationFilterRequest;
import com.hau.labhau.dto.response.ApplicationPageResponse;
import com.hau.labhau.dto.response.BuildingResponse;
import com.hau.labhau.service.ApplicationService;
import com.hau.labhau.service.BuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/buildings")
public class BuildingController {

    private final BuildingService buildingService;
    private final ApplicationService applicationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BuildingResponse>> getAll() {
        return ResponseEntity.ok(buildingService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BuildingResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(buildingService.findById(id));
    }

    @GetMapping("/{id}/applications")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<ApplicationPageResponse> getApplicationsByBuilding(
            @PathVariable UUID id,
            @Valid @ModelAttribute ApplicationFilterRequest filter
    ) {
        return ResponseEntity.ok(applicationService.findByBuilding(id, filter));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        buildingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
