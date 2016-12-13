package com.spb.kns;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Resources {
    public final static BufferedImage panzer;

    static {
        try {
            panzer = ImageIO.read(SolderModel.class.getResourceAsStream("panzeriv_h.gif"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
