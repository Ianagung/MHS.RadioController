/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.len.tdl.share;

import java.util.Date;
import org.len.tdl.common.StructHeader;
import org.len.tdl.common.StructText;
import org.len.tdl.common.StructTrack;
import org.len.tdl.core.DEF;
import org.len.tdl.thread_mc.UdpTx;
//import org.len.tdl.udp.UdpTx;

/**
 *
 * @author APS
 */
public class MainTxGps {
    
    
    private final UdpTx udpTransmit = new UdpTx();
    private int textCounter = 0;
    
    public void setSocket(int port, String ipAddress)
    {
        udpTransmit.setSocket(port, ipAddress);
    }
    
    public void sendText(String sText, int Npu)
    {
        // INPUT HEADER
        int source = Npu;
        int destination = 0;
        int topic = DEF.TOPIC_CCD2TG;  
        int typeMsg = DEF.TYPE_TX_MSG;
        int typeSubMsg = DEF.MSG_TYPE_TEXT;     
        StructHeader structHeader = new StructHeader();
        byte[] header = structHeader.getBytesHeader(source, destination, topic, typeMsg, typeSubMsg);
        
        // INPUT TRAK
        String textString = sText;
        int textNumber = textCounter++;        
        StructText structText = new StructText();         
        byte[] textBytes = structText.getBytesText(textString, textNumber);   
        
        // COMBINE HEADER AND TEXT
        byte[] packet = new byte[header.length + textBytes.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(textBytes, 0, packet, header.length, textBytes.length);
        
        // SEND VIA UDP
        udpTransmit.sendUdp(packet);
    }
    
    public void sendTrack(double latitude, double longitude, double speed, double course, int height, int attribute, int number, int mmsi, int group, int Npu)
    {
        // INPUT HEADER
        int source = Npu;
        int destination = 0;
        int topic = DEF.TOPIC_CCD2TG;  
        int typeMsg = DEF.TYPE_TX_TRACK;
        int typeSubMsg = DEF.TRACK_TYPE_GPS;          
        StructHeader structHeader = new StructHeader();
        byte[] header = structHeader.getBytesHeader(source, destination, topic, typeMsg, typeSubMsg);
        
        // INPUT TRACK        
        StructTrack structTrack = new StructTrack();
        structTrack.setLongitude(longitude);
        structTrack.setLatitude(latitude);
        structTrack.setSpeed(speed);
        structTrack.setCourse(course);
        structTrack.setHeight(height);
        structTrack.setAttribute(attribute);
        structTrack.setOwner(Npu);
        structTrack.setGroup(group);
        structTrack.setNumber(number);
        structTrack.setMmsi(mmsi);        
        byte[] track = structTrack.getBytesTrack();   
        
        // COMBINE HEADER AND TRACK
        byte[] packet = new byte[header.length + track.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(track, 0, packet, header.length, track.length);
        
        // SEND VIA UDP
        udpTransmit.sendUdp(packet); 
    }
    
        public void sendTrackToCore(byte[] track, int typeMsg, int typeSubMsg, int Npu)
    {
        // INPUT HEADER
        int source = Npu;
        int destination = 0;
        int topic = DEF.TOPIC_TG2CORE;  
        //int typeMsg = DEF.TYPE_TX_TRACK;
        //int typeSubMsg = DEF.TRACK_TYPE_GPS;     
        
        long mst = System.currentTimeMillis();
        Date dNow = new Date(mst);
        int pc_hours = dNow.getHours();
        int pc_mnt = dNow.getMinutes();
        int pc_sec = dNow.getSeconds();
        
        byte[] header = new byte[8];
        header[0] = (byte) topic;
        header[1] = (byte) typeMsg;
        header[2] = (byte) typeSubMsg;
        header[3] = (byte) 1; // jumlah track
        header[4] = (byte) destination;
        header[5] = (byte) pc_hours;
        header[6] = (byte) pc_mnt;
        header[7] = (byte) pc_sec;
        
        // COMBINE HEADER AND TRACK
        byte[] packet = new byte[header.length + track.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(track, 0, packet, header.length, track.length);
        
        // SEND VIA UDP
        udpTransmit.sendUdp(packet); 
        
        
        //System.out.println("TYPE: " + typeMsg);
    }
    
    public void sendTextToCore(byte[] text, int typeMsg, int typeSubMsg, int Npu, int Dest)
    {
        // INPUT HEADER
        int source = Npu;
        int destination = Dest;
        int topic = DEF.TOPIC_TG2CORE;  
        
        long mst = System.currentTimeMillis();
        Date dNow = new Date(mst);
        int pc_hours = dNow.getHours();
        int pc_mnt = dNow.getMinutes();
        int pc_sec = dNow.getSeconds();
        
        byte[] header = new byte[8];
        header[0] = (byte) topic;
        header[1] = (byte) typeMsg;
        header[2] = (byte) typeSubMsg;
        header[3] = (byte) source;
        header[4] = (byte) destination;
        header[5] = (byte) pc_hours;
        header[6] = (byte) pc_mnt;
        header[7] = (byte) pc_sec;
        
        // COMBINE HEADER AND TRACK
        byte[] packet = new byte[header.length + text.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(text, 0, packet, header.length, text.length);
        
        // SEND VIA UDP
        udpTransmit.sendUdp(packet); 
        
        
        //System.out.println("TYPE: " + typeMsg);
    }
    
    
    public void sendTrackToCcd(byte[] track, int typeMsg, int typeSubMsg, int Npu)
    {
        // INPUT HEADER
        int source = Npu;
        int destination = 0;
        int topic = DEF.TOPIC_TG2CCD;  
        //int typeMsg = DEF.TYPE_TX_TRACK;
        //int typeSubMsg = DEF.TRACK_TYPE_GPS;          
        StructHeader structHeader = new StructHeader();
        byte[] header = structHeader.getBytesHeader(source, destination, topic, typeMsg, typeSubMsg);
        
     
        
        // COMBINE HEADER AND TRACK
        byte[] packet = new byte[header.length + track.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(track, 0, packet, header.length, track.length);
        
        // SEND VIA UDP
        udpTransmit.sendUdp(packet); 
    }
    
    
    public void sendGpsGprmc(byte[] packet, int port, String ipAddress)
    {
        udpTransmit.setSocket(port, ipAddress);
        udpTransmit.sendUdp(packet); 
    }

}

