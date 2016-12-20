package com.spb.kns.structures;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Scanner;

public class WorldObject {
    public int id;
    public int team;
    public double x;
    public double y;
    public double angle;
    public int hp;

    public WorldObject(JsonNode json) {
        String d = json.findValue("data").toString();
        d = d.substring(1, d.length() - 1);
        try (Scanner scan = new Scanner(d)) {
            id = scan.nextInt();
            team = scan.nextInt();

            x = scan.nextDouble();
            y = scan.nextDouble();
            angle = scan.nextDouble();
            hp = scan.nextInt();
        }
    }
}
