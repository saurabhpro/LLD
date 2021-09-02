package zdha;

import zdha.activeorderqueue.ActiveOrderPublisher;
import zdha.exceptions.OrderException;
import zdha.exchange.NSE;
import zdha.exchange.OrderStockPosition;
import zdha.exchange.StockExchange;
import zdha.exchange.StockExchangeImpl;
import zdha.order.MarketOrder;
import zdha.order.Order;
import zdha.order.OrderStatus;
import zdha.order.ReturnStatus;
import zdha.users.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static zdha.order.ReturnStatus.FAIL;
import static zdha.order.ReturnStatus.INSUFFICIENT_FUNDS;
import static zdha.order.ReturnStatus.INSUFFICIENT_QUANTITY;
import static zdha.order.ReturnStatus.NO_STOCK_POSITION;
import static zdha.order.ReturnStatus.SUCCESS;

public class UserTradingPlatform implements StockTradingPlatform {
    private final Account account;
    private final StockExchange stockExchange = new StockExchangeImpl(new NSE());
    private final ActiveOrderPublisher publisher = new ActiveOrderPublisher();

    public UserTradingPlatform(Account account) {
        this.account = account;
    }

    @Override
    public ReturnStatus sellOrder(String stockId, int quantity) throws OrderException {

        // check if member has this stock position
        if (!account.stockPositions().containsKey(stockId)) {
            throw new OrderException(NO_STOCK_POSITION.name());
        }

        var stockPosition = account.stockPositions().get(stockId);
        int totalQuantity = stockPosition.stream().mapToInt(OrderStockPosition::quantity).sum();

        // check if the member has enough quantity available to sell
        if (totalQuantity < quantity) {
            throw new OrderException(INSUFFICIENT_QUANTITY.name());
        }

        var order = new MarketOrder(stockId, quantity, false);
        return getReturnStatus(order);
    }

    @Override
    public ReturnStatus buyOrder(String stockId, int quantity) throws OrderException {

        // check if the member has enough funds to buy this stock
        if (account.availableFundsForTrading() < quantity * stockExchange.getStockMarketPrice(stockId)) {
            throw new OrderException(INSUFFICIENT_FUNDS.name());
        }

        var order = new MarketOrder(stockId, quantity, true);
        return getReturnStatus(order);
    }

    private ReturnStatus getReturnStatus(Order order) {
        final UUID orderId = order.saveInDB();

        boolean success = stockExchange.submitOrder(order);
        if (!success) {
            order.setStatus(OrderStatus.FAILED);
            order.updateInDB();
            return FAIL;
        } else {
            publisher.publish(orderId, order);
        }

        return SUCCESS;
    }

    // this function will be invoked whenever there is an update from
    // stock exchange against an order
    public void callbackStockExchange(UUID orderId,
                                      OrderStatus status) {
        Order order = publisher.get(orderId);
        order.setStatus(status);
        order.updateInDB();

        if (status == OrderStatus.FILLED || status == OrderStatus.CANCELLED) {
            publisher.acknowledge(orderId);
        } else if (status == OrderStatus.PARTIALLY_FILLED) {
            Order remaining;
            if (order.isBuyOrder) {
                remaining = new MarketOrder(order.stockId, order.quantity - order.filledQuantity, true);
            } else {
                remaining = new MarketOrder(order.stockId, order.quantity - order.filledQuantity, false);
            }


            getReturnStatus(remaining);
            publisher.acknowledge(orderId);
        }

        account.stockPositions().putIfAbsent(order.stockId, new ArrayList<>());
        account.stockPositions().get(order.stockId)
                .add(new OrderStockPosition(order.quantity, order.quantity * order.pricePerUnit));
    }

    public Map<String, List<OrderStockPosition>> showCurrentPositions() {
        return this.account.stockPositions();
    }
}