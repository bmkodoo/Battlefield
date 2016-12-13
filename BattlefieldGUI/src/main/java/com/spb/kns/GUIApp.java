package com.spb.kns;

import javax.swing.*;
import java.awt.*;

public class GUIApp {
    public static void main(String[] args) {
        WorldModel world = new WorldModel();

        new WorldFromROSUpdater(world);
        invokeWorldView(world);
    }

    static void invokeWorldView(WorldModel world) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new WorldView(world));
            frame.pack();
            frame.setVisible(true);
        });
    }
}
