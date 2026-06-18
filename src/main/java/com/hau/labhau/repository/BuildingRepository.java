package com.hau.labhau.repository;

import com.hau.labhau.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BuildingRepository extends JpaRepository<Building, UUID> {

    Optional<Building> findByCityAndStreetAndHouseNumber(String city, String street, String houseNumber);
}
