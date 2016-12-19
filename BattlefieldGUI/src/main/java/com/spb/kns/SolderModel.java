package com.spb.kns;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SolderModel {

    private double x;
    private double y;
    private double angle;

    private final int id;

    private final int team;
    private boolean wasLaunched = false;

    public SolderModel(double x, double y, int team, int id) {
        this.x = x;
        this.y = y;
        this.team = team;
        this.id = id;
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
