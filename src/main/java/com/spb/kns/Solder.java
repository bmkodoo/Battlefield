package com.spb.kns;

public class Solder {

    private static int solders_count = 0;

    private double x;
    private double y;
    private double angle;

    private final int id = solders_count++;

    private final int team;
    private boolean wasLaunched = false;

    public Solder(double x, double y, int team) {
        this.x = x;
        this.y = y;
        this.team = team;
    }

    public SolderAlive launch() {
        if (wasLaunched) {
            throw new RuntimeException("Was already launched!");
        }
        wasLaunched = true;
        return new SolderAlive(this);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    public int getId() {
        return id;
    }

    public int getTeam() {
        return team;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
