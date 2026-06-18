package com.hau.labhau.controller;

import com.hau.labhau.dto.response.ApartmentResponse;
import com.hau.labhau.security.CustomUserDetails;
import com.hau.labhau.service.ApartmentService;
import com.hau.labhau.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/apartments")
public class ApartmentController {

    private final ApartmentService apartmentService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<ApartmentResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(apartmentService.findById(id));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<ApartmentResponse> getMy() {
        CustomUserDetails user = SecurityUtils.currentUser();
        return ResponseEntity.ok(apartmentService.findByResident(user.getUserId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        apartmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
