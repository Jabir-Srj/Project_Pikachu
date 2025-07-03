package model;

import java.time.LocalDateTime;

/**
 * Represents a refund request for a booking.
 */
public class RefundRequest {
    private String refundId;
    private String bookingId;
    private String customerId;
    private String reason;
    private double refundAmount;
    private RefundStatus status;
    private LocalDateTime requestDate;
    private LocalDateTime reviewDate;
    private String reviewedBy;
    private String rejectionReason;
    private RefundMethod refundMethod;

    public RefundRequest() {
        this.requestDate = LocalDateTime.now();
        this.status = RefundStatus.PENDING;
    }

    public RefundRequest(String refundId, String bookingId, String customerId, 
                        String reason, double refundAmount) {
        this();
        this.refundId = refundId;
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.reason = reason;
        this.refundAmount = refundAmount;
    }

    /**
     * Approve the refund request
     * @param reviewedBy User who approved
     */
    public void approve(String reviewedBy) {
        this.status = RefundStatus.APPROVED;
        this.reviewedBy = reviewedBy;
        this.reviewDate = LocalDateTime.now();
    }

    /**
     * Reject the refund request
     * @param reviewedBy User who rejected
     * @param rejectionReason Reason for rejection
     */
    public void reject(String reviewedBy, String rejectionReason) {
        this.status = RefundStatus.REJECTED;
        this.reviewedBy = reviewedBy;
        this.rejectionReason = rejectionReason;
        this.reviewDate = LocalDateTime.now();
    }

    /**
     * Process the approved refund
     */
    public void processRefund() {
        if (this.status == RefundStatus.APPROVED) {
            this.status = RefundStatus.PROCESSED;
        }
    }

    /**
     * Check if refund is still pending
     * @return true if pending
     */
    public boolean isPending() {
        return this.status == RefundStatus.PENDING;
    }

    /**
     * Check if refund was approved
     * @return true if approved
     */
    public boolean isApproved() {
        return this.status == RefundStatus.APPROVED;
    }

    // Getters and Setters
    public String getRefundId() { return refundId; }
    public void setRefundId(String refundId) { this.refundId = refundId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(double refundAmount) { this.refundAmount = refundAmount; }

    public RefundStatus getStatus() { return status; }
    public void setStatus(RefundStatus status) { this.status = status; }

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public RefundMethod getRefundMethod() { return refundMethod; }
    public void setRefundMethod(RefundMethod refundMethod) { this.refundMethod = refundMethod; }

    @Override
    public String toString() {
        return "RefundRequest{" +
                "refundId='" + refundId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", refundAmount=" + refundAmount +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                '}';
    }
}

/**
 * Enum for refund status
 */
enum RefundStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    PROCESSED("Processed");

    private final String displayName;

    RefundStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

/**
 * Enum for refund methods
 */
enum RefundMethod {
    ORIGINAL_PAYMENT("Original Payment Method"),
    BANK_TRANSFER("Bank Transfer"),
    STORE_CREDIT("Store Credit"),
    CHECK("Check");

    private final String displayName;

    RefundMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 