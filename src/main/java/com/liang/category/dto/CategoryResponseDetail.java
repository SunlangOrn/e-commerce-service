package com.liang.category.dto;

import com.liang.category.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDetail {
  private Long id;
  private String name;
  private Long parentId;
  private Status status;
}
