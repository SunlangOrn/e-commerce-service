package com.liang.category.controller;

import static com.liang.shared.api.ControllerHandler.responseSucceed;

import com.liang.category.dto.CategoryResponse;
import com.liang.category.service.CategoryService;
import com.liang.shared.entity.HttpBodyResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class UserCategoryController {
  private final CategoryService categoryService;

  @GetMapping
  public ResponseEntity<HttpBodyResponse<List<CategoryResponse>>> publicList() {
    return responseSucceed(categoryService.publicList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<HttpBodyResponse<CategoryResponse>> publicView(@PathVariable Long id) {
      return responseSucceed(categoryService.publicView(id));
  }

}
