package com.precodata.imageUtils.Scanner;

public class Scanner {
    private String name;
    private String driver;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public static Scanner newScanner(String driver, String name) {
        Scanner scanner = new Scanner();
        scanner.setDriver(driver);
        scanner.setName(name);
        return scanner;
    }

}
