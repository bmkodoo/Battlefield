package com.spb.kns;


import ros.RosBridge;

public class Listener {

    public static void main(String[] args) {

        RosBridge bridge = RosBridge.createConnection(
                System.getProperty("ros.battlefield.bridge_addr", "ws://localhost:9090"));
        bridge.waitForConnection();

        bridge.subscribe("/solders_positions", "std_msgs/String",
                (data, stringRep) -> System.out.println("I received: " + stringRep));
        System.err.println("sadas");
    }
}
