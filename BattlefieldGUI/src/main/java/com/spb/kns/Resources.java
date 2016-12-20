package com.spb.kns;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Resources {
    public final static BufferedImage panzer;
    public final static BufferedImage panzer_2;
    public final static BufferedImage rip;


    static {
        try {
            panzer = ImageIO.read(SolderModel.class.getResourceAsStream("panzeriv_h.gif"));
            panzer_2 = ImageIO.read(SolderModel.class.getResourceAsStream("tango-tank.png"));
            rip = ImageIO.read(SolderModel.class.getResourceAsStream("rip.gif"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
