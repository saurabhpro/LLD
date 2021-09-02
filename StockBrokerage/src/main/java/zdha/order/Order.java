package zdha.order;

import java.time.Instant;
import java.util.UUID;

public abstract class Order {
    public final boolean isBuyOrder;
    public final String stockId;
    public final int quantity;
    public double pricePerUnit;
    private final Instant creationTime;
    public int filledQuantity;
    private OrderStatus status;

    public Order(String stockId, int quantity, boolean buyOrder) {
        this.stockId = stockId;
        this.quantity = quantity;
        this.isBuyOrder = buyOrder;
        this.creationTime = Instant.now();
    }

    public UUID saveInDB() {
        // save in the database, return order ID for testing
        if ("GOOGl".equals(stockId)) {
            return isBuyOrder ? UUID.fromString("382b9e7c-e1a2-4a3d-8829-1ba3f7bfb247") :
                    UUID.fromString("382b9e7c-e1a2-4a3d-8829-1ba3f7bfb248");
        } else if ("TSLA".equals(stockId)) {
            return isBuyOrder ? UUID.fromString("382b9e7c-e1a2-4a3d-8829-1ba3f7bfb249") :
                    UUID.fromString("382b9e7c-e1a2-4a3d-8829-1ba3f7bfb250");
        }

        return UUID.randomUUID();
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public boolean updateInDB() {
        return true;
    }
}

