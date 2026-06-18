package com.hau.labhau.repository;

import com.hau.labhau.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApartmentRepository extends JpaRepository<Apartment, UUID> {

    Optional<Apartment> findByResidentId(UUID residentId);

    Optional<Apartment> findByBuildingIdAndNumber(UUID buildingId, String number);
}
