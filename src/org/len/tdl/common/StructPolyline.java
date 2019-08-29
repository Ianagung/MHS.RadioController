/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.common;

import org.len.tdl.core.DEF;
import org.len.tdl.tools_ryt.crc16_ccitt;

/**
 *
 * @author datalink2
 */

/**
 * 
 * This is StructPolyline class
 */
public class StructPolyline {

     /**
     * Reference Variables
     */
    private boolean valid;
    private double[] longitudes;
    private double[] latitudes;
    private int num_point;
    private int number;
    private byte area_color;
    private byte line_color;
    private byte line_width;
    private byte line_type;
    private byte arrow_type;
    private byte note_length;
    private byte[] note_bytes;
    private String notes;
    private boolean property_enable;
    //private final DEF DEF = new DEF();
    private final crc16_ccitt crc16 = new crc16_ccitt();

    /**
     * Method to call StructPolyline class
     */
    public StructPolyline() {
    }

    /**
     * Method to checking crc of data
     * @param data
     */
    public StructPolyline(byte[] data) {

        int data_length = data.length;
        byte[] crc = crc16.compute(data, 0, data_length - 2);

        if ((crc[0] == data[data_length - 2]) && (crc[1] == data[data_length - 1])) {

            num_point = data[0];
            longitudes = new double[num_point];
            latitudes = new double[num_point];

            int k = 1;
            for (int i = 0; i < num_point; i++) {
                long longit = (data[k++] & 0xff) + (data[k++] & 0xff) * 0x100 + (data[k++] & 0xff) * 0x10000 + (data[k++] & 0xff) * 0x1000000;
                longitudes[i] = longit * 360.0 / 4294967295.0;
                long latit = (data[k++] & 0xff) + (data[k++] & 0xff) * 0x100 + (data[k++] & 0xff) * 0x10000 + (data[k++] & 0xff) * 0x1000000;
                latitudes[i] = latit * 180.0 / 4294967295.0;
            }

            byte property_flag = data[k++];
            number = data[k++] * 256 + data[k++];

            if (property_flag == 1) {
                area_color = data[k++];
                line_color = data[k++];
                line_width = data[k++];
                line_type = data[k++];
                arrow_type = data[k++];
                note_length = data[k++];
                note_bytes = new byte[note_length];
                System.arraycopy(data, k, note_bytes, 0, note_length);
                notes = new String(note_bytes);
            }

            valid = true;
        } else {
            valid = false;
        }
    }

    /**
     * Method to get bytes of polyline data
     * @param s_polyline
     * @param polyline_number
     * @return pldata
     */
    public byte[] getBytesPolyline(String s_polyline, int polyline_number) {

        String[] c1 = s_polyline.split(",");
        int pldatalen = (c1.length) * 4 + 6;

        int dt_length = pldatalen;
        int pack_length = DEF.PACK_LENGTH * (dt_length / DEF.PACK_LENGTH);
        if ((dt_length % DEF.PACK_LENGTH) != 0) {
            pack_length = pack_length + DEF.PACK_LENGTH;
        }

        byte[] pldata = new byte[pack_length];

        int num_point = (c1.length) / 2;
        int i = 1;
        pldata[0] = (byte) num_point;
        for (int j = 0; j < num_point; j++) {
            long longitude = Math.round((Double.valueOf(c1[2 * j]) / 360.0) * 4294967295.0);
            long latitude = Math.round((Double.valueOf(c1[2 * j + 1]) / 180.0) * 4294967295.0);
            pldata[i++] = (byte) longitude;
            pldata[i++] = (byte) (longitude >> 8);
            pldata[i++] = (byte) (longitude >> 16);
            pldata[i++] = (byte) (longitude >> 24);
            pldata[i++] = (byte) latitude;
            pldata[i++] = (byte) (latitude >> 8);
            pldata[i++] = (byte) (latitude >> 16);
            pldata[i++] = (byte) (latitude >> 24);
        }

        pldata[i++] = (byte) 0;
        pldata[i++] = (byte) (polyline_number >> 8);
        pldata[i++] = (byte) (polyline_number & 0xFF);

        byte[] crc = crc16.compute(pldata, 0, pack_length - 2);
        pldata[pack_length - 2] = crc[0];
        pldata[pack_length - 1] = crc[1];

        return pldata;
    }

    /**
     * Method to get bytes of polyline data with property
     * @param s_polyline
     * @param polyline_number
     * @param property
     * @return pldata
     */
    public byte[] getBytesPolyline(String s_polyline, int polyline_number, byte[] property) {

        String[] c1 = s_polyline.split(",");

        int length_property = property.length;
        int pldatalen = (c1.length) * 4 + 6;

        int dt_length = pldatalen + length_property;
        int pack_length = DEF.PACK_LENGTH * (dt_length / DEF.PACK_LENGTH);
        if ((dt_length % DEF.PACK_LENGTH) != 0) {
            pack_length = pack_length + DEF.PACK_LENGTH;
        }

        byte[] pldata = new byte[pack_length];

        int num_point = (c1.length) / 2;
        int i = 1;
        pldata[0] = (byte) num_point;
        for (int j = 0; j < num_point; j++) {
            long longitude = Math.round((Double.valueOf(c1[2 * j]) / 360.0) * 4294967295.0);
            long latitude = Math.round((Double.valueOf(c1[2 * j + 1]) / 180.0) * 4294967295.0);
            pldata[i++] = (byte) longitude;
            pldata[i++] = (byte) (longitude >> 8);
            pldata[i++] = (byte) (longitude >> 16);
            pldata[i++] = (byte) (longitude >> 24);
            pldata[i++] = (byte) latitude;
            pldata[i++] = (byte) (latitude >> 8);
            pldata[i++] = (byte) (latitude >> 16);
            pldata[i++] = (byte) (latitude >> 24);
        }

        pldata[i++] = (byte) 1;    
        pldata[i++] = (byte) (polyline_number >> 8);
        pldata[i++] = (byte) (polyline_number & 0xFF);
        System.arraycopy(property, 0, pldata, i, length_property);

        byte[] crc = crc16.compute(pldata, 0, pack_length - 2);
        pldata[pack_length - 2] = crc[0];
        pldata[pack_length - 1] = crc[1];

        return pldata;
    }

    /**
     * Method to set properties
     * @param area_color
     * @param line_color
     * @param line_width
     * @param line_type
     * @param arrow_type
     * @param notes
     * @return property
     */
    public byte[] setProperties(int area_color, int line_color, int line_width, int line_type, int arrow_type, String notes) {
        byte[] note = notes.getBytes();
        int length_property = 6 + note.length;

        byte[] property = new byte[length_property];
        property[0] = (byte) area_color;
        property[1] = (byte) line_color;
        property[2] = (byte) line_width;
        property[3] = (byte) line_type;
        property[4] = (byte) arrow_type;
        property[5] = (byte) note.length;
        System.arraycopy(note, 0, property, 6, note.length);

        return property;
    }

    /**
     * Method to get bytes of delete polyline data
     * @param polyline_no
     * @return pldata
     */
    public byte[] getBytesDeletePolyline(int polyline_no) {

        int pack_length = DEF.PACK_LENGTH;

        byte[] pldata = new byte[pack_length];
        pldata[0] = (byte) ((polyline_no >> 8) & 0xFF);
        pldata[1] = (byte) (polyline_no & 0xFF);

        byte[] crc = crc16.compute(pldata, 0, pack_length - 2);
        pldata[pack_length - 2] = crc[0];
        pldata[pack_length - 1] = crc[1];

        return pldata;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @return the longitudes
     */
    public double[] getLongitudes() {
        return longitudes;
    }

    /**
     * @return the latitudes
     */
    public double[] getLatitudes() {
        return latitudes;
    }

    /**
     * @return the num_point
     */
    public int getNum_point() {
        return num_point;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @return the area_color
     */
    public byte getArea_color() {
        return area_color;
    }

    /**
     * @return the line_color
     */
    public byte getLine_color() {
        return line_color;
    }

    /**
     * @return the line_width
     */
    public byte getLine_width() {
        return line_width;
    }

    /**
     * @return the line_type
     */
    public byte getLine_type() {
        return line_type;
    }

    /**
     * @return the arrow_type
     */
    public byte getArrow_type() {
        return arrow_type;
    }

    /**
     * @return the note_length
     */
    public byte getNote_length() {
        return note_length;
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @return the property_enable
     */
    public boolean isProperty_enable() {
        return property_enable;
    }

}