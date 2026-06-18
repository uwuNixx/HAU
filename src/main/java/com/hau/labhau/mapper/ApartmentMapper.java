package com.hau.labhau.mapper;

import com.hau.labhau.dto.response.ApartmentResponse;
import com.hau.labhau.entity.Apartment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {BuildingMapper.class, UserMapper.class})
public interface ApartmentMapper {

    @Mapping(target = "resident", source = "resident")
    ApartmentResponse toResponse(Apartment apartment);
}
