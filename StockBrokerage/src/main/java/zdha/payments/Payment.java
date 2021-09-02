package zdha.payments;

import java.util.UUID;

public record Payment(
        UUID paymentId,
        UUID tradeOrderId,
        PaymentMode paymentMode,
        double tradeAmount) {
}
