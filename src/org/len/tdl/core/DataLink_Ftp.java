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
import java.io.File;
import java.io.FileOutputStream;
import jssc.SerialPortException;
import org.len.tdl.tools_ryt.WTConnection2;
import org.len.tdl.tools_ryt.aes128_class;
import org.len.tdl.tools_ryt.compress_class;
import org.len.tdl.tools_ryt.crc16_ccitt;
import org.len.tdl.tools_ryt.crc8_class;
import org.len.tdl.tools_ryt.kiss_class;

/**
 *
 * @author riyanto
 */

/**
 * 
 * This is Datalink_Ftp class
 */
public class DataLink_Ftp {

     /**
     * Reference Variables
     */
    
    public int own_npu = 0;
    private final compress_class cmpres = new compress_class();
    private final kiss_class kiss = new kiss_class();
    private final crc8_class crc8bit = new crc8_class();
    private final crc16_ccitt crc16 = new crc16_ccitt();
    private final DEF DEF = new DEF();
    private byte[] file_compress;
    private final byte[] file_bytes = new byte[DEF.MAX_FTP_LENGTH + 264];
    public int encrypted = 0;
    public int compressed = 0;
    private byte dest_addr = 1;
    private final aes128_class aes = new aes128_class();
    public String ftp_skey = "";
    private WTConnection2 conn_modem;

     /**
     * Method to start Datalink ftp
     */
    public DataLink_Ftp() {
        timer_ftp.start();
    }

     /**
     * Method to set modem connection
     * @param conn
     */
    public void setConnection(WTConnection2 conn) {
        conn_modem = conn;
    }

     /**
     * Method to send file
     * @param filename
     * @param data
     * @param destination
     * @return file_length
     */
    public int send_file(String filename, byte[] data, byte destination) {

        dest_addr = destination;

        int file_length = 0;
        try {
            file_compress = cmpres.compress(data);
            if (file_compress.length < data.length) {
                compressed = 1;
                file_length = file_compress.length;
            } else {
                compressed = 0;
                file_length = data.length;
            }
        } catch (Exception e) {
        }


        int file_length_tx = 0;
        if (compressed == 1) {
            file_length_tx = bundle_file(filename, file_compress);
        } else {
            file_length_tx = bundle_file(filename, data);
        }


        packet_data(file_length_tx, destination);

        ftp_count_part_tx = 0;
        ftp_tik = DEF.FTP_TIMEOUT;
        ftp_run = true;
        ftp_ack_oke = true;

        return file_length;
    }
    
    /**
     * Method to bundle files
     * @param filename
     * @param data
     * @return k
     */
    private int bundle_file(String filename, byte[] data) {

        int file_length = data.length;
             
        int filename_length = filename.length();
        byte[] filename_bytes = filename.getBytes();

        file_bytes[0] = (byte) 1;
        file_bytes[1] = (byte) (compressed + (encrypted << 1));
        file_bytes[5] = (byte) (filename_length & 0xff);
        byte[] crc = crc16.compute(data, 0, data.length);
        file_bytes[6] = crc[0];
        file_bytes[7] = crc[1];
        int k = 8;
        System.arraycopy(filename_bytes, 0, file_bytes, k, filename_length);
        k += filename_length;

        if (encrypted == 1) {
            byte[] data_enc = aes.file_encrypt(data, ftp_skey.getBytes());
            file_length = data_enc.length;
            System.arraycopy(data_enc, 0, file_bytes, k, file_length);
        } else {
            System.arraycopy(data, 0, file_bytes, k, file_length);
        }
        k += file_length;

        file_bytes[2] = (byte) ((file_length >> 16) & 0xff);
        file_bytes[3] = (byte) ((file_length >> 8) & 0xff);
        file_bytes[4] = (byte) (file_length & 0xff);

        return k;
    }

     /**
     * Reference Variables
     */
    private byte[] _pack_buff_tx;
    private int[] _pack_info_length_tx;
    private int data_length = 1024;
    private int ftp_num_packet = 6;

     /**
     *
     * @param data_length_tx
     * @param dest
     */
    public void packet_data(int data_length_tx, byte dest) {
        int pack_length = data_length + DEF.FTP_HDR_LENGTH;
        int num_pack = (int) Math.ceil((double) data_length_tx / (double) data_length);

        int data_length_rem = data_length_tx - (num_pack - 1) * data_length;
        int pack_length_tx = (num_pack - 1) * pack_length + data_length_rem + DEF.FTP_HDR_LENGTH;

        _pack_buff_tx = new byte[pack_length_tx];
        _pack_info_length_tx = new int[num_pack];

        for (int i = 0; i < (num_pack - 1); i++) {
            _pack_info_length_tx[i] = data_length;
        }
        _pack_info_length_tx[num_pack - 1] = data_length_rem;

        int idx_source = 0;
        int idx_dest = 0;

        byte[] ftp_header = new byte[DEF.FTP_HDR_LENGTH];

        for (int i = 0; i < num_pack; i++) {
            int copy_length = _pack_info_length_tx[i];

            ftp_header[0] = (byte) 0;
            ftp_header[1] = (byte) i;
            ftp_header[2] = (byte) num_pack;
            ftp_header[3] = (byte) ((copy_length >> 8) & 0xff);
            ftp_header[4] = (byte) (copy_length & 0xff);

            byte[] crc = crc16.compute(file_bytes, idx_source, copy_length);
            ftp_header[5] = crc[0];
            ftp_header[6] = crc[1];
            ftp_header[7] = (byte) 0; 
            ftp_header[8] = (byte) 0; 
            ftp_header[9] = (byte) 0; 
            ftp_header[10] = (byte) 8; 
            ftp_header[11] = (byte) dest;
            ftp_header[12] = (byte) 101; 
            ftp_header[13] = (byte) own_npu;
            ftp_header[14] = (byte) 0;
            ftp_header[15] = (byte) crc8bit.compute(ftp_header, 15);

            System.arraycopy(ftp_header, 0, _pack_buff_tx, idx_dest, DEF.FTP_HDR_LENGTH);

            System.arraycopy(file_bytes, idx_source, _pack_buff_tx, idx_dest + DEF.FTP_HDR_LENGTH, copy_length);

            idx_source += copy_length;
            idx_dest += (copy_length + DEF.FTP_HDR_LENGTH);
        }

        ftp_num_packet = num_pack;

    }

     /**
     * Reference Variables
     */
    private int ftp_tik = 0;
    public boolean ftp_run = false;
    private boolean ftp_next_pack = false;
    private int ftp_count_part_tx = 0;
    private boolean ftp_ack_oke = false;
    private int ftp_ack_not_oke_counter = 0;
    public boolean ftp_enable = false;
    public int ftp_progress_tx = 0;
    public int ftp_progress_rx = 0;
    
     /**
     * Method to set time ftp
     */
    javax.swing.Timer timer_ftp = new javax.swing.Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            ftp_tik++;
            if ((ftp_tik > DEF.FTP_TIMEOUT) && ftp_run) {
                ftp_tik = 0;

                if (ftp_count_part_tx <= ftp_num_packet) {

                    if (ftp_ack_oke || (dest_addr == 0)) {

                            ftp_ack_oke = false;

                        ftp_count_part_tx++;

                        if (ftp_count_part_tx > ftp_num_packet) {
                            ftp_run = false;
                        }
                    } else {
                        ftp_ack_not_oke_counter++;
                        if (ftp_ack_not_oke_counter > DEF.FTP_MAX_REPLAY) {
                            ftp_ack_not_oke_counter = 0;
                            ftp_run = false;
                        }
                    }

                    
                    if (ftp_run == true) {
                        
                        ftp_progress_tx = (ftp_count_part_tx * 100) / ftp_num_packet;

                        int pack_length = _pack_info_length_tx[ftp_count_part_tx - 1] + DEF.FTP_HDR_LENGTH;
                        byte[] data_tx = new byte[pack_length];
                        System.arraycopy(_pack_buff_tx, (ftp_count_part_tx - 1) * (data_length + DEF.FTP_HDR_LENGTH), data_tx, 0, pack_length);

                        ftp_send_to_modem(data_tx);
                    }

                }

            }

        }
    });

     /**
     * Method to send ftp to modem
     * @param data_pack
     * @return length_tx
     */
    private int ftp_send_to_modem(byte[] data_pack) {

        byte[] data_kiss = kiss.enc(data_pack);

        try {

            conn_modem.serialport.writeBytes(data_kiss);
        } catch (SerialPortException e2) {
        }

        int length_tx = data_kiss.length;
        return length_tx;
    }
    
    /**
     * Reference Variables
     */
    private byte[] ftp_buff_rx = new byte[DEF.MAX_FTP_LENGTH];
    private int ftp_counter_rx = 0;
    private int ftp_i_pack_prev = 0;

     /**
     * Method to receive file
     * @param sdata_rx
     */
    public void rcv_file(byte sdata_rx) {

        if (kiss.dec(sdata_rx)) {
            byte[] data_rcv = kiss.get_kiss_rcv();

            byte crc_header = (byte) crc8bit.compute(data_rcv, 15);

            if (crc_header == data_rcv[15]) {

                byte i_pack_rx = data_rcv[1];
                byte num_pack_rx = data_rcv[2];
                int pack_length_rx = ((int) (data_rcv[3] & 0xff) << 8) + (int) (data_rcv[4] & 0xff);
                byte datatype_rx = data_rcv[10];
                byte dest_rx = data_rcv[11];
                byte attb_rx = data_rcv[12];
                byte sender_rx = data_rcv[13];

                byte[] crc = crc16.compute(data_rcv, DEF.FTP_HDR_LENGTH, pack_length_rx);

                if (attb_rx == DEF.ATTB_FTP) {
                    if ((dest_rx == own_npu) || (dest_rx == 0)) {

                        switch (datatype_rx) {
                            case 0:
                                ftp_ack_oke = false;
                                break;

                            case 1:
                                ftp_ack_oke = true;
                                break;

                            case 8:
                                
                                ftp_progress_rx = (i_pack_rx + 1) * 100 / num_pack_rx;

                                if (i_pack_rx == 0) {
                                    ftp_counter_rx = 0;
                                }

                                if (i_pack_rx == (num_pack_rx - 1)) {
                                    
                                    System.arraycopy(data_rcv, DEF.FTP_HDR_LENGTH, ftp_buff_rx, ftp_counter_rx, pack_length_rx);

                                    extract_ftp_rx();

                                } else {
                                    System.arraycopy(data_rcv, DEF.FTP_HDR_LENGTH, ftp_buff_rx, ftp_counter_rx, pack_length_rx);
                                    ftp_counter_rx = (i_pack_rx + 1) * pack_length_rx;
                                }

                                byte ack_rx;
                                byte[] crc_rx = crc16.compute(data_rcv, DEF.FTP_HDR_LENGTH, pack_length_rx);
                                if ((crc_rx[0] == data_rcv[5]) && (crc_rx[1] == data_rcv[6])) {
                                    ack_rx = (byte) 1;
                                } else {
                                    ack_rx = (byte) 0;
                                }

                                if (dest_rx > 0) {
                                    ftp_send_ack(i_pack_rx, num_pack_rx, sender_rx, ack_rx);
                                }
                                break;
                        }

                    }
                }
            } else {
           }
        }
    }

     /**
     * Directory receive variable
     */
    public String directory_rx = "C:/DataLink_Received_File";

     /**
     * Method to decompress received ftp
     */
    private void extract_ftp_rx() {

        int compressed_rx = ftp_buff_rx[1] & 0x01;
        int encrypted_rx = (ftp_buff_rx[1] & 0x03) >> 1;
        int file_length_rx = ((int) (ftp_buff_rx[2] & 0xff) << 16) + ((int) (ftp_buff_rx[3] & 0xff) << 8) + (int) (ftp_buff_rx[4] & 0xff);
        int filename_length_rx = ftp_buff_rx[5] & 0xff;
        byte crc0_rx = ftp_buff_rx[6];
        byte crc1_rx = ftp_buff_rx[7];
        byte[] filename_rx = new byte[filename_length_rx];
        System.arraycopy(ftp_buff_rx, 8, filename_rx, 0, filename_length_rx);
        String sfilename_rx = new String(filename_rx);
        byte[] file_rx = new byte[file_length_rx];
        System.arraycopy(ftp_buff_rx, 8 + filename_length_rx, file_rx, 0, file_length_rx);

        byte[] data_dec = new byte[file_length_rx];
        System.arraycopy(file_rx, 0, data_dec, 0, file_length_rx);

        if (encrypted_rx == 1) {
            data_dec = aes.file_decrypt(file_rx, ftp_skey.getBytes());
        }

        byte[] crc = crc16.compute(data_dec, 0, data_dec.length);
        if ((crc[0] == crc0_rx) && (crc[1] == crc1_rx)) {

            File received_folder = new File(directory_rx);
            if (!received_folder.exists()) {

                try {
                    received_folder.mkdir();
                } catch (SecurityException se) {

                }
            }

            if (compressed_rx == 1) {
                try {
                    byte[] file_decompress = cmpres.decompress(data_dec);
                    FileOutputStream out = new FileOutputStream(directory_rx + "/" + sfilename_rx);
                    out.write(file_decompress);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    FileOutputStream out = new FileOutputStream(directory_rx + "/" + sfilename_rx);
                    out.write(data_dec);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
          } else {
       }
}

     /**
     * Method to send ftp acknowledge
     * @param pack_no
     * @param num_pack
     * @param dest
     * @param ack 
     */
    private void ftp_send_ack(byte pack_no, byte num_pack, byte dest, byte ack) {

        byte[] data_tx = new byte[16];

        data_tx[0] = (byte) 0;
        data_tx[1] = pack_no;
        data_tx[2] = num_pack;
        data_tx[3] = (byte) 0;
        data_tx[4] = (byte) 0;
        data_tx[5] = (byte) 0;
        data_tx[6] = (byte) 0;
        data_tx[7] = (byte) 0;
        data_tx[8] = (byte) 0;
        data_tx[9] = (byte) 0;
        data_tx[10] = ack;
        data_tx[11] = (byte) dest;
        data_tx[12] = (byte) 101;
        data_tx[13] = (byte) own_npu;
        data_tx[14] = (byte) 0;
        data_tx[15] = (byte) crc8bit.compute(data_tx, 15);

        ftp_send_to_modem(data_tx);

    }
}