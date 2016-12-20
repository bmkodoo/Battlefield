package com.spb.kns;

import com.spb.kns.structures.Bullet;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;


class WorldView extends JPanel {
    public static final double SCALE = 64;

    private WorldModel world;


    public WorldView(WorldModel world) {
        this.world = world;
        Timer timer = new Timer(40, e -> repaint());
        timer.start();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800 , 600);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        drawSoldersAndAims(g, g2d);
        while (!world.getBullets().isEmpty()) {
            Bullet bullet = world.getBullets().pop();
            g.setColor(Color.red);
            g.drawLine(
                    (int) (bullet.x * SCALE),
                    (int) (bullet.y * SCALE),
                    (int) (1000 * Math.cos(bullet.angle)),
                    (int) (1000 * Math.sin(bullet.angle))
            );
        }
        g2d.dispose();
    }

    private void drawSoldersAndAims(Graphics g, Graphics2D g2d) {
        world.getSolders().values().forEach((solder) -> {
            double locationX = Resources.panzer.getWidth() / 2;
            double locationY = Resources.panzer.getHeight() / 2;
            AffineTransform tx = AffineTransform.getRotateInstance(solder.getAngle(), locationX, locationY);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

            g.setColor(Color.green);
            if (solder.getId() == -1) {
                g.drawString("x (" + solder.getTeam() + ")", (int) (solder.getX() * SCALE), (int) (solder.getY() * SCALE));
                return;
            }

            switch (solder.getTeam()) {
                case 0:
                    g2d.drawImage(op.filter(Resources.panzer, null), (int) (solder.getX() * SCALE), (int) (solder.getY() * SCALE), this);
                    break;
                case 1:
                    g2d.drawImage(op.filter(Resources.panzer_2, null), (int) (solder.getX() * SCALE), (int) (solder.getY() * SCALE), this);
            }

            g.setColor(Color.black);
            g.drawString(String.valueOf(solder.getId()), (int) (solder.getX() * SCALE), (int) (solder.getY() * SCALE + 60));
        });
    }
}
