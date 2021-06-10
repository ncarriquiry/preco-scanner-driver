package com.precodata.imageUtils.Scanner;

import com.precodata.imageUtils.Converter;
import spark.Service;

import static spark.Service.ignite;
import static spark.Spark.*;

public class Start {

    public static void main(String[] args) {
        //Logger logger = Logger.getLogger(Start.class);
        ignite();
        ipAddress("127.0.0.1");
        port(1235);
        staticFiles.location("/static");
        staticFiles.externalLocation("/Models/preco-scanner-driver");
        enableCORS("*", "*", "*");

        get("/getScannerList", (request, response)  -> {
            response.header("Access-Control-Request-Method", "*");
            response.header("Content-Type", "text/json"); return ScannerList.get();});
        get("/getFormat", (request, response)  -> {response.header("Content-Type", "text/json"); return Converter.getFormats();});
        get("/getModes", (request, response)  -> {response.header("Content-Type", "text/json"); return Image.getModes(request);});
        get("/getBase64", (request, response)  -> {return Image.getBase64FromRequest(request, response); });
        get("/getImage", (request, response)  -> { return Image.getFromRequestInline(request, response);});
        get("/getDownloadImage", (request, response)  -> {return Image.getFromRequestAttach(request, response);});
        get("/test", (request, response)  -> {{response.redirect("/static/index.html"); return null;
            }
        });
    }

    private static void enableCORS(final String origin, final String methods, final String headers) {
/*
        options("/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });*/

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }
}