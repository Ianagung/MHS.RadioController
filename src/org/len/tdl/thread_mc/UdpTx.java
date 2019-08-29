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
import java.net.SocketException;

/**
 *
 * @author datalink2
 */

/**
 * 
 * This is UdpTx class
 */
public class UdpTx {
    
     /**
     * Reference Variables
     */
    private DatagramSocket socketUdp;
    private DatagramPacket outPacket;
    private byte[] txBuf = new byte[64 * 1024];
    private InetAddress address;
    private boolean udpTxState;
    private DatagramSocket socket = null;
    public int port;
    public InetAddress multicastAddress;
    
    /**
     * Method to start or set UDP socket connection
     */
    public UdpTx()
    {
        try {
            socketUdp = new DatagramSocket();
        } catch (IOException ex) {
        }          
    }
   
    /**
     * Method to set socket IP Address and Port
     * @param port
     * @param add
     */
    public void setSocket(int port, String add) {
        try {
            address = InetAddress.getByName(add);
            outPacket = new DatagramPacket(txBuf, txBuf.length, address, port);
        } catch (IOException ex) {
        }
    }
    
    /**
     *
     * @param udpTxState
     */
    public void setUdpTxState(boolean udpTxState) {
        this.udpTxState = udpTxState;
    }
        
    /**
     * Method to send packet data via UDP
     * @param data
     */
    public void sendUdp(byte[] data)
    {
        try {
            outPacket.setData(data);
            socketUdp.send(outPacket);
        } catch (IOException ioe) {
        }
    }

    /**
     * Method to send packet data via UDP
     * @param udpPacket
     */
    public void kirimdata(byte[] udpPacket) {
         try {
         outPacket.setData(udpPacket);
         outPacket.setLength(udpPacket.length);
         this.socket.send(outPacket);

        } catch (SocketException ex) {
            
        } catch (IOException ex) {
           
        }
    }   
}