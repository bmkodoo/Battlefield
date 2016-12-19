package com.spb.kns;

import com.spb.kns.structures.Command;
import com.spb.kns.utils.PublishUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Arena {

    public static final int MAX_SOLDERS = 10;
    static final double H = 10;
    static final double W = 10;
    private static final long UPDATE_TIME = 50;

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

        for (int i = 0; i < 2; i++) {
            Solder solder = new Solder(
                    rand.nextDouble() * W,
                    (rand.nextDouble() * (H / 2)) + (H / 2),
                    1, i);
            solder.launch();
        }

        arena.launch();

        Thread.sleep(1000);

        Solder solder = new Solder(
                rand.nextDouble() * W,
                rand.nextDouble() * H,
                0, -1);
        solder.launch();
        PublishUtils.sendCommand(new Command(
                0,
                Command.Type.MOVE,
                0,
                0,
                0,
                0
        ));
    }
}
