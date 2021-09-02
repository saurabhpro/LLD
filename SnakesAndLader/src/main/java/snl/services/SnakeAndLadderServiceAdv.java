package snl.services;

import snl.models.Player;
import snl.models.SnakeAndLadderBoard;

import java.util.List;

import static snl.constants.Constants.DEFAULT_NO_OF_DICES;

public class SnakeAndLadderServiceAdv extends SnakeAndLadderService {
    private boolean isGameCompleted;

    private int noOfDices; //Optional Rule 1
    private boolean shouldGameContinueTillLastPlayer; //Optional Rule 3
    private boolean shouldAllowMultipleDiceRollOnSix; //Optional Rule 4

    public SnakeAndLadderServiceAdv(List<Player> playerList, SnakeAndLadderBoard snakeAndLadderBoard) {
        super(playerList, snakeAndLadderBoard);
        this.noOfDices = DEFAULT_NO_OF_DICES;
    }

    /**
     * ====Setters for making the game more extensible====
     */

    public void setNoOfDices(int noOfDices) {
        this.noOfDices = noOfDices;
    }

    public void setShouldGameContinueTillLastPlayer(boolean shouldGameContinueTillLastPlayer) {
        this.shouldGameContinueTillLastPlayer = shouldGameContinueTillLastPlayer;
    }

    public void setShouldAllowMultipleDiceRollOnSix(boolean shouldAllowMultipleDiceRollOnSix) {
        this.shouldAllowMultipleDiceRollOnSix = shouldAllowMultipleDiceRollOnSix;
    }

    /**
     * ==========Core business logic for the game==========
     */


    /**
     * =======================================================
     */
}