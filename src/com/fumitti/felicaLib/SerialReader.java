package com.fumitti.felicaLib;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by fumitti on 2015/11/06.
 */
public class SerialReader implements SerialPortEventListener {
    private InputStream in;
    private ArrayList<Byte> buffer = new ArrayList<Byte>();
    private String temp = "";

    public SerialReader(InputStream in) {
        this.in = in;
    }

    public void serialEvent(SerialPortEvent arg0) {
        int data;

        try {
            buffer.clear();
            int len = 0;
            while ((data = in.read()) > -1) {
                if (data == '\n') {
                    break;
                }
                buffer.add((byte) data);
            }
            for (byte b : buffer) {
                temp+=String.format("%02x ",b);
                if(temp.equals("00 00 ff 00 ff 00 ")){
                    System.out.println("---ACK Recv---");
                    temp = "";
                }
            }
            if(!temp.equals("")) {
                System.out.println(temp);
                temp = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
