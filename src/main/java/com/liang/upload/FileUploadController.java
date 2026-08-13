package com.liang.upload;

import com.liang.product.dto.ProductResponseDetail;
import com.liang.shared.api.ApiResponse;
import com.liang.shared.auth.UserResponse;
import com.liang.shared.metadata.Metadata;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file-uploads")
public class FileUploadController {
  private final FileUploadService fileUploadService;

  @PostMapping(value = "/products/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<ProductResponseDetail> uploadProductImage(
      @PathVariable Long productId,
      @RequestPart("file") MultipartFile file) {
    return ApiResponse.ok("Product image uploaded", fileUploadService.uploadProductImage(productId, file));
  }

  @PostMapping(value = "/users/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ApiResponse<UserResponse> uploadUserProfileImage(@RequestPart("file") MultipartFile file) {
    return ApiResponse.ok("Profile image uploaded", fileUploadService.uploadUserProfileImage(new Metadata(), file));
  }
}
