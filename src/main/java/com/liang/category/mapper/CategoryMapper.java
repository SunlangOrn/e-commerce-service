package com.liang.category.mapper;

import com.liang.category.dto.CategoryRequest;
import com.liang.category.dto.CategoryResponse;
import com.liang.category.dto.CategoryResponseDetail;
import com.liang.category.entity.Category;
import com.liang.category.entity.Status;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CategoryMapper {

    CategoryResponseDetail toResponseDetail(Category category);

    CategoryResponse toResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "status", expression = "java(mapStringToStatus(request.getStatus()))")
    Category from(CategoryRequest request);

    default Status mapStringToStatus(String statusStr) {
        if (statusStr == null || statusStr.isBlank()) {
            return Status.ACTIVE;
        }
        try {
            return Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + statusStr);
        }
    }

    default String mapStatusToString(Status status) {
        return status != null ? status.name() : null;
    }
}