package com.precodata.imageUtils.test;
import com.precodata.imageUtils.Converter;
import org.json.JSONArray;

import java.io.File;
import java.util.Date;

public class ConverterTest {
    public static void main(String[] args)  {

        System.out.print("reading file : " + "image.jpg");
        long beforeRead = (new Date()).getTime();
        String jpg = Converter.readFile("image.jpg", "jpg");
        long afterRead = (new Date()).getTime();
        System.out.println(". readTime " + (afterRead - beforeRead) + "ms." );

        JSONArray jsa = new JSONArray(Converter.getFormats());
        System.out.println("formats : " + jsa.toString());

        for (int i =0 ; i < jsa.length(); i++ ) {
            String format = (String) jsa.get(i);
            System.out.println("format " + format);
            JSONArray jscm = new JSONArray(Converter.getCompressionMethods(format));
            if (jscm.length() > 0)
                System.out.println("    compression for " + format + " : " + jscm.toString());
            else
                System.out.println("    no compression types for " + format);

            if (jscm.length() > 0) {
                for (int j = 0; j < jscm.length(); j++) {
                    String compressionType = (String) jscm.get(j);
                    System.out.print("        format " + format + ", compression " + compressionType + ". ");
                    long beforeConvert = (new Date()).getTime();
                    String image = Converter.toImage(jpg, format, compressionType, 0.5);
                    long afterConvert = (new Date()).getTime();
                    if (image != "error")
                        Converter.saveFile(image, "image_" + i + "_" + compressionType + "." + format);
                    else
                        System.out.print("omitiendo " + format + "/" + compressionType + ". ");
                    long afterSave = (new Date()).getTime();
                    File f = new File("imag e_" + i + "_" + compressionType + "." + format);
                    System.out.println("conversion " + (afterConvert - beforeConvert) + "ms, saveFile " + (afterSave - afterConvert) + "ms, fileSize " + f.length() + "bytes" );
                }
            } else {
                long beforeConvert = (new Date()).getTime();
                String image = Converter.toImage(jpg, format, "", 0.5);
                long afterConvert = (new Date()).getTime();
                if (image != "error")
                    Converter.saveFile(image, "image_" + i + "." + format);
                else
                    System.out.print("omitiendo " + format + ". ");
                long afterSave = (new Date()).getTime();
                File f = new File("image_" + i + "." + format);
                System.out.println("conversion " + (afterConvert - beforeConvert) + "ms, saveFile " + (afterSave - afterConvert) + "ms, fileSize " + f.length() + "bytes" );
            }
        }

        String jpg2 = Converter.readFile("image2.jpg", "jpg");
        System.out.print("saving multipage Tiff. " );
        long beforeWrite = (new Date()).getTime();
        String tiff = Converter.toMultiPageTiff("[\""+jpg+"\",\""+jpg2+"\"]", "JPEG");
        Converter.saveFile(tiff, "multiPage.tiff");
        long afterWrite = (new Date()).getTime();
        System.out.println("Merge and savetime " + (afterWrite - beforeWrite) + "ms." );

        System.out.print("saving splitted Tiff. " );
        beforeWrite = (new Date()).getTime();
        String tiff_1 = Converter.readPage( "multiPage.tiff", 0);
        Converter.saveFile(Converter.toImage(tiff_1, "jpg", "JPEG", 1), "page_1.jpeg");
        String tiff_2 = Converter.readPage( "multiPage.tiff", 1);
        Converter.saveFile(Converter.toImage(tiff_2, "jpg", "JPEG", 1), "page_2.jpeg");
        afterWrite = (new Date()).getTime();
        System.out.println("Split and savetime " + (afterWrite - beforeWrite) + "ms." );

    }
}

