package com.liang.product.dto;

import com.liang.category.entity.Status;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private Long categoryId;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;

    @Min(0)
    private Integer stockQuantity;

    private String imageUrl;

    @Pattern(regexp = "^(?i)(ACTIVE|INACTIVE)$")
    private String status;
}
