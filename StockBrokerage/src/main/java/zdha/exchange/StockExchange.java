package zdha.exchange;

import zdha.order.Order;

import java.util.UUID;

public interface StockExchange {

    boolean submitOrder(Order order);

    double getStockMarketPrice(String stockId);

    boolean cancelOrder(UUID orderId);

}
