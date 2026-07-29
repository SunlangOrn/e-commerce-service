package com.liang.shared.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class HttpBodyPagingResponse {
  private int page;
  private int size;
  private long total;
  private int totalPages;
}
