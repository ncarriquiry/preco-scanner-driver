package com.precodata.imageUtils.test;

import SK.gnome.morena.Morena;
import SK.gnome.morena.MorenaImage;
import SK.gnome.morena.MorenaSource;
import com.precodata.imageUtils.Scanner.SynchronousHelper;
import eu.gnome.morena.Device;
import eu.gnome.morena.Manager;
import eu.gnome.morena.Scanner;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args)  throws Exception {
        return;
    }
    public static void other(String[] args)  throws Exception {

        Manager manager = Manager.getInstance();
        List devices = manager.listDevices();
        System.out.println(devices.toString());
        if(devices.isEmpty()) {

            MorenaSource source= Morena.selectSource(null);
            System.err.println("Selected source is "+source);
            if (source!=null)
            { MorenaImage image=new MorenaImage(source);
                System.err.println("Size of acquired image is "
                        +image.getWidth()+" x "
                        +image.getHeight()+" x "
                        +image.getPixelSize());
                Morena.close();
            }
        } else {
            Device device = (Device) devices.get(1);
            System.out.println(device.toString());

            if (device instanceof Scanner) {
                Scanner scanner = (Scanner) device;
                scanner.setMode(Scanner.RGB_8);
                scanner.setResolution(75);
                scanner.setFrame(100, 100, 500, 500);

                BufferedImage bimage = SynchronousHelper.scanImage(scanner);

                // Do the necessary processes with bimage
                ByteArrayOutputStream out = null;//Converter.toTiff(bimage);
                System.out.println("Salida : " + out.toByteArray().length);
                 //Converter.saveTiff("image.tiff", bimage);
                   /* ImageIO.write(bimage, "tiff", out);

               /* String s = Base64.getEncoder().encodeToString(out.toByteArray());
                FileOutputStream image = new FileOutputStream("image.base64");

                image.write(s.getBytes());
                image.flush();
                image.close();*/

                FileOutputStream tiff = new FileOutputStream("image.tiff");

                tiff.write(out.toByteArray());
                tiff.flush();
                tiff.close();

                manager.close();
            } else {
                System.out.println("Please Connect A Scanner");
            }
        }
    }

}


