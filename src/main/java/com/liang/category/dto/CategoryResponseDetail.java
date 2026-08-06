package com.liang.category.dto;

import com.liang.category.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDetail {
  private Long id;
  private String status;
  private String name;
}
