package com.liang.shared.security;

import com.liang.shared.auth.UserResponse;
import com.liang.shared.auth.UserStatus;
import com.liang.shared.metadata.Metadata;
import org.springframework.data.domain.Page;

public interface AdminUserService {

    Page<UserResponse> listAll(Integer page, Integer size);

    UserResponse view(Long id);

    UserResponse updateRole(Metadata metadata, Long id, Role role);

    UserResponse updateStatus(Metadata metadata, Long id, UserStatus status);
}
