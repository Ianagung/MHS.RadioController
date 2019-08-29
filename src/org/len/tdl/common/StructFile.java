/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.common;

import java.io.FileOutputStream;
import org.len.tdl.core.DEF;
import org.len.tdl.tools_ryt.compress_class;
import org.len.tdl.tools_ryt.crc16_ccitt;

/**
 *
 * @author datalink2
 */

/**
 * 
 * This is StructFile class
 */

public class StructFile {

     /**
     * Reference Variables
     */
    private boolean valid;
    private String fileName;
    private boolean compressed;
    private compress_class cmp = new compress_class();
    private final crc16_ccitt crc16 = new crc16_ccitt();
    //private final DEF DEF = new DEF();

    /**
     * Method to call StructFile class
     */
    
    public StructFile() {
    }
    
    /**
     * Method to checking crc of data
     * @param data
     */
    public StructFile(byte[] data) {

        int data_length = data.length;
        byte[] crc = crc16.compute(data, 0, data_length - 2);

        if ((crc[0] == data[data_length - 2]) && (crc[1] == data[data_length - 1])) {

            byte compressed_rx = data[0];
            compressed = compressed_rx > 0;
            int file_length = (data[1] & 0xFF) * 256 + (data[2] & 0xFF);

            byte[] file_rx = new byte[file_length];
            int filename_length = data[3] & 0xFF;
            System.arraycopy(data, 4 + filename_length, file_rx, 0, file_rx.length);

            byte[] file_name = new byte[filename_length];
            System.arraycopy(data, 4, file_name, 0, filename_length);
            String sfilename_rx = new String(file_name);
           
            fileName = sfilename_rx;
            valid = true;
        } else {
            valid = false;
        }
    }

    /**
     *
     * @param data
     * @param directory_rx
     */
    public StructFile(byte[] data, String directory_rx) {

        int data_length = data.length;
        byte[] crc = crc16.compute(data, 0, data_length - 2);

        if ((crc[0] == data[data_length - 2]) && (crc[1] == data[data_length - 1])) {

            byte compressed_rx = data[0];
            int file_length = (data[1] & 0xFF) * 256 + (data[2] & 0xFF);

            byte[] file_rx = new byte[file_length];
            int filename_length = data[3] & 0xFF;
            System.arraycopy(data, 4 + filename_length, file_rx, 0, file_rx.length);

            byte[] file_name = new byte[filename_length];
            System.arraycopy(data, 4, file_name, 0, filename_length);
            String sfilename_rx = new String(file_name);
            
            if (compressed_rx == 1) {

                try {
                    byte[] file_decompress = cmp.decompress(file_rx);
                    FileOutputStream out = new FileOutputStream(directory_rx + "/" + sfilename_rx);
                    out.write(file_decompress);
                    out.close();

                } catch (Exception e) {

                }
            } else {
                try {
                    FileOutputStream out = new FileOutputStream(directory_rx + "/" + sfilename_rx);
                    out.write(file_rx);
                    out.close();
                } catch (Exception e) {

                }
            }

            fileName = sfilename_rx;
            valid = true;
        } else {
            valid = false;
        }
    }

    private byte[] msg_compress;

    /**
     * Method to get Bytes of File data
     * @param filename
     * @param data
     * @return file_bytes
     */
    public byte[] getBytesFile(String filename, byte[] data) {
        int file_length = data.length;
        byte compressed = 0;
        try {
            msg_compress = cmp.compress(data);
            if (msg_compress.length < data.length) {
                compressed = 1;
                file_length = msg_compress.length;
            } else {
                compressed = 0;
                file_length = data.length;
            }
        } catch (Exception e) {

        }

        int filename_length = filename.length();
        
        int pack_length = file_length + filename_length + 6;
        if ((pack_length % DEF.PACK_LENGTH) != 0) {
            pack_length = pack_length + DEF.PACK_LENGTH;
        }
        
        
        byte[] file_bytes = new byte[pack_length];
        byte[] filename_bytes = filename.getBytes();

        file_bytes[0] = compressed;
        file_bytes[1] = (byte) ((file_length >> 8) & 0xff);
        file_bytes[2] = (byte) (file_length & 0xff);
        file_bytes[3] = (byte) (filename_length & 0xff);
        int k = 4;
        System.arraycopy(filename_bytes, 0, file_bytes, k, filename_length);
        k += filename_length;

        if (compressed == 0) {
            System.arraycopy(data, 0, file_bytes, k, file_length);
        } else {
            System.arraycopy(msg_compress, 0, file_bytes, k, file_length);
        }
        k += file_length;

        byte[] crc = crc16.compute(file_bytes, 0, file_bytes.length - 2);
        file_bytes[file_bytes.length - 2] = crc[0];
        file_bytes[file_bytes.length - 1] = crc[1];

        return file_bytes;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the compressed
     */
    public boolean isCompressed() {
        return compressed;
    }

}