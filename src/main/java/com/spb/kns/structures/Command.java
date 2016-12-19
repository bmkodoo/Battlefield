package com.spb.kns.structures;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Scanner;

public class Command {

    public enum Type {
        MOVE, FIRE, ANEMY, INJURED, DONE;
    }

    public int team;
    public Type type;
    public int id;
    public double alpha;
    public double x;
    public double y;

    public Command(JsonNode json) {
        String d = json.findValue("data").toString();
        d = d.substring(1, d.length() - 1);
        try (Scanner scan = new Scanner(d)) {
            team = scan.nextInt();
            type = Type.valueOf(scan.next());
            id = scan.nextInt();
            alpha = scan.nextDouble();
            x = scan.nextDouble();
            y = scan.nextDouble();
        }
    }

    public Command(int team, Type type, int id, double alpha, double x, double y) {
        this.team = team;
        this.type = type;
        this.id = id;
        this.alpha = alpha;
        this.x = x;
        this.y = y;
    }
}
