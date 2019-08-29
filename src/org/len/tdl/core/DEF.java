/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.core;

/**
 *
 * @author riyanto
 */

/**
 * 
 * This is Datalink_Constanta class
 */
public class DEF {
    
    public static final boolean NILAI_TRUE = true;
    public static final boolean NILAI_FALSE = false;
    
//<editor-fold defaultstate="collapsed" desc="KONSTANTA ">
    public static final int NILAI_MIN_2=-2;
    public static final int NILAI_MIN_1=-1;
    public static final int NILAI_MIN_0=0;
    public static final int NILAI_1=1;
    public static final int NILAI_2=2;
    public static final int NILAI_3=3;
    public static final int NILAI_4=4;
    public static final int NILAI_5=5;
    public static final int NILAI_6=6;
    public static final int NILAI_7=7;
    public static final int NILAI_8=8;
    public static final int NILAI_9=9;
//</editor-fold>
    
    // GENERAL
    public static final int MAX_NPU                        = 30;
    public static final int MAX_TRAK                       = 702;
    public static final int NDATA_TRAK                     = 32;//13;
    public static final int PACK_LENGTH                    = 32;//16;
    public static final int PACK_LENGTH_M2                 = 30;//15;
    public static final int NPU_TIMEOUT                    = 60; // detik
    public static final int MAX_MSG_LENGTH                 = 10240; // 10 kB
    public static final int MAX_NUM_MSG                    = 255;
    public static final byte MSG_ALL_ADDR                  = 0; // broad cast
    public static final int MSG_HDR_LENGTH                 = 32;//16;
    public static final int MAX_OBJ                        = 1000;
    public static final int MAX_TEXT_LENGTH                = 240;
    public static final int MAX_FILE_LENGTH                = 8192; //8 kB
    
  
    // TRACK INDEX
    public static final int LON1_IDX                      = 0;
    public static final int LON2_IDX                      = 1;
    public static final int LON3_IDX                      = 2;
    public static final int LON4_IDX                      = 3;
    public static final int LAT1_IDX                      = 4;
    public static final int LAT2_IDX                      = 5;
    public static final int LAT3_IDX                      = 6;
    public static final int LAT4_IDX                      = 7;
    public static final int SPD1_IDX                      = 8;
    public static final int SPD2_IDX                      = 9;
    public static final int SPD3_IDX                      = 10;
    public static final int CRS1_IDX                      = 11;
    public static final int CRS2_IDX                      = 12;
    public static final int CRS3_IDX                      = 13;
    public static final int HGT1_IDX                      = 14;
    public static final int HGT2_IDX                      = 15;
    public static final int HGT3_IDX                      = 16;
    public static final int ATTB1_IDX                     = 17;
    public static final int ATTB2_IDX                     = 18;
    public static final int OWNNPU1_IDX                   = 19;
    public static final int OWNNPU2_IDX                   = 20;
    public static final int TRAKNO1_IDX                   = 21;
    public static final int TRAKNO2_IDX                   = 22;
    public static final int MMSI1_IDX                     = 23;
    public static final int MMSI2_IDX                     = 24;
    public static final int MMSI3_IDX                     = 25;
    public static final int MMSI4_IDX                     = 26;
    public static final int CRC1_IDX                      = 30;
    public static final int CRC2_IDX                      = 31;
    
    
    // MSG INDEX
    public static final int MSG_CODE_IDX                  = 0;
    public static final int NO_PACK_IDX                   = 1;
    public static final int TOTAL_PACK_IDX                = 2;
    public static final int PACK_LEN1_IDX                 = 3;
    public static final int PACK_LEN2_IDX                 = 4;
    public static final int MSG_CRC1_IDX                  = 5;
    public static final int MSG_CRC2_IDX                  = 6;
    public static final int OBJ_NO1_IDX                   = 7;
    public static final int OBJ_NO2_IDX                   = 8;
    public static final int MSG_NO_IDX                    = 9;
    public static final int MSG_TYPE_IDX                  = 10;
    public static final int ADDR_RCV_IDX                  = 11;
    public static final int GRP_RCV_IDX                   = 12;
    public static final int MSG_LENGTH1_IDX               = 13; // LSB
    public static final int MSG_LENGTH2_IDX               = 14; 
    public static final int MSG_ATTB_IDX                  = 17; // 15
    public static final int MSG_OWNNPU_IDX                = 19; //17
   
  
    
    // ATTRIBUTE NAME
    public static final int ATTB_OWN_SHIP                 = 1;
    public static final int ATTB_ASSFRIEND_AIR            = 2;
    public static final int ATTB_ASSFRIEND_SUBSURFACE     = 3;
    public static final int ATTB_ASSFRIEND_SURFACE        = 4;
    public static final int ATTB_FRIEND_AIR               = 5;
    public static final int ATTB_FRIEND_SUBSURFACE        = 6;
    public static final int ATTB_FRIEND_SURFACE           = 7;
    public static final int ATTB_HOSTILE_AIR              = 8;
    public static final int ATTB_HOSTILE_SUBSURFACE       = 9;
    public static final int ATTB_HOSTILE_SURFACE          = 10;    
    public static final int ATTB_NEUTRAL_AIR              = 11;
    public static final int ATTB_NEUTRAL_SUBSURFACE       = 12;
    public static final int ATTB_NEUTRAL_SURFACE          = 13;     
    public static final int ATTB_SUSPECT_AIR              = 14;
    public static final int ATTB_SUSPECT_SUBSURFACE       = 15;
    public static final int ATTB_SUSPECT_SURFACE          = 16;
    public static final int ATTB_UNKNOWN_AIR              = 17;
    public static final int ATTB_UNKNOWN_SUBSURFACE       = 18;
    public static final int ATTB_UNKNOWN_SURFACE          = 19; 
    public static final int ATTB_MSG                      = 100;
    public static final int ATTB_FTP                      = 101;   
    
    // MSG TYPE
    public static final byte MSG_TYPE_NACK                 = 0;
    public static final byte MSG_TYPE_ACK                  = 1;
    public static final byte MSG_TYPE_DRAW_CIRCLE          = 2;
    public static final byte MSG_TYPE_DEL_CIRCLE           = 3;
    public static final byte MSG_TYPE_DRAW_POLYLINE        = 4;
    public static final byte MSG_TYPE_DEL_POLYLINE         = 5;
    public static final byte MSG_TYPE_UPDATE_PARAM         = 6;
    public static final byte MSG_TYPE_TEXT                 = 7;
    public static final byte MSG_TYPE_FTP                  = 8;
//    
    public static final byte TRACK_TYPE_GPS                = 1;
    public static final byte TRACK_TYPE_AIS                = 2;
    public static final byte TRACK_TYPE_RADAR              = 3;
    public static final byte TRACK_TYPE_RAM                = 4;  
    public static final byte TRACK_TYPE_RAM_DELETE         = 5; 
    public static final byte TRACK_TYPE_GPS_OWN            = 6; 
    
    public static final byte INFO_TYPE_FTP_PROGRESS        = 1;
    public static final byte INFO_TYPE_CURRENT_PTT         = 2;


    public static final int TYPE_EVENT_TRACK_RX              = 0;
    public static final int TYPE_EVENT_MSG_RX                = 1;
    public static final int TYPE_EVENT_TRACK_TX              = 2;
    public static final int TYPE_EVENT_MSG_TX                = 3;
    public static final int TYPE_EVENT_ACK_TX                = 4;

    public static final int TYPE_TX_TRACK                  = 1;
    public static final int TYPE_RX_TRACK                  = 2;
    public static final int TYPE_TX_MSG                    = 3;        
    public static final int TYPE_RX_MSG                    = 4;
    public static final int TYPE_TX_INFO                   = 5;
    public static final int TYPE_RX_INFO                   = 6;
    
    public static final int LENGTH_TOPIC                   = 8;
    
    
    public static final int TOPIC_TG2CORE                 = 1;
    public static final int TOPIC_CORE2TG                 = 2;
    public static final int TOPIC_CCD2CORE                = 3;
    public static final int TOPIC_CORE2CCD                = 4;
    public static final int TOPIC_TG2CCD                  = 5;
    public static final int TOPIC_CCD2TG                  = 6;
    public static final int TOPIC_CORE2LOGGER             = 7;
      
    
    // FTP 
    public static final int MAX_FTP_LENGTH                = 1024 * 1024; // 1 MB
    public static final int FTP_MAX_REPLAY                = 3;
    public static final int FTP_TIMEOUT                   = 5; // 
    public static final int FTP_HDR_LENGTH                = 16;
    
    
    public static final int TYPE_EVENT_TRACK              = 0;
    public static final int TYPE_EVENT_MSG                = 1;
    
    public static final int API_HEADER_LENGTH             = 16;
    
}