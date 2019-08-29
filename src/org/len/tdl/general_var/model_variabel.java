/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.general_var;

import org.len.tdl.core.DEF;

/**
 *
 * @author novita
 */

/**
 * 
 * This is model_variabel class
 */
public class model_variabel {

     /**
     * Reference Variables
     */
    private byte[] tmp_trak_data;
    private boolean status_mc = false;
    private boolean mute = true;
    private int item_request = 4;
    private int ownnpu = 1;
    private byte[][] tmp_trak_data_rx;
    private byte[] tmp_send_trak;
    //private final DEF DEF = new DEF();
    private int dtdma_npu_txtime = 0;
    private byte[] outBuf;
    private boolean multicast_send_trak;
    private boolean multicast_send_msg;

     /**
     * Reference Variables of model_variable method
     */
    public model_variabel() {
        
        multicast_send_trak = true;
        multicast_send_msg = false;
        tmp_trak_data = new byte[DEF.NDATA_TRAK * DEF.MAX_TRAK];
        tmp_trak_data_rx = new byte[DEF.MAX_NPU + 1][DEF.MAX_TRAK * DEF.NDATA_TRAK];
        tmp_send_trak = new byte[DEF.LENGTH_TOPIC + DEF.MAX_NPU * DEF.MAX_TRAK * DEF.PACK_LENGTH];
        outBuf = new byte[3200];
    }

    /**
     * @return the tmp_trak_data
     */
    public byte[] getTmp_trak_data() {
        return tmp_trak_data;
    }

    /**
     * @param tmp_trak_data the tmp_trak_data to set
     */
    public void setTmp_trak_data(byte[] tmp_trak_data) {
        this.tmp_trak_data = tmp_trak_data;
    }

     /**
     * 
     * @param tmp_trak_data tmp_trak data to set
     * @param i 
     */
    public void setTmp_trak_data(byte tmp_trak_data, int i) {
        this.tmp_trak_data[i] = tmp_trak_data;
    }

    /**
     * @return the status_mc
     */
    public boolean isStatus_mc() {
        return status_mc;
    }

    /**
     * @param status_mc the status_mc to set
     */
    public void setStatus_mc(boolean status_mc) {
        this.status_mc = status_mc;
    }

    /**
     * @return the item_request
     */
    public int getItem_request() {
        return item_request;
    }

    /**
     * @param item_request the item_request to set
     */
    public void setItem_request(int item_request) {
        this.item_request = item_request;
    }

    /**
     * @return the ownnpu
     */
    public int getOwnnpu() {
        return ownnpu;
    }

    /**
     * @param ownnpu the ownnpu to set
     */
    public void setOwnnpu(int ownnpu) {
        this.ownnpu = ownnpu;
    }

    /**
     * @return the tmp_trak_data_rx
     */
    public byte[][] getTmp_trak_data_rx() {
        return tmp_trak_data_rx;
    }

    /**
     * @param tmp_trak_data_rx the tmp_trak_data_rx to set
     */
    public void setTmp_trak_data_rx(byte[][] tmp_trak_data_rx) {
        this.tmp_trak_data_rx = tmp_trak_data_rx;
    }

    /**
     * @return the tmp_send_trak
     */
    public byte[] getTmp_send_trak() {
        return tmp_send_trak;
    }

    /**
     * @param tmp_send_trak the tmp_send_trak to set
     */
    public void setTmp_send_trak(byte[] tmp_send_trak) {
        this.tmp_send_trak = tmp_send_trak;
    }

    /**
     * @return the mute
     */
    public boolean isMute() {
        return mute;
    }

    /**
     * @param mute the mute to set
     */
    public void setMute(boolean mute) {
        this.mute = mute;
    }

    /**
     * @return the dtdma_npu_txtime
     */
    public int getDtdma_npu_txtime() {
        return dtdma_npu_txtime;
    }

    /**
     * @param dtdma_npu_txtime the dtdma_npu_txtime to set
     */
    public void setDtdma_npu_txtime(int dtdma_npu_txtime) {
        this.dtdma_npu_txtime = dtdma_npu_txtime;
    }

    /**
     * @return the outBuf
     */
    public byte[] getOutBuf() {
        return outBuf;
    }

    /**
     * @param outBuf the outBuf to set
     */
    public void setOutBuf(byte[] outBuf) {
        this.outBuf = outBuf;
    }

    /**
     * @return the multicast_send_trak
     */
    public boolean isMulticast_send_trak() {
        return multicast_send_trak;
    }

    /**
     * @param multicast_send_trak the multicast_send_trak to set
     */
    public void setMulticast_send_trak(boolean multicast_send_trak) {
        this.multicast_send_trak = multicast_send_trak;
    }

    /**
     * @return the multicast_send_msg
     */
    public boolean isMulticast_send_msg() {
        return multicast_send_msg;
    }

    /**
     * @param multicast_send_msg the multicast_send_msg to set
     */
    public void setMulticast_send_msg(boolean multicast_send_msg) {
        this.multicast_send_msg = multicast_send_msg;
    }
}