/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.len.tdl.general_var.model_variabel;
import org.len.tdl.tools_ryt.*;
import org.len.tdl.common.*;
import org.len.tdl.modem.*;

/**
 *
 * @author riyanto
 */

/**
 * 
 * This is Datalink114_ryt class
 */
public class DataLink114_ryt {
     
    /**
     * Reference variables
     */
    private String directory_rx;
    private WTConnection2 conn_gps;
    private WTConnection2 conn_modem;
    private final kiss_class kiss = new kiss_class();
    private final crc16_ccitt crc16 = new crc16_ccitt();
    private final aes128_class crypto = new aes128_class();
    //private final DEF DEF = new DEF();
    public DataLink_Ftp ftp = new DataLink_Ftp();
    private model_variabel mod_var;
    private byte[] traktx = new byte[DEF.PACK_LENGTH];
    public String dtdma_skey = "Len Industri 442";
    private boolean multicast_send_trak = false;
    private int send_trak_length = 0;
    private List<DataListener> listeners = new ArrayList<DataListener>();
    
    private final AudioTx_Rev1 audioTx = new AudioTx_Rev1();
    //private final AudioRx audioRx = new AudioRx();
    private final AudioRx_Rev1 audioRx = new AudioRx_Rev1();
    private final PacketReceive decodeCallback = new PacketReceive();
    private final BpskRx125 bpsk_rx_125 = new BpskRx125();
    private final BpskRx500 bpsk_rx_500 = new BpskRx500();
    private final BpskRx2000_Rev1 bpsk_rx_2000 = new BpskRx2000_Rev1();
    
    private int selectedDeviceInputIndex = 0;
    private List<String> soundDevices;
    private int selectedDeviceOutputIndex = 0;
    private List<String> soundDevicesOutput;
    private int DATARATE = 0;


    


     /**
     * method to call DataLink114_ryt class with param
     * @param model
     */
    public DataLink114_ryt(model_variabel model) {
        tmout.start();
        timer_gps_update.start();
        mod_var = model;
        
        
        soundDevicesOutput = audioTx.ListAudioOutputDevices(selectedDeviceOutputIndex);
        //audioTx.playAudio();
        //audioRx.StartCapture(0);
        
        soundDevices = audioRx.ListAudioInputDevices(selectedDeviceInputIndex);
        audioRx.setCallback(decodeCallback);
    }
    
    public List<String> getListLineInput() {
        return soundDevices;
    }
    
    public List<String> getListLineOutput() {
        return soundDevicesOutput;
    }
    
    public void StartMonitoringLevelsOnMixer(String _MixerName) {        
        audioRx.StartMonitoringLevelsOnMixer(_MixerName);
    }       
    
    public void StartPlayingSound(String _MixerName) {
        audioTx.StartPlayingSound(_MixerName);
    }
    
    public class PacketReceive implements Decode_ICallback {

        @Override
        public void onBufferTx(double[] buf) {
            //onBufferTx_(buf);
        }

        @Override
        public void onBufferRx(double[] buf_i, double[] buf_q) {
            onBufferRx_(buf_i, buf_q);
        }

        @Override
        public void onPacketRx(byte[] buf, int length_data) {
            //onPacketRx_(buf, length_data);
        }
    }
    
    private final double[] tmp_scope = new double[PARAM.N_SCOPE_BB_48K];
    public double[] getDataScope()
    {
        double[] tmp = new double[PARAM.N_SCOPE_BB_48K];
        System.arraycopy(tmp_scope, 0, tmp, 0, PARAM.N_SCOPE_BB_48K);
        return tmp;
    }

    public void onBufferRx_(double[] buf_i, double[] buf_q) {
        if (buf_i == null) {
            return;
        }
             //double[] tmp = new double[PARAM.N_SCOPE_BB_48K];
        System.arraycopy(buf_i, 0, tmp_scope, 0, PARAM.N_SCOPE_BB_48K);
        //scopePanel.displayData(tmp);   
        
        bpsk_rx_125.received(buf_i);
        bpsk_rx_500.received(buf_i);
        bpsk_rx_2000.received(buf_i);
        
        int rx_flag = 0;
        byte[] data_rx = new byte[64];
        
        
        
        if (bpsk_rx_2000.isDataReceived())
        {
            data_rx = bpsk_rx_2000.getDataReceived(); 
            rx_flag = 1;            
        }
        
        if (bpsk_rx_500.isDataReceived())
        {
            data_rx = bpsk_rx_500.getDataReceived(); 
            rx_flag = 1;            
        }
                
        if (bpsk_rx_125.isDataReceived())
        {
            data_rx = bpsk_rx_125.getDataReceived(); 
            rx_flag = 1;            
        }
            
            // test data error
            //data_rx[10] = (byte) (((int)data_rx[10] ^ 0xFF) & 0xFF);
            
        if (rx_flag == 1) {    
            byte[] data = fec_decode(data_rx);            
            int npack = data.length / DEF.PACK_LENGTH;            
            msg_rx_pack = npack - 1;

            for (int i = 0; i < npack; i++) {
                byte[] rxdata = new byte[DEF.PACK_LENGTH];
                System.arraycopy(data, i * DEF.PACK_LENGTH, rxdata, 0, DEF.PACK_LENGTH);
                frame_rx_process(rxdata);
            }
            
            
            
        }
        
      
        //packet_decoder.process(demod.process2(buf_q));
    }
    
    public double[] getSymbolReal()
    {
        double[] s = new double[256];
        switch (DATARATE)
        {
            case 0:        
                s = bpsk_rx_125.getSymbolReal();
                break;
                
            case 1:        
                s = bpsk_rx_500.getSymbolReal();
                break;
                
            case 2:        
                s = bpsk_rx_2000.getSymbolReal();
                break;
        }
        return s;
    }
    
    public double[] getSymbolimaginary()
    {
        
        double[] s = new double[256];
        switch (DATARATE)
        {
            case 0:        
                s = bpsk_rx_125.getSymbolImaginary();
                break;
                
            case 1:        
                s = bpsk_rx_500.getSymbolImaginary();
                break;
                
            case 2:        
                s = bpsk_rx_2000.getSymbolImaginary();
                break;
        }
        return s;
    }
    
     /**
     * This method is used to add Data listeners
     * @param listener
     */
    public void addListener(DataListener listener) {
        listeners.add(listener);
    }
    
     /**
     * This method is used to add Data listeners
     * @param listener
     */
    void moveData(byte[] data, int type){
        for(DataListener listener : listeners){
            listener.dataReceive(data, type);
        }
    }
    
     /**
     * This method is used to move info listener
     * @param data 
     */
    void moveInfo(byte[] data){
        for(DataListener listener : listeners){
            listener.infoReceive(data);
        }
    }

     /**
     * This method is used to get data from modem connection and GPS connection
     * @param con_data
     */
    public void initialKoneksi(WTConnection2 con_data, WTConnection2 con_gps) {
        try {
            this.conn_modem = con_data;
            this.conn_gps = con_gps;

            conn_gps.serialport.addEventListener(new SerialPortEventListener() {
                @Override
                public void serialEvent(jssc.SerialPortEvent event) {
                    if (event.isRXCHAR() && event.getEventValue() > 0) {
                        try {
                            if (conn_gps.serialport.getInputBufferBytesCount() > 0) {
                                byte[] data_serial_gps = conn_gps.serialport.readBytes();
                                for (int i = 0; i < data_serial_gps.length; i++) {
                                    byte byte_gps = data_serial_gps[i];
                                    rcv_gps(byte_gps);
                                }
                            }
                        } catch (SerialPortException ex) {
                        }
                    }
                }
            });

            conn_modem.serialport.addEventListener(new SerialPortEventListener() {
                @Override
                public void serialEvent(jssc.SerialPortEvent event) {
                    if (event.isRXCHAR() && event.getEventValue() > 0) {
                        try {
                            if (conn_modem.serialport.getInputBufferBytesCount() > 0) {
                                byte[] data_serial_modem = conn_modem.serialport.readBytes();
                                for (int i = 0; i < data_serial_modem.length; i++) {
                                    byte byte_modem = data_serial_modem[i];
                                    if (ftp.ftp_enable) {
                                        ftp.rcv_file(byte_modem);
                                    } else {
                                        rcv_frame(byte_modem);
                                    }
                                }
                            }
                        } catch (SerialPortException ex) {
                        }
                    }

                }
            });

        } catch (SerialPortException ex) {
        }
    }

    
         /**
     * This method is used to get data from modem connection and GPS connection
     * @param con_data
     */
    public void initialModemKoneksi(WTConnection2 con_data) {
        try {
            this.conn_modem = con_data;
 

            conn_modem.serialport.addEventListener(new SerialPortEventListener() {
                @Override
                public void serialEvent(jssc.SerialPortEvent event) {
                    if (event.isRXCHAR() && event.getEventValue() > 0) {
                        try {
                            if (conn_modem.serialport.getInputBufferBytesCount() > 0) {
                                byte[] data_serial_modem = conn_modem.serialport.readBytes();
                                for (int i = 0; i < data_serial_modem.length; i++) {
                                    byte byte_modem = data_serial_modem[i];
                                    if (ftp.ftp_enable) {
                                        ftp.rcv_file(byte_modem);
                                    } else {
                                        rcv_frame(byte_modem);
                                    }
                                }
                            }
                        } catch (SerialPortException ex) {
                        }
                    }

                }
            });

        } catch (SerialPortException ex) {
        }
    }
    
         /**
     * This method is used to get data from modem connection and GPS connection
     * @param con_data
     */
    public void initialGpsKoneksi(WTConnection2 con_gps) {
        try {

            this.conn_gps = con_gps;

            conn_gps.serialport.addEventListener(new SerialPortEventListener() {
                @Override
                public void serialEvent(jssc.SerialPortEvent event) {
                    if (event.isRXCHAR() && event.getEventValue() > 0) {
                        try {
                            if (conn_gps.serialport.getInputBufferBytesCount() > 0) {
                                byte[] data_serial_gps = conn_gps.serialport.readBytes();
                                for (int i = 0; i < data_serial_gps.length; i++) {
                                    byte byte_gps = data_serial_gps[i];
                                    rcv_gps(byte_gps);
                                }
                            }
                        } catch (SerialPortException ex) {
                        }
                    }
                }
            });

       } catch (SerialPortException ex) {
        }
    }
    
    
    
     /**
     * Method to set timeout of received data
     */
    javax.swing.Timer tmout = new javax.swing.Timer(1000, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {

            for (int i = 1; i <= DEF.MAX_NPU; i++) {

                if (npu_life[i] > 0) {
                    npu_life[i] = npu_life[i] - 1;
                }

                for (int j = 0; j < DEF.MAX_TRAK; j++) {
                    trak_life[i][j]++;
                    if (trak_life[i][j] > DEF.NPU_TIMEOUT) {
                        mod_var.getTmp_trak_data_rx()[i][DEF.NDATA_TRAK * j + DEF.ATTB1_IDX] = 0;
                        mod_var.getTmp_trak_data_rx()[i][DEF.NDATA_TRAK * j + DEF.ATTB2_IDX] = 0;

                    }
                }
            }
        }
    });

    /**
     * Reference variables
     */
    private final int[][] trak_life = new int[DEF.MAX_NPU + 1][DEF.MAX_TRAK];
    public int[] npu_life = new int[DEF.MAX_NPU + 1];
    public final byte[] trak_data = new byte[DEF.NDATA_TRAK * DEF.MAX_TRAK];
    public int nitem_tx;
    private int dtdma_nitem; 
    public int lastnpu = 0;
    public int item_request = 4;
    public int item_counter = 0;
    public byte[][][] trackTx = new byte[DEF.MAX_NPU + 1][DEF.MAX_TRAK][DEF.PACK_LENGTH];

    /**
     * method to arraycopy tracks data to variable trackTx
     * @param trak_tmp
     * @param npu
     * @param trakno 
     */
    public void UpdateTrak(byte[] trak_tmp, int npu, int trakno) {
        System.arraycopy(trak_tmp, 0, trackTx[npu][trakno], 0, DEF.PACK_LENGTH); 
    }
     /**
      * method to clear tracks data from variable trackTx
      */
    public void ClearTrak() {
        trackTx = new byte[DEF.MAX_NPU + 1][DEF.MAX_TRAK][DEF.PACK_LENGTH]; 
    }
     /**
     * Reference variables
     */
    public int[] list_no_traks = new int[DEF.MAX_TRAK];
    public int[] list_no_npu = new int[DEF.MAX_NPU + 1];
    /**
     * method to encrypt data frame and send it to modem
     * @return txdata
     */
    public byte[] encript_frame() {

        byte[] aes_data_input = new byte[DEF.PACK_LENGTH];

        if (msg_toggle) {
            msg_toggle = false;
        } else {
            msg_toggle = true;
        }
        
        if ((msg_sent_idx != msg_tx_num) && msg_toggle) {
            
            //int txtlen = DEF.PACK_LENGTH * ((int) (msg_tx_len[msg_sent_idx] / DEF.PACK_LENGTH)) + DEF.PACK_LENGTH;
            //if (msg_tx_len[msg_sent_idx] % DEF.PACK_LENGTH == 0) {
            //    txtlen = txtlen - DEF.PACK_LENGTH;
            //}
            //System.arraycopy(msg_tx_header, msg_sent_idx * DEF.PACK_LENGTH, buf_fifo_tx, 0, DEF.PACK_LENGTH);
            //System.arraycopy(msg_tx_data, msg_sent_idx * DEF.MAX_MSG_LENGTH, buf_fifo_tx, DEF.PACK_LENGTH, txtlen);

            
            System.arraycopy(msg_tx_header, msg_sent_idx * DEF.PACK_LENGTH, buf_fifo_tx, 0, DEF.PACK_LENGTH);            
            int pkg_tx_len = (buf_fifo_tx[5] << 8 ) + buf_fifo_tx[6];           
            int txtlen = DEF.PACK_LENGTH * ((int) (pkg_tx_len / DEF.PACK_LENGTH)) + DEF.PACK_LENGTH;
            if (pkg_tx_len % DEF.PACK_LENGTH == 0) {
                txtlen = txtlen - DEF.PACK_LENGTH;
            }            
            System.arraycopy(msg_tx_data, msg_sent_idx * DEF.MAX_MSG_LENGTH, buf_fifo_tx, DEF.PACK_LENGTH, txtlen);

            
            nitem_tx = 1 + (txtlen / DEF.PACK_LENGTH);
            msg_sent_idx++;
            if (msg_sent_idx > DEF.MAX_NUM_MSG) {
                msg_sent_idx = 0;
            }
            update_progress_file_tx();
            
            byte[] tmpHeaderTx = new byte[DEF.PACK_LENGTH];
            System.arraycopy(buf_fifo_tx, 0, tmpHeaderTx, 0, 32);
            
            if (tmpHeaderTx[DEF.MSG_TYPE_IDX] == DEF.MSG_TYPE_ACK)
                moveData(tmpHeaderTx, DEF.TYPE_EVENT_ACK_TX);
            if (tmpHeaderTx[DEF.MSG_TYPE_IDX] == DEF.MSG_TYPE_TEXT)
                moveData(tmpHeaderTx, DEF.TYPE_EVENT_MSG_TX);
            

        } else {
            int idx_rqts = item_counter;
            item_counter = item_counter + dtdma_nitem;
            if (item_counter >= mod_var.getItem_request()) {
                item_counter = 0;
                nitem_tx = mod_var.getItem_request() - idx_rqts;
            } else {
                nitem_tx = dtdma_nitem;
            }

            for (int i = 0; i < nitem_tx; i++) {
                int trak = list_no_traks[i + idx_rqts];
                int npuuu = list_no_npu[i + idx_rqts];
                System.arraycopy(trackTx[npuuu][trak], 0, buf_fifo_tx, i * 32, 32);                
            }
            
            if (nitem_tx > 0)
            {
                byte[] tmpDataRx = new byte[DEF.PACK_LENGTH];
                System.arraycopy(buf_fifo_tx, 0, tmpDataRx, 0, 32);
                moveData(tmpDataRx, DEF.TYPE_EVENT_TRACK_TX);
            }
        }

        byte[] txdata = new byte[nitem_tx * DEF.PACK_LENGTH];
        for (int i = 0; i < nitem_tx; i++) {
            System.arraycopy(buf_fifo_tx, i * DEF.PACK_LENGTH, aes_data_input, 0, DEF.PACK_LENGTH);
            byte[] aes_data_out = crypto.block_encrypt(aes_data_input, dtdma_skey.getBytes());
            System.arraycopy(aes_data_out, 0, txdata, i * DEF.PACK_LENGTH, DEF.PACK_LENGTH);
        }
        return txdata;
    }
/**
 * method to send frame to modem
 * @param data_pack
 * @return length_tx
 */
    private final BCH_127_63_10 bch = new BCH_127_63_10();
    private byte[] fec_encode(byte[] x)
    {
        byte[] tmp = new byte[x.length * 2];
        bch.encode_data(x, tmp, x.length);
        return tmp;        
    }
    
    private byte[] fec_decode(byte[] x)
    {
        byte[] tmp = new byte[x.length / 2];
        bch.decode_data(x, tmp);
        return tmp;        
    }
    
    private int send_frame_to_modem(byte[] data_pack) {
        
        
        //System.out.println("SEND FRAME");
        byte[] data = fec_encode(data_pack);        
        int length_tx = audioTx.bit_packet(data, data.length);
        //System.out.println("SEND FRAME " + length_tx);
        /*
        int length_tx;
        byte[] data_modem = kiss.enc(data_pack);
        try {
            conn_modem.serialport.writeBytes(data_modem);
            length_tx = data_modem.length;
            lastnpu = mod_var.getOwnnpu();
            npu_life[lastnpu] = DEF.NPU_TIMEOUT;
        } catch (SerialPortException ex) {
            length_tx = 0;
        }
        */
        return length_tx;
    }
    
    
    
    
    
    
    
    /**
     * method to receive frame from modem connection
     * @param sdata_rx 
     */
    public void rcv_frame(byte sdata_rx) {
        
        if (kiss.dec(sdata_rx)) {

            byte[] data = kiss.get_kiss_rcv();
            int npack = data.length / DEF.PACK_LENGTH;            
            msg_rx_pack = npack - 1;

            for (int i = 0; i < npack; i++) {
                byte[] rxdata = new byte[DEF.PACK_LENGTH];
                System.arraycopy(data, i * DEF.PACK_LENGTH, rxdata, 0, DEF.PACK_LENGTH);
                frame_rx_process(rxdata);
            }
        }
    }
    /**
     * Reference variables
     */
    public byte[][] msg_rcv = new byte[DEF.MAX_NPU + 1][DEF.MAX_MSG_LENGTH];
    public int[] msg_rx_length = new int[DEF.MAX_NPU + 1];
    private final int[] msg_rx_ptr = new int[DEF.MAX_NPU + 1];
    private final byte[][] hdr_rcv = new byte[DEF.MAX_NPU + 1][DEF.MSG_HDR_LENGTH];
    private final byte[] readBuffer = new byte[DEF.PACK_LENGTH];
    private byte msg_rx_i = 0;
    private int msg_rx_pack = 0;
    private boolean msg_rx_new = false;

    /**
     * method is about receive frame process from modem connection
     * @param data_rcv 
     */
    private void frame_rx_process(byte[] data_rcv) {

        byte[] rx_dec = crypto.block_decrypt(data_rcv, dtdma_skey.getBytes());
        System.arraycopy(rx_dec, 0, readBuffer, 0, DEF.PACK_LENGTH);
        if (msg_rx_new == true) {
            System.arraycopy(readBuffer, 0, msg_rcv[lastnpu], msg_rx_ptr[lastnpu], DEF.PACK_LENGTH);
            msg_rx_ptr[lastnpu] = msg_rx_ptr[lastnpu] + DEF.PACK_LENGTH;
            if (msg_rx_ptr[lastnpu] >= msg_rx_length[lastnpu]) {
                msg_rx_new = false;
                msg_rx_ptr[lastnpu] = 0;
                display_msg_rcv();
            }
            msg_rx_i++;
            if (msg_rx_i == msg_rx_pack) {
                msg_rx_new = false;
                msg_rx_i = 0;
            }
        } else {

            byte[] crc16_cek = crc16.compute(readBuffer, 0, DEF.PACK_LENGTH_M2);
            if ((readBuffer[DEF.CRC1_IDX] == crc16_cek[0]) && (readBuffer[DEF.CRC2_IDX] == crc16_cek[1])) {
                lastnpu = (readBuffer[DEF.OWNNPU1_IDX] & 0xFF + (readBuffer[DEF.OWNNPU2_IDX] & 0xff) * 0x100);

                if ((lastnpu > 0) && (lastnpu <= DEF.MAX_NPU)) 
                {
                    npu_life[lastnpu] = DEF.NPU_TIMEOUT;

                    if (readBuffer[DEF.ATTB1_IDX] == DEF.ATTB_MSG) {

                        int dest_npu = (readBuffer[DEF.ADDR_RCV_IDX] & 0xFF);
                        if ((dest_npu >= 0) && (dest_npu <= DEF.MAX_NPU)) {                      
                            System.arraycopy(readBuffer, 0, hdr_rcv[lastnpu], 0, DEF.PACK_LENGTH);
                            int msg_i_pkg = (readBuffer[DEF.NO_PACK_IDX] & 0xFF);
                            int msg_num_pkg = (readBuffer[DEF.TOTAL_PACK_IDX] & 0xFF);
                            int msg_type = (readBuffer[DEF.MSG_TYPE_IDX] & 0xFF);
                            if (msg_type == DEF.MSG_TYPE_FTP) {
                                update_progress_file_rx(msg_i_pkg, msg_num_pkg);
                            }

                            readBuffer[DEF.ATTB1_IDX] = 0;
                            msg_rx_length[lastnpu] = (hdr_rcv[lastnpu][DEF.PACK_LEN1_IDX] & 0xFF) * 256 + (hdr_rcv[lastnpu][DEF.PACK_LEN2_IDX] & 0xFF);
                            msg_rx_new = true;
                            if (msg_i_pkg == 0) {
                                msg_rx_ptr[lastnpu] = 0;
                            }
                        } else {
                        }
                    } else {
                        msg_rx_new = false;
                        int i = ((readBuffer[DEF.TRAKNO1_IDX] & 0xff) + ((readBuffer[DEF.TRAKNO2_IDX] & 0xff) * 0x100));
                        System.arraycopy(readBuffer, 0, mod_var.getTmp_trak_data_rx()[lastnpu], DEF.NDATA_TRAK * i, DEF.NDATA_TRAK);
                        trak_life[lastnpu][i] = 0;                    
                        byte[] tmpDataRx = new byte[8 + DEF.PACK_LENGTH];
                        System.arraycopy(readBuffer, 0, tmpDataRx,8,DEF.PACK_LENGTH);
                        moveData(tmpDataRx, DEF.TYPE_EVENT_TRACK_RX);
                    
                    }
                } else {
                    for (int n = 0; n < DEF.PACK_LENGTH; n++) {
                    }
                }
            } else {
            }                                      
        }        
    }
    /**
     * Reference variables
     */
    //private final byte MSG_TYPE_FTP = 8;
    public boolean msg_rcv_ready = false;
    public String display_string = " ";

    /**
     * method to display received messages
     */
    private void display_msg_rcv() {

        byte rcv_address = hdr_rcv[lastnpu][DEF.ADDR_RCV_IDX];
        byte sender_address = hdr_rcv[lastnpu][DEF.MSG_OWNNPU_IDX];
        byte msg_data_type = hdr_rcv[lastnpu][DEF.MSG_TYPE_IDX];
        int msg_no_rx = hdr_rcv[lastnpu][DEF.MSG_NO_IDX];
        int objno_rx = (hdr_rcv[lastnpu][DEF.OBJ_NO2_IDX] & 0xFF) + 256 * (hdr_rcv[lastnpu][DEF.OBJ_NO1_IDX] & 0xFF);

        if ((rcv_address == 0) || (rcv_address == (byte) mod_var.getOwnnpu())) {

            byte[] msg = new byte[msg_rx_length[lastnpu]];
            System.arraycopy(msg_rcv[lastnpu], 0, msg, 0, msg_rx_length[lastnpu]);
            
            byte[] header = new byte[8];
            header[0] = sender_address;
            header[1] = rcv_address;
            header[2] = msg_data_type;
            header[3] = (byte) ( msg_no_rx >> 8 );
            header[4] = (byte) ( msg_no_rx & 0xFF );
            header[5] = (byte) ( objno_rx >> 8 );
            header[6] = (byte) ( objno_rx & 0xFF );
                    
            byte[] msgRcv = new byte[header.length + msg.length];
            System.arraycopy(header, 0, msgRcv, 0, header.length);
            System.arraycopy(msg, 0, msgRcv, header.length, msg.length);

            moveData(msgRcv, DEF.TYPE_EVENT_MSG_RX);

            msg_rcv_ready = true;
            String ack1 = "msg(" + msg_no_rx + ") oke";
            byte[] txtack = ack1.getBytes();
            if (msg_data_type > 1) {
            }
        }
    }

     /**
     * Reference variables
     */    
    public int msg_file_progress_tx = 0;
    public int msg_file_progress_rx = 0;

    /**
     * method to update progress of transmitted file
     */
    private void update_progress_file_tx() {
        byte msg_type = buf_fifo_tx[DEF.MSG_TYPE_IDX];
        if (msg_type == DEF.MSG_TYPE_FTP) {
            int no_pack = buf_fifo_tx[1] + 1;
            int jml_pack = buf_fifo_tx[2];
            msg_file_progress_tx = no_pack * 100 / jml_pack;
                       
            byte[] data_info = new byte[32];
            data_info[0] = (byte) DEF.TYPE_TX_INFO;
            data_info[1] = (byte) DEF.INFO_TYPE_FTP_PROGRESS;
            data_info[2] = (byte) no_pack;
            data_info[3] = (byte) jml_pack;
            moveInfo(data_info);            
        }
    }

    /**
     * method to update progress of received file
     * @param no_pack
     * @param jml_pack 
     */
    private void update_progress_file_rx(int no_pack, int jml_pack) {
        msg_file_progress_rx = (no_pack + 1) * 100 / jml_pack;
    }

     /**
     * Reference variables
     */
    public boolean msg_rx_exist = false;
    private boolean msg_toggle = false;
    public int msg_tx_num = 0;
    private int msg_sent_idx = 0;
    private final byte[] buf_fifo_tx = new byte[DEF.MAX_NUM_MSG * (DEF.MAX_NPU + 1) * DEF.PACK_LENGTH];
    public byte[] msg_tx_data = new byte[DEF.MAX_NUM_MSG * DEF.MAX_MSG_LENGTH];
    //public int[] msg_tx_len = new int[DEF.MAX_NUM_MSG];
    public byte[] msg_tx_header = new byte[DEF.MAX_NUM_MSG * DEF.PACK_LENGTH];
        
    /**
     * method to send a message via modem connection
     * @param msg
     * @param msg_type
     * @param owner
     * @param rcvadd 
     */
    public void sendmsg(byte[] msg, byte msg_type, byte owner, byte rcvadd) {

        int txt_length = msg.length; 
        int num_pkg_tx = (dtdma_nitem - 1) * DEF.PACK_LENGTH;
        int num_pkg;
        if (txt_length % num_pkg_tx == 0) {
            num_pkg = (txt_length / num_pkg_tx);
        } else {
            num_pkg = (txt_length / num_pkg_tx) + 1;
        }

        int k = 0;
        for (int i_pkg = 0; i_pkg < num_pkg; i_pkg++) {

            msg_tx_num++;
            if (msg_tx_num >= DEF.MAX_NUM_MSG) {
                msg_tx_num = 0;
            }

            int i = msg_tx_num - 1;
            int pkg_tx_len = 0;//msg_tx_len[i];
            if (i_pkg == (num_pkg - 1)) {
                pkg_tx_len = txt_length - (i_pkg * num_pkg_tx);
                //msg_tx_len[i] = txt_length - (i_pkg * num_pkg_tx);
            } else {
                //msg_tx_len[i] = num_pkg_tx;
                pkg_tx_len = num_pkg_tx;
            }
            
            

            byte[] msg_header = new byte[DEF.PACK_LENGTH];
            msg_header[0] = (byte) 0;
            msg_header[1] = (byte) i_pkg;
            msg_header[2] = (byte) num_pkg;
            msg_header[3] = (byte) ((txt_length >> 8) & 0xFF);
            msg_header[4] = (byte) (txt_length  & 0xFF);
            msg_header[5] = (byte) ((pkg_tx_len >> 8) & 0xFF);
            msg_header[6] = (byte) (pkg_tx_len  & 0xFF);
            msg_header[7] = (byte) 0;
            msg_header[8] = (byte) 0;
            msg_header[9] = (byte) msg_tx_num;
            msg_header[10] = (byte) msg_type;
            msg_header[DEF.ADDR_RCV_IDX] = rcvadd;
            msg_header[12] = (byte) 0;
            msg_header[13] = (byte) 0;
            msg_header[14] = (byte) 0;
            msg_header[DEF.ATTB1_IDX] = (byte) DEF.ATTB_MSG;
            msg_header[DEF.ATTB2_IDX] = (byte) 0;
            msg_header[DEF.OWNNPU1_IDX] = (byte) owner;
            msg_header[DEF.OWNNPU2_IDX] = (byte) 0;
            msg_header[21] = (byte) 0;
            msg_header[22] = (byte) 0;

            byte[] crc_hdr = crc16.compute(msg_header, 0, DEF.PACK_LENGTH_M2);
            msg_header[30] = (byte) crc_hdr[0];
            msg_header[31] = (byte) crc_hdr[1];

            System.arraycopy(msg_header, 0, msg_tx_header, i * DEF.MSG_HDR_LENGTH, DEF.MSG_HDR_LENGTH);
            System.arraycopy(msg, k, msg_tx_data, i * DEF.MAX_MSG_LENGTH, pkg_tx_len);
            k += pkg_tx_len;
        }       
    }
    
     /**
     * Reference variables
     */
    public int dtdma_ownnpu = 1;
    public int[][] dtdma_scenario = new int[DEF.MAX_NPU][3];
    public int dtdma_step = 2;
    private int dtdma_Tcycle = 500;
    public int dtdma_Tpkcg = 100;
    private final int dtdma_Toffset = 10;
    private boolean dtdma_txdone = false;
    public int dtdma_npu_txtime = 0;
    public boolean dtdma_syncronized = false;

    /**
     * Method to counting cycle time of dtdma
     * @return t1
     */
    public int Tcycle() {
        int t1 = 0;
        for (int i = 0; i < dtdma_step; i++) {
            t1 = t1 + dtdma_Tpkcg * dtdma_scenario[i][1] + dtdma_scenario[i][2];
        }
        dtdma_Tcycle = t1;
        return t1;
    }
    
     /**
     * Reference variables
     */
    private int npu_prev = 0;
    /**
     * method to get NPU who using ptt
     * @param npu 
     */
    private void fire_ptt_time(int npu) {   

        if (npu != npu_prev) {        
           
        
        
            byte[] data_info = new byte[32];
            data_info[0] = (byte) DEF.TYPE_TX_INFO;
            data_info[1] = (byte) DEF.INFO_TYPE_CURRENT_PTT;
            data_info[2] = (byte) npu;
            data_info[3] = (byte) npu_prev;   
            data_info[4] = (byte) 0xcc; 
            moveInfo(data_info);  
        }
        
        npu_prev = npu;        
    }
    /**
    * method to compute transmit time
    * @return txtm
    */
    private boolean txtime() {
        boolean txtm = false;
        int ncycle = (3600000 - dtdma_Toffset) / dtdma_Tcycle;
        long syncT = get_sync_time();
        int curstep = (int) ((double) (syncT - dtdma_Toffset) / dtdma_Tcycle);
        if (curstep >= ncycle) {
            curstep = 0;
        }
        int Tstart = dtdma_Toffset + curstep * dtdma_Tcycle;
        int Trelatif = (int) (syncT - Tstart);
        int t1 = 0;

        if (dtdma_syncronized) {
            for (int i = 0; i < dtdma_step; i++) {
                t1 = t1 + dtdma_Tpkcg * dtdma_scenario[i][1] + dtdma_scenario[i][2];
                if (t1 > Trelatif) {

                    mod_var.setDtdma_npu_txtime(dtdma_scenario[i][0]);
                    
                    fire_ptt_time(dtdma_scenario[i][0]);

                    if (dtdma_scenario[i][0] == mod_var.getOwnnpu()) {
                        if (!dtdma_txdone) {
                            dtdma_nitem = dtdma_scenario[i][1];
                            txtm = true;                            
                        }
                        dtdma_txdone = true;
                    } else {
                        dtdma_txdone = false;
                    }
                    break;
                }
            }
        }
        return txtm;
    }
     /**
     * Reference variables
     */
    public boolean dtdma_silent_mode = true;
    private boolean dtdma_inited = false;
    private boolean dtdma_stop = false;

     /**
     * Method to stop Dtdma
     */
    public void stopDtdma() {
        dtdma_stop = true;
    }
    
     /**
     * Thread to running Dtdma
     */
    public Thread thread_dtdma = new Thread() {
        @Override
        public void run() {
            if (!dtdma_inited) {
                Tcycle();
                dtdma_inited = true;
            }

            while (!dtdma_stop) {

                if (txtime() && (dtdma_silent_mode == false) ) {
                    send_frame_to_modem(encript_frame());
                }

                try {
                    Thread.sleep(5);
                } catch (Exception e1) {
                }
            }
            dtdma_stop = false;
        }
    };
    /**
    * method to get sync time
    * @return delta
    */
    private long get_sync_time() {
        long mst = System.currentTimeMillis();
        Date dNow = new Date(mst);
        int mnt = dNow.getMinutes();
        int sec = dNow.getSeconds();
        int msec = (int) (mst % 1000);
        int ms_now = (mnt * 60 + sec) * 1000 + msec;

        int delta = ms_now - delta_gps;
        if (delta >= 3600000) {
            delta = delta - 3600000;
        }
        if (delta < 0) {
            delta = delta + 3600000;
        }
        return delta;
    }
    /**
     * Reference variables
     */
    private int delta_gps = 0;
    public int[] delta_gps_time = new int[3];

    /**
     * method to set sync time
     * @param s0 
     */
    private void set_sync_time(String s0) {
 
        long mst = System.currentTimeMillis();
        Date dNow = new Date(mst);
        int pc_hours = dNow.getHours();
        int pc_mnt = dNow.getMinutes();
        int pc_sec = dNow.getSeconds();
        int pc_msec = (int) (mst % 1000);
        int ms_now = (pc_mnt * 60 + pc_sec) * 1000 + pc_msec;
        
        String val[] = s0.split(",");
        
        if (val.length > 1) 
        {  
                String gps_time_string = val[1];

                char[] gps_time_char = gps_time_string.toCharArray();

                if (gps_time_char.length >= 6) {
                
                int gps_jam = Integer.valueOf(String.copyValueOf(gps_time_char, 0, 2));
                int gps_menit = Integer.valueOf(String.copyValueOf(gps_time_char, 2, 2));
                int gps_detik = Integer.valueOf(String.copyValueOf(gps_time_char, 4, 2));

                delta_gps_time[0] = pc_hours - gps_jam;
                delta_gps_time[1] = pc_mnt - gps_menit;
                delta_gps_time[2] = pc_sec - gps_detik;
                }
        }
        
        if (val.length > 1) {

                String s2 = val[1];
                char[] s3 = s2.toCharArray();

                if (s3.length >= 6) {
                    s2 = String.copyValueOf(s3, 4, s3.length - 4);
                    double dsec = Double.valueOf(s2);
                    int msec = (int) (1000 * dsec);
                    s2 = String.copyValueOf(s3, 2, 2);
                    int mnt = Integer.valueOf(s2);
                    int ms_gps = 60000 * mnt + msec;

                    int delta = ms_now - ms_gps;
                    if (delta >= 3600000) {
                        delta = delta - 3600000;
                    }
                    if (delta < 0) {
                        delta = delta + 3600000;
                    }
                    delta_gps = delta;
                    dtdma_syncronized = true;
                }
            }
    }

    /**
     * Reference Variables
     */
    public boolean update_periodic = false;
    public int GPS_TIMEOUT = 15;
    private boolean gps_need_update = true;
    private int gps_tik = 0;

    /**
     * method to update gps timeout
     */
    private javax.swing.Timer timer_gps_update = new javax.swing.Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (update_periodic == true) {
                gps_tik = gps_tik + 1;
                if (gps_tik > GPS_TIMEOUT) {
                    gps_need_update = true;
                }
            }
        }
    });
    
    public void processUdpGps(byte[] data)
    {
        if (data.length > 0) 
        {
            for (int i = 0; i < data.length; i++) {
                byte byte_gps = data[i];
                    rcv_gps(byte_gps);
            }
        }
    }
    
    
    /**
     * Reference Variables
     */
    private int ptr_serial = 0;
    byte[] buffer_serial = new byte[258];
    byte[] byte_rx = new byte[3];
    /**
     * method to receive GPS data
     * @param byte_rcv 
    */
    private void rcv_gps(byte byte_rcv) {

        byte_rx[0] = byte_rx[1];
        byte_rx[1] = byte_rx[2];
        byte_rx[2] = byte_rcv;

        byte byte_tmp = byte_rx[0];
        if (gps_need_update == true) {

            if (byte_tmp == '$') {
                ptr_serial = 0;
            }

            if (byte_tmp == '*') {
                if ((buffer_serial[1] == 'G')
                        && (buffer_serial[2] == 'P')
                        && (buffer_serial[3] == 'R')
                        && (buffer_serial[4] == 'M')
                        && (buffer_serial[5] == 'C')) {

                    buffer_serial[0] = '$';

                    if (ptr_serial > 16) {                                       
                        byte[] gps2 = new byte[ptr_serial];
                        System.arraycopy(buffer_serial, 0, gps2, 0, gps2.length);

                        byte xor = gps2[1];
                        for (int i = 2; i < gps2.length; i++) {
                            xor ^= gps2[i];
                        }

                        String s6 = new String(gps2);
                        String s_xor = String.format("%02X", xor);
                        byte[] bcrc = new byte[2];
                        bcrc[0] = byte_rx[1];
                        bcrc[1] = byte_rx[2];
                        String tmp = new String(bcrc);
                        if (s_xor.equalsIgnoreCase(tmp)) {
                            set_sync_time(s6);
                            if (dtdma_syncronized == true) {
                                gps_need_update = false;
                                gps_tik = 0;
                            }
                        }
                    }
                }
            } else {               
                buffer_serial[ptr_serial] = byte_tmp;
                ptr_serial = (ptr_serial + 1) & 0xff;
            }
        }
    }

    /**
     * @return the directory_rx
     */
    public String getDirectory_rx() {
        return directory_rx;
    }

    /**
     * @param directory_rx the directory_rx to set
     */
    public void setDirectory_rx(String directory_rx) {
        this.directory_rx = directory_rx;
    }
    
    public int getLevelTx()
    {
        return audioTx.getLevel();
    }
    
     public int getTickTx()
    {
        return audioTx.getTick();
    }   
    
     public int getCounterTx()
    {
        return audioTx.getCounter();
    }   
     
    public int getTimeProcessTx()
    {
        return audioTx.getTimeProcess();
    }   
    
    public int getTimeSampleTx()
    {
        return audioTx.getTimeSample();
    }   
    
    public int getPersenTx()
    {
        return audioTx.getPersenTx();
    }   
     
     public void setTxVolume(int vol_persen)
     {
         audioTx.setVolume(vol_persen);
     }
    
    public int getLevelRx()
    {
        return audioRx.getLevel();
    }
    
     public int getTickRx()
    {
        return audioRx.getTick();
    }   
    
     public int getCounterRx()
    {
        return audioRx.getCounter();
    }   
     
    public int getTimeProcessRx()
    {
        return audioRx.getTimeProcess();
    }   
    
    public int getTimeSampleRx()
    {
        return audioRx.getTimeSample();
    }
    
    public int[] getInfoRx()
    {
       //bpsk_rx_125.getInfoRx();
        int[] r = new int[6];
        switch (DATARATE)
        {
            case 0:
                r = bpsk_rx_125.getInfoRx();
                break;
                
            case 1:
                r = bpsk_rx_500.getInfoRx();
                break;
                
            case 2:
                r = bpsk_rx_2000.getInfoRx();
                break;
        }
        return r;        
    }
    
    public int getStatusTx()
    {
        return audioTx.getStatusTx();
    }
    
    public void setPreambleLength(int val)
    {
        audioTx.setPreambleLength(val);
    }
        
    public void setPostambleLength(int val)
    {
        audioTx.setPostambleLength(val);
    }
    
    public void setDataRate(int val)
    {
        DATARATE = val;
        audioTx.setDataRate(val);
        //audioRx.setDataRate(val);
    }
    
    
}