package com.spb.kns.utils;

import com.spb.kns.structures.Bullet;
import com.spb.kns.structures.Command;
import com.spb.kns.Solder;
import ros.Publisher;
import ros.RosBridge;

public class PublishUtils {

    public static final String bridgeAddr = System.getProperty("ros.battlefield.bridge_addr", "ws://localhost:9090");
    private static final RosBridge bridge = RosBridge.createConnection(bridgeAddr);

    static {
        System.err.println("Waiting for connection to the bridge... " + bridgeAddr);
        bridge.waitForConnection();
        System.err.println("Connected.");
    }

    private static Publisher positionPublisher = new Publisher("/solders_positions", "std_msgs/String", bridge);
    private static Publisher commandsPublisher = new Publisher("/commands", "std_msgs/String", bridge);
    private static Publisher bulletsPublisher = new Publisher("/bullets", "std_msgs/String", bridge);

    public static void sendSolderPosition(Solder solder) {
        final StringBuilder json = new StringBuilder();
        json.append("{ \"data\": \"")
                .append(String.valueOf(solder.getId())).append(" ")
                .append(String.valueOf(solder.getTeam())).append(" ")
                .append(String.valueOf(solder.getX())).append(" ")
                .append(String.valueOf(solder.getY())).append(" ")
                .append(String.valueOf(solder.getAngle())).append(" ")
                .append(String.valueOf(solder.getHp()))
            .append("\"}");

        positionPublisher.publishJsonMsg(String.valueOf(json));
    }

    public static void sendCommand(Command command) {
        final StringBuilder json = new StringBuilder();
        json.append("{ \"data\": \"")
                .append(String.valueOf(command.team)).append(" ")
                .append(String.valueOf(command.type)).append(" ")
                .append(String.valueOf(command.id)).append(" ")
                .append(String.valueOf(command.alpha)).append(" ")
                .append(String.valueOf(command.x)).append(" ")
                .append(String.valueOf(command.y))
                .append("\"}");

        commandsPublisher.publishJsonMsg(String.valueOf(json));
    }

    public static void sendBullet(Bullet bullet) {
        final StringBuilder json = new StringBuilder();
        json.append("{ \"data\": \"")
                .append(String.valueOf(bullet.team)).append(" ")
                .append(String.valueOf(bullet.angle)).append(" ")
                .append(String.valueOf(bullet.x)).append(" ")
                .append(String.valueOf(bullet.y))
                .append("\"}");

        System.err.println("FIRESEND " + json);

        bulletsPublisher.publishJsonMsg(String.valueOf(json));
    }
}
