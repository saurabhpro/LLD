package snl;

import snl.models.Ladder;
import snl.models.Player;
import snl.models.Snake;
import snl.models.SnakeAndLadderBoard;
import snl.services.SnakeAndLadderService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static snl.constants.Constants.DEFAULT_BOARD_SIZE;

public class Driver {

    public static void main(String[] args) {

        final String msg = """
                Welcome to Snake and Ladder Game.
                Version: 1.0.0
                Developed by: Saurabh Kumar

                Rules:
                  1. Initially both the players are at starting position i.e. 0.
                     Take it in turns to roll the dice.
                     Move forward the number of spaces shown on the dice.
                  2. If you lands at the bottom of a ladder, you can move up to the top of the ladder.
                  3. If you lands on the head of a snake, you must slide down to the bottom of the snake.
                  4. The first player to get to the FINAL position is the winner.
                  5. Hit enter to roll the dice.

                """;
        System.out.println(msg);

        /*
         * ==================Initialize board==================
         */
        final var snakes = getSnakes();
        final var ladders = getLadders();

        Scanner scanner = new Scanner(System.in);
        int noOfPlayers = 2;
        System.out.println("Enter players name:");
        List<Player> players = IntStream.range(0, noOfPlayers)
                .mapToObj(i -> new Player(scanner.next()))
                .toList();


        var snakeAndLadderBoard = new SnakeAndLadderBoard(DEFAULT_BOARD_SIZE, snakes, ladders, initPlayerPieces(players));
        var snakeAndLadderService = new SnakeAndLadderService(players, snakeAndLadderBoard);

        snakeAndLadderService.startGame();
    }

    private static List<Snake> getSnakes() {
        // snake takes you down from 'start' to 'end'
        final int[][] snakesInput = new int[][]{
                new int[]{8, 4},
                new int[]{18, 1},
                new int[]{26, 10},
                new int[]{39, 5},
                new int[]{51, 6},
                new int[]{54, 36},
                new int[]{56, 1},
                new int[]{60, 23},
                new int[]{75, 28},
                new int[]{83, 45},
                new int[]{85, 59},
                new int[]{90, 48},
                new int[]{92, 25},
                new int[]{97, 87},
                new int[]{99, 63}};

        return Arrays.stream(snakesInput)
                .map(snake -> new Snake(snake[0], snake[1]))
                .toList();
    }

    private static List<Ladder> getLadders() {
        // ladder takes you up from 'start' to 'end'
        final int[][] laddersInput = new int[][]{
                new int[]{3, 20},
                new int[]{6, 14},
                new int[]{11, 28},
                new int[]{15, 34},
                new int[]{17, 74},
                new int[]{22, 37},
                new int[]{38, 59},
                new int[]{49, 67},
                new int[]{57, 76},
                new int[]{61, 78},
                new int[]{73, 86},
                new int[]{81, 98},
                new int[]{88, 91}};


        return Arrays.stream(laddersInput)
                .map(ladder -> new Ladder(ladder[0], ladder[1]))
                .toList();
    }

    private static Map<String, Integer> initPlayerPieces(List<Player> players) {
        //Each player has a piece which is initially kept outside the board (i.e., at position 0).
        return players.stream()
                .collect(Collectors.toMap(Player::id, player -> 0, (a, b) -> b));
    }
}