package zdha.users;

import zdha.exchange.OrderStockPosition;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record Account(
        User user,
        AccountStatus status,
        double availableFundsForTrading,
        LocalDate dateOfMembership,
        Map<String, List<OrderStockPosition>> stockPositions) {
}