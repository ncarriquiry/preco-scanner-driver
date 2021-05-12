package com.precodata.imageUtils.Scanner;

import SK.gnome.morena.Morena;
import SK.gnome.morena.MorenaException;
import SK.gnome.morena.MorenaSource;
import SK.gnome.twain.TwainException;
import SK.gnome.twain.TwainManager;
import com.google.gson.Gson;
import eu.gnome.morena.Device;
import eu.gnome.morena.Manager;

import java.util.ArrayList;
import java.util.List;

public class ScannerList {

    private ArrayList<Scanner> scannerList = new  ArrayList<Scanner>();

    private ScannerList() {
        ArrayList<Scanner> list = getScannerList();

        Manager manager = Manager.getInstance();
        List devices = manager.listDevices();
        for (int i = 0; i < devices.size(); i++)
            list.add(Scanner.newScanner("wia", devices.get(i).toString()));

        try {
            MorenaSource[] sources = TwainManager.listSources();
            System.out.println("twain devices : " + sources.length);
            for (int i = 0; i < sources.length; i++)
                list.add(Scanner.newScanner("twain", sources[i].toString()));

        } catch (TwainException e)
        {
            System.out.println(e.getMessage());
        }
        setScannerList(list);
        try {
            Morena.close();
        } catch (MorenaException e) {
            e.printStackTrace();
        }
    }

    public static Device wiaGet(String name)
    {
        Manager manager = Manager.getInstance();
        List devices = manager.listDevices();
        for (int i = 0; i < devices.size(); i++)
            if (devices.get(i).toString().equals(name))
                return (Device) devices.get(i);
            return null;
    }

    public static MorenaSource twainGet(String name)
    {
        try {
            MorenaSource[] sources = TwainManager.listSources();
            for (int i = 0; i < sources.length; i++)
                if (sources.toString().equals(name))
                    return sources[i];
        } catch (TwainException e)
        {
        }
        return null;
    }


    private String toJSON() {return new Gson().toJson(getScannerList());}

    private void setScannerList(ArrayList<Scanner> scannerList) {
        this.scannerList = scannerList;
    }

    private ArrayList<Scanner> getScannerList() {
        return scannerList;
    }

    public static Scanner getFirst() {
        ScannerList scl = new ScannerList();
        if (scl.getScannerList().size() > 0)
            return scl.getScannerList().get(0);
        else
            return Scanner.newScanner("","");
    }

    public static String get() {
        ScannerList scl = new ScannerList();
        return scl.toJSON();
    }
}
