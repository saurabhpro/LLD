package snl.helpers;

import org.junit.jupiter.api.Test;
import snl.models.Ladder;
import snl.models.Snake;
import snl.services.DiceService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnakeLadderHelperTest {

    private final SnakeLadderHelper helper = new SnakeLadderHelper();

    @Test
    void getTotalValueAfterDiceRolls() {
        final int dice = DiceService.roll();
        assertTrue(dice > 0);
        assertTrue(dice < 7);
    }

    @Test
    void ladder() {
        final int pos = helper.getNewPositionAfterGoingThroughSnakesAndLadders(8,
                List.of(new Snake(13, 7)),
                List.of(new Ladder(8, 43)));

        assertEquals(43, pos);
    }

    @Test
    void snake() {
        final int pos = helper.getNewPositionAfterGoingThroughSnakesAndLadders(13,
                List.of(new Snake(13, 7)),
                List.of(new Ladder(8, 43)));

        assertEquals(7, pos);
    }

    @Test
    void snakeAndLadder() {
        final int pos = helper.getNewPositionAfterGoingThroughSnakesAndLadders(9,
                List.of(new Snake(9, 3)),
                List.of(new Ladder(3, 54)));

        assertEquals(54, pos);
    }
}