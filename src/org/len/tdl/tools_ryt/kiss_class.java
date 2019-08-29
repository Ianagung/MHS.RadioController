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
 * This is kiss class
 */
public class kiss_class {

     /**
     * Reference Variables
     */
    final byte FEND = (byte) 0xc0;
    final byte FESC = (byte) 0xdb;
    final byte TFEND = (byte) 0xdc;
    final byte TFESC = (byte) 0xdd;

    /**
     * Method to encrypt
     * @param x
     * @return y
     */
    public byte[] enc(byte[] x) {
        int k = 0;
        byte[] tmp = new byte[2 * x.length];
        for (int i = 0; i < x.length; i++) {
            byte c = x[i];
            if (c == FEND) {
                tmp[k] = FESC;
                k = k + 1;
                tmp[k] = TFEND;
                k = k + 1;
            } else if (c == FESC) {
                tmp[k] = FESC;
                k = k + 1;
                tmp[k] = TFESC;
                k = k + 1;
            } else {
                tmp[k] = c;
                k = k + 1;
            }
        }
        byte[] y = new byte[k + 3];
        y[0] = FEND;
        y[1] = 0;
        y[2 + k] = FEND;
        System.arraycopy(tmp, 0, y, 2, k);
        return y;
    }

  /**
  * Reference variables
  */
    private int kiss_count = 0;
    private byte tmp_prev = 0;
    private byte[] kiss_rcv;
    private final int MAX_KISS_BUFF = 10240;
    private final byte[] kiss_buff = new byte[MAX_KISS_BUFF + 1];
    private boolean kiss_ret;

    /**
     * Method to decrypt
     * @param tmp
     * @return kiss_ret
     */
    public boolean dec(byte tmp) {
        
        kiss_ret = false;
        if (tmp != FEND) {
            if (tmp != FESC) {
                if (tmp_prev == FESC) {
                    if (tmp == TFEND) {
                        kiss_buff[kiss_count] = FEND;
                    } else if (tmp == TFESC) {
                        kiss_buff[kiss_count] = FESC;
                    }
                } else {
                    kiss_buff[kiss_count] = tmp;
                }
                kiss_count++;
                if ( kiss_count > MAX_KISS_BUFF) {
                    kiss_count = MAX_KISS_BUFF;
                }
            }
        } else {
            if ( kiss_count > 0) {
                kiss_rcv = new byte[kiss_count-1];
                System.arraycopy(kiss_buff, 1, kiss_rcv, 0, kiss_rcv.length);
                kiss_ret = true;
            }
            kiss_count = 0;
        }
        tmp_prev = tmp;

        return kiss_ret;
    }
    
    /**
     * Method to get receive kiss
     * @return kiss_rcv
     */
    public byte[] get_kiss_rcv() {
        kiss_ret = false;
        return kiss_rcv;
    }

    /**
     *
     * @param x
     * @param cmd
     * @param port
     * @return y
     */
    public byte[] cmd(byte[] x, int cmd, int port) {
        port = port & 0x0f;
        cmd = cmd & 0x0f;
        byte[] y = new byte[3 + x.length];
        System.arraycopy(x, 0, y, 2, x.length);
        y[0] = FEND;
        y[1] = (byte) ((port << 4) | cmd);
        y[2 + x.length] = FEND;
        return y;
    }

    /**
     * method to send
     * @param x
     * @return y
     */
    public byte[] send(byte[] x) {
        byte[] y = new byte[3 + x.length];
        System.arraycopy(x, 0, y, 2, x.length);
        y[0] = FEND;
        y[1] = 0;
        y[2 + x.length] = FEND;
        return y;
    }

    /**
     * method to exit
     * @return y
     */
    public byte[] exit_kiss() {
        byte[] y = new byte[3];
        y[0] = FEND;
        y[1] = -1;
        y[2] = FEND;
        return y;
    }
}