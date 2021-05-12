package com.precodata.imageUtils.Scanner;

import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.*;

public class Start {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Start.class);
        port(1235);
        staticFiles.location("/static");
        staticFiles.externalLocation("/Models/preco-scanner-driver");
        get("/scannerList", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return ScannerList.get();
            }
        });
        get("/getImage", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return Image.getFromRequest(request);
            }
        });
        get("/test", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                response.redirect("/static/index.html");
                return null;
            }
        });
    }
}