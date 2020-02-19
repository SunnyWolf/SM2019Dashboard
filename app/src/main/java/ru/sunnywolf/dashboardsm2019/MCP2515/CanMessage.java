package ru.sunnywolf.dashboardsm2019.MCP2515;

import java.nio.ByteBuffer;

public class CanMessage {
    private long id;
    private ByteBuffer data;
    private boolean isRtr;



    public CanMessage(long id, boolean isRtr, ByteBuffer data){
        this.data = data;
        this.id = id;
        this.isRtr = isRtr;
    }

    public boolean isRemoteTransmitRequest(){
        return isRtr;
    }

    public long getId(){
        return id;
    }

    public ByteBuffer getData() {
        return data;
    }

    public int getDataLength(){
        return data.capacity();
    }
}
