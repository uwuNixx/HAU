package com.hau.labhau.service;

import com.hau.labhau.dto.response.ServiceCategoryResponse;
import com.hau.labhau.exception.ResourceNotFoundException;
import com.hau.labhau.mapper.ServiceCategoryMapper;
import com.hau.labhau.repository.ServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceCategoryService {

    private final ServiceCategoryRepository categoryRepository;
    private final ServiceCategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<ServiceCategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceCategoryResponse findById(UUID id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена"));
    }
}
