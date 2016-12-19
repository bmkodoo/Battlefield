package com.spb.kns;

import com.spb.kns.structures.Command;
import com.spb.kns.structures.WorldObject;
import com.spb.kns.utils.PublishUtils;
import ros.RosBridge;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SolderAlive implements Runnable  {

    private static final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(Arena.MAX_SOLDERS);
    private static final int DELAY = 100;
    private static final Random rand = new Random();
    private static final double SPEED = 0.2;
    private static final double NEAR_DISTANCE = 20;
    private static final double SIZE = 1;
    private static final int FIRE_SPEED = 10;

    private final Map<Integer, Solder> alias;

    private static final String bridge_addr = System.getProperty("ros.battlefield.bridge_addr", "ws://localhost:9090");;

    private enum State {
        MOVING, FIRE, STAND
    }

    private State state = State.STAND;
    private Solder destination;

    private final Solder solder;

    private int tickCount = 0;

    SolderAlive(Solder solder) {
        this.solder = solder;
        alias = new HashMap<>();
        threadPool.scheduleAtFixedRate(this, DELAY, DELAY, TimeUnit.MILLISECONDS);
        listenObjects();
        listenCommands();
    }

    private void listenObjects() {
        RosBridge bridge = RosBridge.createConnection(bridge_addr);
        bridge.waitForConnection();
        bridge.subscribe("/solders_positions", "std_msgs/String",
                (data, stringRep) -> {
                    WorldObject object = new WorldObject(data);
                    if (object.team == solder.getTeam()) {
                        updateAlias(object);
                    } else if (isNear(object)) {
//                        PublishUtils.sendCommand(
//                                new Command(
//                                        solder.getTeam(),
//                                        Command.Type.ANEMY,
//                                        object.id,
//                                        0,
//                                        object.x,
//                                        object.y
//                                )
//                        );
                    }
                }
        );
    }

    private void listenCommands() {
        RosBridge bridge = RosBridge.createConnection(bridge_addr);
        bridge.waitForConnection();
        bridge.subscribe("/commands", "std_msgs/String",
                (data, stringRep) -> {
                    Command command = new Command(data);
                    if (command.team != solder.getTeam()) {
                        return;
                    }

                    switch (command.type) {
                        case MOVE:
                            if (!alias.containsKey(solder.getId() - 1)) {
                                return;
                            }

                            destination = alias.get(solder.getId() - 1);
                            state = State.MOVING;
                            break;

                        case INJURED:
                            if (command.id < solder.getId()) {
                                solder.setId(solder.getId() - 1);
                            }

                            if (command.id == destination.getId()) {
                                destination = alias.getOrDefault(destination.getId() - 1, this.solder);
                            }
                            break;

                        case FIRE:
                            state = State.FIRE;
                            destination.setAngle(command.alpha);
                    }
                }
        );
    }

    private boolean isNear(WorldObject object) {
        return distanceTo(object.x, object.y) < NEAR_DISTANCE;
    }

    private double distanceTo(double x, double y) {
        return Math.sqrt(Math.pow(x - solder.x, 2) + Math.pow(y - solder.y, 2));
    }

    private void updateAlias(WorldObject object) {
        Solder newSolder;
        if ((newSolder = alias.get(object.id)) != null) {
            newSolder.setX(object.x);
            newSolder.setY(object.y);
            newSolder.setAngle(object.angle);
        } else {
            alias.put(object.id, new Solder(object.x, object.y, object.id, object.team));
        }
    }

    @Override
    public void run() {
        tickCount++;
        PublishUtils.sendSolderPosition(solder);

        /*System.err.printf("%s [%s]: (%1.2f, %1.2f) %f\n",
                Integer.toHexString(this.hashCode()),
                state,
                solder.getX(),
                solder.getY(),
                solder.getAngle()
        );*/
        switch (state) {
            case STAND:
                return;

            case MOVING:
                if (isReachDestination()) {
                    return;
                }

                moveTowardDestination();
                break;

            case FIRE:
                if (tickCount % FIRE_SPEED == 0) {
                    //TODO: fire
                }
        }
    }

    private void moveTowardDestination() {
        solder.setAngle(Math.atan2(
                destination.getY() - solder.getY(),
                destination.getX() - solder.getX()
        ));

        solder.setX(solder.getX() + Math.cos(solder.getAngle()) * SPEED);
        solder.setY(solder.getY() + Math.sin(solder.getAngle()) * SPEED);
    }

    private boolean isReachDestination() {
        return distanceTo(destination.x, destination.y) <= SIZE;
    }
}
