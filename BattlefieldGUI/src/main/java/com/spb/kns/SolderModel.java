package com.spb.kns;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SolderModel {

    private static int solders_count = 0;

    private double x;
    private double y;
    private double angle;

    private final int id = solders_count++;

    private final int team;
    private boolean wasLaunched = false;

    public SolderModel(double x, double y, int team) {
        this.x = x;
        this.y = y;
        this.team = team;
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
