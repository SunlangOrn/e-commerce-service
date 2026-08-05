package com.liang.shared.auth;

import com.liang.shared.api.ApiResponse;
import com.liang.shared.metadata.Metadata;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @GetMapping("/me")
  ApiResponse<UserResponse> me() {
    return ApiResponse.ok("Current user", authService.me(new Metadata()));
  }

  @PostMapping("/signup")
  ApiResponse<TokenResponse> signup(@Valid @RequestBody SignupRequest request) {
    return ApiResponse.ok("Account created", authService.signup(request));
  }

  @PostMapping("/signin")
  ApiResponse<TokenResponse> signin(@Valid @RequestBody SigninRequest request) {
    return ApiResponse.ok("Signed in", authService.signin(request));
  }

  @PostMapping("/refresh")
  ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
    return ApiResponse.ok("Token refreshed", authService.refresh(request.getRefreshToken()));
  }

  @PostMapping("/logout")
  ApiResponse<Void> logout(@Valid @RequestBody RefreshRequest request) {
    authService.logout(request.getRefreshToken());
    return ApiResponse.ok("Signed out", null);
  }
}
