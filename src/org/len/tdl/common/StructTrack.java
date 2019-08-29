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
 * This is StructTrack class
 */
public class StructTrack {
    
     /**
     * Reference Variables
     */
    private boolean valid;
    private double longitude;
    private double latitude;
    private double speed;
    private double course;
    private int height;
    private int attribute;
    private int owner;
    private int group;
    private int number;
    private int mmsi;
    //private final DEF DEF = new DEF();
    private final crc16_ccitt crc16 = new crc16_ccitt();
    
    /**
     * Method to call StructTrack class
     */
    public StructTrack()
    {
    }
    
    /**
     * Method to checking crc of data
     * @param data
     */
    public StructTrack(byte[] data)
    {
        int data_length = data.length;
        byte[] crc = crc16.compute(data, 0, DEF.PACK_LENGTH_M2);
        
        if ((crc[0] == data[data_length - 2]) && (crc[0] == data[data_length - 2])) {
            
            valid = true;
            
            long longit1 = (data[DEF.LON1_IDX] & 0xff) + (data[DEF.LON2_IDX] & 0xff) * 0x100 + (data[DEF.LON3_IDX] & 0xff) * 0x10000 + (data[DEF.LON4_IDX] & 0xff) * 0x1000000;
            longitude = longit1 * 360.0 / 4294967295.0;

            long latit1 = (data[DEF.LAT1_IDX] & 0xff) + (data[DEF.LAT2_IDX] & 0xff) * 0x100 + (data[DEF.LAT3_IDX] & 0xff) * 0x10000 + (data[DEF.LAT4_IDX] & 0xff) * 0x1000000;
            latitude = latit1 * 180.0 / 4294967295.0;

            long spd1 = (data[DEF.SPD1_IDX] & 0xff) + (data[DEF.SPD2_IDX] & 0xff) * 0x100 + (data[DEF.SPD3_IDX] & 0xff) * 0x10000;
            speed = spd1 * 4000.0 / 0xffffff;

            long crs1 = (data[DEF.CRS1_IDX] & 0xff) + (data[DEF.CRS2_IDX] & 0xff) * 0x100 + (data[DEF.CRS3_IDX] & 0xff) * 0x10000;
            course = crs1 * 360.0 / 0xffffff;

            height = (data[DEF.HGT1_IDX] & 0xff) + (data[DEF.HGT2_IDX] & 0xff) * 0x100 + (data[DEF.HGT3_IDX] & 0xff) * 0x10000;

            attribute = (data[DEF.ATTB1_IDX] & 0xff) + (data[DEF.ATTB2_IDX] & 0xff) * 0x100;

            owner = data[DEF.OWNNPU1_IDX];
            
            group = data[DEF.OWNNPU2_IDX];

            number = (data[DEF.TRAKNO1_IDX]  & 0xff)+ (data[DEF.TRAKNO2_IDX] & 0xff) * 256 ;
            
            mmsi = (data[DEF.MMSI1_IDX] & 0xff) + (data[DEF.MMSI2_IDX] & 0xff) * 0x100 + (data[DEF.MMSI3_IDX] & 0xff) * 0x10000 + (data[DEF.MMSI4_IDX] & 0xff) * 0x1000000;
            
        } 
        else 
        {
            valid = false;
        }
    }
    
    /**
     * Method to get Bytes of Track data
     * @return trak_tmp
     */
    public byte[] getBytesTrack()
    {
        long longit1 = Math.round((getLongitude() / 360.0) * 4294967295.0);
        long latit1 = Math.round(getLatitude() * 4294967295.0 / 180.0);
        long spd1 = Math.round(getSpeed() * 0xffffff / 4000.0);
        long crs1 = Math.round(getCourse() * 0xffffff / 360.0);

        byte[] trak_tmp = new byte[DEF.PACK_LENGTH];
        trak_tmp[0] = (byte) longit1;
        trak_tmp[1] = (byte) (longit1 >> 8);
        trak_tmp[2] = (byte) (longit1 >> 16);
        trak_tmp[3] = (byte) (longit1 >> 24);
        trak_tmp[4] = (byte) latit1;
        trak_tmp[5] = (byte) (latit1 >> 8);
        trak_tmp[6] = (byte) (latit1 >> 16);
        trak_tmp[7] = (byte) (latit1 >> 24);
        trak_tmp[8] = (byte) spd1;
        trak_tmp[9] = (byte) (spd1 >> 8);
        trak_tmp[10] = (byte) (spd1 >> 16);
        trak_tmp[11] = (byte) crs1;
        trak_tmp[12] = (byte) (crs1 >> 8);
        trak_tmp[13] = (byte) (crs1 >> 16);
        trak_tmp[14] = (byte) getHeight();
        trak_tmp[15] = (byte) (getHeight() >> 8);
        trak_tmp[16] = (byte) (getHeight() >> 16);
        trak_tmp[17] = (byte) getAttribute();
        trak_tmp[18] = (byte) (getAttribute() >> 8);
        trak_tmp[19] = (byte) getOwner();
        trak_tmp[20] = (byte) (getOwner() >> 8);
        trak_tmp[21] = (byte) (getNumber() & 0xff);
        trak_tmp[22] = (byte) (getNumber() >> 8);
        trak_tmp[23] = (byte)  getMmsi();
        trak_tmp[24] = (byte) (getMmsi() >> 8);
        trak_tmp[25] = (byte) (getMmsi() >> 16);
        trak_tmp[26] = (byte) (getMmsi() >> 24);
        trak_tmp[27] = (byte) 0;
        trak_tmp[28] = (byte) 0;
        trak_tmp[29] = (byte) 0;

        byte[] crc_data = crc16.compute(trak_tmp, 0, DEF.PACK_LENGTH_M2);
        trak_tmp[30] = (byte) crc_data[0];
        trak_tmp[31] = (byte) crc_data[1];

        return trak_tmp;        
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
     * @return the speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * @return the course
     */
    public double getCourse() {
        return course;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the attribute
     */
    public int getAttribute() {
        return attribute;
    }

    /**
     * @return the owner
     */
    public int getOwner() {
        return owner;
    }


    /**
     * @return the group
     */
    public int getGroup() {
        return group;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }
    
    /**
     *
     * @return the mmsi
     */
    public int getMmsi(){
        return mmsi;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * @param course the course to set
     */
    public void setCourse(double course) {
        this.course = course;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(int owner) {
        this.owner = owner;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(int group) {
        this.group = group;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(int number) {
        this.number = number;
    }
    
    /**
     *
     * @param mmsi the mmsi to set
     */
    public void setMmsi(int mmsi){
        this.mmsi = mmsi;
    }
}