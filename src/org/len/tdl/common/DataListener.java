/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.common;

/**
 *
 * @author datalink2
 */


public interface DataListener{

    /**
     *
     * @param data
     * @param type
     */
    void dataReceive(byte[] data, int type);

    /**
     *
     * @param data
     */
    void infoReceive(byte[] data);
}