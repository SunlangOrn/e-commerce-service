package com.liang.product.mapper;

import com.liang.product.dto.ProductImageResponse;
import com.liang.product.dto.ProductRequest;
import com.liang.product.dto.ProductResponse;
import com.liang.product.entity.Product;
import com.liang.product.entity.ProductImage;
import com.liang.category.entity.Status;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "status", source = "isActive")
    ProductResponse toResponse(Product product);

    ProductImageResponse toResponse(ProductImage image);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isActive", source = "status")
    Product from(ProductRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isActive", source = "status")
    void updateFrom(ProductRequest request, @MappingTarget Product product);

    // Entity/DB stay a plain boolean column; only the API layer speaks in Status.
    default Boolean map(Status status) {
        return status == null ? null : status == Status.ACTIVE;
    }

    default Status map(Boolean isActive) {
        return isActive == null ? null : (isActive ? Status.ACTIVE : Status.INACTIVE);
    }
}
