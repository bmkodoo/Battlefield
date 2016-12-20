package com.spb.kns;

import com.spb.kns.structures.Bullet;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class WorldModel {

    private Map<Integer, SolderModel> solders;
    private ArrayBlockingQueue<Bullet> bullets;

    WorldModel() {
        solders = new ConcurrentHashMap<>();
        bullets = new ArrayBlockingQueue<>(100);
    }

    public Map<Integer, SolderModel> getSolders() {
        return solders;
    }

    public void bulletHere(Bullet bullet) {
        bullets.offer(bullet);
    }

    public ArrayBlockingQueue<Bullet> getBullets() {
        return bullets;
    }
}

