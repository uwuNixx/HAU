package com.hau.labhau.mapper;

import com.hau.labhau.dto.response.UserBriefResponse;
import com.hau.labhau.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserBriefResponse toBrief(User user);
}
