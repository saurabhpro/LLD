package zdha.activeorderqueue;

import zdha.order.Order;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActiveOrderPublisher {
    private final Map<UUID, Order> activeOrders = new HashMap<>();

    public void publish(UUID orderId, Order order) {
        activeOrders.put(orderId, order);
    }

    public Order get(UUID orderId) {
        return activeOrders.get(orderId);
    }

    public void acknowledge(UUID orderId) {
        activeOrders.remove(orderId);
    }
}
