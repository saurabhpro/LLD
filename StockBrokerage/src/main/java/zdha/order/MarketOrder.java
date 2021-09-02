package zdha.order;

public class MarketOrder extends Order {

    public MarketOrder(String stockId, int quantity, boolean buyOrder) {
        super(stockId, quantity, buyOrder);
    }
}
