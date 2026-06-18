package com.hau.labhau.mapper;

import com.hau.labhau.dto.response.ApplicationResponse;
import com.hau.labhau.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ApartmentMapper.class, ServiceCategoryMapper.class, UserMapper.class})
public interface ApplicationMapper {

    ApplicationResponse toResponse(Application application);
}
