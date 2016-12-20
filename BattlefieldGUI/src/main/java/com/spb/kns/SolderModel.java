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

    private int hp;

    public SolderModel(double x, double y, int team, int id, int hp) {
        this.x = x;
        this.y = y;
        this.team = team;
        this.id = id;
        this.hp = hp;
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

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}
