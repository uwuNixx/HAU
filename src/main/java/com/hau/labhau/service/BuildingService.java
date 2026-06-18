package com.hau.labhau.service;

import com.hau.labhau.dto.response.BuildingResponse;
import com.hau.labhau.entity.Building;
import com.hau.labhau.exception.ResourceNotFoundException;
import com.hau.labhau.mapper.BuildingMapper;
import com.hau.labhau.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;

    @Transactional(readOnly = true)
    public List<BuildingResponse> findAll() {
        return buildingRepository.findAll().stream()
                .map(buildingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BuildingResponse findById(UUID id) {
        return buildingRepository.findById(id)
                .map(buildingMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Дом не найден"));
    }

    @Transactional
    public void delete(UUID id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Дом не найден"));
        buildingRepository.delete(building);
    }
}
