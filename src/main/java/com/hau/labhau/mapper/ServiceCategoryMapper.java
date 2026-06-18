package com.hau.labhau.mapper;

import com.hau.labhau.dto.response.ServiceCategoryResponse;
import com.hau.labhau.entity.ServiceCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceCategoryMapper {

    ServiceCategoryResponse toResponse(ServiceCategory category);
}
