package model;

import java.time.LocalDate;

/**
 * Represents payment details for a booking with encryption for security.
 */
public class PaymentDetails {
    private String paymentId;
    private String cardNumber; // Will be encrypted
    private String cardholderName;
    private LocalDate expiryDate;
    private String cvv; // Will be encrypted
    private PaymentMethod paymentMethod;
    private String billingAddress;
    private double amount;
    private PaymentStatus status;

    public PaymentDetails() {
        this.status = PaymentStatus.PENDING;
    }

    public PaymentDetails(String cardNumber, String cardholderName, 
                         LocalDate expiryDate, String cvv) {
        this();
        this.cardNumber = encryptCardNumber(cardNumber);
        this.cardholderName = cardholderName;
        this.expiryDate = expiryDate;
        this.cvv = encryptCvv(cvv);
        this.paymentMethod = PaymentMethod.CREDIT_CARD;
    }

    /**
     * Encrypt card number for security (simplified)
     * @param cardNumber Original card number
     * @return Encrypted card number
     */
    private String encryptCardNumber(String cardNumber) {
        // Simple masking - in real implementation use proper encryption
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    /**
     * Encrypt CVV for security (simplified)
     * @param cvv Original CVV
     * @return Encrypted CVV
     */
    private String encryptCvv(String cvv) {
        // Simple masking - in real implementation use proper encryption
        return "***";
    }

    /**
     * Process the payment
     * @return true if payment successful
     */
    public boolean processPayment() {
        // Simulate payment processing
        try {
            Thread.sleep(100); // Simulate processing time
            this.status = PaymentStatus.COMPLETED;
            return true;
        } catch (InterruptedException e) {
            this.status = PaymentStatus.FAILED;
            return false;
        }
    }

    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = encryptCardNumber(cardNumber); }

    public String getCardholderName() { return cardholderName; }
    public void setCardholderName(String cardholderName) { this.cardholderName = cardholderName; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = encryptCvv(cvv); }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "PaymentDetails{" +
                "cardNumber='" + cardNumber + '\'' +
                ", cardholderName='" + cardholderName + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                '}';
    }
}

/**
 * Enum for payment methods
 */
enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    PAYPAL("PayPal"),
    BANK_TRANSFER("Bank Transfer");

    private final String displayName;

    PaymentMethod(String displayName) {
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
 * Enum for payment status
 */
enum PaymentStatus {
    PENDING("Pending"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    REFUNDED("Refunded");

    private final String displayName;

    PaymentStatus(String displayName) {
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