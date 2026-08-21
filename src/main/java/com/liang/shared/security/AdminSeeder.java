package com.liang.shared.security;

import com.liang.shared.auth.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates a default admin account (admin@shop.local / admin123) on first startup,
 * so there's always a way to log in as an admin on a fresh database.
 */
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    if (userRepository.existsByEmail("admin@shop.local")) {
      return;
    }
    User admin = new User();
    admin.setName("Admin");
    admin.setEmail("admin@email.com");
    admin.setPassword(passwordEncoder.encode("admin123"));
    admin.setRole(Role.ADMIN);
    admin.setUserStatus(UserStatus.ENABLE);
    userRepository.save(admin);
  }
}
