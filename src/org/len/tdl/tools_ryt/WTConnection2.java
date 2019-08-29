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

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * 
 * This is WTConnection2 class
 */

public class WTConnection2 {
    
   /**
   * Reference Variables
   */
    public SerialPort serialport;
    String portName;
    boolean portOpen;
    final static int BAUDRATE = 2400;
    final static int FLOWCONTROL = SerialPort.FLOWCONTROL_NONE;
    final static int DATABITS = SerialPort.DATABITS_8;
    final static int STOPBITS = SerialPort.STOPBITS_1;
    final static int PARITY = SerialPort.PARITY_NONE;

     /**
     * Method to get ports of modem name
     * @param port_name
     */
    public WTConnection2(String port_name) {
        this.portName = port_name;
    }
    
    /**
     * Method to start connection of selected modem
     * @param brate
     */
    public void OpenConnection(int brate) {
        try {
            serialport = new SerialPort(portName);  
            serialport.openPort();
            serialport.setParams(brate, 
            SerialPort.DATABITS_8,
            SerialPort.STOPBITS_1,
            SerialPort.PARITY_NONE);
            portOpen = true;
        }
        catch (SerialPortException ex) {
            portOpen = false;
        }
    }
    
    /**
     * Method to stop connection of selected modem
     */
    public void CloseConnection() {
        try {
            serialport.closePort();
            portOpen = false;
        } catch (SerialPortException ex) {
        }
    }   
}