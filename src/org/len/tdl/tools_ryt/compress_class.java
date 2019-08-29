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

 import java.io.ByteArrayOutputStream;  
 import java.io.IOException;  
 import java.util.zip.DataFormatException;  
 import java.util.zip.Deflater;  
 import java.util.zip.Inflater; 

/**
 *
 * This is compress class
 */
public class compress_class {

    /**
     * Method to compress the data
     * @param data
     * @return output
     * @throws IOException
     */
    public byte[] compress(byte[] data) throws IOException {  

        Deflater deflater = new Deflater();  
        deflater.setInput(data);  
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);   
        deflater.finish(); 
        
        byte[] buffer = new byte[1024];   
        while (!deflater.finished()) {  
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);   
        }  

        outputStream.close();  
        byte[] output = outputStream.toByteArray();

        return output;
    }  

    /**
     * Method to decompress the data
     * @param data
     * @return output
     * @throws IOException
     * @throws DataFormatException
     */
    public byte[] decompress(byte[] data) throws IOException, DataFormatException {  
        Inflater inflater = new Inflater();   
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);  
        byte[] buffer = new byte[1024];  

        while (!inflater.finished()) {  
            int count = inflater.inflate(buffer);  
            outputStream.write(buffer, 0, count);  
        }  

        outputStream.close();  
        byte[] output = outputStream.toByteArray();  

        return output;  
    }  
}