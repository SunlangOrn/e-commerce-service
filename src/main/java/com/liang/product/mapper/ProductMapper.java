package com.liang.product.mapper;

import com.liang.category.entity.Status;
import com.liang.product.dto.ProductRequest;
import com.liang.product.dto.ProductResponse;
import com.liang.product.dto.ProductResponseDetail;
import com.liang.product.entity.Product;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponse toResponse(Product product);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponseDetail toDetailResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product from(ProductRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateFrom(ProductRequest request, @MappingTarget Product product);

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