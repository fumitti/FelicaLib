package com.fumitti.felicaLib;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            felicaLib lib = new felicaLib("COM10");
            lib.reset();
            Thread.sleep(100);
            lib.setParameters(false,false,false,true,true,true);
            Thread.sleep(100);
            int[] mifareparams = {0x00,0x04,0x00,0x00,0x00,0x40};
            int[] felicaparams = {0x00,0x01,0x14,0x51,0x41,0x91,0x98,0x10,0x00,0x01,0x14,0x51,0x41,0x91,0x98,0x10,0xff,0xff};
            int[] nfcid3t = {0,0,0,0,0,0,0,0,0,0};
            int[] gt = {0,0};
            while (true) {
                lib.tgInitAsTarget(false, false, mifareparams, felicaparams, nfcid3t, gt);
                Thread.sleep(5000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
