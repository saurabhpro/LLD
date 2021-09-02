package zdha.exchange;

import zdha.order.Order;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class NSE implements StockExchange {
    @Override
    public boolean submitOrder(Order order) {
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException ignored) {
        }

        order.pricePerUnit = ThreadLocalRandom.current().nextDouble(100);
        return true;
    }

    @Override
    public double getStockMarketPrice(String stockId) {
        return 0;
    }

    @Override
    public boolean cancelOrder(UUID orderId) {
        return false;
    }
}
