package com.itt.ecommerce.dto;

import java.util.List;

public class CartDto {
    private List<CartItemDto> cartItems;

    public CartDto() {}

    public CartDto(List<CartItemDto> cartItems) {
        this.cartItems = cartItems;
    }

    public List<CartItemDto> getCartItems() {
        return cartItems;
    }
}
