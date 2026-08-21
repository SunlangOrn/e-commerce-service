package com.liang.shared.security;

import static com.liang.shared.api.ControllerHandler.responsePaging;
import static com.liang.shared.api.ControllerHandler.responseSucceed;

import com.liang.shared.auth.UserResponse;
import com.liang.shared.entity.HttpBodyPagingResponse;
import com.liang.shared.entity.HttpBodyResponse;
import com.liang.shared.metadata.Metadata;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/v1/users")
public class AdminUserController {
  private final AdminUserService adminUserService;

  @GetMapping
  public ResponseEntity<HttpBodyResponse<List<UserResponse>>> listAll(
      @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
    Page<UserResponse> users = adminUserService.listAll(page, size);
    return responsePaging(
        users.getContent(),
        HttpBodyPagingResponse.of(
            users.getNumber(), users.getSize(), users.getTotalElements(), users.getTotalPages()));
  }

  @PatchMapping("/{id}/role")
  public ResponseEntity<HttpBodyResponse<UserResponse>> updateRole(
      @PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
    return responseSucceed(adminUserService.updateRole(new Metadata(), id, request.getRole()));
  }
}
