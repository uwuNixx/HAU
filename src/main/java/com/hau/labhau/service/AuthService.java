package com.hau.labhau.service;

import com.hau.labhau.dto.request.AuthRequest;
import com.hau.labhau.dto.request.RegisterRequest;
import com.hau.labhau.dto.response.AuthResponse;
import com.hau.labhau.entity.*;
import com.hau.labhau.exception.BadRequestException;
import com.hau.labhau.exception.ResourceNotFoundException;
import com.hau.labhau.repository.*;
import com.hau.labhau.security.CustomUserDetails;
import com.hau.labhau.security.CustomUserDetailsService;
import com.hau.labhau.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BuildingRepository buildingRepository;
    private final ApartmentRepository apartmentRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(request.email());
        return new AuthResponse(jwtService.generateToken(userDetails));
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("Пользователь с таким email уже существует");
        }

        Role residentRole = roleRepository.findByName("RESIDENT")
                .orElseThrow(() -> new ResourceNotFoundException("Роль RESIDENT не найдена"));

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .enabled(true)
                .roles(Set.of(residentRole))
                .build();
        user = userRepository.save(user);

        Building building = buildingRepository
                .findByCityAndStreetAndHouseNumber(request.city(), request.street(), request.houseNumber())
                .orElseThrow(() -> new BadRequestException(
                        "Дом по адресу: " + request.city() + ", " + request.street() + " " + request.houseNumber() + " не найден. Обратитесь к администратору."));

        if (apartmentRepository.findByBuildingIdAndNumber(building.getId(), request.apartmentNumber()).isPresent()) {
            throw new BadRequestException("Квартира с номером " + request.apartmentNumber() + " в этом доме уже занята");
        }

        Apartment apartment = Apartment.builder()
                .number(request.apartmentNumber())
                .floor(request.floor())
                .building(building)
                .resident(user)
                .build();
        apartmentRepository.save(apartment);

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(request.email());
        return new AuthResponse(jwtService.generateToken(userDetails));
    }
}
