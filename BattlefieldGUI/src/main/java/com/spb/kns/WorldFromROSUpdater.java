package com.spb.kns;

import com.fasterxml.jackson.databind.JsonNode;
import com.spb.kns.structures.Bullet;
import com.spb.kns.structures.WorldObject;
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
                WorldObject msg = new WorldObject(data);
                SolderModel solder;
                if ((solder = world.getSolders().get(msg.id + msg.team * 100)) != null) {
                    solder.setX(msg.x);
                    solder.setY(msg.y);
                    solder.setAngle(msg.angle);
                } else {

                    world.getSolders().put(msg.id + msg.team * 100, new SolderModel(msg.x, msg.y, msg.team, msg.id, msg.hp));
                }
            }
        );

        bridge.subscribe("/bullets", "std_msgs/String",
            (data, stringRep) -> {
                Bullet bullet = new Bullet(data);
                world.bulletHere(bullet);
            }
        );
    }
}
