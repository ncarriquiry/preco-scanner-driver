package com.precodata.imageUtils;

import org.json.JSONArray;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import javax.xml.bind.DatatypeConverter;

import java.io.*;
import java.util.*;


public class Converter {

    public static String getFormats() {

        List<String> list = new ArrayList(Arrays.asList(ImageIO.getWriterFormatNames()));

        TreeSet<String> seen = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        seen.add("pcx");
        seen.add("wbmp");
        seen.add("raw");

        Iterator<String> itr = list.iterator();
        while (itr.hasNext()) {
            String item = itr.next();
            if (seen.contains(item) ) {
                itr.remove();
            }
        }

        return (new JSONArray(list)).toString();
    };
    public static String getCompressionMethods(String format) {
        JSONArray jsa = new JSONArray();

        ImageWriteParam iwp = ImageIO.getImageWritersByFormatName(format).next().getDefaultWriteParam();

        if (iwp.canWriteCompressed()) {

            List<String> list = new ArrayList(Arrays.asList(iwp.getCompressionTypes()));

            TreeSet<String> seen = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            Iterator<String> itr = list.iterator();
            while (itr.hasNext()) {
                String item = itr.next();
                if (seen.contains(item) ) {
                    itr.remove();
                }
            }

            jsa = new JSONArray(list);
        }
        return  jsa.toString();
    };
    public static String readFile(String fileName, String format) {
        try {
            BufferedImage image = ImageIO.read(new File(fileName));
            return DatatypeConverter.printBase64Binary(toByteArray(image, format));
        } catch (IOException e)
        {
            e.printStackTrace();
            return "error";
        }
    };

    public static String readPage(String fileName, int page) {

        ImageInputStream is = null;
        try {
            is = ImageIO.createImageInputStream(new File(fileName));
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(is);
            ImageReader reader = iterator.next();
            reader.setInput(is);
            if (page <= reader.getNumImages(true)) {
                BufferedImage image = reader.read(page);
                return DatatypeConverter.printBase64Binary(toByteArray(image, "tif"));
            } else
               return "error";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
    public static void saveFile(String base64Content, String filename) {
        try {
            OutputStream fos = new FileOutputStream(filename);
            fos.write(DatatypeConverter.parseBase64Binary(base64Content));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String toImage(String base64Image, String format, String compressionType, double quality) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream(37628);

            ImageOutputStream  ios =  ImageIO.createImageOutputStream(baos);
            ImageWriter writer = ImageIO.getImageWritersByFormatName(format).next();
            writer.setOutput(ios);
            IIOImage iioImage = new IIOImage(toBufferedImage(DatatypeConverter.parseBase64Binary(base64Image)), null, null);


            ImageWriteParam iwp = writer.getDefaultWriteParam();
            if (!compressionType.equals(""))
            {
                iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                iwp.setCompressionType(compressionType);
                if (quality >= 0)
                    iwp.setCompressionQuality((float)quality);
            }
            writer.write(null, iioImage, iwp);

            //ByteArrayInputStream bai = new ByteArrayInputStream(baos.toByteArray());
            //RenderedImage out = ImageIO.read(bai);
            byte[] byteArray = baos.toByteArray();
            if (byteArray.length == 0)
                return DatatypeConverter.printBase64Binary(getBytes(ios));
            else
                return DatatypeConverter.printBase64Binary(byteArray);

        } catch (IOException e) {
            return "error";
        }
    }
    public static String toMultiPageTiff(String base64ImageArray, String compressionType)  {
        try {

            ImageIO.setUseCache(false);
            ImageWriter writer = ImageIO.getImageWritersByFormatName("TIF").next();

            ImageOutputStream ios = ImageIO.createImageOutputStream(new ByteArrayOutputStream(255));
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionType(compressionType);

            writer.setOutput(ios);

            writer.prepareWriteSequence(null);
            JSONArray arr = new JSONArray(base64ImageArray);
            for(int i = 0; i < arr.length(); i++) {
                System.out.println("page " + i);
                IIOImage iioImage = new IIOImage(toBufferedImage(DatatypeConverter.parseBase64Binary( (String) arr.get(i))), null, null);
                writer.writeToSequence(iioImage, writeParam);
            }
            writer.dispose();

            return DatatypeConverter.printBase64Binary(getBytes(ios));
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
    // convert BufferedImage to byte[]
    private static byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        return baos.toByteArray();

    }

    // convert byte[] to BufferedImage
    private static BufferedImage toBufferedImage(byte[] bytes)
            throws IOException {

        InputStream is = new ByteArrayInputStream(bytes);
        BufferedImage bi = ImageIO.read(is);
        return bi;

    }

    private static byte[] getBytes(ImageOutputStream ios) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(255);

        try {
            ios.seek(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                bos.write(ios.readByte());
            } catch (EOFException e) {
                break;
            } catch (IOException e) {
                break;
            }
        }
        byte[] retValue = bos.toByteArray();
        return retValue;
    }
}
