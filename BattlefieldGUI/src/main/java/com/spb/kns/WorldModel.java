package com.spb.kns;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldModel {

    private Map<Integer, SolderModel> solders;

    WorldModel() {
        solders = new ConcurrentHashMap<>();
    }

    public Map<Integer, SolderModel> getSolders() {
        return solders;
    }
}

