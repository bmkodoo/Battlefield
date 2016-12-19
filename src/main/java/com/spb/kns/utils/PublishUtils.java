package com.spb.kns.utils;

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


    public static void sendSolderPosition(Solder solder) {
        final StringBuilder json = new StringBuilder();
        json.append("{ \"data\": \"")
                .append(String.valueOf(solder.getId())).append(" ")
                .append(String.valueOf(solder.getTeam())).append(" ")
                .append(String.valueOf(solder.getX())).append(" ")
                .append(String.valueOf(solder.getY())).append(" ")
                .append(String.valueOf(solder.getAngle()))
            .append("\"}");

        System.err.println("PUBLISH POS" + String.valueOf(json));

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

        System.err.println("PUBLISH COMMAND" + String.valueOf(json));

        commandsPublisher.publishJsonMsg(String.valueOf(json));
    }
}
