package com.liang.shared.auth;

import com.liang.shared.security.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
  private Long id;
  private String name;
  private String email;
  private String phone;
  private String profileImageUrl;
  private Role role;
  private UserStatus status;
}
