package ru.sunnywolf.dashboardsm2019.MCP2515;

public class Registers {
    public static final byte CANCTRL = (byte)0x0F;
    public static final byte CANSTAT = (byte)0x0E;

    public static final byte RXF0SIDH = (byte)0x00;
    public static final byte RXF0SIDL = (byte)0x01;
    public static final byte RXF0EID8 = (byte)0x02;
    public static final byte RXF0EID0 = (byte)0x03;
    public static final byte RXF1SIDH = (byte)0x04;
    public static final byte RXF1SIDL = (byte)0x05;
    public static final byte RXF1EID8 = (byte)0x06;
    public static final byte RXF1EID0 = (byte)0x07;
    public static final byte RXF2SIDH = (byte)0x08;
    public static final byte RXF2SIDL = (byte)0x09;
    public static final byte RXF2EID8 = (byte)0x0A;
    public static final byte RXF2EID0 = (byte)0x0B;
    public static final byte RXF3SIDH = (byte)0x10;
    public static final byte RXF3SIDL = (byte)0x11;
    public static final byte RXF3EID8 = (byte)0x12;
    public static final byte RXF3EID0 = (byte)0x13;
    public static final byte RXF4SIDH = (byte)0x14;
    public static final byte RXF4SIDL = (byte)0x15;
    public static final byte RXF4EID8 = (byte)0x16;
    public static final byte RXF4EID0 = (byte)0x17;
    public static final byte RXF5SIDH = (byte)0x18;
    public static final byte RXF5SIDL = (byte)0x19;
    public static final byte RXF5EID8 = (byte)0x1A;
    public static final byte RXF5EID0 = (byte)0x1B;

    public static final byte TEC = (byte)0x1C;
    public static final byte REC = (byte)0x1D;
    public static final byte EFLG = (byte)0x2D;

    public static final byte CANINTE = (byte)0x2B;
    public static final byte CANINTF = (byte)0x2C;

    public static final byte RXM0SIDH = (byte)0x20;
    public static final byte RXM0SIDL = (byte)0x21;
    public static final byte RXM0EID8 = (byte)0x22;
    public static final byte RXM0EID0 = (byte)0x23;
    public static final byte RXM1SIDH = (byte)0x24;
    public static final byte RXM1SIDL = (byte)0x25;
    public static final byte RXM1EID8 = (byte)0x26;
    public static final byte RXM1EID0 = (byte)0x27;


    public static final byte CFG3 = (byte)0x28;
    public static final byte CFG2 = (byte)0x29;
    public static final byte CFG1 = (byte)0x2a;

    public static final byte TXB0CTRL = (byte)0x30;
    public static final byte TXB0SIDH = (byte)0x31;
    public static final byte TXB0SIDL = (byte)0x32;
    public static final byte TXB0EID8 = (byte)0x33;
    public static final byte TXB0EID0 = (byte)0x34;
    public static final byte TXB0DLC = (byte)0x35;
    public static final byte TXB0D = (byte)0x36;
    public static final byte TXB1CTRL = (byte)0x40;
    public static final byte TXB1SIDH = (byte)0x41;
    public static final byte TXB1SIDL = (byte)0x42;
    public static final byte TXB1EID8 = (byte)0x43;
    public static final byte TXB1EID0 = (byte)0x44;
    public static final byte TXB1DLC = (byte)0x45;
    public static final byte TXB1D = (byte)0x46;
    public static final byte TXB2CTRL = (byte)0x50;
    public static final byte TXB2SIDH = (byte)0x51;
    public static final byte TXB2SIDL = (byte)0x52;
    public static final byte TXB2EID8 = (byte)0x53;
    public static final byte TXB2EID0 = (byte)0x54;
    public static final byte TXB2DLC = (byte)0x55;
    public static final byte TXB2D = (byte)0x56;
    public static final byte RXB0CTRL = (byte)0x60;
    public static final byte RXB0SIDH = (byte)0x61;
    public static final byte RXB0SIDL = (byte)0x62;
    public static final byte RXB0EID8 = (byte)0x63;
    public static final byte RXB0EID0 = (byte)0x64;
    public static final byte RXB0DLC = (byte)0x65;
    public static final byte RXB0D = (byte)0x66;
    public static final byte RXB1CTRL = (byte)0x70;
    public static final byte RXB1SIDH = (byte)0x71;
    public static final byte RXB1SIDL = (byte)0x72;
    public static final byte RXB1EID8 = (byte)0x73;
    public static final byte RXB1EID0 = (byte)0x74;
    public static final byte RXB1DLC = (byte)0x75;
    public static final byte RXB1D = (byte)0x76;
}
