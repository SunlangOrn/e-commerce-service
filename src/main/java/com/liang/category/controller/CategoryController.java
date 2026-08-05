package com.liang.category.controller;

import static com.liang.shared.api.ControllerHandler.responseCreated;
import static com.liang.shared.api.ControllerHandler.responseDeleted;
import static com.liang.shared.api.ControllerHandler.responseSucceed;

import com.liang.category.dto.CategoryRequest;
import com.liang.category.dto.CategoryResponseDetail;
import com.liang.category.service.CategoryService;
import com.liang.shared.entity.HttpBodyResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/v1/categories")
public class CategoryController {
  private final CategoryService categoryService;

  @GetMapping
  public ResponseEntity<HttpBodyResponse<List<CategoryResponseDetail>>> list(
      @RequestParam(required = false) Long parentId) {
    return responseSucceed(categoryService.listAll(parentId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<HttpBodyResponse<CategoryResponseDetail>> view(@PathVariable Long id) {
    return responseSucceed(categoryService.view(id));
  }

  @PostMapping
  public ResponseEntity<HttpBodyResponse<CategoryResponseDetail>> create(
      @Valid @RequestBody CategoryRequest request) {
    return responseCreated(categoryService.create(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<HttpBodyResponse<CategoryResponseDetail>> update(
      @PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
    return responseSucceed(categoryService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    categoryService.delete(id);
    return responseDeleted();
  }
}
