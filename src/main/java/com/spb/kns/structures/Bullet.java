package com.spb.kns.structures;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Scanner;

public class Bullet {

    public int team;
    public double angle;
    public double x;
    public double y;

    public Bullet(int team, double angle, double x, double y) {
        this.team = team;
        this.angle = angle;
        this.x = x;
        this.y = y;
    }

    public Bullet(JsonNode json) {
        String d = json.findValue("data").toString();
        d = d.substring(1, d.length() - 1);
        try (Scanner scan = new Scanner(d)) {
            team = scan.nextInt();
            angle = scan.nextDouble();
            x = scan.nextDouble();
            y = scan.nextDouble();
        }
    }
}
