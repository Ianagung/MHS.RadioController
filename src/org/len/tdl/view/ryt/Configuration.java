/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.len.tdl.view.ryt;

/**
 *
 * @author APS
 */
public class Configuration {    
    public static int ownNpu = 1;
    public static String modemPort = "COM3";
    public static int modemBaudrate = 9600;
    public static String gpsPort = "COM4";
    public static int gpsBaudrate = 4800;
    public static int gpsUpdateInterval = 60;
    public static String key = "LenIndustri442";
    public static int modemDatarate = 1200;
    public static String udpTxAddress = "localhost";
    public static int udpTxPort = 1111;
    public static String udpRxAddress = "localhost";
    public static int udpRxPort = 1112;   
    public static String udpGpsAddress = "localhost";
    public static int udpGpsPort = 4242;
    public static String udpLoggerAddress = "localhost";
    public static int udpLoggerPort = 1002;
    public static String npuList = "1,2,3,4";
    public static String itemList = "4,4,4,4";
    public static String delayList = "200,200,200,200";
    public static boolean silentMode = true;
    public static boolean autorun = true;    
    public static int modeSync = 0;   
    public static boolean udpLoggerOn = true; 
}
