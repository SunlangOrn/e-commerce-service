package com.liang.shared.auth;

import com.liang.shared.metadata.Metadata;

public interface AuthService {
  TokenResponse signup(SignupRequest request);

  TokenResponse signin(SigninRequest request);

  TokenResponse refresh(String refreshTokenValue);

  void logout(String refreshTokenValue);

  UserResponse me(Metadata metadata);

  UserResponse updateProfile(Metadata metadata, UserProfileUpdateRequest request);
}
