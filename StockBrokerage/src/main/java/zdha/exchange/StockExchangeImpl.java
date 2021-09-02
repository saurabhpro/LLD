package zdha.exchange;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import zdha.order.Order;

import java.util.UUID;

@Singleton
public class StockExchangeImpl implements StockExchange {

    private final StockExchange stockExchange;

    @Inject
    public StockExchangeImpl(StockExchange stockExchangeInstance) {
        this.stockExchange = stockExchangeInstance;
    }

    @Override
    public boolean submitOrder(Order order) {
        return stockExchange.submitOrder(order);
    }

    @Override
    public double getStockMarketPrice(String stockId) {
        return stockExchange.getStockMarketPrice(stockId);
    }

    @Override
    public boolean cancelOrder(UUID orderId) {
        return stockExchange.cancelOrder(orderId);
    }
}
