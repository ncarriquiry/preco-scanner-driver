package com.precodata.imageUtils.Scanner;

public class Mode {
    private int mode;
    private String description;

    public Mode(int mode, String description) {
        this.mode = mode;
        this.description = description;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
