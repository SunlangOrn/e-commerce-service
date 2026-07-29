package com.liang.shared.metadata;

import com.liang.shared.security.Role;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Info about who's calling a service method - filled in automatically by
 * MetadataHandlerAspect before the method body runs, for methods annotated
 * with @MetadataHandler that take a Metadata as their first parameter.
 * Callers just pass `new Metadata()`.
 */
@Data
@Builder
@NoArgsConstructor
public class Metadata {
  private Long userId;
  private String email;
  private Role role;

  public Metadata(Long userId, String email, Role role) {
    this.userId = userId;
    this.email = email;
    this.role = role;
  }
}
