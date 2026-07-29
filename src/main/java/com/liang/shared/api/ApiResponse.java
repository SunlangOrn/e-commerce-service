package com.liang.shared.api;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;
  private ErrorBody error;
  private Instant timestamp;

  public static <T> ApiResponse<T> ok(String message, T data) {
    return new ApiResponse<>(true, message, data, null, Instant.now());
  }

  public static <T> ApiResponse<T> fail(String message, String code, Object details) {
    return new ApiResponse<>(false, message, null, new ErrorBody(code, details), Instant.now());
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ErrorBody {
    private String code;
    private Object details;
  }
}
