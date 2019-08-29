/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.thread_mc;

/**
 *
 * @author datalink2
 */

/**
 * 
 * This is UdpTxTest class
 */
public class UdpTxTest {
    
    /**
     *
     * @param args
     */
    public static void main(String args[])
    {
        UdpTx udpTransmit = new UdpTx();
        udpTransmit.setSocket(8888, "localhost");
        
        int counter = 0;
        while(true) {
            udpTransmit.sendUdp(("Hallo " + counter++).getBytes());             
            try { Thread.sleep(1000); } catch (Exception e1) {}
        }
    } 
}