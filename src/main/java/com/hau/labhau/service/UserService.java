package com.hau.labhau.service;

import com.hau.labhau.entity.User;
import com.hau.labhau.exception.ResourceNotFoundException;
import com.hau.labhau.repository.ApartmentRepository;
import com.hau.labhau.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;

    @Transactional
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        // Отвязать пользователя от квартиры, если он является жильцом
        apartmentRepository.findByResidentId(id)
                .ifPresent(apartment -> {
                    apartment.setResident(null);
                    apartmentRepository.save(apartment);
                });

        userRepository.delete(user);
    }
}
