package com.precodata.imageUtils.Tools;

import SK.gnome.morena.BASE64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Tools {
    public static String ImageToBase64(BufferedImage image, String format)
    {
        try {
            ByteArrayOutputStream tmp = new ByteArrayOutputStream();
            ImageIO.write(image, format, tmp);
            tmp.close();
            System.out.println(tmp.size());
            return BASE64.encode(tmp.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
