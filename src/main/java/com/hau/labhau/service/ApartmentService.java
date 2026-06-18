package com.hau.labhau.service;

import com.hau.labhau.dto.response.ApartmentResponse;
import com.hau.labhau.entity.Apartment;
import com.hau.labhau.exception.ResourceNotFoundException;
import com.hau.labhau.mapper.ApartmentMapper;
import com.hau.labhau.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final ApartmentMapper apartmentMapper;

    @Transactional(readOnly = true)
    public ApartmentResponse findById(UUID id) {
        return apartmentRepository.findById(id)
                .map(apartmentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Квартира не найдена"));
    }

    @Transactional(readOnly = true)
    public ApartmentResponse findByResident(UUID residentId) {
        return apartmentRepository.findByResidentId(residentId)
                .map(apartmentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Квартира для жильца не найдена"));
    }

    @Transactional
    public void delete(UUID id) {
        Apartment apartment = apartmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Квартира не найдена"));
        apartmentRepository.delete(apartment);
    }
}
