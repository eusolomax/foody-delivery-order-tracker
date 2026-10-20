package com.foodydelivery.order_tracker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.foodydelivery.order_tracker.auth.dto.RegisterRequest;
import com.foodydelivery.order_tracker.auth.dto.UserResponse;
import com.foodydelivery.order_tracker.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(RegisterRequest request);

    UserResponse toResponse(User user);
}
