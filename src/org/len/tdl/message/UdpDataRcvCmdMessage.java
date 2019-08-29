/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.message;

import org.len.ccad.baseinterface.AbstractMessage;

/**
 *
 * @author DELL1
 */

/**
 * 
 * This is UdpDataRcvCmdMessage class
 */
public class UdpDataRcvCmdMessage extends AbstractMessage {
        
     /**
     * Reference Variables
     */
    private byte[] aData;
    private int aType;
     
    /**
     * 
     * @param aData
     * @param aType 
     */
    public UdpDataRcvCmdMessage(byte[] aData, int aType){
        this.aData = aData;
        this.aType = aType;
    }

    /**
     * @return the aData
     */
    public byte[] getaData() {
        return aData;
    }

    /**
     * @param aData the aData to set
     */
    public void setaData(byte[] aData) {
        this.aData = aData;
    }

    /**
     * @return the aType
     */
    public int getaType() {
        return aType;
    }

    /**
     * @param aType the aType to set
     */
    public void setaType(int aType) {
        this.aType = aType;
    }    
}