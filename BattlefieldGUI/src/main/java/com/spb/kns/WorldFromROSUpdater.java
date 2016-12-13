package com.spb.kns;

import com.fasterxml.jackson.databind.JsonNode;
import ros.RosBridge;

import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WorldFromROSUpdater implements Runnable {
    private static final String bridge_addr = System.getProperty("ros.battlefield.bridge_addr", "ws://localhost:9090");;

    Executor executor = Executors.newSingleThreadExecutor();
    private WorldModel world;

    WorldFromROSUpdater(WorldModel world) {
        this.world = world;
        executor.execute(this);
    }

    @Override
    public void run() {
        RosBridge bridge = RosBridge.createConnection(bridge_addr);
        bridge.waitForConnection();
        bridge.subscribe("/solders_positions", "std_msgs/String",
            (data, stringRep) -> {
                Msg msg = new Msg(data);
                SolderModel solder;
                if ((solder = world.getSolders().get(msg.id)) != null) {
                    solder.setX(msg.x);
                    solder.setY(msg.y);
                    solder.setAngle(msg.angle);
                } else {
                    world.getSolders().put(msg.id, new SolderModel(msg.x, msg.y, msg.team));
                }
            }
        );
    }

    static class Msg {
        int id;
        int team;
        double x;
        double y;
        double angle;

        Msg(JsonNode json) {
            String d = json.findValue("data").toString();
            d = d.substring(1, d.length() - 1);
            try (Scanner scan = new Scanner(d)) {
                id = scan.nextInt();
                team = scan.nextInt();

                x = scan.nextDouble();
                y = scan.nextDouble();
                angle = scan.nextDouble();
            }
        }
    }
}
