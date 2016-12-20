package com.spb.kns;

import java.util.Random;

public class Arena {

    public static final int MAX_SOLDERS = 10;
    static final double H = 10;
    static final double W = 10;
    private static final long UPDATE_TIME = 50;
    public static final int MAX_TEAMS = 2;

    private boolean wasLaunched = false;


    public void launch() {
        if (wasLaunched) {
            throw new RuntimeException("Was already launched!");
        }
        wasLaunched = true;
    }


    public static void main(String[] args) throws InterruptedException {

        Random rand = new Random();

        Arena arena = new Arena();
        for (int i = 0; i < 4; i++) {
            Solder solder = new Solder(
                    rand.nextDouble() * W,
                    3,
                    0, i);
            solder.launch();
        }

        for (int i = 0; i < 3; i++) {
            Solder solder = new Solder(
                    rand.nextDouble() * W,
                    (rand.nextDouble() * (H / 2)) + (H / 2),
                    1, i);
            solder.launch();
        }

        new Commander(0, 4);
        new Commander(1, 3);

        arena.launch();

        Thread.sleep(1000);


    }
}
