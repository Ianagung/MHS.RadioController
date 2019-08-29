/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.common;

import org.len.tdl.core.DEF;
import org.len.tdl.tools_ryt.crc16_ccitt;

/**
 *
 * @author datalink
 */

/**
 * 
 * This is StructText class
 */
public class StructText {
    
     /**
     * Reference Variables
     */
    private boolean valid;
    private String stext;
    private int lengthText = 0;
    private int textNumber = 0;
    //private final DEF DEF = new DEF();
    private final crc16_ccitt crc16 = new crc16_ccitt();
    
    /**
     * Method to call StructText class
     */
    public StructText() {}
    
    /**
     * Method to checking crc of data
     * @param data
     */
    public StructText(byte[] data) {  
        
        int data_length = data.length;
        byte[] crc = crc16.compute(data, 0, data_length - 2); 
        
        if ( (crc[0] == data[data_length-2]) && (crc[1] == data[data_length-1])) {
            valid = true;
//            textNumber = (int) data[0] << 8 + data[1];
            textNumber = (int) data[0] * 256 + data[1];
            lengthText = data[2] * 256 + data[3];      
            stext = new String(data,4, getLengthText());            
        } 
        else 
        {
            valid = false;
        }
    }
        
    /**
     * Method to get bytes of Text data
     * @param s_text
     * @param text_number
     * @return dataText
     */
    public byte[] getBytesText(String s_text, int text_number) {        
        
        byte[] byte_text = s_text.getBytes();
        int text_length = byte_text.length;    
        int dt_length = 6 + text_length;
        int pack_length = DEF.PACK_LENGTH * (dt_length / DEF.PACK_LENGTH);
        if ((dt_length % DEF.PACK_LENGTH) != 0) {
             pack_length = pack_length + DEF.PACK_LENGTH;
        }
        
        byte[] dataText = new byte[pack_length];
        
        dataText[0] = (byte) ( (text_number >> 8) & 0xFF);
        dataText[1] = (byte) ( text_number & 0xFF);
        dataText[2] = (byte) ( (text_length >> 8) & 0xFF);
        dataText[3] = (byte) ( text_length & 0xFF);
        
        System.arraycopy(byte_text, 0, dataText, 4, byte_text.length);
        byte[] crc = crc16.compute(dataText, 0, pack_length - 2);
        dataText[pack_length - 2] = crc[0];
        dataText[pack_length - 1] = crc[1];
        
        return dataText;
    }
    
    /**
     * Method to checking validation of data
     * @return valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @return the textNumber
     */
    public int getTextNumber() {
        return textNumber;
    }

    /**
     * @return the lengthText
     */
    public int getLengthText() {
        return lengthText;
    }

    /**
     * @return the stext
     */
    public String getStext() {
        return stext;
    }   
}