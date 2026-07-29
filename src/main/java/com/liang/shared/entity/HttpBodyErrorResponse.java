package com.liang.shared.entity;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpBodyErrorResponse {
  private String code;
  private String message;
  private Map<String, String> bodyRequestError;
}
