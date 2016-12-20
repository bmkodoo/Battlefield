package com.spb.kns;

import com.spb.kns.structures.Command;
import com.spb.kns.utils.CircleLine;
import com.spb.kns.utils.PublishUtils;
import ros.RosBridge;

import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Commander implements Runnable {

    private static final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(Arena.MAX_TEAMS);
    private static final String bridge_addr = System.getProperty("ros.battlefield.bridge_addr", "ws://localhost:9090");;

    private final Solder aim;
    private Random rand = new Random();

    private Map<Integer, Boolean> ready;
    private List<CircleLine.Point> anemys;

    private int team;
    private int anemyIndex = 0;

    public Commander(int team, int solderCount) {
        this.team = team;
        this.ready = new HashMap<>(solderCount);

        this.anemys = new ArrayList<>(2);
        anemys.add(0, new CircleLine.Point(0, 0));
        anemys.add(1, new CircleLine.Point(0, 0));

        for (int i = 0; i < solderCount; i++) {
            ready.put(i, false);
        }
        threadPool.scheduleAtFixedRate(this, 1, 5, TimeUnit.SECONDS);
        aim = new Solder(
                rand.nextDouble() * Arena.W,
                rand.nextDouble() * Arena.H,
                team, -1);
        aim.launch();
        listenCommands();
    }

    private void listenCommands() {
        RosBridge bridge = RosBridge.createConnection(bridge_addr);
        bridge.waitForConnection();
        bridge.subscribe("/commands", "std_msgs/String",
                (data, stringRep) -> {
                    Command command = new Command(data);
                    if (command.team != team) {
                        return;
                    }

                    switch (command.type) {
                        case ANEMY:
                            anemyIndex++;
                            anemys.add(anemyIndex % 2, new CircleLine.Point(command.x, command.y));
                            break;
                        case INJURED:
                            ready.remove(command.id);
                        case DONE:
                            System.err.println(team + " Catch done from " + command.id);
                            ready.values().forEach((v) -> {
                                System.out.print(" " + v + " ");
                            });
                            System.out.println();
                            ready.put(command.id, true);
                            if (ready.values().stream().allMatch((b) -> b)) {
                                System.err.println("All ready FIRE");
                                PublishUtils.sendCommand(new Command(
                                        team,
                                        Command.Type.FIRE,
                                        0,
                                        Math.atan2(
                                            anemys.get(0).y - aim.getY(),
                                            anemys.get(0).x - aim.getX()
                                        ),
                                        0,
                                        0
                                ));
                                resetReady();
                            }
                            break;
                    }
                }
        );
    }

    private void resetReady() {
        ready.keySet().forEach((k) -> ready.put(k, false));
    }

    @Override
    public void run() {
        resetReady();

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
