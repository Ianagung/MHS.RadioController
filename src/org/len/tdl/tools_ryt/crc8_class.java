/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.tools_ryt;

/**
 *
 * @author riyanto
 */

/**
 *
 * This is crc8 class
 */
public class crc8_class {   
    
    /**
     * Reference Variables
     */
    private int[] table = new int[256];
    private int poly = 0xd5;

    /**
     * Method to compute the crc of 8 bytes length
     */
    public crc8_class() {
        int i, j, tmp;    
        for (i = 0; i < 256; ++i) {
            tmp = i;
            for (j = 0; j < 8; ++j) {
                if ((tmp & 0x80) != 0) {
                    tmp = (tmp << 1) ^ poly;
                } else {
                    tmp <<= 1;
                }
            }
            table[i] = tmp & 0xff;
        }
    }
    
    /**
     * Method to compute the crc
     * @param x
     * @param nx
     * @return
     */
    public byte compute(byte[] x, int nx) {
        int i, crc, tmp;
        crc = 0;
        for (i = 0; i < nx; i++) {
            tmp = (int) x[i] & 0xff;
            crc = table[crc ^ tmp];
        }
        return (byte) (crc & 0xff);
    }  
}