package com.liang.shared.security;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body for {@code PATCH /api/admin/users/{id}/role}. Set role=ADMIN to grant, role=CUSTOMER to revoke. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleRequest {
  @NotNull private Role role;
}
