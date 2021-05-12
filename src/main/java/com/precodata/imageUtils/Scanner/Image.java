package com.precodata.imageUtils.Scanner;

import SK.gnome.morena.*;
import eu.gnome.morena.Device;
import spark.Request;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Image {
    static String getFromRequest(Request request)
    {   Scanner defScanner = ScannerList.getFirst();
        String driver = defScanner.getDriver();
        String name = defScanner.getName();
        String format = "png";
        int resolution = 300;
        double left = 0;
        double top = 0;
        double right = 0;
        double bottom = 0;
        int mode = -8;

        if (request.params().containsKey("driver")) driver = request.queryParams("driver");
        if (request.params().containsKey("format")) format = request.queryParams("format");
        if (request.params().containsKey("resolution")) resolution = Integer.parseInt(request.queryParams("resolution"));
        if (request.params().containsKey("top")) top = Double.parseDouble(request.queryParams("top"));
        if (request.params().containsKey("left")) left = Double.parseDouble(request.queryParams("left"));
        if (request.params().containsKey("top")) right = Double.parseDouble(request.queryParams("right"));
        if (request.params().containsKey("top")) bottom = Double.parseDouble(request.queryParams("bottom"));
        if (request.params().containsKey("mode")) mode = Integer.parseInt(request.queryParams("mode"));
        return get(driver, name, format, resolution, left, top, right, bottom, mode);
    }
    static String get(String driver, String name, String format, int resolution, double left, double top, double right, double bottom, int mode) {
        if (driver.toLowerCase().equals("twain"))
        {
            return TwainGet(name, format, resolution, left, top, right, bottom, mode);
        }
        if (driver.toLowerCase().equals("wia"))
        {
            return WiaGet(name, format, resolution, left, top, right, bottom, mode);
        }

        return "{error:\"Invalid Driver : " + driver + ", valid types : twain, wia.\"}";
    }

    private static String TwainGet(String name, String format, int resolution, double top, double left, double right, double bottom, int mode)  {
        try {
            MorenaSource source = ScannerList.twainGet(name);
            if (source != null) {
                source.setVisible(false);
                source.setColorMode();
                source.setResolution(resolution);
                source.setFrame(left, top, right, bottom);
                MorenaImage morenaImage = new MorenaImage(source);
                java.awt.Image image = Toolkit.getDefaultToolkit().createImage(morenaImage);
                ByteArrayOutputStream tmp=new ByteArrayOutputStream();
                BufferedImage bufferedImage=new BufferedImage(image.getWidth(null), image.getHeight(null), mode);
                bufferedImage.createGraphics().drawImage(image, 0, 0, null);
                Morena.close();

                return ImageToBase64(bufferedImage, format);
            }

        } catch (MorenaException e) {}
        return "";
    }
    private static String WiaGet(String name, String format, int resolution, double top, double left, double right, double bottom, int mode) {
        try {
            Device device = ScannerList.wiaGet(name);
            if (device instanceof eu.gnome.morena.Scanner) {
                eu.gnome.morena.Scanner scanner = (eu.gnome.morena.Scanner) device;
                scanner.setMode(mode);
                scanner.setResolution(resolution);
                if (right > left && right > 0 && bottom > top && bottom > 0)
                    scanner.setFrame((int) left, (int) top, (int) right, (int) bottom);
                String out = ImageToBase64(SynchronousHelper.scanImage(scanner), format);
                Morena.close();
                return out;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private static String ImageToBase64(BufferedImage image, String format)
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
