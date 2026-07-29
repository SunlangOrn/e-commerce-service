package com.liang.shared.security;

import com.liang.shared.auth.UserResponse;
import com.liang.shared.metadata.Metadata;
import org.springframework.data.domain.Page;

/** ADMIN-only: list every account and grant/revoke the ADMIN role. */
public interface AdminUserService {
  Page<UserResponse> listAll(Integer page, Integer size);

  UserResponse updateRole(Metadata metadata, Long id, Role role);
}
