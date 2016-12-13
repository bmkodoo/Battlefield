package com.spb.kns;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SolderAlive implements Runnable  {

    private static final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(Arena.MAX_SOLDERS);
    private static final int DELAY = 50;
    private static final Random rand = new Random();
    private static final double SPEED = 0.1;

    private enum State {
        MOVING, FIRE, STAND
    }

    private State state = State.STAND;
    private double desinationX;
    private double desinationY;
    private double desinationAngle;

    private final Solder solder;

    SolderAlive(Solder solder) {
        this.solder = solder;
        threadPool.scheduleAtFixedRate(this, DELAY, DELAY, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        /*System.err.printf("%s [%s]: (%1.2f, %1.2f) %f\n",
                Integer.toHexString(this.hashCode()),
                state,
                solder.getX(),
                solder.getY(),
                solder.getAngle()
        );*/

        if (state == State.STAND) {
            decideToMoveToRandom();
            return;
        }

        if (state == State.MOVING) {
            if (isReachDestination()) {
                state = State.STAND;
                return;
            }

            moveTowardDestination();
        }
    }

    private void moveTowardDestination() {
        solder.setAngle(Math.atan2(
                desinationY - solder.getY(),
                desinationX - solder.getX()
        ));

        solder.setX(solder.getX() + Math.cos(solder.getAngle()) * SPEED);
        solder.setY(solder.getY() + Math.sin(solder.getAngle()) * SPEED);
    }

    private void decideToMoveToRandom() {
        desinationX = rand.nextDouble() * Arena.W;
        desinationY = rand.nextDouble() * Arena.H;
        state = State.MOVING;
    }

    private boolean isReachDestination() {
        return Math.hypot(solder.getX() - desinationX, solder.getY() - desinationY) <= SPEED;
    }
}
