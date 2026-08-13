package com.liang.upload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
  private Long id;
  private String fileName;
  private String fileUrl;
  private String fileType;
  private Long fileSize;
  private FileUploadModule module;
  private Long targetId;
}
