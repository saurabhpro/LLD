package snl.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SnakeAndLadderBoard(int size,
                                  List<Snake> snakes, // The board also contains some snakes and ladders.
                                  List<Ladder> ladders,
                                  Map<String, Integer> playerPosition) {

    public SnakeAndLadderBoard(int size) {
        this(size, new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    }
}