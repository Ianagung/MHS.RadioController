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
 * This is StructCircle class
 */
public class StructCircle {

     /**
     * Reference Variables
     */
    private boolean valid;
    private double longitude;
    private double latitude;
    private double range;
    private int number;
    private byte area_color;
    private byte line_color;
    private byte line_width;
    private byte note_length;
    private byte[] note_bytes;
    private String notes;
    private boolean property_enable;
    //private final DEF DEF = new DEF();
    private final crc16_ccitt crc16 = new crc16_ccitt();

    /**
    * Method to call StructCircle class
    */
    public StructCircle() {
    }

    /**
     * Method to checking crc of data
     * @param data
     */
    public StructCircle(byte[] data) {

        int data_length = data.length;
        byte[] crc = crc16.compute(data, 0, data_length - 2);

        if ((crc[0] == data[data_length - 2]) && (crc[1] == data[data_length - 1])) {

            long longit = (data[0] & 0xff) + (data[1] & 0xff) * 0x100 + (data[2] & 0xff) * 0x10000 + (data[3] & 0xff) * 0x1000000;
            longitude = longit * 360.0 / 4294967295.0;

            long latit = (data[4] & 0xff) + (data[5] & 0xff) * 0x100 + (data[6] & 0xff) * 0x10000 + (data[7] & 0xff) * 0x1000000;
            latitude = latit * 180.0 / 4294967295.0;

            long range_rx = (data[8] & 0xff) + (data[9] & 0xff) * 0x100 + (data[10] & 0xff) * 0x10000 + (data[11] & 0xff) * 0x1000000;
            range = range_rx * 1.0;

            number = data[12] * 256 + data[13];

            if (data[14] == 1) {
                area_color = data[15];
                line_color = data[16];
                line_width = data[17];
                note_length = data[18];
                note_bytes = new byte[note_length];
                System.arraycopy(data, 19, note_bytes, 0, note_length);

                notes = new String(note_bytes);
                property_enable = true;
            } else {
                notes = " ";
                property_enable = false;
                note_length = 0;
            }

            valid = true;
        } else {
            valid = false;
        }

    }

     /**
     * Method to get bytes of Circle data
     * @param s_circle
     * @param circle_no
     * @return crcldata
     */
    public byte[] getBytesCircle(String s_circle, int circle_no) {

        String[] c1 = s_circle.split(",");

        long clongit1 = Math.round((Double.valueOf(c1[0]) / 360.0) * 4294967295.0);
        long clatit1 = Math.round((Double.valueOf(c1[1]) / 180.0) * 4294967295.0);
        long crng = Long.valueOf(c1[2]);

        int dt_length = 17;
        int pack_length = DEF.PACK_LENGTH * (dt_length / DEF.PACK_LENGTH);
        if ((dt_length % DEF.PACK_LENGTH) != 0) {
            pack_length = pack_length + DEF.PACK_LENGTH;
        }

        byte[] crcldata = new byte[pack_length];
        crcldata[0] = (byte) clongit1;
        crcldata[1] = (byte) (clongit1 >> 8);
        crcldata[2] = (byte) (clongit1 >> 16);
        crcldata[3] = (byte) (clongit1 >> 24);
        crcldata[4] = (byte) clatit1;
        crcldata[5] = (byte) (clatit1 >> 8);
        crcldata[6] = (byte) (clatit1 >> 16);
        crcldata[7] = (byte) (clatit1 >> 24);
        crcldata[8] = (byte) crng;
        crcldata[9] = (byte) (crng >> 8);
        crcldata[10] = (byte) (crng >> 16);
        crcldata[11] = (byte) (crng >> 24);
        crcldata[12] = (byte) ((circle_no >> 8) & 0xFF);
        crcldata[13] = (byte) (circle_no & 0xFF);
        crcldata[14] = (byte) 1;

        byte[] crc = crc16.compute(crcldata, 0, pack_length - 2);
        crcldata[pack_length - 2] = crc[0];
        crcldata[pack_length - 1] = crc[1];

        return crcldata;
    }

    /**
     *
     * @param s_circle
     * @param circle_no
     * @param property
     * @return crcldata
     */
    public byte[] getBytesCircle(String s_circle, int circle_no, byte[] property) {

        String[] c1 = s_circle.split(",");

        long clongit1 = Math.round((Double.valueOf(c1[0]) / 360.0) * 4294967295.0);
        long clatit1 = Math.round((Double.valueOf(c1[1]) / 180.0) * 4294967295.0);
        long crng = Long.valueOf(c1[2]);

        int length_property = property.length;
        int dt_length = 17 + length_property;
        int pack_length = DEF.PACK_LENGTH * (dt_length / DEF.PACK_LENGTH);
        if ((dt_length % DEF.PACK_LENGTH) != 0) {
            pack_length = pack_length + DEF.PACK_LENGTH;
        }

        byte[] crcldata = new byte[pack_length];
        crcldata[0] = (byte) clongit1;
        crcldata[1] = (byte) (clongit1 >> 8);
        crcldata[2] = (byte) (clongit1 >> 16);
        crcldata[3] = (byte) (clongit1 >> 24);
        crcldata[4] = (byte) clatit1;
        crcldata[5] = (byte) (clatit1 >> 8);
        crcldata[6] = (byte) (clatit1 >> 16);
        crcldata[7] = (byte) (clatit1 >> 24);
        crcldata[8] = (byte) crng;
        crcldata[9] = (byte) (crng >> 8);
        crcldata[10] = (byte) (crng >> 16);
        crcldata[11] = (byte) (crng >> 24);
        crcldata[12] = (byte) ((circle_no >> 8) & 0xFF);
        crcldata[13] = (byte) (circle_no & 0xFF);
        crcldata[14] = (byte) 1;

        System.arraycopy(property, 0, crcldata, 15, length_property);

        byte[] crc = crc16.compute(crcldata, 0, pack_length - 2);
        crcldata[pack_length - 2] = crc[0];
        crcldata[pack_length - 1] = crc[1];

        return crcldata;
    }

    /**
     * reference variable
     */
    public boolean enable_property = false;

    /**
     * Method to set properties
     * @param area_color
     * @param line_color
     * @param line_width
     * @param notes
     * @return byte_property
     */
    public byte[] setProperties(int area_color, int line_color, int line_width, String notes) {
        byte[] note = notes.getBytes();
        int length_property = 4 + note.length;
        byte[] byte_property = new byte[length_property];
        byte_property[0] = (byte) area_color;
        byte_property[1] = (byte) line_color;
        byte_property[2] = (byte) line_width;
        byte_property[3] = (byte) note.length;
        System.arraycopy(note, 0, byte_property, 4, note.length);

        return byte_property;
    }

    /**
     * Method to get bytes of Delete Circle data
     * @param circle_no
     * @return crcldata
     */
    public byte[] getBytesDeleteCircle(int circle_no) {

        int pack_length = DEF.PACK_LENGTH;

        byte[] crcldata = new byte[pack_length];
        crcldata[0] = (byte) ((circle_no >> 8) & 0xFF);
        crcldata[1] = (byte) (circle_no & 0xFF);

        byte[] crc = crc16.compute(crcldata, 0, pack_length - 2);
        crcldata[pack_length - 2] = crc[0];
        crcldata[pack_length - 1] = crc[1];

        return crcldata;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return the range
     */
    public double getRange() {
        return range;
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

    /**
     * @return the note_length
     */
    public byte getNote_length() {
        return note_length;
    }

}