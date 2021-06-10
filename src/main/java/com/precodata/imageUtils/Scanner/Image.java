package com.precodata.imageUtils.Scanner;

import SK.gnome.morena.*;
import com.google.gson.Gson;
import com.precodata.imageUtils.Tools.Tools;
import eu.gnome.morena.Device;
import spark.Request;
import spark.Response;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Image {

    static String getBase64FromRequest(Request request, Response response)
    {
        parseParameters(request);
        response.header("Content-Type", "text/plain; charset=\"utf-8\"");
        return getBase64();
    }

    private static String getBase64() {
        BufferedImage bi = get();
        if (bi != null)
            return Tools.ImageToBase64(bi, format);
        return "{error:\"Invalid Driver : " + driver + ", valid types : twain, wia.\"}";
    }

    static byte[] getFromRequestInline(Request request, Response response)
    {
        response.header("Content-Disposition", "inline; filename=\"scanned." + format + "\"");
        return getFromRequest(request, response);
    }

    static byte[] getFromRequestAttach(Request request, Response response)
    {
        response.header("Content-Disposition", "attach; filename=\"scanned." + format + "\"");
        return getFromRequest(request, response);
    }

    static byte[] getFromRequest(Request request, Response response)
    {
        parseParameters(request);
        BufferedImage bi = get();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, format, baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.header("Content-Type", "image/" + format );
        response.header("Content-Disposition", "inline; filename=\"scanned." + format + "\"");
        return baos.toByteArray();
    }

    private static BufferedImage get() {
        if (driver.toLowerCase().equals("twain"))
            return TwainGet();
        if (driver.toLowerCase().equals("wia"))
            return WiaGet();
        return null;
    }

    private static BufferedImage TwainGet()  {
        try {
            MorenaSource source = ScannerList.twainGet(name);
            if (source != null) {
                source.setVisible(false);
                if (mode == 1) source.setColorMode(); else source.setGrayScaleMode();
                source.setResolution(resolution);
                source.setFrame(left, top, right, bottom);
                MorenaImage morenaImage = new MorenaImage(source);
                java.awt.Image image = Toolkit.getDefaultToolkit().createImage(morenaImage);
                ByteArrayOutputStream tmp=new ByteArrayOutputStream();
                BufferedImage bufferedImage=new BufferedImage(image.getWidth(null), image.getHeight(null), mode);
                bufferedImage.createGraphics().drawImage(image, 0, 0, null);
                Morena.close();

                return bufferedImage;
            }

        } catch (MorenaException e) {}
        return null;
    }
    private static BufferedImage WiaGet() {
        try {
            Device device = ScannerList.wiaGet(name);
            if (device instanceof eu.gnome.morena.Scanner) {
                eu.gnome.morena.Scanner scanner = (eu.gnome.morena.Scanner) device;
                scanner.setMode(mode);
                scanner.setResolution(resolution);
                if (right > left && right > 0 && bottom > top && bottom > 0)
                    scanner.setFrame((int) left, (int) top, (int) right, (int) bottom);
                BufferedImage bufferedImage = SynchronousHelper.scanImage(scanner);
                Morena.close();
                return bufferedImage;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getModes(Request request) {
        List<Mode> modes = new ArrayList<Mode>();
        parseParameters(request);
        if (driver.toLowerCase().equals("twain"))
            {
                modes.add(new Mode(BufferedImage.TYPE_INT_RGB, "Color"));
                modes.add(new Mode(BufferedImage.TYPE_BYTE_GRAY, "Gris"));
                modes.add(new Mode(BufferedImage.TYPE_BYTE_BINARY, "Blanco y Negro"));

            };
        if (driver.toLowerCase().equals("wia"))
            {
                modes.add(new Mode(eu.gnome.morena.Scanner.BLACK_AND_WHITE, "Blanco y Negro"));
                modes.add(new Mode(eu.gnome.morena.Scanner.GRAY_8, "Gris"));
                modes.add(new Mode(eu.gnome.morena.Scanner.GRAY_16, "Gris 16 bits"));
                modes.add(new Mode(eu.gnome.morena.Scanner.RGB_8, "Color"));
                modes.add(new Mode(eu.gnome.morena.Scanner.RGB_16, "Color 16 bits"));
            };

        return new Gson().toJson(modes);

    }
    private static void parseParameters(Request request) {
        if (request.queryParams().contains("driver")) driver = request.queryParams("driver");
        if (request.queryParams().contains("name")) name = request.queryParams("name");
        if (request.queryParams().contains("format")) format = request.queryParams("format");
        if (request.queryParams().contains("resolution")) resolution = Integer.parseInt(request.queryParams("resolution"));
        if (request.queryParams().contains("top")) top = Double.parseDouble(request.queryParams("top"));
        if (request.queryParams().contains("left")) left = Double.parseDouble(request.queryParams("left"));
        if (request.queryParams().contains("top")) right = Double.parseDouble(request.queryParams("right"));
        if (request.queryParams().contains("top")) bottom = Double.parseDouble(request.queryParams("bottom"));
        if (request.queryParams().contains("mode")) mode = Integer.parseInt(request.queryParams("mode"));
    }
    private static Scanner defScanner = ScannerList.getFirst();
    private static String driver = defScanner.getDriver();
    private static String name = defScanner.getName();
    private static String format = "png";
    private static int resolution = 300;
    private static double left = 0;
    private static double top = 0;
    private static double right = 0;
    private static double bottom = 0;
    private static int mode = -8;

}
