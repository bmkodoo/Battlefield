package com.spb.kns;

import com.spb.kns.structures.Bullet;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldModel {

    private Map<Integer, SolderModel> solders;
    private Deque<Bullet> bullets;

    WorldModel() {
        solders = new ConcurrentHashMap<>();
        bullets = new ArrayDeque<>();
    }

    public Map<Integer, SolderModel> getSolders() {
        return solders;
    }

    public void bulletHere(Bullet bullet) {
        bullets.push(bullet);
    }

    public Deque<Bullet> getBullets() {
        return bullets;
    }
}

