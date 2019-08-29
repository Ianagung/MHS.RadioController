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
 * This is crc16_ccitt class
 */
public class crc16_ccitt {
        
    /**
     * Reference Variables
     */
    private int polynomial = 0x1021;
    private int[] table = new int[256];

    /**
     * Method to compute the crc
     * @param x
     * @param idx
     * @param nx
     * @return out_crc
     */
    public byte[] compute(byte[] x, int idx, int nx) {
        byte[] out_crc = new byte[2];
        int crc = 0x1D0F;
        for (int i = 0; i < nx; ++i) {
           crc = (crc << 8) ^ table[(x[i + idx] ^ (crc >> 8)) & 0xff];
        }

        out_crc[0] = (byte) ( (crc >> 8) & 0xff );
        out_crc[1] = (byte) ( crc  & 0xff );

        return out_crc;
    }

    /**
     * Method to compute the crc of 16 bytes length
     */
    public crc16_ccitt() 
    {
        int temp, a;
        for (int i = 0; i < table.length; ++i) {
            temp = 0;
            a = (short)(i << 8);
            for (int j = 0; j < 8; ++j) {
                if (((temp ^ a) & 0x8000) != 0) {
                        temp = (short)((temp << 1) ^ polynomial);
                } else {
                        temp <<= 1;
                }
                a <<= 1;
            }
            table[i] = temp;
        }
    }
}