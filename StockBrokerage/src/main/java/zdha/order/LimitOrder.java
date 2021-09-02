package zdha.order;

public class LimitOrder extends Order {
    private double priceLimit;

    public LimitOrder(String stockId, int quantity, boolean buyOrder, double limitPrice) {

        super(stockId, quantity, buyOrder);
    }
}


