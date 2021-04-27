package com.precodata.imageUtils.test;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import eu.gnome.morena.Device;
import eu.gnome.morena.DeviceBase;
import eu.gnome.morena.TransferDoneListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SynchronousHelper {
    public static final int WIA_ERROR_PAPER_EMPTY = 417;

    public SynchronousHelper() {
    }

    public static BufferedImage scanImage(Device var0) throws Exception {
        return scanImage(var0, 0);
    }

    public static BufferedImage scanImage(Device var0, int var1) throws Exception {
        SynchronousHelper.ImageTransferHandler var2 = new SynchronousHelper.ImageTransferHandler();
        synchronized(var2) {
            ((DeviceBase)var0).startTransfer(var2, var1);

            while(true) {
                if (var2.transferDone) {
                    break;
                }

                var2.wait();
            }
        }

        if (var2.image != null) {
            return var2.image;
        } else {
            throw new Exception(var2.error);
        }
    }

    public static File scanFile(Device var0) throws Exception {
        return scanFile(var0, 0);
    }

    public static File scanFile(Device var0, int var1) throws Exception {
        SynchronousHelper.FileTransferHandler var2 = new SynchronousHelper.FileTransferHandler();
        synchronized(var2) {
            ((DeviceBase)var0).startTransfer(var2, var1);

            while(true) {
                if (var2.transferDone) {
                    break;
                }

                var2.wait();
            }
        }

        if (var2.imageFile != null) {
            return var2.imageFile;
        } else {
            throw new Exception(var2.error);
        }
    }

    static class FileTransferHandler implements TransferDoneListener {
        File imageFile;
        int code;
        String error;
        boolean transferDone = false;

        FileTransferHandler() {
        }

        public void transferDone(File var1) {
            this.imageFile = var1;
            this.notifyRequestor();
        }

        public void transferFailed(int var1, String var2) {
            this.code = var1;
            this.error = var2;
            this.notifyRequestor();
        }

        private synchronized void notifyRequestor() {
            this.transferDone = true;
            this.notify();
        }
    }

    static class ImageTransferHandler implements TransferDoneListener {
        BufferedImage image;
        int code;
        String error;
        boolean transferDone = false;

        ImageTransferHandler() {
        }

        public void transferDone(File var1) {
            if (var1 != null) {
                try {
                    this.image = ImageIO.read(var1);
                } catch (IOException var3) {
                    this.error = var3.getLocalizedMessage();
                }
            }

            this.notifyRequestor();
        }

        public void transferFailed(int var1, String var2) {
            this.code = var1;
            this.error = var2;
            this.notifyRequestor();
        }

        private synchronized void notifyRequestor() {
            this.transferDone = true;
            this.notify();
        }
    }
}
