package com.fumitti.felicaLib;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class felicaLib {
    private SerialPort port;
    private InputStream reader;
    private final OutputStream write;
    private int buf = 0;
    private int nowx = 0;

    public felicaLib(String portname) throws IOException {
        // Serial port initialize
        CommPortIdentifier portId = null;
        try {
            portId = CommPortIdentifier.getPortIdentifier(portname);
        } catch (NoSuchPortException e) {
            e.printStackTrace();
        }
        try {
            port = (SerialPort) portId.open("serial", 2000);
        } catch (PortInUseException e) {
            e.printStackTrace();
        }

        try {
            port.setSerialPortParams(
                    115200,                   // 通信速度[bps]
                    SerialPort.DATABITS_8,   // データビット数
                    SerialPort.STOPBITS_1,   // ストップビット
                    SerialPort.PARITY_NONE   // パリティ
            );
            port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        }

        reader = port.getInputStream();
        write = port.getOutputStream();


        try {
            port.addEventListener(new SerialReader(reader));
            port.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void ack() throws IOException {
        write(0x00);
    }

    //Miscellaneous Commands

    @Deprecated
    public void diagnose(int test, int... inParam) throws IOException {
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x00);
        l.add(test);
        for (int i : inParam) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    public void getFirmwareVersion() throws IOException {
        write(0xd4, 0x02);
    }

    public void getGeneralStatus() throws IOException {
        write(0xd4, 0x04);
    }

    @Deprecated
    public void readRegister(int... address) throws IOException {
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x06);
        for (int i : address) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    @Deprecated
    public void writeRegister(int... data) throws IOException {
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x08);
        for (int i : data) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    @Deprecated
    public void readGPIO() throws IOException {
        write(0xd4, 0x0c);
    }

    @Deprecated
    public void writeGPIO(int p3, int p7) throws IOException {
        write(0xd4, 0x0e, p3, p7);
    }

    public void setParameters(boolean fAutomaticATR_RES) throws IOException {
        setParameters(false, false, fAutomaticATR_RES, true, true, false);
    }

    @Deprecated
    public void setParameters(boolean fNADUsed, boolean fDIDUsed, boolean fAutomaticATR_RES, boolean fTDApowered, boolean fAutomaticRATS, boolean fSecure) throws IOException {
        int flag = 0;
        if (fNADUsed)
            flag += 1;
        if (fDIDUsed)
            flag += 2;
        if (fAutomaticATR_RES)
            flag += 4;
        if (fTDApowered)
            flag += 8;
        if (fAutomaticRATS)
            flag += 16;
        if (fSecure)
            flag += 32;
        write(0xd4, 0x12, flag);
    }

    public void powerDown(boolean UART, boolean RFLevel) throws IOException {
        int p = 0;
        if (UART) {
            if (RFLevel) {
                p = 0x18;
            } else {
                p = 0x10;
            }
        } else {
            if (RFLevel) {
                p = 0x08;
            } else {
            }
        }
        write(0xd4, 0x16, p);
    }


    public void reset() throws IOException {
        write(0xd4, 0x18, 0x01);
    }

    @Deprecated
    public void alparCommandForTDA(int... command) throws IOException {
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x18);
        for (int i : command) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    //RF Communication Commands

    public void rfConfiguration_RFfield(boolean RFOn, boolean AutoRFCA) throws IOException {
        int flag = 0;
        if (RFOn)
            flag += 1;
        if (AutoRFCA)
            flag += 2;
        write(0xd4, 0x32, 0x01, flag);
    }

    public void rfConfiguration_VariousTimings(int RFU, int fATR_RES_Timeout, int fRetryTimeout) throws IOException {
        write(0xd4, 0x32, 0x02, RFU, fATR_RES_Timeout, fRetryTimeout);
    }

    @Deprecated
    public void rfConfiguration_MaxRtyCOM(int retry) throws IOException {
        write(0xd4, 0x32, 0x04, retry);
    }

    public void rfConfiguration_MaxRetries(int MxRtyATR, int MxRtyPSL, int MxRtyPassiveActivation) throws IOException {
        write(0xd4, 0x32, 0x05, MxRtyATR, MxRtyPSL, MxRtyPassiveActivation);
    }

    @Deprecated
    public void rfConfiguration_AnalogSettingsFor106kbpsTypeA(int CIU_RFCfg, int CIU_GsNOn, int CIU_CWGsP, int CIU_ModGsP, int CIU_DemodRfOn_WhenRFOn, int CIU_RxThreshold, int CIU_DemondRfOff_WhenRFOff, int CIU_GsNOff, int CIU_ModWidth, int CIU_MifNFC, int CIU_TxBitPhare) throws IOException {
        write(0xd4, 0x32, 0x0A, CIU_RFCfg, CIU_GsNOn, CIU_CWGsP, CIU_ModGsP, CIU_DemodRfOn_WhenRFOn, CIU_RxThreshold, CIU_DemondRfOff_WhenRFOff, CIU_GsNOff, CIU_ModWidth, CIU_MifNFC, CIU_TxBitPhare);
    }

    @Deprecated
    public void rfConfiguration_AnalogSettingsFor212and424kbps(int CIU_RFCfg, int CIU_GsNOn, int CIU_CWGsP, int CIU_ModGsP, int CIU_DemodRfOn_WhenRFOn, int CIU_RxThreshold, int CIU_DemondRfOff_WhenRFOff, int CIU_GsNOff) throws IOException {
        write(0xd4, 0x32, 0x0A, CIU_RFCfg, CIU_GsNOn, CIU_CWGsP, CIU_ModGsP, CIU_DemodRfOn_WhenRFOn, CIU_RxThreshold, CIU_DemondRfOff_WhenRFOff, CIU_GsNOff);
    }

    @Deprecated
    public void rfConfiguration_AnalogSettingsForTypeB(int CIU_GsNOn, int CIU_ModGsP, int CIU_RxThreshold) throws IOException {
        write(0xd4, 0x32, 0x0A, CIU_GsNOn, CIU_ModGsP, CIU_RxThreshold);
    }

    @Deprecated
    public void rfConfiguration_AnalogSettingsFor212and424and847kbpsWithISO_IEC14443_4(int CIU_RxThreshold_212, int CIU_ModWidth_212, int CIU_MifNFC_212, int CIU_RxThreshold_424, int CIU_ModWidth_424, int CIU_MifNFC_424, int CIU_RxThreshold_847, int CIU_ModWidth_847, int CIU_MifNFC_847) throws IOException {
        write(0xd4, 0x32, 0x0D, CIU_RxThreshold_212, CIU_ModWidth_212, CIU_MifNFC_212, CIU_RxThreshold_424, CIU_ModWidth_424, CIU_MifNFC_424, CIU_RxThreshold_847, CIU_ModWidth_847, CIU_MifNFC_847);
    }

    public void rfConfiguration_AddWait(int Time) throws IOException {
        write(0xd4, 0x32, 0x81, Time);
    }

    public void rfConfiguration_DEPTimeOut(int gbyAtrResTo, int gbyRtox, int gbyTargetStoTo) throws IOException {
        write(0xd4, 0x32, 0x82, gbyAtrResTo, gbyRtox, gbyTargetStoTo);
    }

    @Deprecated
    public void rfRegulationTest(boolean isFelica, int TxSpeed) throws IOException {
        int flag = 0;
        if (isFelica)
            flag += 2;
        switch (TxSpeed) {
            case 0:
            case 106:
                break;
            case 1:
            case 212:
                flag += 16;
                break;
            case 10:
            case 424:
                flag += 32;
                break;
            case 11:
            case 847:
                flag += 48;
                break;
            default:
                return;
        }
        write(0xd4, 0x58, flag);
    }

    //Initiator

    @Deprecated
    public void inJumpForDEP(boolean isActive_Actpass, int BaudRare, int NextType,int[] PassiveInitiatorData,int[] NFCID3i, int... Gi) throws IOException {
        int Actpass = 0;
        if (isActive_Actpass) {
            Actpass += 1;
        }
        int BR = 0;
        switch (BaudRare) {
            case 0:
            case 106:
                break;
            case 1:
            case 212:
                BR = 1;
                break;
            case 2:
            case 424:
                BR = 2;
                break;
            default:
                return;
        }
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x56);
        l.add(Actpass);
        l.add(BR);
        l.add(NextType);
        for (int i : PassiveInitiatorData) {
            l.add(i);
        }
        for (int i : NFCID3i) {
            l.add(i);
        }
        for (int i : Gi) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    @Deprecated
    public void inJumpForPSL(boolean isActive_Actpass, int BaudRare, int NextType,int[] PassiveInitiatorData,int[] NFCID3i, int... Gi) throws IOException {
        int Actpass = 0;
        if (isActive_Actpass) {
            Actpass += 1;
        }
        int BR = 0;
        switch (BaudRare) {
            case 0:
            case 106:
                break;
            case 1:
            case 212:
                BR = 1;
                break;
            case 2:
            case 424:
                BR = 2;
                break;
            default:
                return;
        }
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x46);
        l.add(Actpass);
        l.add(BR);
        l.add(NextType);
        for (int i : PassiveInitiatorData) {
            l.add(i);
        }
        for (int i : NFCID3i) {
            l.add(i);
        }
        for (int i : Gi) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    public void inListPassiveTarget(int BrTy, int... InitiatorData) throws IOException {
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x4a);
        l.add(0x01);
        l.add(BrTy);
        for (int i : InitiatorData) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    @Deprecated
    public void inListPassiveTarget2(int MaxTg, int BrTy, int... InitiatorData) throws IOException {
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x4a);
        l.add(MaxTg);
        l.add(BrTy);
        for (int i : InitiatorData) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    @Deprecated
    public void inATR(int Tg, int Next,int[] NFCID3i, int... Gi) throws IOException {
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x50);
        l.add(Tg);
        l.add(Next);
        for (int i : NFCID3i) {
            l.add(i);
        }
        for (int i : Gi) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    @Deprecated
    public void inPSL(int Tg, int BRit, int BRti) throws IOException {
        write(0xd4, 0x4e, Tg, BRit, BRti);
    }

    @Deprecated
    public void inDataExchange(int Tg, int... DataOut) throws IOException {
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x40);
        l.add(Tg);
        for (int i : DataOut) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    @Deprecated
    public void inCommunicateThru(int... DataOut) throws IOException {
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x42);
        for (int i : DataOut) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    @Deprecated
    public void inQuartetByteExchange(int Tg, int Type, int... DataOut) throws IOException {
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x38);
        l.add(Tg);
        l.add(Type);
        for (int i : DataOut) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    @Deprecated
    public void inDeselect(int Tg) throws IOException {
        write(0xd4, 0x44, Tg);
    }

    @Deprecated
    public void inRelease(int Tg) throws IOException {
        write(0xd4, 0x52, Tg);
    }

    @Deprecated
    public void inSelect(int Tg) throws IOException {
        write(0xd4, 0x44, Tg);
    }

    @Deprecated
    public void inActivateDeactivatePaypass(int PaypassItem) throws IOException {
        write(0xd4, 0x48, PaypassItem);
    }

    //Target Commands

    public void tgInitAsTarget(int[] MIFAREParams,int[] FeliCaParams,int[] NFCID3t,int... Gt)throws IOException{
        tgInitAsTarget(false,true,MIFAREParams,FeliCaParams,NFCID3t,Gt);
    }

    @Deprecated
    public void tgInitAsTarget(boolean isPassiveOnly,boolean isDEPOnly,int[] MIFAREParams,int[] FeliCaParams,int[] NFCID3t,int... Gt) throws IOException {
        if(MIFAREParams.length !=6 || FeliCaParams.length != 18 || NFCID3t.length != 10){
            return;
        }
        int flag = 0;
        if (isPassiveOnly)
            flag+=1;
        if(isDEPOnly)
            flag+=2;
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x8c);
        l.add(flag);
        for (int i : MIFAREParams) {
            l.add(i);
        }
        for (int i : FeliCaParams) {
            l.add(i);
        }
        for (int i : NFCID3t) {
            l.add(i);
        }
        for (int i : Gt) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }
    //tgInitTarget 8c

    public void tgSetGeneralBytes(int... Gt) throws IOException{
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x92);
        for (int i : Gt) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }
    //tgSetGeneralBytes 92

    public void tgGetData() throws IOException{
        write(0xd4,0x86);
    }
    //tgGetDEPData 86

    public void tgSetData(int... DataOut) throws IOException{
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x8e);
        for (int i : DataOut) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }
    //tgSetDEPData 8e

    @Deprecated
    public void tgSetDataSecure(int... DataOut) throws IOException{
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x96);
        for (int i : DataOut) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    @Deprecated
    public void tgSetMetaData(int... DataOut) throws IOException{
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x94);
        for (int i : DataOut) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    @Deprecated
    public void tgSetMetaDataSecure(int... DataOut) throws IOException{
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x98);
        for (int i : DataOut) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    @Deprecated
    public void tgGetInitiatorCommand() throws IOException{
        write(0xd4,0x88);
    }

    @Deprecated
    public void tgResponseToInitiator(int... TgResponse) throws IOException{
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0x90);
        for (int i : TgResponse) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }

    @Deprecated
    public void tgGetTargetStatus() throws IOException{
        write(0xd4,0x8A);
    }

    public void communicateThruEX(int timeout,int... DataOut) throws IOException{
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(0xd4);
        l.add(0xa0);
        if (timeout < 256) {
            l.add(timeout);//xL
            l.add(0x00);//xH
        } else if (timeout < 512) {
            l.add(timeout - 256);//xL
            l.add(0x01);//xH
        } else if (timeout == 512) {
            l.add(timeout - 512);//xL
            l.add(0x02);//xH
        }
        for (int i : DataOut) {
            l.add(i);
        }
        int[] wr = new int[l.size()];
        int c = 0;
        for (int in : l.toArray(new Integer[l.size()])) {
            wr[c] = in;
            c++;
        }
        write(wr);
    }
    //CommunicateThruEX a0

    //

    private void write(int... i) throws IOException {
        if (i.length >= 255) {
            writeEX(i);
        } else {
            synchronized (write) {
                write.write(0x00);//Preamble
                write.write(0x00);//Start Of Packet
                write.write(0xff);//Start Of Packet
                write.write(i.length);//Length
                write.write(-i.length);//Length CheckSum
                for (int t : i) {
                    write.write(t);
                }
                write.write(calcDCS(i));//Data CheckSum
                write.write(0x00);//Postamble
            }
        }
    }

    private void writeEX(int... i) throws IOException {
        synchronized (write) {
            write.write(0x00);//Preamble
            write.write(0x00);//Start Of Packet
            write.write(0xff);//Start Of Packet
            write.write(0xff);//Extend MagicCode
            write.write(0xff);//Extend MagicCode
            write.write((i.length >> 8) & 0xff);//Length
            write.write((i.length >> 0) & 0xff);//Length
            write.write(-(((i.length >> 8) & 0xff) + (i.length >> 8) & 0xff));//Length CheckSum
            for (int t : i) {
                write.write(t);
            }
            write.write(calcDCS(i));//Data CheckSum
            write.write(0x00);//Postamble

            System.out.println("----Read ACK----");
            for (int r : readACK()) {
                System.out.printf("%x%n", r);
            }
            System.out.println("----Read Res----");
            for (int r : read()) {
                System.out.printf("%x%n", r);
            }
            System.out.println("----END----");
        }
    }

    public List<Integer> read() throws IOException {
        List<Integer> read = new ArrayList<Integer>();
        while (true) {
            int r = reader.read();
            if (r < 0) {
                break;
            }
            read.add(r);
        }
        return read;
    }

    public List<Integer> readACK() throws IOException {
        List<Integer> read = new ArrayList<Integer>();
        int i = 0;
        while (true) {
            int r = reader.read();
            if (r < 0 || i == 6) {
                break;
            }
            read.add(r);
            i++;
        }
        return read;
    }

    public int calcDCS(int... i) {
        int sum = 0;
        for (int t : i) {
            sum += t;
        }
        return -(sum & 0xff);
    }

    public void print(String s) throws IOException {
        for (byte b : s.getBytes()) {
            write(b);
        }
    }
}
