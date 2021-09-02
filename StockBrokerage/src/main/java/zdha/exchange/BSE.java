package zdha.exchange;

import zdha.order.Order;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BSE implements StockExchange {

    Map<String, Double> stockPrices = new HashMap<>();
    Map<String, Integer> stockQuantities = new HashMap<>();

    @Override
    public boolean submitOrder(Order order) {
        try {
            if (order.isBuyOrder) {
                stockQuantities.put(order.stockId, stockQuantities.get(order.stockId) - order.quantity);
            } else {
                stockQuantities.put(order.stockId, stockQuantities.get(order.stockId) + order.quantity);
            }
        } catch (Exception ignored) {
            return false;
        }

        return true;
    }

    @Override
    public double getStockMarketPrice(String stockId) {
        return stockPrices.get(stockId);
    }

    @Override
    public boolean cancelOrder(UUID orderId) {
        return false;
    }
}
