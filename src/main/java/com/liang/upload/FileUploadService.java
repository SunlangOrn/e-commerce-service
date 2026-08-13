package com.liang.upload;

import com.liang.product.dto.ProductResponseDetail;
import com.liang.shared.auth.UserResponse;
import com.liang.shared.metadata.Metadata;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
  FileUploadResponse store(MultipartFile file, FileUploadModule module, Long targetId);

  ProductResponseDetail uploadProductImage(Long productId, MultipartFile file);

  UserResponse uploadUserProfileImage(Metadata metadata, MultipartFile file);
}
