package com.liang.category.mapper;

import com.liang.category.dto.CategoryRequest;
import com.liang.category.dto.CategoryResponse;
import com.liang.category.dto.CategoryResponseDetail;
import com.liang.category.entity.Category;
import com.liang.category.entity.Status;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CategoryMapper {

  @Mapping(target = "status", source = "isActive")
  CategoryResponseDetail toResponseDetail(Category category);

  CategoryResponse toResponse(Category category);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "isActive", source = "status")
  Category from(CategoryRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "isActive", source = "status")
  void updateFrom(CategoryRequest request, @MappingTarget Category category);

  default Boolean map(Status status) {
    return status == null ? null : status == Status.ACTIVE;
  }

  default Status map(Boolean isActive) {
    return isActive == null ? null : (isActive ? Status.ACTIVE : Status.INACTIVE);
  }
}
