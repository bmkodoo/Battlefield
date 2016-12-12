package com.spb.kns;

import ros.Publisher;
import ros.RosBridge;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Arena {

    public static final int MAX_SOLDERS = 10;
    static final double H = 10;
    static final double W = 10;

    private Map<Integer, Solder> solders;
    private Random random;

    public Arena() {
        solders = new HashMap<>();
        random = new Random();
    }

    public Arena createRandomSolder(int team) {
        Solder solder = new Solder(
                random.nextDouble() * W,
                random.nextDouble() * H,
                team);
        solders.put(solder.getId(), solder);
        solder.launch();

        return this;
    }

    public static void main(String[] args) {

        Arena arena = new Arena();
        arena.createRandomSolder(0);
    }
}
