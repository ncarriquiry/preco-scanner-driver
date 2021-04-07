import SK.gnome.morena.Morena;
import SK.gnome.morena.MorenaImage;
import SK.gnome.morena.MorenaSource;
import eu.gnome.morena.Device;
import eu.gnome.morena.Manager;
import eu.gnome.morena.Scanner;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Main {
    public static void main(String[] args)  throws Exception {
        Manager manager = Manager.getInstance();
        List devices = manager.listDevices();
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
        }
        Device device = (Device) devices.get(0);

        if (device instanceof Scanner)  {
            Scanner scanner = (Scanner) device;
            scanner.setMode(Scanner.RGB_8);
            scanner.setResolution(75);
            scanner.setFrame(100, 100, 500, 500);

            BufferedImage bimage = SynchronousHelper.scanImage(scanner);

            // Do the necessary processes with bimage

            manager.close();
        }
        else {
            System.out.println("Please Connect A Scanner");
        }
    }
}

