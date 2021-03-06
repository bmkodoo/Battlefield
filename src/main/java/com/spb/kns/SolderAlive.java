package com.spb.kns;

import com.spb.kns.structures.Bullet;
import com.spb.kns.structures.Command;
import com.spb.kns.structures.WorldObject;
import com.spb.kns.utils.CircleLine;
import com.spb.kns.utils.PublishUtils;
import ros.RosBridge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SolderAlive implements Runnable  {

    private static final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(Arena.MAX_SOLDERS);
    private static final int DELAY = 100;
    private static final Random rand = new Random();
    private static final double NEAR_DISTANCE = 20;
    private static final double SIZE = 1;
    private static final int FIRE_SPEED = 10;
    public static final double NORM_SPEED = 0.15;
    public static final double FAST_SPEED = 0.2;

    private double speed = 0.2;

    private final Map<Integer, Solder> alias;

    private static final String bridge_addr = System.getProperty("ros.battlefield.bridge_addr", "ws://localhost:9090");;

    private enum State {
        MOVING, FIRE, STAND
    }

    private State state = State.STAND;
    private Solder destination;

    private final Solder solder;

    private int tickCount = 0;

    private boolean injured = false;

    SolderAlive(Solder solder) {
        destination = new Solder(0, 0, solder.getTeam(), -2);
        this.solder = solder;
        alias = new HashMap<>();
        threadPool.scheduleAtFixedRate(this, DELAY, DELAY, TimeUnit.MILLISECONDS);
        listenObjects();
        listenCommands();
        listenBullets();
    }

    private void listenObjects() {

        RosBridge bridge = RosBridge.createConnection(bridge_addr);
        bridge.waitForConnection();
        bridge.subscribe("/solders_positions", "std_msgs/String",
                (data, stringRep) -> {
                    if (injured) return;

                    WorldObject object = new WorldObject(data);
                    if (object.team == solder.getTeam()) {
                        updateAlias(object);
                    } else if (isNear(object)) {
                        if (state != State.MOVING)
                            return;

                        PublishUtils.sendCommand(
                                new Command(
                                        solder.getTeam(),
                                        Command.Type.ANEMY,
                                        object.id,
                                        0,
                                        object.x,
                                        object.y
                                )
                        );
                    }
                }
        );
    }

    private void listenCommands() {
        RosBridge bridge = RosBridge.createConnection(bridge_addr);
        bridge.waitForConnection();
        bridge.subscribe("/commands", "std_msgs/String",
                (data, stringRep) -> {
                    if (injured) return;

                    Command command = new Command(data);
                    if (command.team != solder.getTeam() || solder.getId() == -1) {
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
                            solder.setAngle(command.alpha);
                            destination = new Solder(0, 0, solder.getTeam(), -2);
                            destination.setAngle(command.alpha);
                    }
                }
        );
    }

    private void listenBullets() {
        if (solder.getId() == -1) return;

        RosBridge bridge = RosBridge.createConnection(bridge_addr);
        bridge.waitForConnection();
        bridge.subscribe("/bullets", "std_msgs/String",
                (data, stringRep) -> {
                    if (injured) return;

                    Bullet bullet = new Bullet(data);
                    if (bullet.team == solder.getTeam()) {
                        return;
                    }

                    List<CircleLine.Point> intersection = CircleLine.getCircleLineIntersectionPoint(
                            new CircleLine.Point(bullet.x, bullet.y),
                            new CircleLine.Point(
                                    1000 * Math.cos(bullet.angle),
                                    1000 * Math.sin(bullet.angle)
                            ),
                            new CircleLine.Point(solder.x, solder.y),
                            SIZE
                    );

                    if (intersection.isEmpty()) {
                        return;
                    }

                    CircleLine.Point point = intersection.get(0);
                    if (distanceTo(point.x, point.y) < SIZE * 2.1) {
                        solder.setHp(solder.getHp() - 1);
                    }

                    if (solder.hp < 0) {
                        injured();
                    }
                }
        );
    }

    private void injured() {
        injured = true;
        PublishUtils.sendCommand(new Command(
                solder.getTeam(),
                Command.Type.INJURED,
                solder.getId(),
                0,
                0,
                0
        ));
        solder.setId(rand.nextInt() * -1);
        state = State.STAND;
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

        if (injured) return;

        switch (state) {
            case STAND:
                return;

            case MOVING:
                if (distanceTo(destination.x, destination.y) <= SIZE) {
                    PublishUtils.sendCommand(new Command(
                            solder.getTeam(),
                            Command.Type.DONE,
                            solder.getId(),
                            0,
                            0,
                            0
                    ));
                    System.out.println("PRIBIL");
                    return;
                }

                if (solder.getId() == 0) {
                    moveToward(destination.x, destination.y);
                } else if (solder.getId() % 2 == 1) {
                    //to the back
                    moveToward(
                            destination.x - SIZE * Math.cos(destination.getAngle()),
                            destination.y - SIZE * Math.sin(destination.getAngle())
                    );
                } else {
                    // to the right
                    moveToward(
                            destination.x + SIZE * Math.cos(destination.getAngle() + Math.PI / 2),
                            destination.y + SIZE * Math.sin(destination.getAngle() + Math.PI / 2)
                    );
                }

                solder.setAngle(destination.angle);
                break;

            case FIRE:
                if (tickCount % FIRE_SPEED == 0) {
                    PublishUtils.sendBullet(new Bullet(
                            solder.getTeam(),
                            destination.getAngle() + 0.5 - rand.nextDouble(),
                            solder.x,
                            solder.y
                    ));
                }
        }
    }

    private void moveToward(double x, double y) {
        double sp;
        if (solder.getId() == 0) {
            sp = NORM_SPEED;
        } else {
            sp = (NORM_SPEED + distanceTo(x, y) * 0.1);
        }

        double distAng = Math.atan2(
                y - solder.getY(),
                x - solder.getX()
        );
        solder.setX(solder.getX() + Math.cos(distAng) * sp);
        solder.setY(solder.getY() + Math.sin(distAng) * sp);
    }

}
