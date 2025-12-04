package com.gramoh.laundry.gramoh_laundry_web.model;

/**
 * Tracks the current status of a laundry order.
 */

public enum OrderStatus {
    PENDING,          // Order placed, waiting for clothes to be logged
    AWAITING_APPROVAL, // Clothes logged, waiting for staff approval
    WASHING,          // Order is being washed/processed
    READY,            // Washing completed, ready for delivery
    DELIVERED,
    VERIFIED

}
