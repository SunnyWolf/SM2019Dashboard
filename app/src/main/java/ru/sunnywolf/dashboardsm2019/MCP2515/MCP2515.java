package ru.sunnywolf.dashboardsm2019.MCP2515;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.LogRecord;

public class MCP2515 implements AutoCloseable {
    private static final String TAG = MCP2515.class.getSimpleName();

    private final SpiDevice device;
    private final Gpio interruptPin;

    public interface MessageReceivedListener{
        public void onReceived(CanMessage message);
    }
    private MessageReceivedListener listener = null;

    private GpioCallback interruptCb = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            processInterrupt();
            return true;
        }
    };

    public void processInterrupt(){
        try {
            int flags = readRegister(Registers.CANINTF);
            int len = 0;

            byte eflg = readRegister(Registers.EFLG);
            Log.w(TAG, "EFLG: " + byte2hex(eflg));

            if ((flags & Flags.CANINTF_RX0IF) > 0 ){
                CanMessage message = readMessage(0);
                Log.w(TAG, "Received msg on RX0: " + String.format("0x%04x",message.getId()) );
                if (listener != null){
                    listener.onReceived(message);
                }
            }

            if ((flags & Flags.CANINTF_RX1IF) > 0){
                CanMessage message = readMessage(1);
                Log.w(TAG, "Received msg on RX1: " + String.format("0x%04x",message.getId()) );
                if (listener != null){
                    listener.onReceived(message);
                }
            }

            // Clean up interrupt
            writeRegister(Registers.CANINTF, (byte)0x00);
            // Clean up EFLG
            writeRegister(Registers.EFLG, (byte)0x00);

        } catch (IOException e) {
            Log.e(TAG, "Unable to catch interruption", e);
        }
    }

    public static MCP2515 create(String spiBusPort, String intPin) throws IOException{
        PeripheralManager manager = PeripheralManager.getInstance();
        try {
            return new MCP2515(
                    manager.openSpiDevice(spiBusPort),
                    manager.openGpio(intPin) );
        } catch (IOException e){
            throw new IOException("Unable to open SPI device in bus port " + spiBusPort);
        }
    }

    public void setListener(MessageReceivedListener listener){
        this.listener = listener;
    }

    private MCP2515(SpiDevice device, Gpio interruptPin) throws IOException {
        this.device = device;
        this.interruptPin = interruptPin;

        interruptPin.setDirection(Gpio.DIRECTION_IN);
        interruptPin.setEdgeTriggerType(Gpio.EDGE_FALLING);
        interruptPin.setActiveType(Gpio.ACTIVE_HIGH);
        interruptPin.registerGpioCallback(interruptCb);

        device.setFrequency(10000000); // 10 MHz
        device.setMode(SpiDevice.MODE0);
        device.setBitsPerWord(8);

        configureDevice();
    }

    private void configureDevice() throws IOException{
        resetDevice();

        // set configuration mode
        writeRegister(Registers.CANCTRL, Flags.CANCTRL_MODE_CONFIG);

        // 16 MHz oscillator and 1Mb rate
        // Enable wakeup filter
        writeRegister(Registers.CFG1, (byte) 0x00);
        writeRegister(Registers.CFG2, (byte) 0xC9);
        writeRegister(Registers.CFG3, (byte) 0x42);

        // Set filter and buffer
        writeRegister(Registers.RXB0CTRL, Flags.RXB0CTRL_RXM_FILTER);
        // Filter on command 'RPM':     0b00 000 000010 00000
        writeRegister(Registers.RXF0SIDH, (byte)0x00);
        writeRegister(Registers.RXF0SIDL, (byte)0x40);
        // Filter on command 'Current': 0b00 000 000011 00000
        writeRegister(Registers.RXF1SIDH, (byte)0x00);
        writeRegister(Registers.RXF1SIDL, (byte)0x60);
        // Mask0 on any Controller and command 'RPM and Current':  0b11 000 111111 00000
        writeRegister(Registers.RXM0SIDH, (byte)0xC7);
        writeRegister(Registers.RXM0SIDL, (byte)0xE0);

        // Mask1  0b11 111 111111 11111
        writeRegister(Registers.RXM1SIDH, (byte)0xFF);
        writeRegister(Registers.RXM1SIDL, (byte)0xFF);
        // Enable interrupt
        writeRegister(Registers.CANINTE, Flags.CANINTE_RX0IE);

        // set normal mode
        writeRegister(Registers.CANCTRL,
                (byte)( Flags.CANCTRL_MODE_NORMAL | Flags.CANCTRL_CLKPRE_1 )
        );
    }

    @Override
    public void close() {
        try {
            interruptPin.close();
            device.close();
        } catch (IOException e){

        }
        listener = null;
    }

    private void resetDevice(){
        byte[] buffer = new byte[1];
        buffer[0] = Commands.RESET;
        try {
            device.write(buffer, 1);
        } catch (IOException e){
            Log.e(TAG, "resetDevice: unable to reset device", e);
        }
    }

    private void writeRegister(byte reg, byte value) throws IOException{
        byte[] buffer = new byte[3];
        buffer[0] = Commands.WRITE;
        buffer[1] = reg;
        buffer[2] = value;
        try {
            device.write(buffer, 3);
        } catch (IOException e){
            throw new IOException("writeRegister: ", e);
        }
    }

    private byte readRegister(byte reg) throws IOException{
        byte[] buffer = new byte[3];
        buffer[0] = Commands.READ;
        buffer[1] = reg;
        try {
            device.transfer(buffer, buffer,3);
            return buffer[2];
        } catch (IOException e){
            throw new IOException("readRegister: ", e);
        }
    }

    private void modifyRegister(byte reg, byte mask, byte data) throws IOException{
        byte[] buffer = new byte[4];
        buffer[0] = Commands.BIT_MODIFY;
        buffer[1] = reg;
        buffer[2] = mask;
        buffer[3] = data;
        try {
            device.write(buffer,4);
        } catch (IOException e){
            throw new IOException("modifyRegister: ", e);
        }
    }

    private CanMessage readMessage(int buffer_id) throws IOException{
        int len = 0;
        // 1 - command, 4 - id, 1 - DLC, 8 - data
        byte[] temp = new byte[1 + 4 + 1 + 8];

        long id = 0;
        boolean isRtr = false;
        CanMessage message = null;

        if (buffer_id > 1 || buffer_id < 0){
            return null;
        }

        try {
            len = 0x0F & readRegister( (byte)(Registers.RXB0DLC + 0x10 * buffer_id) );

            if (len > 8){
                throw new IOException("Bad data length");
            }

            temp[0] = (byte)(Commands.READ_RX_0_ID + 0x04 * buffer_id);
            device.transfer(temp, temp, 6 + len);

            for (int i = 0; i < 4; i++){
                id |= ( 0xFF & ((int)temp[i + 1]) ) << ( 8 * (3 - i) );
            }

            ByteBuffer buffer = ByteBuffer.allocate(len);
            if (len > 0) {
                buffer.put(temp,6,len);
            }

            if ( (id & Flags.ID_IDE) == 0 ){
                id = id >> 21;
            }
            if ( (id & Flags.ID_SRR) > 0 ){
                isRtr = true;
            }

            message = new CanMessage(id, isRtr, buffer);

        } catch (IOException e){
            throw new IOException("readMessage: Unable to get data", e);
        }

        return message;
    }

    public void sendTest(){
        Log.w(TAG, "sendTest");
        try {
            byte[] buffer = new byte[16];

            Log.w(TAG, "TXB0CTRL: " + byte2hex(readRegister(Registers.TXB0CTRL)));

            buffer[0] = Commands.WRITE;
            buffer[1] = Registers.TXB0CTRL;
            buffer[2] = 0x00;
            buffer[3] = 0x08; // SIDH
            buffer[4] = 0x40; // SIDL
            buffer[5] = 0x00; // EIDH
            buffer[6] = 0x00; // SIDL
            buffer[7] = 0x08; // DLC
            buffer[8] = 0x01; // DATA0
            buffer[9] = 0x02; // DATA1
            buffer[10] = 0x03; // DATA2
            buffer[11] = 0x04; // DATA3
            buffer[12] = 0x05; // DATA4
            buffer[13] = 0x06; // DATA5
            buffer[14] = 0x07; // DATA6
            buffer[15] = 0x08; // DATA7

            device.write(buffer,16);

            writeRegister(Registers.TXB0CTRL, (byte)0x08);

            buffer[0] = Commands.RTS_BUFFER_0;
            device.write(buffer, 1);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e){

            }

            Log.w(TAG, "SENT TEST PACKET, ID: " + String.format("0x%04x", 0x0840));
            /*
            Log.w(TAG, "CANSTAT: " + byte2hex(readRegister(Registers.CANSTAT)) );
            Log.w(TAG, "EFLG: " + byte2hex(readRegister(Registers.EFLG)) );
            Log.w(TAG, "TEC: " + byte2hex(readRegister(Registers.TEC)) );
            Log.w(TAG, "REC: " + byte2hex(readRegister(Registers.REC)) );
            Log.w(TAG, "CANINTF: " +  byte2hex(readRegister(Registers.CANINTF)) );
            Log.w(TAG, "RXB0DLC: " + byte2hex(readRegister(Registers.RXB0DLC)));
            Log.w(TAG, "RXB0SIDH: " + byte2hex(readRegister(Registers.RXB0SIDH)) );
            Log.w(TAG, "RXB0SIDL: " + byte2hex(readRegister(Registers.RXB0SIDL)) );
            Log.w(TAG, "DATA0: " + byte2hex(readRegister(Registers.RXB0D)) );
            Log.w(TAG, "DATA1: " + byte2hex(readRegister((byte)(Registers.RXB0D + 1))) );
            Log.w(TAG, "GPIO: " + interruptPin.getValue());
            */
        } catch (IOException e){
            Log.e(TAG, "sendTest: unable to send", e);
        }
    }

    static String byte2hex(byte b){
        return String.format("0x%02x", b);
    }
}
