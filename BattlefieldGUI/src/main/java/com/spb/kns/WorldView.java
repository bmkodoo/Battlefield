package com.spb.kns;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;


class WorldView extends JPanel {
    public static final double SCALE = 32;

    private int yOffset = 0;
    private int yDelta = 4;
    private WorldModel world;


    public WorldView(WorldModel world) {
        this.world = world;
        Timer timer = new Timer(40, e -> repaint());
        timer.start();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(640 , 480);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        world.getSolders().values().forEach((solder) -> {
            double locationX = Resources.panzer.getWidth() / 2;
            double locationY = Resources.panzer.getHeight() / 2;
            AffineTransform tx = AffineTransform.getRotateInstance(solder.getAngle(), locationX, locationY);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
            g2d.drawImage(op.filter(Resources.panzer, null), (int) (solder.getX() * SCALE), (int) (solder.getY() * SCALE), this);
        });
        g2d.dispose();
    }
}
