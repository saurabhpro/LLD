package snl.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snl.helpers.SnakeLadderHelper;
import snl.models.Player;
import snl.models.SnakeAndLadderBoard;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SnakeAndLadderService {
    private static final Logger LOG = LoggerFactory.getLogger(SnakeAndLadderService.class);

    private final SnakeAndLadderBoard snakeAndLadderBoard;
    private final int initialNumberOfPlayers;
    private final Queue<Player> players; // Comment: Keeping players in game service as they are specific to this game and not the board. Keeping pieces in the board instead.
    private final SnakeLadderHelper helper;

    public SnakeAndLadderService(List<Player> playerList, SnakeAndLadderBoard snakeAndLadderBoard) {
        this.snakeAndLadderBoard = snakeAndLadderBoard;
        this.players = new LinkedList<>(playerList);
        this.initialNumberOfPlayers = playerList.size();
        this.helper = new SnakeLadderHelper();
    }

    /**
     * ==========Core business logic for the game==========
     */
    public void startGame() {
        int round = 0;
        while (!isGameCompleted()) {
            if (round++ % 2 == 0) {
                System.out.println();
            }

            // Each player rolls the dice when their turn comes.
            final int totalDiceValue = helper.getTotalValueAfterDiceRolls();

            // get the player
            final Player currentPlayer = players.remove();

            movePlayer(currentPlayer, totalDiceValue);

            if (hasPlayerWon(currentPlayer)) {
                LOG.info("\n{} wins the game!", currentPlayer.name());
                snakeAndLadderBoard.playerPosition().remove(currentPlayer.id());
            } else {
                // re-queue the player so to get the next-> next turn
                players.add(currentPlayer);
            }
        }
    }


    private boolean isGameCompleted() {
        // Can use shouldGameContinueTillLastPlayer to change the logic of determining if game is completed (Optional requirements)
        int currentNumberOfPlayers = players.size();
        return currentNumberOfPlayers < initialNumberOfPlayers;
    }

    private void movePlayer(Player player, int diceValue) {
        final int boardSize = snakeAndLadderBoard.size();

        final int oldPosition = snakeAndLadderBoard.playerPosition().get(player.id());
        int newPosition = oldPosition + diceValue; // Based on the dice value, the player moves their piece forward that number of cells.

        if (newPosition > boardSize) {
            newPosition = oldPosition; // After the dice roll, if a piece is supposed to move outside position 100, it does not move.
        } else {
            newPosition = helper.getNewPositionAfterGoingThroughSnakesAndLadders(newPosition,
                    snakeAndLadderBoard.snakes(),
                    snakeAndLadderBoard.ladders());
        }

        // update player position
        snakeAndLadderBoard.playerPosition().put(player.id(), newPosition);

        helper.printMovement(player, diceValue, oldPosition, newPosition);
    }


    private boolean hasPlayerWon(Player player) {
        // Can change the logic a bit to handle special cases when there are more than one dice (Optional requirements)
        final int playerPosition = snakeAndLadderBoard.playerPosition().get(player.id());
        final int winningPosition = snakeAndLadderBoard.size();
        return playerPosition == winningPosition; // A player wins if it exactly reaches the position 100 and the game ends there.
    }

    /**
     * =======================================================
     */
}