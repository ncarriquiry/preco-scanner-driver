package com.precodata.imageUtils.Scanner;

public class Test {
    public static String get() {
        return "<html>\n" +
                "<head>\n" +
                "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<script>\n" +
                "  $.get(\"http://localhost:1235/getImage\", \n" +
                "\tfunction(data, status){\n" +
                "    $(\"body\").append($('<img></img>').attr('src', 'data:image/png;base64,' + data));\n" +
                "  });\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
//                 "  $.get(\"http://localhost:1235/getImage?driver=wia&name=Impresora&format=png&resolution=300&top=0&left=0&right=2000&bottom=2000&mode=16\", \n" +
    }
}
