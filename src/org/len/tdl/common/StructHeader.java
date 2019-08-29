/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.len.tdl.common;

import java.util.Date;
import org.len.tdl.core.DEF;
import org.len.tdl.tools_ryt.crc16_ccitt;
//import org.len.tdl.tools.crc16_ccitt;

/**
 *
 * @author datalink
 */
public class StructHeader {

    /**
     * @return the source
     */
    public int getSource() {
        return source;
    }

    /**
     * @return the destination
     */
    public int getDestination() {
        return destination;
    }

    /**
     * @return the topic
     */
    public int getTopic() {
        return topic;
    }

    /**
     * @return the type_msg
     */
    public int getType_msg() {
        return type_msg;
    }

    /**
     * @return the type_sub_msg
     */
    public int getType_sub_msg() {
        return type_sub_msg;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @return the sub_version
     */
    public int getSub_version() {
        return sub_version;
    }

    /**
     * @return the hours
     */
    public int getHours() {
        return hours;
    }

    /**
     * @return the minutes
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * @return the seconds
     */
    public int getSeconds() {
        return seconds;
    }
    
    private boolean valid;
    private int source;
    private int destination = 0;
    private int topic = 0;
    private int type_msg;
    private int type_sub_msg;
    private int version;
    private int sub_version;
    private int hours;
    private int minutes;
    private int seconds;
    
    //private final DEF DEF = new DEF();
    private final crc16_ccitt crc16 = new crc16_ccitt();
    
    public StructHeader() {}
    
    public StructHeader(byte[] data) {  
        
        int data_length = DEF.API_HEADER_LENGTH;
        byte[] crc = crc16.compute(data, 0, data_length - 2); 
        
        if ( (crc[0] == data[data_length-2]) && (crc[1] == data[data_length-1])) 
        {
            valid = true;
            version = data[0];
            sub_version = data[1];
            topic = data[2];
            type_msg = data[3];
            type_sub_msg = data[4];
            source = data[5];
            destination = data[6];
            hours = data[7];
            minutes = data[8];
            seconds = data[9];       
            System.out.println("CRC HEADER OKE");
        } 
        else 
        {
            valid = false;
            System.out.println("CRC HEADER NOT OKE");
        }
    }
        
    public byte[] getBytesHeader(int source, int destination, int topic, int type_msg, int type_sub_msg) {        
        
        long mst = System.currentTimeMillis();
        Date dNow = new Date(mst);
        int pc_hours = dNow.getHours();
        int pc_mnt = dNow.getMinutes();
        int pc_sec = dNow.getSeconds();
        
        byte[] dataHeader = new byte[16];
        
        dataHeader[0] = (byte) 1;
        dataHeader[1] = (byte) 0;
        dataHeader[2] = (byte) topic;
        dataHeader[3] = (byte) type_msg;
        dataHeader[4] = (byte) type_sub_msg;
        dataHeader[5] = (byte) source;
        dataHeader[6] = (byte) destination;
        dataHeader[7] = (byte) pc_hours;
        dataHeader[8] = (byte) pc_mnt;
        dataHeader[9] = (byte) pc_sec;       
        
        byte[] crc = crc16.compute(dataHeader, 0, 14);
        dataHeader[14] = crc[0];
        dataHeader[15] = crc[1];
        
        return dataHeader;
    }
    
    public boolean isValid() {
        return valid;
    }


    
    
    
}
