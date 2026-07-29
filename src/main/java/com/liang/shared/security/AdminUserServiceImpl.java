package com.liang.shared.security;

import com.liang.shared.api.NotFoundException;
import com.liang.shared.auth.AuthMapper;
import com.liang.shared.auth.UserResponse;
import com.liang.shared.metadata.Metadata;
import com.liang.shared.metadata.MetadataHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
  private final UserRepository userRepository;
  private final AuthMapper authMapper;

  @Override
  public Page<UserResponse> listAll(Integer page, Integer size) {
    Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 20 : size);
    return userRepository.findAll(pageable).map(authMapper::toResponse);
  }

  @Override
  @MetadataHandler
  @Transactional
  public UserResponse updateRole(Metadata metadata, Long id, Role role) {
    if (id.equals(metadata.getUserId())) {
      throw new IllegalArgumentException("You cannot change your own role");
    }
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("User not found"));
    boolean isDemotingAdmin = user.getRole() == Role.ADMIN && role != Role.ADMIN;
    if (isDemotingAdmin && userRepository.countByRole(Role.ADMIN) <= 1) {
      throw new IllegalStateException("Cannot remove the last remaining admin");
    }
    user.setRole(role);
    return authMapper.toResponse(userRepository.save(user));
  }
}
