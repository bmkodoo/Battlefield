package com.spb.kns;

import com.spb.kns.structures.Command;
import com.spb.kns.utils.PublishUtils;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Commander implements Runnable {

    private static final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(Arena.MAX_TEAMS);
    private final Solder aim;
    private Random rand = new Random();

    private int team;

    public Commander(int team) {
        this.team = team;
        threadPool.scheduleAtFixedRate(this, 1, 5, TimeUnit.SECONDS);
        aim = new Solder(
                rand.nextDouble() * Arena.W,
                rand.nextDouble() * Arena.H,
                team, -1);
        aim.launch();
    }

    @Override
    public void run() {
        aim.setX(rand.nextDouble() * Arena.W);
        aim.setY(rand.nextDouble() * Arena.H);

        PublishUtils.sendCommand(new Command(
                team,
                Command.Type.MOVE,
                0,
                0,
                0,
                0
        ));
    }
}
