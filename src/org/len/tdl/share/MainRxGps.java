/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.len.tdl.share;

//import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.len.tdl.common.DataListener;
import org.len.tdl.common.StructTrack;
import org.len.tdl.view.ryt.Configuration;

/**
 *
 * @author riyanto
 */
public class MainRxGps {

    private final int BAUDRATE_GPS = 4800;
    
    private final SerialConnection serialGps = new SerialConnection();
    private final SerialGpsEventListener serialGpsEventListener = new SerialGpsEventListener();
    private final GpsParser gpsParser = new GpsParser();
    private final List<DataListener> listeners = new ArrayList<>();

    /**
     * This method is used to add Data listeners
     *
     * @param listener
     */
    public void addListener(DataListener listener) {
        listeners.add(listener);
    }

        /**
     * This method is used to add Data listeners
     *
     * @param listener
     */
    public void removeListener() {
        listeners.removeAll(listeners);
    }
    
    /**
     * This method is used to move Data listener
     *
     * @param data
     * @param type
     */
    private void moveData(byte[] data, int type) {
        for (DataListener listener : listeners) {
            listener.dataReceive(data, type);
        }
    }

    private void moveInfo(byte[] data) {
        for (DataListener listener : listeners) {
            listener.infoReceive(data);
        }
    }

    public class SerialGpsEventListener implements SerialPortEventListener {

        @Override
        public void serialEvent(jssc.SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    if (serialGps.serialport.getInputBufferBytesCount() > 0) {
                        byte[] data_serial_gps = serialGps.serialport.readBytes();
                        for (int i = 0; i < data_serial_gps.length; i++) {
                            byte byte_gps = data_serial_gps[i];
                            rcv_gps(byte_gps);
                        }

                        for (int i = 0; i < data_serial_gps.length; i++) {
                            byte byte_gps = data_serial_gps[i];
                            if (gpsParser.ReceiveGPS(byte_gps) == true) {
                                System.out.println("LAT: " + gpsParser.getLatitude() + " LON: " + gpsParser.getLongitude()
                                        + " SPEED: " + gpsParser.getSpeed() + " COURSE: " + gpsParser.getCourse() + " HEIGHT: " + gpsParser.getHeight());

                                StructTrack sTrack = new StructTrack();
                                sTrack.setLatitude(gpsParser.getLatitude());
                                sTrack.setLongitude(gpsParser.getLongitude());
                                sTrack.setCourse(gpsParser.getCourse());
                                sTrack.setSpeed(gpsParser.getSpeed());
                                sTrack.setHeight((int) gpsParser.getHeight());
                                sTrack.setAttribute(123);
                                sTrack.setNumber(0);
                                sTrack.setOwner(Configuration.ownNpu);

                                byte[] dataGps = sTrack.getBytesTrack();
                                moveInfo(dataGps);
                            }
                        }
                        //System.out.println("SERIAL FROM GPS " + data_serial_gps.length + "  BYTES");
                    }
                } catch (SerialPortException ex) {
                }
            }

        }
    }

    /**
     * Reference Variables
     */
    private int ptr_serial = 0;
    byte[] buffer_serial = new byte[258];
    byte[] byte_rx = new byte[3];

    /**
     * method to receive GPS data
     *
     * @param byte_rcv
     */
    private void rcv_gps(byte byte_rcv) {

        byte_rx[0] = byte_rx[1];
        byte_rx[1] = byte_rx[2];
        byte_rx[2] = byte_rcv;

        byte byte_tmp = byte_rx[0];

        if (byte_tmp == '$') {
            ptr_serial = 0;
        }

        if (byte_tmp == '*') {
            if ((buffer_serial[1] == 'G')
                    && (buffer_serial[2] == 'P')
                    && (buffer_serial[3] == 'R')
                    && (buffer_serial[4] == 'M')
                    && (buffer_serial[5] == 'C')) {

                buffer_serial[0] = '$';

                if (ptr_serial > 16) {
                    byte[] gps2 = new byte[ptr_serial];
                    System.arraycopy(buffer_serial, 0, gps2, 0, gps2.length);

                    byte xor = gps2[1];
                    for (int i = 2; i < gps2.length; i++) {
                        xor ^= gps2[i];
                    }

                    String s6 = new String(gps2);
                    String s_xor = String.format("%02X", xor);
                    byte[] bcrc = new byte[2];
                    bcrc[0] = byte_rx[1];
                    bcrc[1] = byte_rx[2];
                    String tmp = new String(bcrc);
                    if (s_xor.equalsIgnoreCase(tmp)) {
                        String gprmc = s6 + "*" + tmp;
                        byte[] dataGpsGprmc = gprmc.getBytes();
                        moveData(dataGpsGprmc, 3);
                    }
                }
            }
        } else {
            buffer_serial[ptr_serial] = byte_tmp;
            ptr_serial = (ptr_serial + 1) & 0xff;
        }

    }

    public void setPortName(String port) {
        serialGps.setPortName(port);
    }

    public void disconnectGps() {
        if (serialGps.portOpen) {
            try {
                serialGps.serialport.removeEventListener();
                serialGps.CloseConnection();
            } catch (SerialPortException ex) {
                ///Logger.getLogger(SimulatorModemKantronics.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void connectGps() {
        if (serialGps.portOpen == false) {

            //conn_gps.OpenConnection(Integer.parseInt(jTextFieldGpsBaudrate.getText()));
            serialGps.OpenConnection(BAUDRATE_GPS);
            if (serialGps.portOpen) {
                serialInitCallback();
            }
        }
    }

    public boolean isGpsConnect() {
        return serialGps.portOpen;
    }

    private void serialInitCallback() {
        try {
            serialGps.serialport.addEventListener(serialGpsEventListener);
        } catch (SerialPortException ex) {
        }
    }

}
