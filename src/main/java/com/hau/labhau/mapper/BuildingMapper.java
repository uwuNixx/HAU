package com.hau.labhau.mapper;

import com.hau.labhau.dto.response.BuildingResponse;
import com.hau.labhau.entity.Building;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BuildingMapper {

    @Mapping(target = "fullAddress", expression = "java(building.getCity() + \", \" + building.getStreet() + \", д. \" + building.getHouseNumber())")
    BuildingResponse toResponse(Building building);
}
