package com.example.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddToCartRequest(
        @NotNull @Min(1) Long productId,
        @NotNull @Min(1) Integer quantity) {

}
