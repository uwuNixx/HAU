package com.hau.labhau.controller;

import com.hau.labhau.dto.response.ServiceCategoryResponse;
import com.hau.labhau.service.ServiceCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/service-categories")
public class ServiceCategoryController {

    private final ServiceCategoryService categoryService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ServiceCategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServiceCategoryResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }
}
