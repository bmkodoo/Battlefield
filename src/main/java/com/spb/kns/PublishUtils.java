package com.spb.kns;

import com.fasterxml.jackson.databind.JsonNode;
import ros.Publisher;
import ros.RosBridge;

import java.util.HashMap;
import java.util.Map;

public class PublishUtils {

    public static final String bridgeAddr = System.getProperty("ros.battlefield.bridge_addr", "ws://localhost:9090");
    private static final RosBridge bridge = RosBridge.createConnection(bridgeAddr);

    static {
        System.err.println("Waiting for connection to the bridge... " + bridgeAddr);
        bridge.waitForConnection();
        System.err.println("Connected.");
    }

    private static Publisher positionPublisher = new Publisher("/solders_positions", "std_msgs/String", bridge);

    public static void sendSolderPosition(Solder solder) {
        final StringBuilder json = new StringBuilder();
        json.append("{ data: {")
                .append("id: ").append(String.valueOf(solder.getId())).append(", ")
                .append("team: ").append(String.valueOf(solder.getTeam())).append(", ")
                .append("x: ").append(String.valueOf(solder.getX())).append(", ")
                .append("y: ").append(String.valueOf(solder.getY()))
            .append("}}");

        positionPublisher.publishJsonMsg(String.valueOf(json));
    }
}
