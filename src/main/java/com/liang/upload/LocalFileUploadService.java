package com.liang.upload;

import com.liang.product.dto.ProductResponseDetail;
import com.liang.product.entity.Product;
import com.liang.product.mapper.ProductMapper;
import com.liang.product.repository.ProductRepository;
import com.liang.shared.api.NotFoundException;
import com.liang.shared.auth.AuthMapper;
import com.liang.shared.auth.UserResponse;
import com.liang.shared.metadata.Metadata;
import com.liang.shared.metadata.MetadataHandler;
import com.liang.shared.security.User;
import com.liang.shared.security.UserRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LocalFileUploadService implements FileUploadService {
  private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

  private final FileUploadRepository fileUploadRepository;
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;
  private final UserRepository userRepository;
  private final AuthMapper authMapper;

  @Value("${file-upload.local-dir:uploads}")
  private String localDir;

  @Override
  @Transactional
  public ProductResponseDetail uploadProductImage(Long productId, MultipartFile file) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
    FileUploadResponse upload = store(file, FileUploadModule.PRODUCT, product.getId());
    product.setImageUrl(upload.getFileUrl());
    return productMapper.toDetailResponse(productRepository.save(product));
  }

  @Override
  @Transactional
  @MetadataHandler
  public UserResponse uploadUserProfileImage(Metadata metadata, MultipartFile file) {
    User user = userRepository.findById(metadata.getUserId())
        .orElseThrow(() -> new NotFoundException("User not found"));
    FileUploadResponse upload = store(file, FileUploadModule.USER, user.getId());
    user.setProfileImageUrl(upload.getFileUrl());
    return authMapper.toResponse(userRepository.save(user));
  }

  @Override
  @Transactional
  public FileUploadResponse store(MultipartFile file, FileUploadModule module, Long targetId) {
    validate(file);

    String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename());
    String extension = extensionFrom(originalName);
    String storedName = UUID.randomUUID() + extension;
    String modulePath = module.name().toLowerCase(Locale.ROOT);

    Path storagePath = Paths.get(localDir).toAbsolutePath().normalize().resolve(modulePath);
    Path destination = storagePath.resolve(storedName).normalize();
    if (!destination.startsWith(storagePath)) {
      throw new IllegalArgumentException("Invalid file name");
    }

    try {
      Files.createDirectories(storagePath);
      Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ex) {
      throw new IllegalStateException("Could not store file", ex);
    }

    FileUpload upload = new FileUpload();
    upload.setFileName(storedName);
    upload.setFileUrl("/uploads/" + modulePath + "/" + storedName);
    upload.setFileType(file.getContentType());
    upload.setFileSize(file.getSize());
    upload.setModule(module);
    upload.setTargetId(targetId);

    return toResponse(fileUploadRepository.save(upload));
  }

  private void validate(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("File is required");
    }
    if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
      throw new IllegalArgumentException("Only JPG, PNG, WEBP, or GIF images are allowed");
    }
  }

  private String extensionFrom(String fileName) {
    int dot = fileName.lastIndexOf('.');
    return dot >= 0 ? fileName.substring(dot).toLowerCase(Locale.ROOT) : "";
  }

  private FileUploadResponse toResponse(FileUpload upload) {
    return new FileUploadResponse(
        upload.getId(),
        upload.getFileName(),
        upload.getFileUrl(),
        upload.getFileType(),
        upload.getFileSize(),
        upload.getModule(),
        upload.getTargetId());
  }
}
