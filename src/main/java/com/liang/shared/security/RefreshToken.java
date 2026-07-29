package com.liang.shared.security;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 * A long-lived, database-backed token used to get a new access token without
 * logging in again. Each one is single-use - see AuthServiceImpl.refresh().
 */
@Getter
@Setter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String token;
  private Instant expiresAt;
  private boolean revoked;

  @ManyToOne(optional = false)
  private User user;
}
