package com.hau.labhau.mapper;

import com.hau.labhau.dto.response.FileInfoResponse;
import com.hau.labhau.entity.FileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileMapper {

    @Mapping(target = "uploadedById", source = "uploadedBy.id")
    FileInfoResponse toResponse(FileEntity fileEntity);
}