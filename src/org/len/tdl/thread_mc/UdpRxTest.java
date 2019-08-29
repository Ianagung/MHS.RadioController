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
 * This is UdpRxTest class
 */
public class UdpRxTest {

    /**
     *
     * @param args
     */
    public static void main(String args[])
    {
        UdpRx udpReceive = new UdpRx();
        udpReceive.setSocket(1111, "localhost");
        udpReceive.receiveStart();
        
    }  
}