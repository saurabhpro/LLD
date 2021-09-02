package snl.services;

import java.util.Random;

public final class DiceService {
    private DiceService() {
    }

    public static int roll() {
        return new Random().nextInt(6) + 1; // The game will have a six sided dice numbered from 1 to 6 and will always give a random number on rolling it.
    }
}