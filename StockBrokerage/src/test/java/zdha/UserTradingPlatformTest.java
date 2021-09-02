package zdha;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zdha.exceptions.OrderException;
import zdha.exchange.OrderStockPosition;
import zdha.order.OrderStatus;
import zdha.users.Account;
import zdha.users.AccountStatus;
import zdha.users.Address;
import zdha.users.InvestorType;
import zdha.users.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTradingPlatformTest {

    private UserTradingPlatform zerodha;
    private final UUID orderId1 = UUID.fromString("382b9e7c-e1a2-4a3d-8829-1ba3f7bfb247");
    private final UUID orderId2 = UUID.fromString("382b9e7c-e1a2-4a3d-8829-1ba3f7bfb248");
    private final UUID orderId3 = UUID.fromString("382b9e7c-e1a2-4a3d-8829-1ba3f7bfb249");
    private final UUID orderId4 = UUID.fromString("382b9e7c-e1a2-4a3d-8829-1ba3f7bfb250");

    @BeforeEach
    void setup() {
        Address address = new Address("2", "NY", "NY", "12345", "USA");
        User user = new User("saurabh", "12456", InvestorType.RETAIL_INDIVIDUAL_INVESTOR, address);
        Map<String, List<OrderStockPosition>> stockPositions = new HashMap<>();

        Account account = new Account(user, AccountStatus.ACTIVE, 9999, LocalDate.MIN, stockPositions);
        zerodha = new UserTradingPlatform(account);
    }

    @Test
    void orders() throws OrderException {
        assertThrows(OrderException.class, () -> zerodha.sellOrder("GOOGl", 10));

        zerodha.buyOrder("GOOGl", 10);

        // say the callback is called after the buy Order was executed
        zerodha.callbackStockExchange(orderId1, OrderStatus.FILLED);

        System.out.println(zerodha.showCurrentPositions());
    }

    @Test
    void buyOrder() throws OrderException {

        zerodha.buyOrder("GOOGl", 10);

        // say the callback is called after the buy Order was executed
        zerodha.callbackStockExchange(orderId1, OrderStatus.FILLED);

        zerodha.buyOrder("GOOGl", 15);

        // say the callback is called after the buy Order was executed
        zerodha.callbackStockExchange(orderId1, OrderStatus.FILLED);

        System.out.println(zerodha.showCurrentPositions());
    }
}