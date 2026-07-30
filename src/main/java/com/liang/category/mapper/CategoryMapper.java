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
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "parentId", source = "parent.id")
    CategoryResponseDetail toResponseDetail(Category category);

    @Mapping(target = "parentId", source = "parent.id")
    CategoryResponse toResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "status", source = "status")
    Category from(CategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "status", source = "status")
    void updateFrom(CategoryRequest request, @MappingTarget Category category);

    default Status mapStringToStatus(String statusStr) {
        if (statusStr == null || statusStr.isBlank()) {
            return Status.ACTIVE; // Default fallback value
        }
        try {
            return Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + statusStr);
        }
    }

    // Helper method to convert Status enum -> String
    default String mapStatusToString(Status status) {
        return status != null ? status.name() : null;
    }
}