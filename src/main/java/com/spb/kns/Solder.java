package com.spb.kns;

import java.util.concurrent.atomic.AtomicInteger;

public class Solder extends Unit {

    private static AtomicInteger solders_count = new AtomicInteger();
    private static double SIZE = 1;

    private int id;

    private final int team;
    private boolean wasLaunched = false;

    private Unit back;

    public Solder(double x, double y, int team, int id) {
        super(x, y);
        this.team = team;
        this.back = new Unit(x, y);
        this.id = id;
    }

    public SolderAlive launch() {
        if (wasLaunched) {
            throw new RuntimeException("Was already launched!");
        }
        wasLaunched = true;
        return new SolderAlive(this);
    }

    public int getId() {
        return id;
    }

    public int getTeam() {
        return team;
    }


    public Unit getBackPosition() {
        return back;
    }

    public void setId(int id) {
        this.id = id;
    }


}
