package snl.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snl.models.Ladder;
import snl.models.Player;
import snl.models.Snake;
import snl.services.DiceService;

import java.util.List;

public final class SnakeLadderHelper {
    private static final Logger LOG = LoggerFactory.getLogger(SnakeLadderHelper.class);

    public int getTotalValueAfterDiceRolls() {
        // Can use noOfDices and setShouldAllowMultipleDiceRollOnSix here to get total value (Optional requirements)
        return DiceService.roll();
    }

    public int getNewPositionAfterGoingThroughSnakesAndLadders(int newPosition, List<Snake> snakes, List<Ladder> ladders) {
        int previousPosition;

        do {
            previousPosition = newPosition;

            for (Snake snake : snakes) {
                if (snake.start() == newPosition) {
                    newPosition = snake.end(); // Whenever a piece ends up at a position with the head of the snake,
                    // the piece should go down to the position of the tail of that snake.
                }
            }

            // NOTE: we are keeping both inside a loop - this means there can be a situation where the snake lands on
            // a ladder start
            for (Ladder ladder : ladders) {
                if (ladder.start() == newPosition) {
                    newPosition = ladder.end(); // Whenever a piece ends up at a position with the start of the ladder,
                    // the piece should go up to the position of the end of that ladder.
                }
            }
        } while (newPosition != previousPosition); // There could be another snake/ladder at the tail of the snake or the end position of the ladder and the piece should go up/down accordingly.

        return newPosition;
    }


    public void printMovement(Player player, int diceValue, int oldPosition, int newPosition) {
        String msg = player.name() + " rolled a " + diceValue + " and moved from " + oldPosition + " to ";

        if (newPosition < oldPosition) {
            msg += (oldPosition + diceValue) + " and then found a SNAKE from " + (oldPosition + diceValue) + " to " + newPosition;
        } else if (newPosition > oldPosition + diceValue) {
            msg += (oldPosition + diceValue) + " and then found a LADDER from " + (oldPosition + diceValue) + " to " + newPosition;
        } else {
            msg += newPosition;
        }

        LOG.info(msg);
    }
}
