/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.len.tdl.share;

import jssc.SerialPort;
import jssc.SerialPortException;
/**
 *
 * @author riyanto
 */
public class SerialConnection {
    
    public SerialPort serialport;
    String portName;
    boolean portOpen;
    final static int BAUDRATE = 2400;
    final static int FLOWCONTROL = SerialPort.FLOWCONTROL_NONE;
    final static int DATABITS = SerialPort.DATABITS_8;
    final static int STOPBITS = SerialPort.STOPBITS_1;
    final static int PARITY = SerialPort.PARITY_NONE;

    
    public SerialConnection() {
    }
    
    
     /**
     * Method to get ports of modem name
     * @param port_name
     */
    public SerialConnection(String port_name) {
        this.portName = port_name;
    }
    
    public void setPortName(String port_name) {
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
    
    
//import jssc.SerialPort;
//import jssc.SerialPortEvent;
//import jssc.SerialPortEventListener;
//import jssc.SerialPortException;
// 
//public class Main {
// 
//    static SerialPort serialPort;
// 
//    public static void main(String[] args) {
//        serialPort = new SerialPort("COM1"); 
//        try {
//            serialPort.openPort();
//            serialPort.setParams(9600, 8, 1, 0);
//            //Preparing a mask. In a mask, we need to specify the types of events that we want to track.
//            //Well, for example, we need to know what came some data, thus in the mask must have the
//            //following value: MASK_RXCHAR. If we, for example, still need to know about changes in states 
//            //of lines CTS and DSR, the mask has to look like this: SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR
//            int mask = SerialPort.MASK_RXCHAR;
//            //Set the prepared mask
//            serialPort.setEventsMask(mask);
//            //Add an interface through which we will receive information about events
//            serialPort.addEventListener(new SerialPortReader());
//        }
//        catch (SerialPortException ex) {
//            System.out.println(ex);
//        }
//    }
// 
//    static class SerialPortReader implements SerialPortEventListener {
// 
//        public void serialEvent(SerialPortEvent event) {
//            //Object type SerialPortEvent carries information about which event occurred and a value.
//            //For example, if the data came a method event.getEventValue() returns us the number of bytes in the input buffer.
//            if(event.isRXCHAR()){
//                if(event.getEventValue() == 10){
//                    try {
//                        byte buffer[] = serialPort.readBytes(10);
//                    }
//                    catch (SerialPortException ex) {
//                        System.out.println(ex);
//                    }
//                }
//            }
//            //If the CTS line status has changed, then the method event.getEventValue() returns 1 if the line is ON and 0 if it is OFF.
//            else if(event.isCTS()){
//                if(event.getEventValue() == 1){
//                    System.out.println("CTS - ON");
//                }
//                else {
//                    System.out.println("CTS - OFF");
//                }
//            }
//            else if(event.isDSR()){
//                if(event.getEventValue() == 1){
//                    System.out.println("DSR - ON");
//                }
//                else {
//                    System.out.println("DSR - OFF");
//                }
//            }
//        }
//    }
//}
    
    
    
}