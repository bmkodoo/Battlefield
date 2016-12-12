package com.spb.kns;

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
        final Map<String, String> strData = new HashMap<String, String>();
        strData.put("id", String.valueOf(solder.getId()));
        strData.put("team", String.valueOf(solder.getTeam()));
        strData.put("angle", String.valueOf(solder.getAngle()));
        strData.put("x", String.valueOf(solder.getX()));
        strData.put("y", String.valueOf(solder.getY()));

        positionPublisher.publish(strData);
    }
}
