package com.liang.category.dto;

import com.liang.category.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
  @NotBlank
  @Size(max = 100)
  private String name;

  private Long parentId;

  private Status status;
}
