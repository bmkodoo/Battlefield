package com.spb.kns;

import ros.RosBridge;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class HelloGUI {


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new BackgroundPane());
            frame.pack();
            frame.setVisible(true);
        });
    }
}

class BackgroundPane extends JPanel {
    private static final double SCALE = 32;
    private BufferedImage bg;
    private int yOffset = 0;
    private int yDelta = 4;

    Executor rosListener = Executors.newSingleThreadExecutor();
    private static final String bridge_addr = System.getProperty("ros.battlefield.bridge_addr", "ws://localhost:9090");;

    private Map<Integer, Solder> solders;

    public BackgroundPane() {
        solders = new ConcurrentHashMap<>();

        rosListener.execute(() -> {
            System.err.println("OLolol");
            RosBridge bridge = RosBridge.createConnection(bridge_addr);
            bridge.waitForConnection();
            bridge.subscribe("/solders_positions", "std_msgs/String",
                    (data, stringRep) -> {
                        String d = data.findValue("data").toString();
                        d = d.substring(1, d.length() - 1);
                        System.err.println("Recieved " + d);
                        try (Scanner scan = new Scanner(d)) {
                            int id = scan.nextInt();
                            int team = scan.nextInt();

                            double x = scan.nextDouble();
                            double y = scan.nextDouble();
                            double alpha = scan.nextDouble();

                            Solder solder;
                            if ((solder = solders.get(id)) != null) {
                                solder.setX(x);
                                solder.setY(y);
                                solder.setAngle(alpha);
                            } else {
                                solders.put(id, new Solder(x, y, team));
                            }
                        }
                    }
            );
        });

        try {
            bg = ImageIO.read(HelloGUI.class.getResourceAsStream("panzeriv_h.gif"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Timer timer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yOffset += yDelta;
                if (yOffset > getHeight()) {
                    yOffset = 0;
                }
                repaint();
            }
        });
        timer.start();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(bg.getWidth(),bg.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        solders.values().forEach((solder) -> {
            double locationX = bg.getWidth() / 2;
            double locationY = bg.getHeight() / 2;
            AffineTransform tx = AffineTransform.getRotateInstance(solder.getAngle(), locationX, locationY);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
            g2d.drawImage(op.filter(bg, null), (int) (solder.getX() * SCALE), (int) (solder.getY() * SCALE), this);
        });

        g2d.dispose();
    }
}
