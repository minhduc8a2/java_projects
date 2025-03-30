package com.example.model.enums;

public enum OrderStatus {
    PENDING, // Order placed but not processed
    SHIPPED, // Order shipped but not delivered
    DELIVERED, // Order successfully delivered
    RETURN_REQUESTED, // Customer requested a return
    RETURN_APPROVED, // Admin approved the return
    RETURN_REJECTED, // Admin rejected the return
    RETURNED, // Product has been returned
    REFUNDED, // Refund successfully processed
    CANCELED // Order was canceled before shipping
}
