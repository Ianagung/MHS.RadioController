/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;

/**
 *
 * @author Ian
 */

/**
 * 
 * This is StructRcvData class
 */
public class StructRcvData {

     /**
     * Reference Variables
     */
    private String typeDataRcv;
    private byte[] dataRcv;
    private String sDataRcv;
    private String fileName;
    private String directory_rcv;
    private String fullPath;
    private boolean valid;

     /**
     * Method to get type of received data
     * @param type
     */
    public StructRcvData(String type) {
        setTypeDataRcv(type);
        fileName = pilihFileName(getTypeDataRcv());
    }

    /**
     * @return the typeDataRcv
     */
    public String getTypeDataRcv() {
        return typeDataRcv;
    }

    /**
     * @param typeDataRcv the typeDataRcv to set
     */
    public void setTypeDataRcv(String typeDataRcv) {
        this.typeDataRcv = typeDataRcv;
    }

    /**
     * @return the dataRcv
     */
    public byte[] getDataRcv() {
        return dataRcv;
    }

    /**
     * @param dataRcv the dataRcv to set
     */
    public void setDataRcv(byte[] dataRcv) {
        this.dataRcv = dataRcv;
        if (isValid()) {
            prosesDataRcv(getDataRcv());
        }
    }

    /**
     * @return the directory_rcv
     */
    public String getDirectory_rcv() {
        return directory_rcv;
    }

    /**
     * @param directory_receive the directory_rcv to set
     */
    public void setDirectory_rcv(String directory_rcv) {
        this.directory_rcv = directory_rcv;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @param valid the valid to set
     */
    public void setValid(boolean valid) {
        this.valid = valid;
        fullPath = directory_rcv + "/" + fileName;
    }

    /**
     * @return the sDataRcv
     */
    public String getsDataRcv() {
        return sDataRcv;
    }

    /**
     * @param sDataRcv the sDataRcv to set
     */
    public void setsDataRcv(String sDataRcv) {
        this.sDataRcv = sDataRcv;
        if (isValid()) {
            prosesDataRcv(getsDataRcv().getBytes());
        }
    }

    /**
     *
     * @param data
     */
    public void prosesDataRcv(byte[] data) {
        try {
            Writer output;
            if (getTypeDataRcv().equals("track")){
            }
            output = new BufferedWriter(new FileWriter(fullPath, true));
            output.append(new String(data) + "\n");
            
            output.close();
        } catch (Exception e) {
        }
    }

    /**
     * Method to choose name file
     * @param type
     * @return filename
     */
    public String pilihFileName(String type) {
        String filename;
        switch (type) {
            case "file":
                filename = "LogFileMsgRcv.txt";
                break;
            case "txt":
                filename = "LogTxtMsgRcv.txt";
                break;
            case "circle":
                filename = "LogcircleMsgRcv.txt";
                break;
            case "polyline":
                filename = "LogPolylineMsgRcv.txt";
                break;
            case "other":
                filename = "LogOtherMsgRcv.txt";
                break;
            case "track":
                filename = "LogTRackRcv.txt";
                break;
            default:
                throw new IllegalArgumentException("Invalid data type: " + type);
        }
        return filename;
    }
}