/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.thread_mc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.len.tdl.common.DataListener;

/**
 *
 * @author datalink2
 */

/**
 * 
 * This is UdpRx class
 */
public class UdpRx {
    
     /**
     * Reference Variables
     */
    private DatagramSocket socketUdp;
    private DatagramPacket outPacket;
    private byte[] rxBuf = new byte[64 * 1024];
    private InetAddress address;
    private int portUdp;
    private boolean runUdp = true;
    private List<DataListener> listeners = new ArrayList<DataListener>();

     /**
     * This method is used to add Data listeners
     * @param listener
     */
    public void addListener(DataListener listener) {
        listeners.add(listener);
    }
       
     /**
     * This method is used to move Data listener
     * @param data
     * @param type 
     */
    void moveData(byte[] data, int type){
        for(DataListener listener : listeners){
            listener.dataReceive(data, type);
        }
    }
    
    /**
     * Method to start Thread Udp receive
     */
    public void receiveStart()
    {
        threadUdpRx.start();
    }
   
    /**
     * Method to set Socket IP Address and Port
     * @param port
     * @param add
     */
    public void setSocket(int port, String add) {
        try {
            address = InetAddress.getByName(add);
            portUdp = port;
            socketUdp = new DatagramSocket(portUdp);
            //socketUdp = new DatagramSocket(portUdp, address);
            outPacket = new DatagramPacket(rxBuf, rxBuf.length);
        
        } catch (IOException ex) {
        }
    }

    /**
     * Method to running Thread UDP receive
     */
    public Thread threadUdpRx = new Thread() {
        @Override
        public void run() {
            while (runUdp)
            { 
                try
                {
                    socketUdp.receive(outPacket);
                    int lengthRx = outPacket.getLength(); 
                    byte[] dataRx = new byte[lengthRx];
                    System.arraycopy(rxBuf, 0, dataRx, 0, lengthRx);                    
                    moveData(dataRx, 1);
                    try {
                        Thread.sleep(100);
                    } catch (Exception e1) {
                    }
                } 
                catch (IOException ioe) 
                {
                }
            }           
        }
    };
    
    /**
     * Method to stop udp receive connection
     */
    public void stopUdp()
    {
        runUdp = false;
    }    
}