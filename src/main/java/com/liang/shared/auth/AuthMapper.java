package com.liang.shared.auth;

import com.liang.shared.security.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {
  UserResponse toResponse(User user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updated_At", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "profileImageUrl", ignore = true)
  User from(SignupRequest request);
}
