/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.len.tdl.share;

/**
 *
 * @author riyanto
 */
public class GpsParser {

    /**
     * Variable reference*
     */
    double longit;
    double latit;
    double spd;
    double crs;
    int hgt;
    byte[] serialBuffer = new byte[258];
    byte[] byteRx = new byte[3];
    private int pointerSerial = 0;

    private byte byteTemporary;
    private byte[] gps;
    private byte xor;
    byte[] header_gps;
    private String temporaryGpsData;
    private String slatitude;
    private String slongitude;
    private String sspeed;
    private String scourse;
    private double latitude;
    private double degree;
    private double minute;
    private double second;
    private double decimal_lat;
    private String NS;
    private double longitude;
    private double decimal_long;
    private String EW;
    private double speed;
    private double course;
    private String sheight;
    private double height;
    private byte[] gps3;
    private String s_xor;
    byte[] bcrc;
    String tmp;

    /**
     * The Constructor
     *
     * @param trackGenerator Track Generator
     */
    /**
     * GPS Data Parser Method
     *
     * @param receiveByte Receive Byte
     *
     */
    public double getLongitude() {
        return longit;
    }

    public double getLatitude() {
        return latit;
    }

    public double getSpeed() {
        return spd;
    }

    public double getCourse() {
        return crs;
    }

    public double getHeight() {
        return hgt;
    }

    public boolean ReceiveGPS(byte receiveByte) {

        boolean return_value = false;

        byteRx[0] = byteRx[1];
        byteRx[1] = byteRx[2];
        byteRx[2] = receiveByte;
        byteTemporary = byteRx[0];
        if (byteTemporary == '$') {
            pointerSerial = 0;
        }
        if (byteTemporary == '*') {
            if (((serialBuffer[1] == 'G')
                    && (serialBuffer[2] == 'P')
                    && (serialBuffer[3] == 'R')
                    && (serialBuffer[4] == 'M')
                    && (serialBuffer[5] == 'C'))
                    || ((serialBuffer[1] == 'G')
                    && (serialBuffer[2] == 'P')
                    && (serialBuffer[3] == 'G')
                    && (serialBuffer[4] == 'G')
                    && (serialBuffer[5] == 'A'))) {
                serialBuffer[0] = '$';
                if (pointerSerial > 16) {
                    gps = new byte[pointerSerial];
                    System.arraycopy(serialBuffer, 0, gps, 0, gps.length);

                    temporaryGpsData = new String(gps);
                    String gps_string[] = temporaryGpsData.split(",");
                    header_gps = gps_string[0].getBytes();

                    xor = gps[1];
                    for (int i = 2; i < gps.length; i++) {
                        xor ^= gps[i];
                    }

                    if ((header_gps[3] == 'R') && (header_gps[4] == 'M') && (header_gps[5] == 'C')) {

                        byte[] valid_gps = gps_string[2].getBytes();

                        if (valid_gps[0] == 'V') {
                            System.out.println("GPS NOT VALID!");
                        } else {

                            slatitude = gps_string[3];
                            slongitude = gps_string[5];
                            sspeed = gps_string[7];
                            scourse = gps_string[8];
                            latitude = Double.valueOf(slatitude);
                            degree = Math.floor(latitude / 100);
                            minute = Math.floor(latitude) - (100 * degree);
                            second = latitude - Math.floor(latitude);
                            decimal_lat = degree + (minute / 60) + (second / 3600);
                            NS = gps_string[4];
                            if (NS.equalsIgnoreCase("S")) {
                                decimal_lat = -1 * decimal_lat;
                            }

                            longitude = Double.valueOf(slongitude);
                            degree = Math.floor(longitude / 100);
                            minute = Math.floor(longitude) - (100 * degree);
                            second = longitude - Math.floor(longitude);
                            decimal_long = degree + (minute / 60) + (second / 3600);
                            EW = gps_string[6];
                            if (EW.equalsIgnoreCase("W")) {
                                decimal_long = -1 * decimal_long;
                            }

                            if (sspeed.isEmpty() == false) {
                                speed = Double.valueOf(sspeed);
                            }
                            if (scourse != null) {
                                course = Double.valueOf(scourse);
                            }
                            longit = decimal_long;
                            latit = decimal_lat;
                            spd = speed;
                            crs = course;

                        }

                        if ((header_gps[3] == 'G') && (header_gps[4] == 'G') && (header_gps[5] == 'A')) {
                            sheight = gps_string[9];
                            if (sheight != null) {
                                height = Double.valueOf(sheight);
                            }
                            hgt = (int) height;
                        }

                        s_xor = String.format("%02X", xor);
                        bcrc = new byte[2];
                        bcrc[0] = byteRx[1];
                        bcrc[1] = byteRx[2];
                        tmp = new String(bcrc);

                        if (s_xor.equalsIgnoreCase(tmp)) {

                            return_value = true;

                        }

                    }
                }
            }

        } else {

            serialBuffer[pointerSerial] = byteTemporary;
            pointerSerial = (pointerSerial + 1) & 0xff;

        }

        return return_value;
    }
}
