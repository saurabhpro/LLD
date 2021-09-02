package zdha;

import zdha.exceptions.OrderException;
import zdha.order.ReturnStatus;

public interface StockTradingPlatform {
    ReturnStatus sellOrder(String stockId, int quantity) throws OrderException;

    ReturnStatus buyOrder(String stockId, int quantity) throws OrderException;
}
