package com.hau.labhau.controller;

import com.hau.labhau.dto.request.ApplicationFilterRequest;
import com.hau.labhau.dto.request.CreateApplicationRequest;
import com.hau.labhau.dto.request.UpdateApplicationStatusRequest;
import com.hau.labhau.dto.response.ApplicationPageResponse;
import com.hau.labhau.dto.response.ApplicationResponse;
import com.hau.labhau.dto.response.ApplicationStatsResponse;
import com.hau.labhau.security.CustomUserDetails;
import com.hau.labhau.service.ApplicationService;
import com.hau.labhau.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<ApplicationPageResponse> getAll(@Valid @ModelAttribute ApplicationFilterRequest filter) {
        return ResponseEntity.ok(applicationService.findAll(filter));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApplicationStatsResponse> getStats() {
        return ResponseEntity.ok(applicationService.getStats());
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<ApplicationPageResponse> getMy(@Valid @ModelAttribute ApplicationFilterRequest filter) {
        CustomUserDetails user = SecurityUtils.currentUser();
        return ResponseEntity.ok(applicationService.findByAuthor(user.getUserId(), filter));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApplicationResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(applicationService.findById(id, SecurityUtils.currentUser()));
    }

    @PostMapping
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody CreateApplicationRequest request) {
        CustomUserDetails user = SecurityUtils.currentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.create(request, user.getUserId()));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateApplicationStatusRequest request
    ) {
        return ResponseEntity.ok(applicationService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        applicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
