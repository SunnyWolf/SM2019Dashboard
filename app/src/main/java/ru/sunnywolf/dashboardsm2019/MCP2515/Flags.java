package ru.sunnywolf.dashboardsm2019.MCP2515;

public class Flags {
    public static final byte STATUS_RCV_BUFF_0_FULL = (byte)0x01;
    public static final byte STATUS_RCV_BUFF_1_FULL = (byte)0x02;
    public static final byte STATUS_MES_TRANSMIT_REQ_0 = (byte)0x04;
    public static final byte STATUS_TX_BUFF_EMPTY_0 = (byte)0x08;
    public static final byte STATUS_MES_TRANSMIT_REQ_1 = (byte)0x10;
    public static final byte STATUS_TX_BUFF_EMPTY_1 = (byte)0x20;
    public static final byte STATUS_MES_TRANSMIT_REQ_2 = (byte)0x40;
    public static final byte STATUS_TX_BUFF_EMPTY_2 = (byte)0x80;


    public static final byte CANCTRL_MODE_NORMAL = (byte)0x00;
    public static final byte CANCTRL_MODE_SLEEP = (byte)0x20;
    public static final byte CANCTRL_MODE_LOOPBACK = (byte)0x40;
    public static final byte CANCTRL_MODE_LISTENONLY = (byte)0x60;
    public static final byte CANCTRL_MODE_CONFIG = (byte)0x80;
    public static final byte CANCTRL_MODE_MASK = (byte)0xE0;

    public static final byte CANCTRL_ABAT = (byte)0x10;
    public static final byte CANCTRL_OSM = (byte)0x08;
    public static final byte CANCTRL_CLKEN = (byte)0x04;

    public static final byte CANCTRL_CLKPRE_1 = (byte)0x00;
    public static final byte CANCTRL_CLKPRE_2 = (byte)0x01;
    public static final byte CANCTRL_CLKPRE_4 = (byte)0x02;
    public static final byte CANCTRL_CLKPRE_8 = (byte)0x03;
    public static final byte CANCTRL_CLKPRE_MASK = (byte)0x03;

    public static final byte CANSTAT_ICOD_NO = (byte)0x00;
    public static final byte CANSTAT_ICOD_ERR = (byte)0x02;
    public static final byte CANSTAT_ICOD_WAKEUP = (byte)0x04;
    public static final byte CANSTAT_ICOD_TXB0 = (byte)0x06;
    public static final byte CANSTAT_ICOD_TXB1 = (byte)0x08;
    public static final byte CANSTAT_ICOD_TXB2 = (byte)0x0A;
    public static final byte CANSTAT_ICOD_RXB0 = (byte)0x0C;
    public static final byte CANSTAT_ICOD_RXB1 = (byte)0x0E;
    public static final byte CANSTAT_ICOD_MASK = (byte)0x0E;

    public static final byte RXB0CTRL_RXM_ANY = (byte)0x60;
    public static final byte RXB0CTRL_RXM_FILTER = (byte)0x00;
    public static final byte RXB0CTRL_RXRTR = (byte)0x08;
    public static final byte RXB0CTRL_BUKT = (byte)0x04;
    public static final byte RXB0CTRL_BUKT1 = (byte)0x02;
    public static final byte RXB0CTRL_FILHIT = (byte)0x01;

    public static final byte CANINTE_RX0IE = (byte)0x01;
    public static final byte CANINTE_RX1IE = (byte)0x02;
    public static final byte CANINTE_TX0IE = (byte)0x04;
    public static final byte CANINTE_TX1IE = (byte)0x08;
    public static final byte CANINTE_TX2IE = (byte)0x10;
    public static final byte CANINTE_ERRIE = (byte)0x20;
    public static final byte CANINTE_WAKIE = (byte)0x40;
    public static final byte CANINTE_MERRE = (byte)0x80;

    public static final byte CANINTF_RX0IF = (byte)0x01;
    public static final byte CANINTF_RX1IF = (byte)0x02;
    public static final byte CANINTF_TX0IF = (byte)0x04;
    public static final byte CANINTF_TX1IF = (byte)0x08;
    public static final byte CANINTF_TX2IF = (byte)0x10;
    public static final byte CANINTF_ERRIF = (byte)0x20;
    public static final byte CANINTF_WAKIF = (byte)0x40;
    public static final byte CANINTF_MERRF = (byte)0x80;

    public static final int ID_IDE = 0x00040000;
    public static final int ID_SRR = 0x00100000;
}
