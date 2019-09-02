/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */
package org.len.tdl.view.ryt;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import jssc.SerialPortList;
import org.len.tdl.common.DataListener;
import org.len.tdl.common.StructAck;
import org.len.tdl.common.StructCircle;
import org.len.tdl.common.StructFile;
import org.len.tdl.common.StructPolyline;
import org.len.tdl.common.StructText;
import org.len.tdl.common.StructTrack;
import org.len.tdl.core.DEF;
import org.len.tdl.core.DataLink114_ryt;
import org.len.tdl.duy.xml_CRUD.xml_CRUD;
import org.len.tdl.general_var.*;
import org.len.tdl.modem.ConstellationPanel;
import org.len.tdl.modem.ConstellationProcessor;
import org.len.tdl.modem.ScopePanel;
import org.len.tdl.suara_NPU.*;
import org.len.tdl.sync_view.sync_view;
import org.len.tdl.thread_mc.*;
import org.len.tdl.tools_ryt.WTConnection2;

/**
 *
 * @author riyanto
 * @author Faturrahman
 * @author Ian Agung Prakoso
 */
/**
 *
 * This is Core TDL Jframe Form class
 */
public class MainTest extends javax.swing.JFrame {

    /**
     * Reference Variables
     */
    private final File configFile = new File("config.properties");
    private Properties configProps;

    private final int MAX_NPU = 30;
    private final int MAX_TRAK = 702;
    private boolean udpFromTgDisable = false;
    private model_variabel mod_var;
    static DataLink114_ryt dtl;
    WTConnection2 con_modem;
    WTConnection2 con_gps;
    //private xml_CRUD xml;
    private sync_view sync;
    //private Say_number say;
    private boolean minimode_active = false;
    //private final DEF DEF = new DEF();
    private final UdpTx udpTxDataLogger = new UdpTx();
    private final UdpTx udpTxDataGenerator = new UdpTx();
    private final UdpRx udpRxData = new UdpRx();
    private final UdpRx udpRxGps = new UdpRx();
    private final int[][] trak_life = new int[DEF.MAX_NPU + 1][DEF.MAX_TRAK];
    private final byte[][][] trackRx = new byte[DEF.MAX_NPU + 1][DEF.MAX_TRAK][DEF.PACK_LENGTH];
    
    private final ScopePanel scopePanel = new ScopePanel();
    private final ConstellationPanel constPanel = new ConstellationPanel();
    private final ConstellationProcessor constProcessor = new ConstellationProcessor();
    
    private List<String> soundDevices;
    private List<String> soundDevicesOutput;
    private int selectedDeviceInputIndex = 0;
    private int selectedDeviceOutputIndex = 0;
    
    private boolean  init_selesai = false;
    
    
    /**
     * Creates new form MainTest
     */
    public MainTest() {
        initComponents();
        panelOscilloscope.setLayout(new java.awt.BorderLayout());
        panelOscilloscope.add(scopePanel);
        panelConstellation.setLayout(new java.awt.BorderLayout());
        panelConstellation.add(constPanel);
        
        mod_var = new model_variabel();
        dtl = new DataLink114_ryt(mod_var);
        //xml = new xml_CRUD();
        //say = new Say_number(mod_var);
        dtl.addListener(new CoreRxListener());
        udpRxData.addListener(new UdpRxListener());
        udpRxGps.addListener(new UdpRxGpsListener());
        GUI_addPortList();
        GUI_LoadConfiguration();
        GUI_Modem_Monitoring();
        GUI_Modem_Monitoring_Plot();
        GUI_addSoundDevices();
        init_selesai = true;
    }
    
    private void GUI_addSoundDevices()
    {
        soundDevices = dtl.getListLineInput();
        for (String soundDevice : soundDevices) {
            ComboBox_LineIn.addItem(soundDevice);
        }
        ComboBox_LineIn.setSelectedIndex(selectedDeviceInputIndex);
        
        
        soundDevicesOutput = dtl.getListLineOutput();
        for (String soundDevice : soundDevicesOutput) {
            jComboBox_LineOut.addItem(soundDevice);
        }
        jComboBox_LineOut.setSelectedIndex(selectedDeviceOutputIndex);
    }

    private void saveProperties() throws IOException {
        configProps.setProperty("OWN_NPU", Integer.toString(Configuration.ownNpu));
        configProps.setProperty("MODEM_PORT", Configuration.modemPort);
        configProps.setProperty("MODEM_BAUDRATE", Integer.toString(Configuration.modemBaudrate));
        configProps.setProperty("GPS_PORT", Configuration.gpsPort);
        configProps.setProperty("GPS_BAUDRATE", Integer.toString(Configuration.gpsBaudrate));
        configProps.setProperty("GPS_UPDATE", Integer.toString(Configuration.gpsUpdateInterval));
        configProps.setProperty("KEY", Configuration.key);
        configProps.setProperty("MODEM_DATARATE", Integer.toString(Configuration.modemDatarate));
        configProps.setProperty("UDP_TX_ADDR", Configuration.udpTxAddress);
        configProps.setProperty("UDP_TX_PORT", Integer.toString(Configuration.udpTxPort));
        configProps.setProperty("UDP_RX_ADDR", Configuration.udpRxAddress);
        configProps.setProperty("UDP_RX_PORT", Integer.toString(Configuration.udpRxPort));
        configProps.setProperty("UDP_GPS_ADDR", Configuration.udpGpsAddress);
        configProps.setProperty("UDP_GPS_PORT", Integer.toString(Configuration.udpGpsPort));
        configProps.setProperty("UDP_LOGGER_ADDR", Configuration.udpLoggerAddress);
        configProps.setProperty("UDP_LOGGER_PORT", Integer.toString(Configuration.udpLoggerPort));
        configProps.setProperty("NPU_LIST", Configuration.npuList);
        configProps.setProperty("ITEM_LIST", Configuration.itemList);
        configProps.setProperty("DELAY_LIST", Configuration.delayList);
        configProps.setProperty("SILENT_MODE", Boolean.toString(Configuration.silentMode));
        configProps.setProperty("AUTORUN", Boolean.toString(Configuration.autorun));
        configProps.setProperty("MODE_SYNC", Integer.toString(Configuration.modeSync));
        configProps.setProperty("UDP_LOGGER_ON", Boolean.toString(Configuration.udpLoggerOn));
        OutputStream outputStream = new FileOutputStream(configFile);
        configProps.store(outputStream, "aprs setttings");
        outputStream.close();
    }

    private void loadProperties() throws IOException {
        Properties defaultProps = new Properties();
        // sets default properties
        defaultProps.setProperty("OWN_NPU", Integer.toString(Configuration.ownNpu));
        defaultProps.setProperty("MODEM_PORT", Configuration.modemPort);
        defaultProps.setProperty("MODEM_BAUDRATE", Integer.toString(Configuration.modemBaudrate));
        defaultProps.setProperty("GPS_PORT", Configuration.gpsPort);
        defaultProps.setProperty("GPS_BAUDRATE", Integer.toString(Configuration.gpsBaudrate));
        defaultProps.setProperty("GPS_UPDATE", Integer.toString(Configuration.gpsUpdateInterval));
        defaultProps.setProperty("KEY", Configuration.key);
        defaultProps.setProperty("MODEM_DATARATE", Integer.toString(Configuration.modemDatarate));
        defaultProps.setProperty("UDP_TX_ADDR", Configuration.udpTxAddress);
        defaultProps.setProperty("UDP_TX_PORT", Integer.toString(Configuration.udpTxPort));
        defaultProps.setProperty("UDP_RX_ADDR", Configuration.udpRxAddress);
        defaultProps.setProperty("UDP_RX_PORT", Integer.toString(Configuration.udpRxPort));
        defaultProps.setProperty("UDP_GPS_ADDR", Configuration.udpGpsAddress);
        defaultProps.setProperty("UDP_GPS_PORT", Integer.toString(Configuration.udpGpsPort));
        defaultProps.setProperty("UDP_LOGGER_ADDR", Configuration.udpLoggerAddress);
        defaultProps.setProperty("UDP_LOGGER_PORT", Integer.toString(Configuration.udpLoggerPort));
        defaultProps.setProperty("NPU_LIST", Configuration.npuList);
        defaultProps.setProperty("ITEM_LIST", Configuration.itemList);
        defaultProps.setProperty("DELAY_LIST", Configuration.delayList);
        defaultProps.setProperty("MODE_SYNC", Integer.toString(Configuration.modeSync));
        defaultProps.setProperty("SILENT_MODE", Boolean.toString(Configuration.silentMode));
        defaultProps.setProperty("AUTORUN", Boolean.toString(Configuration.autorun));        
        defaultProps.setProperty("UDP_LOGGER_ON", Boolean.toString(Configuration.udpLoggerOn));
        configProps = new Properties(defaultProps);

        try ( // loads properties from file
                InputStream inputStream = new FileInputStream(configFile)) {
            configProps.load(inputStream);
            inputStream.close();
        }

        // set to configuration variable
        Configuration.ownNpu = Integer.parseInt(configProps.getProperty("OWN_NPU"));
        Configuration.modemPort = configProps.getProperty("MODEM_PORT");
        Configuration.modemBaudrate = Integer.parseInt(configProps.getProperty("MODEM_BAUDRATE"));
        Configuration.gpsPort = configProps.getProperty("GPS_PORT");
        Configuration.gpsBaudrate = Integer.parseInt(configProps.getProperty("GPS_BAUDRATE"));
        Configuration.gpsUpdateInterval = Integer.parseInt(configProps.getProperty("GPS_UPDATE"));
        Configuration.key = configProps.getProperty("KEY");
        Configuration.modemDatarate = Integer.parseInt(configProps.getProperty("MODEM_DATARATE"));
        Configuration.udpTxAddress = configProps.getProperty("UDP_TX_ADDR");
        Configuration.udpTxPort = Integer.parseInt(configProps.getProperty("UDP_TX_PORT"));
        Configuration.udpRxAddress = configProps.getProperty("UDP_RX_ADDR");
        Configuration.udpRxPort = Integer.parseInt(configProps.getProperty("UDP_RX_PORT"));
        Configuration.udpGpsAddress = configProps.getProperty("UDP_GPS_ADDR");
        Configuration.udpGpsPort = Integer.parseInt(configProps.getProperty("UDP_GPS_PORT"));
        Configuration.udpLoggerAddress = configProps.getProperty("UDP_LOGGER_ADDR");
        Configuration.udpLoggerPort = Integer.parseInt(configProps.getProperty("UDP_LOGGER_PORT"));
        Configuration.npuList = configProps.getProperty("NPU_LIST");
        Configuration.itemList = configProps.getProperty("ITEM_LIST");
        Configuration.delayList = configProps.getProperty("DELAY_LIST");
        Configuration.modeSync = Integer.valueOf(configProps.getProperty("MODE_SYNC"));
        Configuration.silentMode = Boolean.valueOf(configProps.getProperty("SILENT_MODE"));
        Configuration.autorun = Boolean.valueOf(configProps.getProperty("AUTORUN"));        
        Configuration.udpLoggerOn = Boolean.valueOf(configProps.getProperty("UDP_LOGGER_ON"));
    }

    private void GUI_LoadConfiguration() {
        // LOAD FILE PROPERTIES
        try {
            loadProperties();
        } catch (IOException ex) {
            System.out.println("The config.properties file does not exist, default properties loaded.");
        }

        // UPDATE TO GUI
        txt_own_npu.setText(Integer.toString(Configuration.ownNpu));
        //txt_boudrate_modem.setText(Integer.toString(Configuration.modemBaudrate));
        jTextFieldGpsBaudrate.setText(Integer.toString(Configuration.gpsBaudrate));
        txt_datalink_key.setText(Configuration.key);
        //txt_data_rate.setText(Integer.toString(Configuration.modemDatarate));
        boolean gpsUpdate = Configuration.gpsUpdateInterval != 0;
        jCheckBoxGpsUpdate.setSelected(gpsUpdate);
        jTextFielGpsUpdateInterval.setEnabled(gpsUpdate);
        if (gpsUpdate) {
            jTextFielGpsUpdateInterval.setText(Integer.toString(Configuration.gpsUpdateInterval));
        }
        chb_silent_mode.setSelected(Configuration.silentMode);
        GUI_Show_Table_Scenario(Configuration.npuList, Configuration.itemList, Configuration.delayList);

        String modemPort = Configuration.modemPort;
        String gpsPort = Configuration.gpsPort;
        String[] portNames = SerialPortList.getPortNames();
        int indexModemPort = -1;
        int indexGpsPort = -1;

        int n_port = portNames.length;
        if (n_port > 0) {
            for (int i = 0; i < n_port; i++) {
                if (gpsPort.compareToIgnoreCase(portNames[i]) == 0) {
                    indexGpsPort = i;
                }
                if (modemPort.compareToIgnoreCase(portNames[i]) == 0) {
                    indexModemPort = i;
                }
            }
        }
        if (indexModemPort >= 0) {
            //cmb_port_data.setSelectedIndex(indexModemPort);
        }
        if (indexGpsPort >= 0) {
            jComboBoxGpsPort.setSelectedIndex(indexGpsPort);
        }

        txt_ip_address.setText(Configuration.udpRxAddress);
        txt_port_udp.setText(Integer.toString(Configuration.udpRxPort));
        txt_ip_address_remote_generator.setText(Configuration.udpTxAddress);
        txt_port_udp_remote_generator.setText(Integer.toString(Configuration.udpTxPort));
        jTextFieldUdpGpsIpAddress.setText(Configuration.udpGpsAddress);
        jTextFieldUdpGpsPort.setText(Integer.toString(Configuration.udpGpsPort));
        jTextFieldUdpLoggerIpAddress.setText(Configuration.udpLoggerAddress);
        jTextFieldUdpLoggerIpPort.setText(Integer.toString(Configuration.udpLoggerPort));
        jComboBoxModeSync.setSelectedIndex(Configuration.modeSync);
        
        GUI_UpdateModeSync();

        jLabelUdpLogger.setEnabled(Configuration.udpLoggerOn);
        jComboBoxUdpLoggerDisable.setEnabled(Configuration.udpLoggerOn);
        jLabelUdpLoggerIpAddress.setEnabled(Configuration.udpLoggerOn);
        jLabelUdpLoggerIpPort.setEnabled(Configuration.udpLoggerOn);
        jTextFieldUdpLoggerIpAddress.setEnabled(Configuration.udpLoggerOn);
        jTextFieldUdpLoggerIpPort.setEnabled(Configuration.udpLoggerOn);

        boolean autorun = Configuration.autorun;
        if (autorun == true) {
            if  ((indexGpsPort >= 0) || (Configuration.modeSync != 1)) {
                btn_update_scenario.doClick();
                jButtonCoreRun.doClick();
            }
        }

//        if (Configuration.modeSync != 1) {
//            jComboBoxGpsPort.setEnabled(false);
//            jTextFieldGpsBaudrate.setEnabled(false);
//        }

    }

    private void GUI_SaveConfiguration() {
        // GET DATA CONFIG FROM GUI
        Configuration.ownNpu = Integer.valueOf(txt_own_npu.getText());
        //Configuration.modemBaudrate = Integer.valueOf(txt_boudrate_modem.getText());
        Configuration.gpsBaudrate = Integer.valueOf(jTextFieldGpsBaudrate.getText());
        Configuration.key = txt_datalink_key.getText();
        //Configuration.modemDatarate = Integer.valueOf(txt_data_rate.getText());
        //Configuration.modemPort = cmb_port_data.getSelectedItem().toString();
        Configuration.gpsPort = jComboBoxGpsPort.getSelectedItem().toString();
        if (jCheckBoxGpsUpdate.isSelected()) {
            Configuration.gpsUpdateInterval = Integer.valueOf(jTextFielGpsUpdateInterval.getText());
        } else {
            Configuration.gpsUpdateInterval = 0;
        }
        Configuration.silentMode = chb_silent_mode.isSelected();
        Configuration.udpRxAddress = txt_ip_address.getText();
        Configuration.udpTxAddress = txt_ip_address_remote_generator.getText();
        Configuration.udpRxPort = Integer.valueOf(txt_port_udp.getText());
        Configuration.udpTxPort = Integer.valueOf(txt_port_udp_remote_generator.getText());
        Configuration.udpGpsAddress = jTextFieldUdpGpsIpAddress.getText();
        Configuration.udpGpsPort = Integer.valueOf(jTextFieldUdpGpsPort.getText());
        Configuration.udpLoggerAddress = jTextFieldUdpLoggerIpAddress.getText();
        Configuration.udpLoggerPort = Integer.valueOf(jTextFieldUdpLoggerIpPort.getText());
        Configuration.modeSync = jComboBoxModeSync.getSelectedIndex();

        GUI_Get_Table_Scenario();

        // SAVE TO CONFIGURATION FILE
        try {
            saveProperties();
        } catch (IOException ex) {
            System.out.println("The config.properties file does not exist");
        }
    }

    private void GUI_Show_Table_Scenario(String npuList, String itemList, String delayList) {

        // CLEAR TABLE
        for (int i = 0; i < jTable_scenario.getRowCount(); i++) {
            for (int j = 0; j < jTable_scenario.getColumnCount(); j++) {
                //jTable_scenario.getModel().setValueAt(null, i, j);
                jTable_scenario.setValueAt(null, i, j);
            }
        }
        //jTable_scenario.getValueAt(ERROR, NORMAL);

        // SET TABLE
        String[] npu = npuList.split(",");
        String[] item = itemList.split(",");
        String[] delay = delayList.split(",");
        for (int i = 0; i < npu.length; i++) {
            jTable_scenario.setValueAt(Integer.parseInt(npu[i]), i, 0);
            jTable_scenario.setValueAt(Integer.parseInt(item[i]), i, 1);
            jTable_scenario.setValueAt(Integer.parseInt(delay[i]), i, 2);
        }
    }

    private void GUI_Get_Table_Scenario() {
        int row = jTable_scenario.getRowCount();

        String npuList = "";
        String itemList = "";
        String delayList = "";

        if (jTable_scenario.getValueAt(0, 0) != null) {
            npuList = npuList + jTable_scenario.getValueAt(0, 0).toString();
        }
        if (jTable_scenario.getValueAt(0, 1) != null) {
            itemList = itemList + jTable_scenario.getValueAt(0, 1).toString();
        }
        if (jTable_scenario.getValueAt(0, 2) != null) {
            delayList = delayList + jTable_scenario.getValueAt(0, 2).toString();
        }

        for (int i = 1; i < row; i++) {
            if (jTable_scenario.getModel().getValueAt(i, 0) != null) {
                npuList = npuList + "," + jTable_scenario.getValueAt(i, 0).toString();
            }
            if (jTable_scenario.getModel().getValueAt(i, 1) != null) {
                itemList = itemList + "," + jTable_scenario.getValueAt(i, 1).toString();
            }
            if (jTable_scenario.getModel().getValueAt(i, 2) != null) {
                delayList = delayList + "," + jTable_scenario.getValueAt(i, 2).toString();
            }
        }

        Configuration.npuList = npuList;
        Configuration.itemList = itemList;
        Configuration.delayList = delayList;
    }

    /**
     * Method to set time out of Received tracks table
     */
    javax.swing.Timer tmout = new javax.swing.Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            for (int i = 1; i <= DEF.MAX_NPU; i++) {
                for (int j = 0; j < DEF.MAX_TRAK; j++) {
                    trak_life[i][j]++;
                    if (trak_life[i][j] > DEF.NPU_TIMEOUT) {
                        trackRx[i][j][DEF.ATTB1_IDX] = 0;
                        trackRx[i][j][DEF.ATTB2_IDX] = 0;
                        trak_life[i][j] = DEF.NPU_TIMEOUT;
                    }
                }
            }
        }
    });
    
    
    
    private void GUI_Modem_Monitoring_Plot()
    {
      // scheduling the task at fixed rate delay
      Timer timer_monitoring = new Timer();
      timer_monitoring.scheduleAtFixedRate(new TimerTask()
      {  
        // this method performs the task
        @Override
        public void run() {
            //System.out.println("working at fixed rate delay");     
            //int tik = dtl.getTick();
            //System.out.println("Level: " + tik); 
            
            //lbl_level_status.setText("LEVEL: " + dtl.getLevelTx());
            int time_proc = dtl.getTimeProcessRx();
            int time_sample = dtl.getTimeSampleRx();
            //int cpu_load = 1000 * time_proc / time_sample;
            //lbl_level_status.setText("TX: " + time_proc / 1000);
            //System.out.println(time_proc + ":" + time_sample);
            int rx_level = dtl.getLevelRx();
            jProgressBarTxLevel.setValue(rx_level);
            //jLabelTxLevel.setText(rx_level + "%");
            
            double[] tmp = dtl.getDataScope();
            scopePanel.displayData(tmp); 
            
            
            double[] xr = dtl.getSymbolReal();
            double[] xi = dtl.getSymbolimaginary();
            
            constProcessor.phaseVectorNormalize(xr, xi, xr.length);
            //DecimalFormat df = new DecimalFormat("0.00");
            double snr_approx = constProcessor.getSnrApprox();
            //jLabelSnrApprox.setText("SNR Approx = " + df.format(snr_approx) + " dB");
            double peak_constellation = constProcessor.getPeakMagnitude();
            //jLabelPeakConstellation.setText("Magnitude = " + df.format(peak_constellation) + " dB");
            double ph_ofset = constProcessor.getFrequencyOfset();
            constPanel.setInfo(snr_approx, ph_ofset, peak_constellation);
            constPanel.displayData(xr, xi, 1.0);
            
        }  
      },500,1000); // delay, interval  
    }
    
    private void GUI_Modem_Monitoring()
    {
      // scheduling the task at fixed rate delay
      Timer timer_monitoring = new Timer();
      timer_monitoring.scheduleAtFixedRate(new TimerTask()
      {  
        // this method performs the task
        @Override
        public void run() {
            
            
            int progress_tx = dtl.getPersenTx();
            jProgressBarTxProgress.setValue(progress_tx);
            
            int[] info_rx = dtl.getInfoRx();
            jLabel_preamble_count.setText(": " + info_rx[0]);
            jLabel_pattern_start.setText(": " + info_rx[1]);
            jLabel_data_count.setText(": " + info_rx[5]);
            jLabel_postamble_count.setText(": " + info_rx[3]); 
            if (info_rx[0] == 0)
            {
                jLabel_preamble_count.setText(": ");
                jLabel_pattern_start.setText(": ");
                jLabel_data_count.setText(": ");
                jLabel_postamble_count.setText(": ");                 
            }
            
            if (info_rx[4] == 1)
            {
                lnpu_status_rx.setBackground(Color.GREEN);
                lnpu_status_rx.setForeground(Color.WHITE);
            }
            else
            {
                lnpu_status_rx.setBackground(Color.LIGHT_GRAY);
                lnpu_status_rx.setForeground(Color.BLACK);                
            }
            
            int statusTx = dtl.getStatusTx();
            if (statusTx == 1)
            {
                lnpu_status_tx.setBackground(Color.RED);
                lnpu_status_tx.setForeground(Color.WHITE);
            }
            else
            {
                lnpu_status_tx.setBackground(Color.LIGHT_GRAY);
                lnpu_status_tx.setForeground(Color.BLACK);
            }
            
            int total_length = info_rx[2];
            int byte_rx = info_rx[5];
            
            int persen_rx = 0;
            if (total_length > 0)
                persen_rx = byte_rx * 100 / total_length;
            jProgressBarRxProgress.setValue(persen_rx);
            
            //System.out.println("working at fixed rate delay");     
            //int tik = dtl.getTick();
            //System.out.println("Level: " + tik); 
            
            //lbl_level_status.setText("LEVEL: " + dtl.getLevelTx());
            //int time_proc = dtl.getTimeProcessRx();
            //int time_sample = dtl.getTimeSampleRx();
            //int cpu_load = 1000 * time_proc / time_sample;
            //lbl_level_status.setText("TX: " + time_proc / 1000);
            //System.out.println(time_proc + ":" + time_sample);
            //int rx_level = dtl.getLevelRx();
            //jProgressBarTxLevel.setValue(rx_level);
            //jLabelTxLevel.setText(rx_level + "%");            
        }  
      },50,300); // delay, interval  
    }
    

    /**
     * Method listeners of Core TDL to receive data from UDP GPS
     */
    class UdpRxGpsListener implements DataListener {

        @Override
        /**
         * Method Listener to receive data from UDP
         */
        public void dataReceive(byte[] data, int type) {
            //System.out.println("MASUK UDP GPS");
            if (jCheckBoxUdpGpsDisable.isSelected() == false) {
                dtl.processUdpGps(data);
                counter_gps_elapse = 10;
            }
        }

        @Override
        public void infoReceive(byte[] data) {

        }
    }

    /**
     * Method listeners receive data from UDP TG
     */
    class UdpRxListener implements DataListener {

        @Override
        /**
         * Method Listener to receive data from UDP TG
         */
        public void dataReceive(byte[] data, int type) {

            //if ((data[0] == '$') && (data[1] == 'G') && (data[2] == 'P')) {
            //    System.out.println("DATA GPRMC");
            //}
            //System.out.println("MASUK UDP1");
            //System.out.println("LENGTH = " + data.length + ", DATA = " + Arrays.toString(data));
            if (data[0] == DEF.TOPIC_TG2CORE) {
                if (data[1] == DEF.TYPE_RX_TRACK && data[3] != 0) {
                    byte[] readBuffer = new byte[DEF.PACK_LENGTH];
                    System.arraycopy(data, 8, readBuffer, 0, readBuffer.length);
                    int no_track_m1 = ((readBuffer[DEF.TRAKNO1_IDX] & 0xff) + ((readBuffer[DEF.TRAKNO2_IDX] & 0xff) * 0x100));
                    int lastnpu = (readBuffer[DEF.OWNNPU1_IDX] & 0xFF + (readBuffer[DEF.OWNNPU2_IDX] & 0xff) * 0x100);
                    trak_life[lastnpu][no_track_m1] = 0;
                    System.arraycopy(data, 8, trackRx[lastnpu][no_track_m1], 0, DEF.PACK_LENGTH);
                }

                if (data[1] == DEF.TYPE_TX_TRACK && data[3] != 0) {
                    byte[] dataTmp = new byte[32];
                    int trakno;
                    int npuu;
                    //if (data[2] != DEF.TRACK_TYPE_GPS_OWN)
                    //    msg_send_txt.append(get_time() + " Data data own trck masuk \n");

                    //System.arraycopy(data, 8 , dataTmp, 0, dataTmp.length);
                    //StructTrack trakTmp = new StructTrack(dataTmp);
                    //trakno = trakTmp.getNumber();
                    //npuu = trakTmp.getOwner();
                    //dtl.UpdateTrak(dataTmp, npuu, trakno);
                    //System.out.println("JUMLAH TRACK: " + data[3] + "  ATTB: " + data[8+DEF.ATTB1_IDX] + " " );
                    for (int x = 0; x < (data[3]); x++) {
                        System.arraycopy(data, 8 + (32 * x), dataTmp, 0, dataTmp.length);
                        StructTrack trakTmp = new StructTrack(dataTmp);
                        trakno = trakTmp.getNumber();
                        npuu = trakTmp.getOwner();
                        dtl.UpdateTrak(dataTmp, npuu, trakno);
                        //if (jCheckBoxShowTxTrack.isSelected())
                        //    msg_send_txt.append(get_time() + " TX TRACK, NPU:" + npuu + "\n");
                    }

                    GUI_Show_Trak_Tx();
                    GUI_Update_Trak_Tx();
                    GUI_get_Tx_Table();
                }

                //if (data[2] == DEF.MSG_TYPE_ACK) 
                //{
                //    byte source_address = data[3];
                //    msg_receive_txt.append(get_time() + " Recv ack from NPU " + source_address + " :  OK");
                //}
                //if (data[2] == DEF.MSG_TYPE_TEXT) 
                if (data[1] == DEF.TYPE_TX_MSG) {
                    //System.out.println("MSG MASUKK " + data[3]);

                    byte source_address = data[3];
                    byte destination_address = data[4];
                    byte[] data_text = new byte[data.length - 8];
                    System.arraycopy(data, 8, data_text, 0, data_text.length);
                    dtl.sendmsg(data_text, DEF.MSG_TYPE_TEXT, source_address, destination_address);
                    StructText smsgText = new StructText(data_text);
                    msg_send_txt.append(get_time() + " TX TEXT TO NPU " + destination_address + " : " + smsgText.getStext() + "\n");
                }

                if (data[2] == DEF.MSG_TYPE_DRAW_CIRCLE) {

                    byte source_address = data[3];
                    byte destination_address = data[4];
                    byte[] data_circle = new byte[data.length - 8];
                    System.arraycopy(data, 8, data_circle, 0, data_circle.length);
                    dtl.sendmsg(data_circle, DEF.MSG_TYPE_DRAW_CIRCLE, source_address, destination_address);
                    StructCircle circle = new StructCircle(data_circle);
                    String display_string = " Send circle From NPU " + source_address + " : Longitude = " + Double.toString(circle.getLongitude()) + ", Latitude " + circle.getLatitude() + ", Range = " + circle.getRange();
                    if (circle.getNote_length() > 0) {
                        display_string = display_string + ", notes : " + circle.getNotes();
                    }
                    msg_send_txt.append(get_time() + display_string + "\n");

                    byte[] udpPacket = new byte[8 + data.length];

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
                    udpPacket[1] = (byte) DEF.TYPE_TX_MSG;
                    udpPacket[2] = (byte) DEF.MSG_TYPE_DRAW_CIRCLE;
                    udpPacket[3] = (byte) 0;

                    byte[] time_gps_now = get_time_bytes();
                    udpPacket[4] = time_gps_now[0];
                    udpPacket[5] = time_gps_now[1];
                    udpPacket[6] = time_gps_now[2];

                    System.arraycopy(data, 0, udpPacket, 8, data.length);
                    if (Configuration.udpLoggerOn)
                        udpTxDataLogger.sendUdp(udpPacket);
                }

                if (data[2] == DEF.MSG_TYPE_DEL_CIRCLE) {

                    byte source_address = data[3];
                    byte destination_address = data[4];
                    byte[] data_circle = new byte[data.length - 8];
                    System.arraycopy(data, 8, data_circle, 0, data_circle.length);
                    dtl.sendmsg(data_circle, DEF.MSG_TYPE_DEL_CIRCLE, source_address, destination_address);
                    StructCircle circle = new StructCircle(data_circle);
                    String display_string = " Send circle From NPU " + source_address + " : Longitude = " + Double.toString(circle.getLongitude()) + ", Latitude " + circle.getLatitude() + ", Range = " + circle.getRange();
                    if (circle.getNote_length() > 0) {
                        display_string = display_string + ", notes : " + circle.getNotes();
                    }
                    msg_send_txt.append(get_time() + display_string + "\n");

                    byte[] udpPacket = new byte[8 + data.length];

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
                    udpPacket[1] = (byte) DEF.TYPE_TX_MSG;
                    udpPacket[2] = (byte) DEF.MSG_TYPE_DEL_CIRCLE;
                    udpPacket[3] = (byte) 0;

                    byte[] time_gps_now = get_time_bytes();
                    udpPacket[4] = time_gps_now[0];
                    udpPacket[5] = time_gps_now[1];
                    udpPacket[6] = time_gps_now[2];

                    System.arraycopy(data, 0, udpPacket, 8, data.length);
                    if (Configuration.udpLoggerOn)
                    udpTxDataLogger.sendUdp(udpPacket);
                }

                if (data[2] == DEF.MSG_TYPE_DRAW_POLYLINE) {

                    byte source_address = data[3];
                    byte destination_address = data[4];
                    byte[] data_polyline = new byte[data.length - 8];
                    System.arraycopy(data, 8, data_polyline, 0, data_polyline.length);
                    dtl.sendmsg(data_polyline, DEF.MSG_TYPE_DRAW_POLYLINE, source_address, destination_address);
                    StructPolyline polyline = new StructPolyline(data_polyline);
                    String display_string = " Send polyline From NPU " + source_address + " : ";
                    for (int i = 0; i < polyline.getNum_point(); i++) {
                        display_string += "Longitude = " + polyline.getLongitudes()[i] + ", Latitude = " + polyline.getLatitudes()[i];
                    }
                    if (polyline.getNote_length() > 0) {
                        display_string += ", notes : " + polyline.getNotes();
                    }
                    msg_send_txt.append(get_time() + display_string + "\n");

                    byte[] udpPacket = new byte[8 + data.length];

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
                    udpPacket[1] = (byte) DEF.TYPE_TX_MSG;
                    udpPacket[2] = (byte) DEF.MSG_TYPE_DRAW_POLYLINE;
                    udpPacket[3] = (byte) 0;

                    byte[] time_gps_now = get_time_bytes();
                    udpPacket[4] = time_gps_now[0];
                    udpPacket[5] = time_gps_now[1];
                    udpPacket[6] = time_gps_now[2];

                    System.arraycopy(data, 0, udpPacket, 8, data.length);
                    if (Configuration.udpLoggerOn)
                    udpTxDataLogger.sendUdp(udpPacket);
                }

                if (data[2] == DEF.MSG_TYPE_DEL_POLYLINE) {

                    byte source_address = data[3];
                    byte destination_address = data[4];
                    byte[] data_polyline = new byte[data.length - 8];
                    System.arraycopy(data, 8, data_polyline, 0, data_polyline.length);
                    dtl.sendmsg(data_polyline, DEF.MSG_TYPE_DEL_POLYLINE, source_address, destination_address);
                    StructPolyline polyline = new StructPolyline(data_polyline);
                    String display_string = " Send delete polyline From NPU " + source_address + " : ";
                    for (int i = 0; i < polyline.getNum_point(); i++) {
                        display_string += "Longitude = " + polyline.getLongitudes()[i] + ", Latitude = " + polyline.getLatitudes()[i];
                    }
                    if (polyline.getNote_length() > 0) {
                        display_string += ", notes : " + polyline.getNotes();
                    }
                    msg_send_txt.append(get_time() + display_string + "\n");

                    byte[] udpPacket = new byte[8 + data.length];

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
                    udpPacket[1] = (byte) DEF.TYPE_TX_MSG;
                    udpPacket[2] = (byte) DEF.MSG_TYPE_DEL_POLYLINE;
                    udpPacket[3] = (byte) 0;

                    byte[] time_gps_now = get_time_bytes();
                    udpPacket[4] = time_gps_now[0];
                    udpPacket[5] = time_gps_now[1];
                    udpPacket[6] = time_gps_now[2];

                    System.arraycopy(data, 0, udpPacket, 8, data.length);
                    if (Configuration.udpLoggerOn)
                    udpTxDataLogger.sendUdp(udpPacket);
                }

                if (data[2] == DEF.MSG_TYPE_FTP) {

                    byte source_address = data[3];
                    byte destination_address = data[4];
                    byte[] data_file = new byte[data.length - 8];
                    System.arraycopy(data, 8, data_file, 0, data_file.length);
                    dtl.sendmsg(data_file, DEF.MSG_TYPE_FTP, source_address, destination_address);
                    StructFile fileTx = new StructFile(data_file);

                    msg_send_txt.append(get_time() + "Send file From NPU " + source_address + " : " + fileTx.getFileName() + "\n");

                    byte[] udpPacket = new byte[8 + data.length];

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
                    udpPacket[1] = (byte) DEF.TYPE_TX_MSG;
                    udpPacket[2] = (byte) DEF.MSG_TYPE_FTP;
                    udpPacket[3] = (byte) 0;

                    byte[] time_gps_now = get_time_bytes();
                    udpPacket[4] = time_gps_now[0];
                    udpPacket[5] = time_gps_now[1];
                    udpPacket[6] = time_gps_now[2];

                    System.arraycopy(data, 0, udpPacket, 8, data.length);
                    if (Configuration.udpLoggerOn)
                    udpTxDataLogger.sendUdp(udpPacket);
                }
            }
        }

        @Override
        public void infoReceive(byte[] aData) {
        }
    }

    /**
     * Method Listener to receive data from Modem
     */
    class CoreRxListener implements DataListener {

        @Override
        public void dataReceive(byte[] data, int type) {

            if (msg_send_txt.getLineCount() >= 100) {
                msg_send_txt.setText("");
            }

            if (msg_receive_txt.getLineCount() >= 100) {
                msg_receive_txt.setText("");
            }

            if (type == DEF.TYPE_EVENT_TRACK_TX) {
                msg_send_txt.append(get_time() + " TX TRACK\n");
            }

            if (type == DEF.TYPE_EVENT_MSG_TX) {
                int npu = data[DEF.ADDR_RCV_IDX];
                if (npu == 0) {
                    msg_send_txt.append(get_time() + " TX MSG TO BROADCAST\n");
                } else {
                    msg_send_txt.append(get_time() + " TX MSG TO NPU " + npu + "\n");
                }

                int i_package = (data[1] + 1) * 100;
                int n_package = data[2];
                if (n_package > 0) {
                    jLabelStatusTx.setText("Status tx: " + i_package / n_package + "%");
                }

                byte[] udpPacketTx = new byte[8 + data.length];

                udpPacketTx[0] = (byte) DEF.TOPIC_CORE2LOGGER;
                udpPacketTx[1] = (byte) DEF.TYPE_TX_MSG;
                udpPacketTx[2] = (byte) data[2];
                udpPacketTx[3] = (byte) data[0];

                byte[] time_gps_now = get_time_bytes();
                udpPacketTx[4] = time_gps_now[0];
                udpPacketTx[5] = time_gps_now[1];
                udpPacketTx[6] = time_gps_now[2];

                System.arraycopy(data, 0, udpPacketTx, 8, data.length);
                if (Configuration.udpLoggerOn)
                udpTxDataLogger.sendUdp(udpPacketTx);
            }

            if (type == DEF.TYPE_EVENT_ACK_TX) {
                msg_send_txt.append(get_time() + " TX MSG ACK TO NPU " + data[DEF.ADDR_RCV_IDX] + "\n");

            }

            if (type == DEF.TYPE_EVENT_TRACK_RX) {

                byte[] readBuffer = new byte[DEF.PACK_LENGTH];
                System.arraycopy(data, 8, readBuffer, 0, readBuffer.length);
                int no_track_m1 = ((readBuffer[DEF.TRAKNO1_IDX] & 0xff) + ((readBuffer[DEF.TRAKNO2_IDX] & 0xff) * 0x100));
                int lastnpu = (readBuffer[DEF.OWNNPU1_IDX] & 0xFF + (readBuffer[DEF.OWNNPU2_IDX] & 0xff) * 0x100);
                trak_life[lastnpu][no_track_m1] = 0;
                System.arraycopy(data, 8, trackRx[lastnpu][no_track_m1], 0, DEF.PACK_LENGTH);

                byte[] data_track = new byte[DEF.PACK_LENGTH];
                System.arraycopy(data, 8, data_track, 0, DEF.PACK_LENGTH);
                byte[] udpPacket = new byte[8 + data_track.length];

                udpPacket[0] = (byte) DEF.TOPIC_CORE2TG;
                udpPacket[1] = (byte) DEF.TYPE_RX_TRACK;
                udpPacket[3] = (byte) 0;

                byte[] time_gps_now = get_time_bytes();
                udpPacket[4] = time_gps_now[0];
                udpPacket[5] = time_gps_now[1];
                udpPacket[6] = time_gps_now[2];

                System.arraycopy(data_track, 0, udpPacket, 8, data_track.length);

                udpTxDataGenerator.sendUdp(udpPacket);

                udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
                if (Configuration.udpLoggerOn)
                udpTxDataLogger.sendUdp(udpPacket);
                String display_string = " RX TRACK FROM NPU " + data_track[DEF.OWNNPU1_IDX];
                msg_receive_txt.append(get_time() + display_string + "\n");
            }

            if (type == DEF.TYPE_EVENT_MSG_RX) {

                if (data[2] == DEF.MSG_TYPE_ACK) {
                    int source = data[0];
                    byte[] udpPacket = new byte[8 + data.length];

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2TG;
                    udpPacket[1] = (byte) DEF.TYPE_RX_MSG;
                    udpPacket[2] = (byte) DEF.MSG_TYPE_ACK;
                    udpPacket[3] = (byte) source;

                    byte[] time_gps_now = get_time_bytes();
                    udpPacket[4] = time_gps_now[0];
                    udpPacket[5] = time_gps_now[1];
                    udpPacket[6] = time_gps_now[2];

                    System.arraycopy(data, 0, udpPacket, 8, data.length);
                    udpTxDataGenerator.sendUdp(udpPacket);
                    GUI_Show_Msg_Rx(data);

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
                    if (Configuration.udpLoggerOn)
                    udpTxDataLogger.sendUdp(udpPacket);
                    jLabelStatusTx.setText("Status tx: terkirim");
                }

                if (data[2] == DEF.MSG_TYPE_TEXT) {
                    int source = data[0];
                    byte[] udpPacket = new byte[8 + data.length];

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2TG;
                    udpPacket[1] = (byte) DEF.TYPE_RX_MSG;
                    udpPacket[2] = (byte) DEF.MSG_TYPE_TEXT;
                    udpPacket[3] = (byte) source;

                    byte[] time_gps_now = get_time_bytes();
                    udpPacket[4] = time_gps_now[0];
                    udpPacket[5] = time_gps_now[1];
                    udpPacket[6] = time_gps_now[2];

                    System.arraycopy(data, 0, udpPacket, 8, data.length);

                    udpTxDataGenerator.sendUdp(udpPacket);
                    GUI_Show_Msg_Rx(data);

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
                    if (Configuration.udpLoggerOn)
                    udpTxDataLogger.sendUdp(udpPacket);
                }

                if (data[2] == DEF.MSG_TYPE_DRAW_CIRCLE) {
                    int source = data[0];
                    byte[] udpPacket = new byte[8 + data.length];

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2TG;
                    udpPacket[1] = (byte) DEF.TYPE_RX_MSG;
                    udpPacket[2] = (byte) DEF.MSG_TYPE_DRAW_CIRCLE;
                    udpPacket[3] = (byte) source;

                    byte[] time_gps_now = get_time_bytes();
                    udpPacket[4] = time_gps_now[0];
                    udpPacket[5] = time_gps_now[1];
                    udpPacket[6] = time_gps_now[2];

                    System.arraycopy(data, 0, udpPacket, 8, data.length);

                    udpTxDataGenerator.sendUdp(udpPacket);
                    GUI_Show_Msg_Rx(data);

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
                    if (Configuration.udpLoggerOn)
                    udpTxDataLogger.sendUdp(udpPacket);
                }

                if (data[2] == DEF.MSG_TYPE_DEL_CIRCLE) {
                    int source = data[0];
                    byte[] udpPacket = new byte[8 + data.length];

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2TG;
                    udpPacket[1] = (byte) DEF.TYPE_RX_MSG;
                    udpPacket[2] = (byte) DEF.MSG_TYPE_DEL_CIRCLE;
                    udpPacket[3] = (byte) source;

                    byte[] time_gps_now = get_time_bytes();
                    udpPacket[4] = time_gps_now[0];
                    udpPacket[5] = time_gps_now[1];
                    udpPacket[6] = time_gps_now[2];

                    System.arraycopy(data, 0, udpPacket, 8, data.length);
                    udpTxDataGenerator.sendUdp(udpPacket);
                    GUI_Show_Msg_Rx(data);

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
                    if (Configuration.udpLoggerOn)
                    udpTxDataLogger.sendUdp(udpPacket);
                }

                if (data[2] == DEF.MSG_TYPE_DRAW_POLYLINE) {
                    int source = data[0];
                    byte[] udpPacket = new byte[8 + data.length];

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2TG;
                    udpPacket[1] = (byte) DEF.TYPE_RX_MSG;
                    udpPacket[2] = (byte) DEF.MSG_TYPE_DRAW_POLYLINE;
                    udpPacket[3] = (byte) source;

                    byte[] time_gps_now = get_time_bytes();
                    udpPacket[4] = time_gps_now[0];
                    udpPacket[5] = time_gps_now[1];
                    udpPacket[6] = time_gps_now[2];

                    System.arraycopy(data, 0, udpPacket, 8, data.length);

                    udpTxDataGenerator.sendUdp(udpPacket);
                    GUI_Show_Msg_Rx(data);

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
                    if (Configuration.udpLoggerOn)
                    udpTxDataLogger.sendUdp(udpPacket);
                }

                if (data[2] == DEF.MSG_TYPE_DEL_POLYLINE) {
                    int source = data[0];
                    byte[] udpPacket = new byte[8 + data.length];

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2TG;
                    udpPacket[1] = (byte) DEF.TYPE_RX_MSG;
                    udpPacket[2] = (byte) DEF.MSG_TYPE_DEL_POLYLINE;
                    udpPacket[3] = (byte) source;

                    byte[] time_gps_now = get_time_bytes();
                    udpPacket[4] = time_gps_now[0];
                    udpPacket[5] = time_gps_now[1];
                    udpPacket[6] = time_gps_now[2];

                    System.arraycopy(data, 0, udpPacket, 8, data.length);

                    //udpTxDataLogger.sendUdp(udpPacket);
                    udpTxDataGenerator.sendUdp(udpPacket);
                    GUI_Show_Msg_Rx(data);

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
                    if (Configuration.udpLoggerOn)
                    udpTxDataLogger.sendUdp(udpPacket);
                }

                if (data[2] == DEF.MSG_TYPE_FTP) {
                    int source = data[0];
                    byte[] udpPacket = new byte[8 + data.length];

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2TG;
                    udpPacket[1] = (byte) DEF.TYPE_RX_MSG;
                    udpPacket[2] = (byte) DEF.MSG_TYPE_FTP;
                    udpPacket[3] = (byte) source;

                    byte[] time_gps_now = get_time_bytes();
                    udpPacket[4] = time_gps_now[0];
                    udpPacket[5] = time_gps_now[1];
                    udpPacket[6] = time_gps_now[2];

                    System.arraycopy(data, 0, udpPacket, 8, data.length);

                    //udpTxDataLogger.sendUdp(udpPacket);
                    udpTxDataGenerator.sendUdp(udpPacket);
                    GUI_Show_Msg_Rx(data);

                    udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
                    if (Configuration.udpLoggerOn)
                    udpTxDataLogger.sendUdp(udpPacket);
                }
            }
        }

        @Override
        public void infoReceive(byte[] iData) {

            byte[] udpPacket = new byte[8 + iData.length];

            byte typeInfo = iData[0]; //DEF.TYPE_TX_INFO;
            byte subTypeInfo = iData[1];//  DEF.INFO_TYPE_CURRENT_PTT;

            udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
            udpPacket[1] = typeInfo;
            udpPacket[2] = subTypeInfo;
            udpPacket[3] = (byte) 0;

            byte[] time_gps_now = get_time_bytes();
            udpPacket[4] = time_gps_now[0];
            udpPacket[5] = time_gps_now[1];
            udpPacket[6] = time_gps_now[2];

            System.arraycopy(iData, 0, udpPacket, 8, iData.length);

            if (iData[1] == DEF.INFO_TYPE_FTP_PROGRESS) {
                //udpTxDataGenerator.sendUdp(udpPacket);
            }

            if (iData[1] == DEF.INFO_TYPE_CURRENT_PTT) {
                if (Configuration.udpLoggerOn)
                udpTxDataLogger.sendUdp(udpPacket);
            }
        }
    }

    /**
     * Method to show received messages
     *
     * @param data
     */
    private void GUI_Show_Msg_Rx(byte[] data) {

        byte[] header_msg = new byte[8];
        byte[] data_msg = new byte[data.length - 8];

        System.arraycopy(data, 0, header_msg, 0, 8);
        System.arraycopy(data, 8, data_msg, 0, data_msg.length);
        int sender_address = header_msg[0];
        int msg_data_type = header_msg[2];
        byte owner_npu = Byte.valueOf(txt_own_npu.getText());
        StructAck ack = new StructAck();
        String display_string = "";

        switch (msg_data_type) {
            case DEF.MSG_TYPE_NACK:
                display_string = " RX NACK FROM NPU " + sender_address + " : " + new String(data_msg);
                break;

            case DEF.MSG_TYPE_ACK:
                display_string = " RX ACK FROM NPU " + sender_address + " :  OK";
                break;

            case DEF.MSG_TYPE_DRAW_CIRCLE:
                StructCircle circle = new StructCircle(data_msg);

                display_string = " Recv draw circle from NPU " + sender_address + " Longitude = " + Double.toString(circle.getLongitude()) + ", Latitude " + circle.getLatitude() + ", Range = " + circle.getRange();
                if (circle.getNote_length() > 0) {
                    display_string = display_string + ", notes : " + circle.getNotes();
                }

                dtl.sendmsg(ack.getBytesAck(1, DEF.MSG_TYPE_DRAW_CIRCLE, circle.getNumber()), DEF.MSG_TYPE_ACK, owner_npu, (byte) sender_address);

                break;

            case DEF.MSG_TYPE_DEL_CIRCLE:
                display_string = " Recv delete circle from NPU " + sender_address;

                dtl.sendmsg(ack.getBytesAck(1, DEF.MSG_TYPE_DEL_CIRCLE, 0), DEF.MSG_TYPE_ACK, owner_npu, (byte) sender_address);

                break;

            case DEF.MSG_TYPE_DRAW_POLYLINE:

                StructPolyline polyline = new StructPolyline(data_msg);
                display_string = " Recv draw polyline from NPU " + sender_address + " : ";
                for (int i = 0; i < polyline.getNum_point(); i++) {
                    display_string += ", Longitude = " + polyline.getLongitudes()[i] + ", Latitude = " + polyline.getLatitudes()[i];
                }
                if (polyline.getNote_length() > 0) {
                    display_string += ", notes : " + polyline.getNotes();
                }

                dtl.sendmsg(ack.getBytesAck(1, DEF.MSG_TYPE_DRAW_POLYLINE, polyline.getNumber()), DEF.MSG_TYPE_ACK, owner_npu, (byte) sender_address);

                break;

            case DEF.MSG_TYPE_DEL_POLYLINE:
                display_string = " Recv delete polyline from NPU " + sender_address;

                dtl.sendmsg(ack.getBytesAck(1, DEF.MSG_TYPE_DEL_POLYLINE, 0), DEF.MSG_TYPE_ACK, owner_npu, (byte) sender_address);

                break;

            case DEF.MSG_TYPE_TEXT:

                StructText text = new StructText(data_msg);
                display_string = " RX TEXT FROM NPU " + sender_address + " : " + text.getStext();
                dtl.sendmsg(ack.getBytesAck(1, DEF.MSG_TYPE_TEXT, text.getTextNumber()), DEF.MSG_TYPE_ACK, owner_npu, (byte) sender_address);
                break;

            case DEF.MSG_TYPE_FTP:
                String directory_rx = lbl_received_folder1.getText();
                StructFile sftp = new StructFile(data_msg, directory_rx);
                display_string = " Recv file from NPU ";
                dtl.sendmsg(ack.getBytesAck(1, DEF.MSG_TYPE_FTP, 0), DEF.MSG_TYPE_ACK, owner_npu, (byte) sender_address);
                dtl.msg_file_progress_rx = 0;
                break;
        }
        msg_receive_txt.append(get_time() + display_string + "\n");
    }

    /**
     * Method to show NPU
     */
    private void GUI_Show_NPU_Tick() {
        lnpu1.setForeground(Color.BLACK);
        lnpu2.setForeground(Color.BLACK);
        lnpu3.setForeground(Color.BLACK);
        lnpu4.setForeground(Color.BLACK);
        lnpu5.setForeground(Color.BLACK);
        lnpu6.setForeground(Color.BLACK);
        lnpu7.setForeground(Color.BLACK);
        lnpu8.setForeground(Color.BLACK);
        lnpu9.setForeground(Color.BLACK);
        lnpu10.setForeground(Color.BLACK);
        lnpu11.setForeground(Color.BLACK);
        lnpu12.setForeground(Color.BLACK);
        lnpu13.setForeground(Color.BLACK);
        lnpu14.setForeground(Color.BLACK);
        lnpu15.setForeground(Color.BLACK);
        lnpu16.setForeground(Color.BLACK);
        lnpu17.setForeground(Color.BLACK);
        lnpu18.setForeground(Color.BLACK);
        lnpu19.setForeground(Color.BLACK);
        lnpu20.setForeground(Color.BLACK);
        lnpu21.setForeground(Color.BLACK);
        lnpu22.setForeground(Color.BLACK);
        lnpu23.setForeground(Color.BLACK);
        lnpu24.setForeground(Color.BLACK);
        lnpu25.setForeground(Color.BLACK);
        lnpu26.setForeground(Color.BLACK);
        lnpu27.setForeground(Color.BLACK);
        lnpu28.setForeground(Color.BLACK);
        lnpu29.setForeground(Color.BLACK);
        lnpu30.setForeground(Color.BLACK);

        switch (mod_var.getDtdma_npu_txtime()) {
            case 1:
                lnpu1.setForeground(Color.RED);
                break;
            case 2:
                lnpu2.setForeground(Color.RED);
                break;
            case 3:
                lnpu3.setForeground(Color.RED);
                break;
            case 4:
                lnpu4.setForeground(Color.RED);
                break;
            case 5:
                lnpu5.setForeground(Color.RED);
                break;
            case 6:
                lnpu6.setForeground(Color.RED);
                break;
            case 7:
                lnpu7.setForeground(Color.RED);
                break;
            case 8:
                lnpu8.setForeground(Color.RED);
                break;
            case 9:
                lnpu9.setForeground(Color.RED);
                break;
            case 10:
                lnpu10.setForeground(Color.RED);
                break;
            case 11:
                lnpu11.setForeground(Color.RED);
                break;
            case 12:
                lnpu12.setForeground(Color.RED);
                break;
            case 13:
                lnpu13.setForeground(Color.RED);
                break;
            case 14:
                lnpu14.setForeground(Color.RED);
                break;
            case 15:
                lnpu15.setForeground(Color.RED);
                break;
            case 16:
                lnpu16.setForeground(Color.RED);
                break;
            case 17:
                lnpu17.setForeground(Color.RED);
                break;
            case 18:
                lnpu18.setForeground(Color.RED);
                break;
            case 19:
                lnpu19.setForeground(Color.RED);
                break;
            case 20:
                lnpu20.setForeground(Color.RED);
                break;
            case 21:
                lnpu21.setForeground(Color.RED);
                break;
            case 22:
                lnpu22.setForeground(Color.RED);
                break;
            case 23:
                lnpu23.setForeground(Color.RED);
                break;
            case 24:
                lnpu24.setForeground(Color.RED);
                break;
            case 25:
                lnpu25.setForeground(Color.RED);
                break;
            case 26:
                lnpu26.setForeground(Color.RED);
                break;
            case 27:
                lnpu27.setForeground(Color.RED);
                break;
            case 28:
                lnpu28.setForeground(Color.RED);
                break;
            case 29:
                lnpu29.setForeground(Color.RED);
                break;
            case 30:
                lnpu30.setForeground(Color.RED);
                break;
        }

    }

    /**
     * method to show progress of message file
     */
    private void GUI_Show_Progres_Msg_File() {
        int tx_persen = dtl.msg_file_progress_tx;
        jProgressBar_msg_file_tx.setValue(tx_persen);
        lbl_msg_file_progress_tx.setText("Tx = " + tx_persen + "%");
        int rx_persen = dtl.msg_file_progress_rx;
        jProgressBar_msg_file_rx.setValue(rx_persen);
        lbl_msg_file_progress_rx.setText("Rx = " + rx_persen + "%");
    }

    /**
     * method to show NPU color when transmit
     */
    private void GUI_Show_NPU_Tx() {

        if (dtl.npu_life[1] == 0) {
            lnpu1.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 1) {
                lnpu1.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu1.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[2] == 0) {
            lnpu2.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 2) {
                lnpu2.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu2.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[3] == 0) {
            lnpu3.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 3) {
                lnpu3.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu3.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[4] == 0) {
            lnpu4.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 4) {
                lnpu4.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu4.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[5] == 0) {
            lnpu5.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 5) {
                lnpu5.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu5.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[6] == 0) {
            lnpu6.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 6) {
                lnpu6.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu6.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[7] == 0) {
            lnpu7.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 7) {
                lnpu7.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu7.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[8] == 0) {
            lnpu8.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 8) {
                lnpu8.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu8.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[9] == 0) {
            lnpu9.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 9) {
                lnpu9.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu9.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[10] == 0) {
            lnpu10.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 10) {
                lnpu10.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu10.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[11] == 0) {
            lnpu11.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 11) {
                lnpu11.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu11.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[12] == 0) {
            lnpu12.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 12) {
                lnpu12.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu12.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[13] == 0) {
            lnpu13.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 13) {
                lnpu13.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu13.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[14] == 0) {
            lnpu14.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 14) {
                lnpu14.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu14.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[15] == 0) {
            lnpu15.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 15) {
                lnpu15.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu15.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[16] == 0) {
            lnpu16.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 16) {
                lnpu16.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu16.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[17] == 0) {
            lnpu17.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 17) {
                lnpu17.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu17.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[18] == 0) {
            lnpu18.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 18) {
                lnpu18.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu18.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[19] == 0) {
            lnpu19.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 19) {
                lnpu19.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu19.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[20] == 0) {
            lnpu20.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 20) {
                lnpu20.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu20.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[21] == 0) {
            lnpu21.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 21) {
                lnpu21.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu21.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[22] == 0) {
            lnpu22.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 22) {
                lnpu22.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu22.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[23] == 0) {
            lnpu23.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 23) {
                lnpu23.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu23.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[24] == 0) {
            lnpu24.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 24) {
                lnpu24.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu24.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[25] == 0) {
            lnpu25.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 25) {
                lnpu25.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu25.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[26] == 0) {
            lnpu26.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 26) {
                lnpu26.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu26.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[27] == 0) {
            lnpu27.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 27) {
                lnpu27.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu27.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[28] == 0) {
            lnpu28.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 28) {
                lnpu28.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu28.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[29] == 0) {
            lnpu29.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 29) {
                lnpu29.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu29.setBackground(Color.YELLOW);
            }
        }

        if (dtl.npu_life[30] == 0) {
            lnpu30.setBackground(Color.GRAY);
        } else {
            if (mod_var.getDtdma_npu_txtime() == 30) {
                lnpu30.setBackground(Color.RED);
                //say.init_say(mod_var.getDtdma_npu_txtime());
            } else {
                lnpu30.setBackground(Color.YELLOW);
            }
        }

    }

    /**
     * Method to show received tracks
     */
    private void GUI_Show_Trak_Rcv() {

        for (int i = 0; i < jTable_trak_rx.getRowCount(); i++) {
            for (int j = 0; j < jTable_trak_rx.getColumnCount(); j++) {
                jTable_trak_rx.getModel().setValueAt(null, i, j);
            }
        }

        int k = 0;
        for (int i = 0; i < DEF.MAX_NPU; i++) {
            int nomor_pu = i + 1;
            for (int j = 0; j < DEF.MAX_TRAK; j++) {
                byte[] data = new byte[DEF.PACK_LENGTH];
                System.arraycopy(trackRx[nomor_pu][j], 0, data, 0, DEF.PACK_LENGTH);

                int attribute_idx = (data[DEF.ATTB1_IDX] & 0xff) + (data[DEF.ATTB2_IDX] & 0xff) * 256;

                if (attribute_idx > 0) {

                    StructTrack aTrack = new StructTrack(data);
                    int nomor_track = aTrack.getNumber();

                    jTable_trak_rx.getModel().setValueAt((double) nomor_pu, k, 0);
                    jTable_trak_rx.getModel().setValueAt(aTrack.getMmsi(), k, 1);
                    jTable_trak_rx.getModel().setValueAt((double) nomor_track, k, 2);
                    jTable_trak_rx.getModel().setValueAt(aTrack.getLongitude(), k, 3);
                    jTable_trak_rx.getModel().setValueAt(aTrack.getLatitude(), k, 4);
                    jTable_trak_rx.getModel().setValueAt(aTrack.getSpeed(), k, 5);
                    jTable_trak_rx.getModel().setValueAt(aTrack.getCourse(), k, 6);
                    jTable_trak_rx.getModel().setValueAt(aTrack.getHeight(), k, 7);
                    jTable_trak_rx.getModel().setValueAt(aTrack.getAttribute(), k, 8);
                    k++;
                }
            }
            lbl_trak_rx_available.setText("trak rx available = " + k);
        }
    }

    /**
     * method to scanning data in table receive and table transmit
     */
    javax.swing.Timer dataScan = new javax.swing.Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            GUI_Show_Trak_Rcv();
            GUI_Show_Trak_Tx();

        }
    });

    /**
     * Method to show transmit tracks
     */
    private void GUI_Show_Trak_Tx() {

        for (int i = 0; i < jTable_trak_tx.getRowCount(); i++) {
            for (int j = 0; j < jTable_trak_tx.getColumnCount(); j++) {
                jTable_trak_tx.getModel().setValueAt(null, i, j);
            }
        }

        int k = 0;
        for (int i = 0; i < DEF.MAX_NPU; i++) {
            int nomor_pu = i + 1;
            for (int j = 0; j < MAX_TRAK; j++) {

                byte[] tmpTrak = new byte[DEF.PACK_LENGTH];
                System.arraycopy(dtl.trackTx[nomor_pu][j], 0, tmpTrak, 0, DEF.PACK_LENGTH);
                StructTrack tmpsTrak = new StructTrack(tmpTrak);
                int val_attribute = tmpsTrak.getAttribute();
                int val_trak_nomor = tmpsTrak.getNumber();
                double val_longitude = tmpsTrak.getLongitude();
                double val_latitude = tmpsTrak.getLatitude();
                double val_speed = tmpsTrak.getSpeed();
                double val_course = tmpsTrak.getCourse();
                int val_height = tmpsTrak.getHeight();
                int val_mmsi = tmpsTrak.getMmsi();

                if (val_attribute > 0) {

                    jTable_trak_tx.setValueAt(nomor_pu, k, 0);
                    jTable_trak_tx.setValueAt(val_mmsi, k, 1);
                    jTable_trak_tx.setValueAt((double) val_trak_nomor, k, 2);
                    jTable_trak_tx.setValueAt(val_longitude, k, 3);
                    jTable_trak_tx.setValueAt(val_latitude, k, 4);
                    jTable_trak_tx.setValueAt(val_speed, k, 5);
                    jTable_trak_tx.setValueAt(val_course, k, 6);
                    jTable_trak_tx.setValueAt((double) val_height, k, 7);
                    jTable_trak_tx.setValueAt((double) val_attribute, k, 8);
                    k++;
                }
            }
        }
        lbl_trak_tx_available.setText("trak tx available = " + k);
    }

    /**
     * Method to get tracks data of table tx and send it to UDP
     */
    private void GUI_get_Tx_Table() {

        StructTrack tmpTrack = new StructTrack();
        int n_itemTx;
        byte[] Bytestrack;
        int row = jTable_trak_tx.getRowCount();

        int count_traks = 0;
        for (int i = 0; i < row; i++) {
            if (jTable_trak_tx.getModel().getValueAt(i, 0) != null) {
                count_traks++;
            }
        }

        n_itemTx = count_traks;
        byte[] udpPacket = new byte[16 + DEF.PACK_LENGTH];
        udpPacket[0] = (byte) DEF.TOPIC_CORE2LOGGER;
        udpPacket[1] = (byte) DEF.TYPE_TX_TRACK;
        udpPacket[3] = (byte) n_itemTx;

        count_traks = 0;
        for (int i = 0; i < row; i++) {
            if (jTable_trak_tx.getModel().getValueAt(i, 0) != null) {

                int owner = (int) jTable_trak_tx.getModel().getValueAt(i, 0);
                tmpTrack.setMmsi((int) jTable_trak_tx.getModel().getValueAt(i, 1));
                int trakno = (int) (double) jTable_trak_tx.getModel().getValueAt(i, 2);
                tmpTrack.setLongitude((double) jTable_trak_tx.getModel().getValueAt(i, 3));
                tmpTrack.setLatitude((double) jTable_trak_tx.getModel().getValueAt(i, 4));
                tmpTrack.setSpeed((double) jTable_trak_tx.getModel().getValueAt(i, 5));
                tmpTrack.setCourse((double) jTable_trak_tx.getModel().getValueAt(i, 6));
                tmpTrack.setHeight((int) (double) jTable_trak_tx.getModel().getValueAt(i, 7));
                tmpTrack.setAttribute((int) (double) jTable_trak_tx.getModel().getValueAt(i, 8));
                tmpTrack.setNumber(trakno);
                tmpTrack.setOwner(owner);

                Bytestrack = tmpTrack.getBytesTrack();

                System.arraycopy(Bytestrack, 0, udpPacket, 16, DEF.PACK_LENGTH);
                count_traks++;
                if (Configuration.udpLoggerOn)
                udpTxDataLogger.sendUdp(udpPacket);
            }
        }
    }

    /**
     * Method to get gps time
     *
     * @return st
     */
    private String get_time() {
        byte[] t = get_time_bytes();
        String st = "[" + t[0] + ":" + t[1] + ":" + t[2] + "]";
        return st;
    }

    private String get_time_utc() {
        byte[] t = get_time_bytes();
        String st = t[0] + ":" + t[1] + ":" + t[2];
        return st;
    }

    /**
     * Method to get time gps bytes
     *
     * @return time_gps_now
     */
    private byte[] get_time_bytes() {

        Date date_tx = new Date();
        int h = date_tx.getHours();
        int m = date_tx.getMinutes();
        int s = date_tx.getSeconds();

        int delta_jam = dtl.delta_gps_time[0];
        int delta_menit = dtl.delta_gps_time[1];
        int delta_detik = dtl.delta_gps_time[2];

        h = h - delta_jam;
        m = m - delta_menit;
        s = s - delta_detik;

        if (s > 59) {
            s = s - 60;
            m = m + 1;
        }

        if (s < 0) {
            s = s + 60;
            m = m - 1;
        }

        if (m > 59) {
            m = m - 60;
            h = h + 1;
        }

        if (m < 0) {
            m = m + 60;
            h = h - 1;
        }

        if (h > 23) {
            h = h - 24;
        }

        if (h < 0) {
            h = h + 24;
        }

        byte[] time_gps_now = new byte[3];
        time_gps_now[0] = (byte) h;
        time_gps_now[1] = (byte) m;
        time_gps_now[2] = (byte) s;

        return time_gps_now;
    }

    private int counter_gps_elapse = 0;

    private void GUI_Show_LED_GPS() {
        if (counter_gps_elapse > 0) {
            counter_gps_elapse--;
            jLabelGpsLed.setForeground(Color.GREEN);
        } else {
            jLabelGpsLed.setForeground(Color.DARK_GRAY);
        }
    }

    /**
     * Method to show UTC Time
     */
    private void GUI_Show_UTC_Time() {
        lbl_gps_time.setText("UTC " + get_time_utc());
    }

    // <editor-fold defaultstate="collapsed" desc="Timer timer"> 
    // =========================================================================
    //                            TIMER
    // =========================================================================
    javax.swing.Timer tscan = new javax.swing.Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            GUI_Show_NPU_Tick();
            GUI_Show_NPU_Tx();

            if (minimode_active) {
                sync.GUI_Show_NPU_Tick(mod_var.getDtdma_npu_txtime());
                sync.GUI_Show_NPU_Active(dtl.npu_life, mod_var.getDtdma_npu_txtime());

            }
            if (dtl.msg_rcv_ready) {
                dtl.msg_rcv_ready = false;
                //msg_receive_txt.append(get_time() + dtl.display_string + "\n");
            }

            GUI_Show_Trak_Rcv();
            GUI_Show_Trak_Tx();

            if (mod_var.isStatus_mc() && !jComboBoxUdpReceiveFromTgDisable.isSelected()) {

                GUI_Show_Trak_Tx();
                mod_var.setStatus_mc(false);
            }

            if (Configuration.modeSync > 0) {
                if (dtl.dtdma_syncronized) {
                    lbl_gps_status.setText("SYNCHRONIZED");
                } else {
                    lbl_gps_status.setText("NOT SYNCHRONIZE");
                }
            }

            //GUI_Show_Progres_FTP();
            GUI_Show_Progres_Msg_File();

            GUI_Show_UTC_Time();

            GUI_Show_LED_GPS();

        }
    });
    // </editor-fold>  

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        msg_send_txt = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        msg_receive_txt = new javax.swing.JTextArea();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable_scenario = new javax.swing.JTable();
        lbl_jml_npu = new javax.swing.JLabel();
        lbl_total_time = new javax.swing.JLabel();
        txt_own_npu = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btn_update_scenario = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jComboBoxGpsPort = new javax.swing.JComboBox();
        jTextFieldGpsBaudrate = new javax.swing.JTextField();
        jTextFielGpsUpdateInterval = new javax.swing.JTextField();
        jCheckBoxGpsUpdate = new javax.swing.JCheckBox();
        jLabelGpsPort = new javax.swing.JLabel();
        jLabelGpsBaudrate = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_datalink_key = new javax.swing.JTextField();
        jComboBoxModeSync = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ComboBox_LineIn = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        jComboBox_LineOut = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable_trak_tx = new javax.swing.JTable();
        btn_update_trak = new javax.swing.JButton();
        lbl_trak_tx_available = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jTabbedPaneMessages = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        sendtxtBtn = new javax.swing.JButton();
        rcvraddtxt = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextAreaSendText = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jLabelCharacterCount = new javax.swing.JLabel();
        jLabelStatusTx = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        txt_msg_file_filename = new javax.swing.JTextField();
        lbl_msg_file_size = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        btn_send_file = new javax.swing.JButton();
        txt_msg_file_rcv_addr = new javax.swing.JTextField();
        lbl_msg_file_progress_tx = new javax.swing.JLabel();
        jProgressBar_msg_file_tx = new javax.swing.JProgressBar();
        lbl_msg_file_progress_rx = new javax.swing.JLabel();
        jProgressBar_msg_file_rx = new javax.swing.JProgressBar();
        btn_set_folder_rx = new javax.swing.JButton();
        jLabel46 = new javax.swing.JLabel();
        lbl_received_folder1 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txt_obj_no = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txt_circle = new javax.swing.JTextField();
        sendcrclbtn = new javax.swing.JButton();
        delscrclbtn = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        cmb_area_color_circle = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        cmb_line_width_circle = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        txt_notes_circle = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        cmb_line_color_circle = new javax.swing.JComboBox();
        chb_enable_properties_circle = new javax.swing.JCheckBox();
        txt_dest_circle = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        sendplbtn = new javax.swing.JButton();
        delsplbtn = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        txt_polyline_long = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        cmb_area_color_polyline = new javax.swing.JComboBox();
        cmb_line_width_polyline = new javax.swing.JComboBox();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        txt_notes_polyline = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        txt_obj_no_polyline = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        cmb_line_color_polyline = new javax.swing.JComboBox();
        chb_enable_properties_polyline = new javax.swing.JCheckBox();
        cmb_line_type_polyline = new javax.swing.JComboBox();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        cmb_arrow_type_polyline = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        txt_dest_polyline = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable_trak_rx = new javax.swing.JTable();
        lbl_trak_rx_available = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jComboBoxUdpReceiveFromTgDisable = new javax.swing.JCheckBox();
        txt_ip_address = new javax.swing.JTextField();
        txt_port_udp = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        cb_snd_mc = new javax.swing.JCheckBox();
        jLabel43 = new javax.swing.JLabel();
        txt_ip_address_remote_generator = new javax.swing.JTextField();
        txt_port_udp_remote_generator = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabelUdpLogger = new javax.swing.JLabel();
        jComboBoxUdpLoggerDisable = new javax.swing.JCheckBox();
        jTextFieldUdpLoggerIpAddress = new javax.swing.JTextField();
        jTextFieldUdpLoggerIpPort = new javax.swing.JTextField();
        jLabelUdpLoggerIpPort = new javax.swing.JLabel();
        jLabelUdpLoggerIpAddress = new javax.swing.JLabel();
        jLabelUdpReceiveFromGps = new javax.swing.JLabel();
        jLabelUdpGpsIpAddress = new javax.swing.JLabel();
        jTextFieldUdpGpsIpAddress = new javax.swing.JTextField();
        jLabelUdpGpsIpPort = new javax.swing.JLabel();
        jTextFieldUdpGpsPort = new javax.swing.JTextField();
        jCheckBoxUdpGpsDisable = new javax.swing.JCheckBox();
        jPanel15 = new javax.swing.JPanel();
        panelConstellation = new javax.swing.JPanel();
        panelOscilloscope = new javax.swing.JPanel();
        jProgressBarTxProgress = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        jProgressBarRxProgress = new javax.swing.JProgressBar();
        jLabel3 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel_preamble_count = new javax.swing.JLabel();
        jLabel_pattern_start = new javax.swing.JLabel();
        jLabel_data_count = new javax.swing.JLabel();
        jLabel_postamble_count = new javax.swing.JLabel();
        jComboBoxPreambleLength = new javax.swing.JComboBox<>();
        jComboBoxPostambleLength = new javax.swing.JComboBox<>();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        lbl_gps_time = new javax.swing.JLabel();
        jProgressBarTxLevel = new javax.swing.JProgressBar();
        jLabel4 = new javax.swing.JLabel();
        lnpu_status_tx = new javax.swing.JLabel();
        lnpu_status_rx = new javax.swing.JLabel();
        jLabelGpsLed = new javax.swing.JLabel();
        lbl_gps_status = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        lnpu1 = new javax.swing.JLabel();
        lnpu2 = new javax.swing.JLabel();
        lnpu3 = new javax.swing.JLabel();
        lnpu4 = new javax.swing.JLabel();
        lnpu5 = new javax.swing.JLabel();
        lnpu6 = new javax.swing.JLabel();
        lnpu7 = new javax.swing.JLabel();
        lnpu8 = new javax.swing.JLabel();
        lnpu9 = new javax.swing.JLabel();
        lnpu10 = new javax.swing.JLabel();
        lnpu11 = new javax.swing.JLabel();
        lnpu12 = new javax.swing.JLabel();
        lnpu13 = new javax.swing.JLabel();
        lnpu14 = new javax.swing.JLabel();
        lnpu15 = new javax.swing.JLabel();
        lnpu16 = new javax.swing.JLabel();
        lnpu17 = new javax.swing.JLabel();
        lnpu18 = new javax.swing.JLabel();
        lnpu19 = new javax.swing.JLabel();
        lnpu20 = new javax.swing.JLabel();
        lnpu21 = new javax.swing.JLabel();
        lnpu22 = new javax.swing.JLabel();
        lnpu23 = new javax.swing.JLabel();
        lnpu24 = new javax.swing.JLabel();
        lnpu25 = new javax.swing.JLabel();
        lnpu26 = new javax.swing.JLabel();
        lnpu27 = new javax.swing.JLabel();
        lnpu28 = new javax.swing.JLabel();
        lnpu29 = new javax.swing.JLabel();
        lnpu30 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        btn_minimode = new javax.swing.JButton();
        chb_silent_mode = new javax.swing.JCheckBox();
        jButtonCoreRun = new javax.swing.JButton();
        jButtonClearLogger = new javax.swing.JButton();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MHS Radio Controller");
        setBackground(new java.awt.Color(204, 204, 204));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        msg_send_txt.setColumns(20);
        msg_send_txt.setRows(5);
        jScrollPane1.setViewportView(msg_send_txt);

        msg_receive_txt.setColumns(20);
        msg_receive_txt.setRows(5);
        jScrollPane2.setViewportView(msg_receive_txt);

        jTabbedPane1.setBackground(new java.awt.Color(204, 204, 204));

        jPanel3.setBackground(java.awt.SystemColor.activeCaptionBorder);
        jPanel3.setForeground(java.awt.Color.darkGray);

        jTable_scenario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                { new Integer(1),  new Integer(4),  new Integer(200)},
                { new Integer(2),  new Integer(4),  new Integer(200)},
                { new Integer(3),  new Integer(4),  new Integer(200)},
                { new Integer(4),  new Integer(4),  new Integer(200)},
                { new Integer(5),  new Integer(4),  new Integer(200)},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NPU", "Item Number", "Delay (ms)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTable_scenario);

        lbl_jml_npu.setText("Jumlah NPU = ");

        lbl_total_time.setText("Total time percycle =  35432 ms");

        txt_own_npu.setText("4");
        txt_own_npu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_own_npuActionPerformed(evt);
            }
        });

        jLabel1.setText("Own NPU");

        btn_update_scenario.setText("Update");
        btn_update_scenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_update_scenarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_jml_npu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(41, 41, 41)
                                .addComponent(txt_own_npu)))
                        .addGap(11, 11, 11))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbl_total_time, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btn_update_scenario)))
                        .addGap(13, 13, 13)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_own_npu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_jml_npu)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_total_time)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_update_scenario, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        jComboBoxGpsPort.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxGpsPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxGpsPortActionPerformed(evt);
            }
        });

        jTextFieldGpsBaudrate.setText("4800");

        jTextFielGpsUpdateInterval.setText("60");
        jTextFielGpsUpdateInterval.setEnabled(false);

        jCheckBoxGpsUpdate.setText("Update");
        jCheckBoxGpsUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxGpsUpdateActionPerformed(evt);
            }
        });

        jLabelGpsPort.setText("GPS Port");

        jLabelGpsBaudrate.setText("Baudrate");

        jLabel7.setText("Encryption");

        txt_datalink_key.setText("LenIndustri442");

        jComboBoxModeSync.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Local PC", "GPS RS232", "GPS UDP" }));
        jComboBoxModeSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxModeSyncActionPerformed(evt);
            }
        });

        jLabel12.setText("Time Sync");

        jLabel6.setText("Line In");

        ComboBox_LineIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_LineInActionPerformed(evt);
            }
        });

        jLabel11.setText("Line Out");

        jComboBox_LineOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_LineOutActionPerformed(evt);
            }
        });

        jLabel13.setText("Data rate modem");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "125 bps", "500 bps", "2000 bps" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ComboBox_LineIn, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox_LineOut, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabelGpsPort, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabelGpsBaudrate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jCheckBoxGpsUpdate)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel12))
                                .addGap(40, 40, 40))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextFielGpsUpdateInterval)
                            .addComponent(jTextFieldGpsBaudrate, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxGpsPort, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxModeSync, 0, 127, Short.MAX_VALUE)
                            .addComponent(txt_datalink_key)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(ComboBox_LineIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_LineOut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(10, 10, 10)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxModeSync, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxGpsPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelGpsPort))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldGpsBaudrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelGpsBaudrate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBoxGpsUpdate)
                    .addComponent(jTextFielGpsUpdateInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_datalink_key, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Setting", jPanel3);

        jPanel4.setBackground(java.awt.SystemColor.activeCaptionBorder);

        jTable_trak_tx.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "NPU", "MMSI", "Trak no", "Longitude", "Latitude", "Speed", "Csr", "Height", "Attribute"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable_trak_tx.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                jTable_trak_txComponentAdded(evt);
            }
        });
        jScrollPane5.setViewportView(jTable_trak_tx);

        btn_update_trak.setText("clear trak");
        btn_update_trak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_update_trakActionPerformed(evt);
            }
        });

        lbl_trak_tx_available.setText("trak tx available = ");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(lbl_trak_tx_available)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_update_trak)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_update_trak)
                    .addComponent(lbl_trak_tx_available))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Tx Track", jPanel4);

        jPanel7.setBackground(java.awt.SystemColor.activeCaptionBorder);

        sendtxtBtn.setText("Send Text");
        sendtxtBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendtxtBtnActionPerformed(evt);
            }
        });

        rcvraddtxt.setText("0");

        jTextAreaSendText.setColumns(20);
        jTextAreaSendText.setRows(5);
        jTextAreaSendText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextAreaSendTextKeyReleased(evt);
            }
        });
        jScrollPane7.setViewportView(jTextAreaSendText);

        jLabel8.setText("To:");

        jLabelCharacterCount.setText("Character count: 0");

        jLabelStatusTx.setForeground(new java.awt.Color(102, 102, 102));
        jLabelStatusTx.setText("Status tx: 0%");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabelStatusTx)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane7)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(sendtxtBtn)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(rcvraddtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addComponent(jLabelCharacterCount)))
                                .addGap(0, 398, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelCharacterCount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rcvraddtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(sendtxtBtn))
                .addGap(5, 5, 5)
                .addComponent(jLabelStatusTx)
                .addContainerGap())
        );

        jTabbedPaneMessages.addTab("Text", jPanel7);

        jPanel8.setBackground(new java.awt.Color(153, 153, 153));

        lbl_msg_file_size.setText("File size = ");

        jButton3.setText("...");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        btn_send_file.setText("send file to");
        btn_send_file.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_send_fileActionPerformed(evt);
            }
        });

        txt_msg_file_rcv_addr.setText("0");

        lbl_msg_file_progress_tx.setText("Tx = 0%");

        lbl_msg_file_progress_rx.setText("Rx = 0%");

        btn_set_folder_rx.setText("Set Folder Rx");
        btn_set_folder_rx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_set_folder_rxActionPerformed(evt);
            }
        });

        jLabel46.setText(" Folder for received file = ");

        lbl_received_folder1.setText("/home/datalink/Desktop");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(txt_msg_file_filename)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(73, 73, 73))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(lbl_msg_file_size, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(btn_send_file, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(txt_msg_file_rcv_addr, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_set_folder_rx, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel46))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_received_folder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jProgressBar_msg_file_rx, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(lbl_msg_file_progress_tx, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jProgressBar_msg_file_tx, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(55, 55, 55))
                            .addComponent(lbl_msg_file_progress_rx, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(166, 166, 166))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_msg_file_filename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_msg_file_size)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_send_file)
                    .addComponent(txt_msg_file_rcv_addr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar_msg_file_tx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_msg_file_progress_tx))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_msg_file_progress_rx)
                    .addComponent(jProgressBar_msg_file_rx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addComponent(btn_set_folder_rx)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(lbl_received_folder1))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jTabbedPaneMessages.addTab("File", jPanel8);

        jPanel9.setBackground(new java.awt.Color(153, 153, 153));

        jLabel9.setText("Circle number");

        txt_obj_no.setText("1");

        jLabel10.setText("longitude, latitude, range");

        txt_circle.setText("104.665,80,36689");

        sendcrclbtn.setText("send  circle");
        sendcrclbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendcrclbtnActionPerformed(evt);
            }
        });

        delscrclbtn.setText("delete circle");
        delscrclbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delscrclbtnActionPerformed(evt);
            }
        });

        jLabel18.setText("Area Color");

        cmb_area_color_circle.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Color", "Red", "Green", "Blue", "Yellow", "Maroon", "Lime", "White" }));

        jLabel19.setText("Line Width");

        cmb_line_width_circle.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0.5", "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0" }));

        jLabel22.setText("Notes");

        txt_notes_circle.setText("Jangan di bom");

        jLabel25.setText("Line Color");

        cmb_line_color_circle.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Black", "Red", "Green", "Blue", "Yellow", "Maroon", "Lime", "White" }));

        chb_enable_properties_circle.setText("Enable properties");
        chb_enable_properties_circle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chb_enable_properties_circleActionPerformed(evt);
            }
        });

        txt_dest_circle.setText("0");

        jLabel20.setText("Destination");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19)
                            .addComponent(jLabel22)
                            .addComponent(jLabel25))
                        .addGap(59, 59, 59)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmb_line_color_circle, 0, 95, Short.MAX_VALUE)
                            .addComponent(cmb_line_width_circle, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txt_circle, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txt_notes_circle, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(18, 18, 18)
                                        .addComponent(txt_obj_no, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(33, 33, 33)
                                        .addComponent(jLabel20))
                                    .addComponent(jLabel10)
                                    .addComponent(chb_enable_properties_circle)
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addGap(110, 110, 110)
                                        .addComponent(cmb_area_color_circle, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(sendcrclbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(delscrclbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txt_dest_circle))))))
                .addContainerGap(170, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txt_obj_no, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_dest_circle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addGap(5, 5, 5)
                .addComponent(jLabel10)
                .addGap(2, 2, 2)
                .addComponent(txt_circle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chb_enable_properties_circle)
                    .addComponent(sendcrclbtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(cmb_area_color_circle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(delscrclbtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(cmb_line_color_circle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmb_line_width_circle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txt_notes_circle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jTabbedPaneMessages.addTab("Circle", jPanel9);

        jPanel10.setBackground(new java.awt.Color(153, 153, 153));

        sendplbtn.setText("send  polyline");
        sendplbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendplbtnActionPerformed(evt);
            }
        });

        delsplbtn.setText("delete polyline");
        delsplbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delsplbtnActionPerformed(evt);
            }
        });

        jLabel23.setText("longitude, latutude, ...");

        txt_polyline_long.setText("104.665, 80.123,103.22, 81.2, 105.21, 80.571");

        jLabel27.setText("Area Color");

        cmb_area_color_polyline.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Color", "Red", "Green", "Blue", "Yellow", "Maroon", "Lime", "White" }));

        cmb_line_width_polyline.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0.5", "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0" }));

        jLabel28.setText("Line Width");

        jLabel29.setText("Notes");

        txt_notes_polyline.setText("Hati-hati daerah musuh");

        jLabel30.setText("Polyline number");

        txt_obj_no_polyline.setText("1");

        jLabel31.setText("Line Color");

        cmb_line_color_polyline.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Black", "Red", "Green", "Blue", "Yellow", "Maroon", "Lime", "White" }));

        chb_enable_properties_polyline.setText("Enable properties");
        chb_enable_properties_polyline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chb_enable_properties_polylineActionPerformed(evt);
            }
        });

        cmb_line_type_polyline.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Solid", "Dash", "Dot", "Dash Dot" }));

        jLabel41.setText("Line Type");

        jLabel42.setText("Arrow Type");

        cmb_arrow_type_polyline.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Arrow", "Begin Arrow", "End Arrow", "Begin and End" }));

        jLabel21.setText("Destination");

        txt_dest_polyline.setText("0");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(chb_enable_properties_polyline)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27)
                            .addComponent(jLabel28)
                            .addComponent(jLabel29)
                            .addComponent(jLabel31))
                        .addGap(59, 59, 59)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmb_line_width_polyline, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel10Layout.createSequentialGroup()
                                                .addComponent(cmb_line_color_polyline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(30, 30, 30)
                                                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel10Layout.createSequentialGroup()
                                                .addComponent(cmb_area_color_polyline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(25, 25, 25)
                                                .addComponent(jLabel41)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(cmb_line_type_polyline, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmb_arrow_type_polyline, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(sendplbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(delsplbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)))
                            .addComponent(txt_notes_polyline)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addGap(18, 18, 18)
                        .addComponent(txt_obj_no_polyline, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(91, 91, 91)
                        .addComponent(jLabel21)
                        .addGap(18, 18, 18)
                        .addComponent(txt_dest_polyline, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_polyline_long))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_dest_polyline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21))
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel30)
                        .addComponent(txt_obj_no_polyline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_polyline_long, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(chb_enable_properties_polyline)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cmb_line_type_polyline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(sendplbtn))
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel27)
                                .addComponent(cmb_area_color_polyline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel41)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel31)
                            .addComponent(cmb_line_color_polyline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(delsplbtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cmb_line_width_polyline, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_notes_polyline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel42)
                            .addComponent(cmb_arrow_type_polyline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jTabbedPaneMessages.addTab("Polyline", jPanel10);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jTabbedPaneMessages)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPaneMessages)
        );

        jTabbedPane1.addTab("Message", jPanel5);

        jTable_trak_rx.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "From NPU", "MMSI", "Trak no", "Longitude", "Latitude", "Speed", "Csr", "Height", "Attribute"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane6.setViewportView(jTable_trak_rx);

        lbl_trak_rx_available.setText("trak rx available = ");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                    .addComponent(lbl_trak_rx_available))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_trak_rx_available)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Received Track", jPanel6);

        jPanel1.setBackground(java.awt.SystemColor.activeCaptionBorder);

        jComboBoxUdpReceiveFromTgDisable.setText("Disable Receiving Data");
        jComboBoxUdpReceiveFromTgDisable.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jComboBoxUdpReceiveFromTgDisableStateChanged(evt);
            }
        });
        jComboBoxUdpReceiveFromTgDisable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxUdpReceiveFromTgDisableActionPerformed(evt);
            }
        });

        txt_ip_address.setBackground(new java.awt.Color(204, 204, 204));
        txt_ip_address.setText("localhost");
        txt_ip_address.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_ip_addressActionPerformed(evt);
            }
        });

        txt_port_udp.setText("1112");
        txt_port_udp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_port_udpActionPerformed(evt);
            }
        });

        jLabel24.setText("PORT");

        jLabel16.setText("IP");

        jLabel45.setFont(new java.awt.Font("Droid Sans Fallback", 1, 14)); // NOI18N
        jLabel45.setText("UDP Receive From TG");

        jLabel44.setFont(new java.awt.Font("Droid Sans Fallback", 1, 14)); // NOI18N
        jLabel44.setText("UDP Transmit To TG");

        cb_snd_mc.setText("Disable Transmit Data");
        cb_snd_mc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_snd_mcActionPerformed(evt);
            }
        });

        jLabel43.setText("IP");

        txt_ip_address_remote_generator.setText("localhost");

        txt_port_udp_remote_generator.setText("1111");

        jLabel26.setText("PORT");

        jLabelUdpLogger.setFont(new java.awt.Font("Droid Sans Fallback", 1, 14)); // NOI18N
        jLabelUdpLogger.setText("UDP Transmit To Debugger");

        jComboBoxUdpLoggerDisable.setText("Disable Transmit Data");
        jComboBoxUdpLoggerDisable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxUdpLoggerDisableActionPerformed(evt);
            }
        });

        jTextFieldUdpLoggerIpAddress.setText("localhost");

        jTextFieldUdpLoggerIpPort.setText("1001");

        jLabelUdpLoggerIpPort.setText("PORT");

        jLabelUdpLoggerIpAddress.setText("IP");

        jLabelUdpReceiveFromGps.setFont(new java.awt.Font("Droid Sans Fallback", 1, 14)); // NOI18N
        jLabelUdpReceiveFromGps.setText("UDP Receive GPS");

        jLabelUdpGpsIpAddress.setText("IP");

        jTextFieldUdpGpsIpAddress.setBackground(new java.awt.Color(204, 204, 204));
        jTextFieldUdpGpsIpAddress.setText("localhost");

        jLabelUdpGpsIpPort.setText("PORT");

        jTextFieldUdpGpsPort.setText("4242");

        jCheckBoxUdpGpsDisable.setText("Disable Receiving Data");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel16)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_ip_address, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel24)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt_port_udp, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel45)
                        .addComponent(jComboBoxUdpReceiveFromTgDisable)
                        .addComponent(jLabelUdpReceiveFromGps))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelUdpGpsIpPort)
                                    .addComponent(jLabelUdpGpsIpAddress))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldUdpGpsIpAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldUdpGpsPort, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jCheckBoxUdpGpsDisable))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 105, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabelUdpLoggerIpPort)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextFieldUdpLoggerIpPort, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(47, 47, 47))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabelUdpLogger)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabelUdpLoggerIpAddress)
                                    .addGap(28, 28, 28)
                                    .addComponent(jTextFieldUdpLoggerIpAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel26)
                                        .addComponent(jLabel43))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txt_ip_address_remote_generator, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txt_port_udp_remote_generator, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGap(49, 49, 49)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxUdpLoggerDisable)
                            .addComponent(jLabel44)
                            .addComponent(cb_snd_mc))
                        .addGap(55, 55, 55))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel45)
                        .addGap(15, 15, 15)
                        .addComponent(jComboBoxUdpReceiveFromTgDisable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_ip_address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_port_udp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel44)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cb_snd_mc)
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_ip_address_remote_generator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel43))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_port_udp_remote_generator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26))))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelUdpLogger)
                    .addComponent(jLabelUdpReceiveFromGps))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBoxUdpLoggerDisable)
                            .addComponent(jCheckBoxUdpGpsDisable))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldUdpLoggerIpAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelUdpLoggerIpAddress))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldUdpLoggerIpPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelUdpLoggerIpPort)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldUdpGpsIpAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelUdpGpsIpAddress))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldUdpGpsPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelUdpGpsIpPort))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Connection", jPanel1);

        panelConstellation.setBackground(new java.awt.Color(50, 50, 50));
        panelConstellation.setPreferredSize(new java.awt.Dimension(140, 140));

        javax.swing.GroupLayout panelConstellationLayout = new javax.swing.GroupLayout(panelConstellation);
        panelConstellation.setLayout(panelConstellationLayout);
        panelConstellationLayout.setHorizontalGroup(
            panelConstellationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );
        panelConstellationLayout.setVerticalGroup(
            panelConstellationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );

        panelOscilloscope.setBackground(new java.awt.Color(50, 50, 50));
        panelOscilloscope.setPreferredSize(new java.awt.Dimension(140, 140));

        javax.swing.GroupLayout panelOscilloscopeLayout = new javax.swing.GroupLayout(panelOscilloscope);
        panelOscilloscope.setLayout(panelOscilloscopeLayout);
        panelOscilloscopeLayout.setHorizontalGroup(
            panelOscilloscopeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 406, Short.MAX_VALUE)
        );
        panelOscilloscopeLayout.setVerticalGroup(
            panelOscilloscopeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );

        jProgressBarTxProgress.setStringPainted(true);

        jLabel2.setText("Tx Progress");

        jProgressBarRxProgress.setStringPainted(true);

        jLabel3.setText("Rx Progress");

        jPanel16.setBackground(new java.awt.Color(204, 204, 204));

        jLabel5.setText("Preamble");

        jLabel14.setText("Data");

        jLabel15.setText("Pattern");

        jLabel17.setText("Postamble");

        jLabel_preamble_count.setText("OK");

        jLabel_pattern_start.setText("OK");

        jLabel_data_count.setText("OK");

        jLabel_postamble_count.setText("OK");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_preamble_count, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(jLabel_pattern_start, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel_data_count, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel_postamble_count, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel_preamble_count))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel_pattern_start))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel_data_count))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel_postamble_count))
                .addContainerGap())
        );

        jComboBoxPreambleLength.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "32", "64", "128", "256" }));
        jComboBoxPreambleLength.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxPreambleLengthActionPerformed(evt);
            }
        });

        jComboBoxPostambleLength.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "32", "64", "128", "256" }));
        jComboBoxPostambleLength.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxPostambleLengthActionPerformed(evt);
            }
        });

        jLabel32.setText("Preamble Length");

        jLabel33.setText("Postamble Length");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(panelConstellation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelOscilloscope, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel32))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jProgressBarTxProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jProgressBarRxProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jComboBoxPreambleLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBoxPostambleLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelConstellation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelOscilloscope, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jProgressBarTxProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jProgressBarRxProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBoxPreambleLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxPostambleLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32)
                            .addComponent(jLabel33)))
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Modem", jPanel15);

        jPanel12.setBackground(new java.awt.Color(204, 204, 204));

        lbl_gps_time.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.foreground"));
        lbl_gps_time.setText("UTC 07:30:00");

        jProgressBarTxLevel.setStringPainted(true);

        jLabel4.setText("Rx Level");

        lnpu_status_tx.setBackground(new java.awt.Color(153, 153, 153));
        lnpu_status_tx.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu_status_tx.setText("TX");
        lnpu_status_tx.setOpaque(true);
        lnpu_status_tx.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu_status_rx.setBackground(new java.awt.Color(153, 153, 153));
        lnpu_status_rx.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu_status_rx.setText("RX");
        lnpu_status_rx.setOpaque(true);
        lnpu_status_rx.setPreferredSize(new java.awt.Dimension(30, 20));

        jLabelGpsLed.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.foreground"));
        jLabelGpsLed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelGpsLed.setText("GPS");

        lbl_gps_status.setText("NOT SYNCRONIZED");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_gps_time)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelGpsLed, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_gps_status)
                .addGap(51, 51, 51)
                .addComponent(lnpu_status_tx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lnpu_status_rx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jProgressBarTxLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_gps_time, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jProgressBarTxLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu_status_tx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu_status_rx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelGpsLed, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_gps_status))
                .addGap(10, 10, 10))
        );

        jPanel13.setBackground(new java.awt.Color(204, 204, 204));

        lnpu1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu1.setText("1");
        lnpu1.setOpaque(true);
        lnpu1.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu2.setText("2");
        lnpu2.setOpaque(true);
        lnpu2.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu3.setText("3");
        lnpu3.setOpaque(true);
        lnpu3.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu4.setText("4");
        lnpu4.setOpaque(true);
        lnpu4.setPreferredSize(new java.awt.Dimension(30, 20));
        lnpu4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lnpu4MouseClicked(evt);
            }
        });

        lnpu5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu5.setText("5");
        lnpu5.setOpaque(true);
        lnpu5.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu6.setText("6");
        lnpu6.setOpaque(true);
        lnpu6.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu7.setText("7");
        lnpu7.setOpaque(true);
        lnpu7.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu8.setText("8");
        lnpu8.setOpaque(true);
        lnpu8.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu9.setText("9");
        lnpu9.setOpaque(true);
        lnpu9.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu10.setText("10");
        lnpu10.setOpaque(true);
        lnpu10.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu11.setText("11");
        lnpu11.setOpaque(true);
        lnpu11.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu12.setText("12");
        lnpu12.setOpaque(true);
        lnpu12.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu13.setText("13");
        lnpu13.setOpaque(true);
        lnpu13.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu14.setText("14");
        lnpu14.setOpaque(true);
        lnpu14.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu15.setText("15");
        lnpu15.setOpaque(true);
        lnpu15.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu16.setText("16");
        lnpu16.setOpaque(true);
        lnpu16.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu17.setText("17");
        lnpu17.setOpaque(true);
        lnpu17.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu18.setText("18");
        lnpu18.setOpaque(true);
        lnpu18.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu19.setText("19");
        lnpu19.setOpaque(true);
        lnpu19.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu20.setText("20");
        lnpu20.setOpaque(true);
        lnpu20.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu21.setText("21");
        lnpu21.setOpaque(true);
        lnpu21.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu22.setText("22");
        lnpu22.setOpaque(true);
        lnpu22.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu23.setText("23");
        lnpu23.setOpaque(true);
        lnpu23.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu24.setText("24");
        lnpu24.setOpaque(true);
        lnpu24.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu25.setText("25");
        lnpu25.setOpaque(true);
        lnpu25.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu26.setText("26");
        lnpu26.setOpaque(true);
        lnpu26.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu27.setText("27");
        lnpu27.setOpaque(true);
        lnpu27.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu28.setText("28");
        lnpu28.setOpaque(true);
        lnpu28.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu29.setText("29");
        lnpu29.setOpaque(true);
        lnpu29.setPreferredSize(new java.awt.Dimension(30, 20));

        lnpu30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lnpu30.setText("30");
        lnpu30.setOpaque(true);
        lnpu30.setPreferredSize(new java.awt.Dimension(30, 20));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lnpu2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lnpu16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lnpu30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel14.setBackground(new java.awt.Color(204, 204, 204));

        btn_minimode.setText("Mini mode");
        btn_minimode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_minimodeActionPerformed(evt);
            }
        });

        chb_silent_mode.setText("Silent Mode");
        chb_silent_mode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chb_silent_modeActionPerformed(evt);
            }
        });

        jButtonCoreRun.setText("Run");
        jButtonCoreRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCoreRunActionPerformed(evt);
            }
        });

        jButtonClearLogger.setText("Clear Log");
        jButtonClearLogger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearLoggerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_minimode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonClearLogger)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chb_silent_mode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonCoreRun, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_minimode)
                    .addComponent(chb_silent_mode)
                    .addComponent(jButtonCoreRun)
                    .addComponent(jButtonClearLogger))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2))
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
/**
     * Method to show and add port list
     */
    private void GUI_addPortList() {
        //cmb_port_data.removeAllItems();
        jComboBoxGpsPort.removeAllItems();
        String[] portNames = SerialPortList.getPortNames();
        for (int i = 0; i < portNames.length; i++) {
            //cmb_port_data.addItem(portNames[i]);
            jComboBoxGpsPort.addItem(portNames[i]);
        }
        //System.out.println("  " + Arrays.toString(portNames));
    }

    /**
     * Method to update Tracks data in table tx
     */
    private void GUI_Update_Trak_Tx() {

        StructTrack trakTmp = new StructTrack();

        int row = jTable_trak_tx.getRowCount();

        int count_trak = 0;
        for (int i = 0; i < row; i++) {
            if (jTable_trak_tx.getModel().getValueAt(i, 0) != null) {

                count_trak++;
                int owner = (int) jTable_trak_tx.getModel().getValueAt(i, 0);
                trakTmp.setMmsi((int) jTable_trak_tx.getModel().getValueAt(i, 1));
                int trakno = (int) (double) jTable_trak_tx.getModel().getValueAt(i, 2);
                trakTmp.setLongitude((double) jTable_trak_tx.getModel().getValueAt(i, 3));
                trakTmp.setLatitude((double) jTable_trak_tx.getModel().getValueAt(i, 4));
                trakTmp.setSpeed((double) jTable_trak_tx.getModel().getValueAt(i, 5));
                trakTmp.setCourse((double) jTable_trak_tx.getModel().getValueAt(i, 6));
                trakTmp.setHeight((int) (double) jTable_trak_tx.getModel().getValueAt(i, 7));
                trakTmp.setAttribute((int) (double) jTable_trak_tx.getModel().getValueAt(i, 8));
                trakTmp.setNumber(trakno);
                trakTmp.setOwner(owner);

                byte[] trakBytes = trakTmp.getBytesTrack();
                dtl.UpdateTrak(trakBytes, owner, trakno);
                dtl.list_no_traks[i] = trakno;
                dtl.list_no_npu[i] = owner;
            }
        }
        mod_var.setItem_request(count_trak);
        dtl.item_counter = 0;
    }

    /**
     * Method to update Scenario
     */
    private void GUI_Update_Scenario() {

        dtl.GPS_TIMEOUT = 60 * Integer.parseInt(jTextFielGpsUpdateInterval.getText());

        int row = jTable_scenario.getRowCount();
        int col = jTable_scenario.getColumnCount();

        int count_npu = 0;
        //String isi_Scenario;
        for (int i = 0; i < row; i++) {
            if (jTable_scenario.getModel().getValueAt(i, 0) != null) {
                count_npu++;
                //isi_Scenario = "";
                for (int j = 0; j < col; j++) {
                    dtl.dtdma_scenario[i][j] = (int) jTable_scenario.getModel().getValueAt(i, j);
                    //isi_Scenario = isi_Scenario + jTable_scenario.getModel().getValueAt(i, j).toString() + "#";
                }
                //String[] parts = isi_Scenario.split("#");
                //xml.updt(parts[0], parts[1], parts[2]);
            }
        }
        int[] rate = {125, 500, 2000};
        int data_rate = rate[jComboBox1.getSelectedIndex()] / 2;
        dtl.dtdma_Tpkcg = (32 * 8 * 1000) / data_rate;//Integer.parseInt(txt_data_rate.getText()) + 1;
        dtl.dtdma_step = count_npu;
        dtl.dtdma_skey = txt_datalink_key.getText();
        mod_var.setOwnnpu(Byte.valueOf(txt_own_npu.getText()));
        dtl.ftp.own_npu = Byte.valueOf(txt_own_npu.getText());
        int tcycle = dtl.Tcycle();
        lbl_jml_npu.setText("Jumlah NPU = " + count_npu);
        lbl_total_time.setText("Total time per cycle = " + tcycle + " ms");
    }
    
    private void GUI_UpdateModeSync()
    {
        switch (Configuration.modeSync)
        {
            case 0:
                jLabelGpsPort.setEnabled(false);
                jLabelGpsBaudrate.setEnabled(false);
                jComboBoxGpsPort.setEnabled(false);
                jTextFieldGpsBaudrate.setEnabled(false);
                jCheckBoxGpsUpdate.setEnabled(false);
                jTextFielGpsUpdateInterval.setEnabled(false);
                jLabelUdpReceiveFromGps.setEnabled(false);
                jCheckBoxUdpGpsDisable.setEnabled(false);
                jTextFieldUdpGpsIpAddress.setEnabled(false);
                jTextFieldUdpGpsPort.setEnabled(false);
                jLabelUdpGpsIpAddress.setEnabled(false);
                jLabelUdpGpsIpPort.setEnabled(false);                
                break;
                
            case 1:
                jLabelGpsPort.setEnabled(true);
                jLabelGpsBaudrate.setEnabled(true);
                jComboBoxGpsPort.setEnabled(true);
                jTextFieldGpsBaudrate.setEnabled(true);
                jCheckBoxGpsUpdate.setEnabled(true);
                if (Configuration.gpsUpdateInterval > 0)
                   jTextFielGpsUpdateInterval.setEnabled(true);
                else
                   jTextFielGpsUpdateInterval.setEnabled(false); 
                jLabelUdpReceiveFromGps.setEnabled(false);
                jCheckBoxUdpGpsDisable.setEnabled(false);
                jTextFieldUdpGpsIpAddress.setEnabled(false);
                jTextFieldUdpGpsPort.setEnabled(false);
                jLabelUdpGpsIpAddress.setEnabled(false);
                jLabelUdpGpsIpPort.setEnabled(false);
                break;
                
            case 2:
                jLabelGpsPort.setEnabled(false);
                jLabelGpsBaudrate.setEnabled(false);
                jComboBoxGpsPort.setEnabled(false);
                jTextFieldGpsBaudrate.setEnabled(false);
                jCheckBoxGpsUpdate.setEnabled(true);
                if (Configuration.gpsUpdateInterval > 0)
                   jTextFielGpsUpdateInterval.setEnabled(true);
                else
                   jTextFielGpsUpdateInterval.setEnabled(false); 
                jLabelUdpReceiveFromGps.setEnabled(true);
                jCheckBoxUdpGpsDisable.setEnabled(true);
                jTextFieldUdpGpsIpAddress.setEnabled(true);
                jTextFieldUdpGpsPort.setEnabled(true);
                jLabelUdpGpsIpAddress.setEnabled(true);
                jLabelUdpGpsIpPort.setEnabled(true); 
                break;
        }
    }

    private void lnpu4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lnpu4MouseClicked
    }//GEN-LAST:event_lnpu4MouseClicked

    private void jButtonCoreRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCoreRunActionPerformed

        if (Configuration.modeSync == 1) {
            //con_modem = new WTConnection2(cmb_port_data.getSelectedItem().toString());
            con_gps = new WTConnection2(jComboBoxGpsPort.getSelectedItem().toString());
            //con_modem.OpenConnection(Integer.parseInt(txt_boudrate_modem.getText()));
            con_gps.OpenConnection(Integer.parseInt(jTextFieldGpsBaudrate.getText()));
            dtl.initialKoneksi(con_modem, con_gps);           
        } else {
            //con_modem = new WTConnection2(cmb_port_data.getSelectedItem().toString());
            //con_modem.OpenConnection(Integer.parseInt(txt_boudrate_modem.getText()));
            //dtl.initialModemKoneksi(con_modem);
        }
        
        

        dtl.ftp.setConnection(con_modem);
        mod_var.setOwnnpu(Byte.valueOf(txt_own_npu.getText()));
        dtl.ftp.own_npu = Byte.valueOf(txt_own_npu.getText());
        dtl.dtdma_skey = txt_datalink_key.getText();
        dtl.thread_dtdma.start();
        if (Configuration.udpLoggerOn)
            udpTxDataLogger.setSocket(Integer.valueOf(jTextFieldUdpLoggerIpPort.getText()), jTextFieldUdpLoggerIpAddress.getText());
        udpTxDataGenerator.setSocket(Integer.parseInt(txt_port_udp_remote_generator.getText()), txt_ip_address_remote_generator.getText());
        udpRxData.setSocket(Integer.valueOf(txt_port_udp.getText()), txt_ip_address.getText());
        udpRxData.receiveStart();
        
        if (Configuration.modeSync == 2) {
            udpRxGps.setSocket(Integer.valueOf(jTextFieldUdpGpsPort.getText()), jTextFieldUdpGpsIpAddress.getText());
            udpRxGps.receiveStart();
        }

        tscan.start();
        dataScan.start();
        tmout.start();
        jButtonCoreRun.setEnabled(false);
        dtl.dtdma_silent_mode = chb_silent_mode.isSelected();
        if (Configuration.modeSync == 0) {
            dtl.dtdma_syncronized = true;
        }
        jTabbedPaneMessages.setVisible(false);


    }//GEN-LAST:event_jButtonCoreRunActionPerformed
//    private void reload_table() {
//        int a = xml.Daftar_skenario.size();
//        for (int i = 0; i < a; i++) {
//            String b = xml.Daftar_skenario.get(i);
//            String[] parts = b.split("#");
//
//            String NPU = parts[0];
//            String item = parts[1];
//            String delay = parts[2];
//            String[] row = {NPU, item, delay};
//
//            jTable_scenario.setValueAt(Integer.parseInt(NPU), i, 0);
//            jTable_scenario.setValueAt(Integer.parseInt(item), i, 1);
//            jTable_scenario.setValueAt(Integer.parseInt(delay), i, 2);
//
//        }
//        txt_boudrate_modem.setText(xml.Daftar_port[1][1]);
//        jTextFieldGpsBaudrate.setText(xml.Daftar_port[0][1]);
//        txt_datalink_key.setText(xml.Daftar_setting[0]);
//        txt_own_npu.setText(xml.Daftar_setting[1]);
//        txt_data_rate.setText(xml.Daftar_setting[2]);
//    }
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        //GUI_addPortList();
        //xml.read();
        //if (xml.isExisting_file()) {
        //    reload_table();
        //}
        //GUI_Update_Scenario();
        //GUI_Update_Trak_Tx();
    }//GEN-LAST:event_formWindowOpened
    public int MAX_FILE_LENGTH = 1024 * 1024;
    String filename;
    JFileChooser fileChooser = new JFileChooser();
    private void chb_silent_modeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chb_silent_modeActionPerformed
        dtl.dtdma_silent_mode = chb_silent_mode.isSelected();
    }//GEN-LAST:event_chb_silent_modeActionPerformed
    private byte[] msg_file;

    private boolean mini_mode = false;
    private void btn_minimodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_minimodeActionPerformed
        sync = new sync_view(this);
        sync.setVisible(true);
        minimode_active = true;
        dispose();

        //jTabbedPane1.setVisible(mini_mode);        
        //if (mini_mode) mini_mode = false;
        //else mini_mode = true;        
    }//GEN-LAST:event_btn_minimodeActionPerformed

    private void cb_snd_mcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_snd_mcActionPerformed
        if (cb_snd_mc.isSelected()) {
        } else {
        }
    }//GEN-LAST:event_cb_snd_mcActionPerformed

    private void txt_ip_addressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_ip_addressActionPerformed
    }//GEN-LAST:event_txt_ip_addressActionPerformed

    private void jComboBoxUdpReceiveFromTgDisableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxUdpReceiveFromTgDisableActionPerformed
        udpFromTgDisable = jComboBoxUdpReceiveFromTgDisable.isSelected();
        //jTabbedPaneMessages.setEnabled(udpFromTgDisable);
        jTabbedPaneMessages.setVisible(udpFromTgDisable);

        if (jComboBoxUdpReceiveFromTgDisable.isSelected()) {
        } else {
        }
    }//GEN-LAST:event_jComboBoxUdpReceiveFromTgDisableActionPerformed

    private void jComboBoxUdpReceiveFromTgDisableStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jComboBoxUdpReceiveFromTgDisableStateChanged
    }//GEN-LAST:event_jComboBoxUdpReceiveFromTgDisableStateChanged

    private void chb_enable_properties_polylineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chb_enable_properties_polylineActionPerformed
    }//GEN-LAST:event_chb_enable_properties_polylineActionPerformed

    private void delsplbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delsplbtnActionPerformed
        int polyline_number = Integer.valueOf(txt_obj_no_polyline.getText());
        byte destination_address = Byte.valueOf(txt_dest_polyline.getText());
        byte source_address = Byte.valueOf(txt_own_npu.getText());
        StructPolyline polyline = new StructPolyline();
        byte[] data_polyline = polyline.getBytesDeletePolyline(polyline_number);
        dtl.sendmsg(data_polyline, DEF.MSG_TYPE_DEL_POLYLINE, source_address, destination_address);
        msg_send_txt.append(get_time() + " Delete polyline  \n");
    }//GEN-LAST:event_delsplbtnActionPerformed

    private void sendplbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendplbtnActionPerformed
        int idx_area_color = cmb_area_color_polyline.getSelectedIndex();
        int idx_line_color = cmb_line_color_polyline.getSelectedIndex();
        int idx_line_width = cmb_line_width_polyline.getSelectedIndex();
        int idx_line_type = cmb_line_type_polyline.getSelectedIndex();
        int idx_arrow_type = cmb_arrow_type_polyline.getSelectedIndex();
        StructPolyline polyline = new StructPolyline();
        byte destination_address = Byte.valueOf(txt_dest_circle.getText());
        byte source_address = Byte.valueOf(txt_own_npu.getText());
        int polyline_number = Integer.valueOf(txt_obj_no_polyline.getText());
        String string_polyline = txt_polyline_long.getText();

        if (chb_enable_properties_polyline.isSelected()) {

            String notes = txt_notes_polyline.getText();
            byte[] property = polyline.setProperties(idx_area_color, idx_line_color, idx_line_width, idx_line_type, idx_arrow_type, notes);

            byte[] data_polyline = polyline.getBytesPolyline(string_polyline, polyline_number, property);
            dtl.sendmsg(data_polyline, DEF.MSG_TYPE_DRAW_POLYLINE, source_address, destination_address);
        } else {
            byte[] data_polyline = polyline.getBytesPolyline(string_polyline, polyline_number);
            dtl.sendmsg(data_polyline, DEF.MSG_TYPE_DRAW_POLYLINE, source_address, destination_address);
        }
        msg_send_txt.append(get_time() + " Send polyline  " + txt_polyline_long.getText() + "\n");
    }//GEN-LAST:event_sendplbtnActionPerformed

    private void chb_enable_properties_circleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chb_enable_properties_circleActionPerformed
    }//GEN-LAST:event_chb_enable_properties_circleActionPerformed

    private void delscrclbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delscrclbtnActionPerformed
        int circle_number = Integer.valueOf(txt_obj_no.getText());
        byte destination_address = Byte.valueOf(rcvraddtxt.getText());
        byte source_address = Byte.valueOf(txt_own_npu.getText());
        StructCircle circle = new StructCircle();
        byte[] data_circle = circle.getBytesDeleteCircle(circle_number);
        dtl.sendmsg(data_circle, DEF.MSG_TYPE_DEL_CIRCLE, source_address, destination_address);
        msg_send_txt.append(get_time() + " Delete circle  \n");
    }//GEN-LAST:event_delscrclbtnActionPerformed

    private void sendcrclbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendcrclbtnActionPerformed
        StructCircle circle = new StructCircle();
        byte destination_address = Byte.valueOf(txt_dest_circle.getText());
        byte source_address = Byte.valueOf(txt_own_npu.getText());
        int circle_number = Integer.valueOf(txt_obj_no.getText());
        String scircle = txt_circle.getText();

        if (chb_enable_properties_circle.isSelected()) {
            int idx_area_color = cmb_area_color_circle.getSelectedIndex();
            int idx_line_color = cmb_line_color_circle.getSelectedIndex();
            int idx_line_width = cmb_line_width_circle.getSelectedIndex();
            String notes = txt_notes_circle.getText();
            byte[] property = circle.setProperties(idx_area_color, idx_line_color, idx_line_width, notes);

            byte[] data_circle = circle.getBytesCircle(scircle, circle_number, property);
            dtl.sendmsg(data_circle, DEF.MSG_TYPE_DRAW_CIRCLE, source_address, destination_address);
        } else {
            byte[] data_circle = circle.getBytesCircle(scircle, circle_number);
            dtl.sendmsg(data_circle, DEF.MSG_TYPE_DRAW_CIRCLE, source_address, destination_address);
        }
        msg_send_txt.append(get_time() + " Send circle " + txt_circle.getText() + "\n");
    }//GEN-LAST:event_sendcrclbtnActionPerformed

    private void btn_set_folder_rxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_set_folder_rxActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new java.io.File("."));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File rx_folder = fc.getSelectedFile();
            dtl.setDirectory_rx(rx_folder.getAbsolutePath());
            lbl_received_folder1.setText(rx_folder.getAbsolutePath());
        }
    }//GEN-LAST:event_btn_set_folder_rxActionPerformed

    private void btn_send_fileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_send_fileActionPerformed
        String sfilename = txt_msg_file_filename.getText();
        byte destination_address = Byte.valueOf(txt_msg_file_rcv_addr.getText());
        byte source_address = Byte.valueOf(txt_own_npu.getText());
        StructFile fileTx = new StructFile();
        byte[] data_file = fileTx.getBytesFile(sfilename, msg_file);
        dtl.sendmsg(data_file, DEF.MSG_TYPE_FTP, source_address, destination_address);
    }//GEN-LAST:event_btn_send_fileActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            int msg_file_length = (int) selectedFile.length();

            txt_msg_file_filename.setText(selectedFile.getName());
            lbl_msg_file_size.setText("File size = " + msg_file_length + " bytes");
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(selectedFile));
                msg_file = new byte[msg_file_length];
                bis.read(msg_file, 0, msg_file_length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void sendtxtBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendtxtBtnActionPerformed
        StructText text = new StructText();
        byte destination_address = Byte.valueOf(rcvraddtxt.getText());
        byte source_address = Byte.valueOf(txt_own_npu.getText());
        int text_number = 0;
        byte[] data_text = text.getBytesText(jTextAreaSendText.getText(), text_number);
        dtl.sendmsg(data_text, DEF.MSG_TYPE_TEXT, source_address, destination_address);
        msg_send_txt.append(get_time() + " SEND TEXT " + jTextAreaSendText.getText().length() + " BYTES\n");
    }//GEN-LAST:event_sendtxtBtnActionPerformed

    private void btn_update_trakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_update_trakActionPerformed
        dtl.ClearTrak();
        lbl_trak_tx_available.setText("trak tx available = " + 0);
    }//GEN-LAST:event_btn_update_trakActionPerformed

    private void btn_update_scenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_update_scenarioActionPerformed
//        for (int i = 0; i < MAX_NPU + 1; i++) {
//            xml.updt(String.valueOf(i), "-", "-");
//        }
        GUI_Update_Scenario();
        GUI_Update_Trak_Tx();
//        xml.updt_sett(txt_datalink_key.getText(), txt_own_npu.getText(), txt_data_rate.getText());
//        xml.updt_port(jComboBoxGpsPort.getSelectedItem().toString(),
//                cmb_port_data.getSelectedItem().toString(),
//                jTextFieldGpsBaudrate.getText(),
//                txt_boudrate_modem.getText());
    }//GEN-LAST:event_btn_update_scenarioActionPerformed

    private void txt_own_npuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_own_npuActionPerformed
    }//GEN-LAST:event_txt_own_npuActionPerformed

    private void jCheckBoxGpsUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxGpsUpdateActionPerformed
        boolean gpsUpdateEnable = jCheckBoxGpsUpdate.isSelected();
        if (gpsUpdateEnable)
            Configuration.gpsUpdateInterval = Integer.parseInt(jTextFielGpsUpdateInterval.getText());
        else
            Configuration.gpsUpdateInterval = 0;
        dtl.update_periodic = gpsUpdateEnable;
        jTextFielGpsUpdateInterval.setEnabled(gpsUpdateEnable);
    }//GEN-LAST:event_jCheckBoxGpsUpdateActionPerformed

    private void jComboBoxGpsPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxGpsPortActionPerformed
    }//GEN-LAST:event_jComboBoxGpsPortActionPerformed

    private void jComboBoxUdpLoggerDisableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxUdpLoggerDisableActionPerformed
    }//GEN-LAST:event_jComboBoxUdpLoggerDisableActionPerformed

    private void txt_port_udpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_port_udpActionPerformed
    }//GEN-LAST:event_txt_port_udpActionPerformed

    private void jTable_trak_txComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_jTable_trak_txComponentAdded
    }//GEN-LAST:event_jTable_trak_txComponentAdded

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        GUI_SaveConfiguration();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonClearLoggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearLoggerActionPerformed
        msg_send_txt.setText("");
        msg_receive_txt.setText("");
    }//GEN-LAST:event_jButtonClearLoggerActionPerformed

    private void jTextAreaSendTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextAreaSendTextKeyReleased
        jLabelCharacterCount.setText("Character count: " + jTextAreaSendText.getText().length());
        jLabelStatusTx.setText("Status tx: ");
    }//GEN-LAST:event_jTextAreaSendTextKeyReleased

    private void jComboBoxModeSyncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxModeSyncActionPerformed
        Configuration.modeSync = jComboBoxModeSync.getSelectedIndex();
        GUI_UpdateModeSync();
    }//GEN-LAST:event_jComboBoxModeSyncActionPerformed

    private void ComboBox_LineInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_LineInActionPerformed
        // TODO add your handling code here:
        if (init_selesai == true) {
            int idx = ComboBox_LineIn.getSelectedIndex();
            if (idx != selectedDeviceInputIndex) {
                dtl.StartMonitoringLevelsOnMixer(ComboBox_LineIn.getSelectedItem().toString());
            }
            selectedDeviceInputIndex = idx;
        }
    }//GEN-LAST:event_ComboBox_LineInActionPerformed

    private void jComboBox_LineOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_LineOutActionPerformed
        // TODO add your handling code here:
        if (init_selesai == true) {
            int idx = jComboBox_LineOut.getSelectedIndex();
            if (idx != selectedDeviceOutputIndex) {
                dtl.StartPlayingSound(jComboBox_LineOut.getSelectedItem().toString());
            }
            selectedDeviceOutputIndex = idx;
        }
    }//GEN-LAST:event_jComboBox_LineOutActionPerformed

    private void jComboBoxPreambleLengthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxPreambleLengthActionPerformed
        int val = Integer.parseInt(jComboBoxPreambleLength.getSelectedItem().toString());
        dtl.setPreambleLength(val);
    }//GEN-LAST:event_jComboBoxPreambleLengthActionPerformed

    private void jComboBoxPostambleLengthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxPostambleLengthActionPerformed
        int val = Integer.parseInt(jComboBoxPostambleLength.getSelectedItem().toString());
        dtl.setPostambleLength(val);
    }//GEN-LAST:event_jComboBoxPostambleLengthActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        dtl.setDataRate(jComboBox1.getSelectedIndex());
    }//GEN-LAST:event_jComboBox1ActionPerformed

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainTest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainTest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainTest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainTest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainTest().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox ComboBox_LineIn;
    private javax.swing.JButton btn_minimode;
    private javax.swing.JButton btn_send_file;
    private javax.swing.JButton btn_set_folder_rx;
    private javax.swing.JButton btn_update_scenario;
    private javax.swing.JButton btn_update_trak;
    private javax.swing.JCheckBox cb_snd_mc;
    private javax.swing.JCheckBox chb_enable_properties_circle;
    private javax.swing.JCheckBox chb_enable_properties_polyline;
    private javax.swing.JCheckBox chb_silent_mode;
    private javax.swing.JComboBox cmb_area_color_circle;
    private javax.swing.JComboBox cmb_area_color_polyline;
    private javax.swing.JComboBox cmb_arrow_type_polyline;
    private javax.swing.JComboBox cmb_line_color_circle;
    private javax.swing.JComboBox cmb_line_color_polyline;
    private javax.swing.JComboBox cmb_line_type_polyline;
    private javax.swing.JComboBox cmb_line_width_circle;
    private javax.swing.JComboBox cmb_line_width_polyline;
    private javax.swing.JButton delscrclbtn;
    private javax.swing.JButton delsplbtn;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButtonClearLogger;
    private javax.swing.JButton jButtonCoreRun;
    private javax.swing.JCheckBox jCheckBoxGpsUpdate;
    private javax.swing.JCheckBox jCheckBoxUdpGpsDisable;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox jComboBoxGpsPort;
    private javax.swing.JComboBox<String> jComboBoxModeSync;
    private javax.swing.JComboBox<String> jComboBoxPostambleLength;
    private javax.swing.JComboBox<String> jComboBoxPreambleLength;
    private javax.swing.JCheckBox jComboBoxUdpLoggerDisable;
    private javax.swing.JCheckBox jComboBoxUdpReceiveFromTgDisable;
    private javax.swing.JComboBox jComboBox_LineOut;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCharacterCount;
    private javax.swing.JLabel jLabelGpsBaudrate;
    private javax.swing.JLabel jLabelGpsLed;
    private javax.swing.JLabel jLabelGpsPort;
    private javax.swing.JLabel jLabelStatusTx;
    private javax.swing.JLabel jLabelUdpGpsIpAddress;
    private javax.swing.JLabel jLabelUdpGpsIpPort;
    private javax.swing.JLabel jLabelUdpLogger;
    private javax.swing.JLabel jLabelUdpLoggerIpAddress;
    private javax.swing.JLabel jLabelUdpLoggerIpPort;
    private javax.swing.JLabel jLabelUdpReceiveFromGps;
    private javax.swing.JLabel jLabel_data_count;
    private javax.swing.JLabel jLabel_pattern_start;
    private javax.swing.JLabel jLabel_postamble_count;
    private javax.swing.JLabel jLabel_preamble_count;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBarRxProgress;
    private javax.swing.JProgressBar jProgressBarTxLevel;
    private javax.swing.JProgressBar jProgressBarTxProgress;
    private javax.swing.JProgressBar jProgressBar_msg_file_rx;
    private javax.swing.JProgressBar jProgressBar_msg_file_tx;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPaneMessages;
    private javax.swing.JTable jTable_scenario;
    private javax.swing.JTable jTable_trak_rx;
    private javax.swing.JTable jTable_trak_tx;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextAreaSendText;
    private javax.swing.JTextField jTextFielGpsUpdateInterval;
    private javax.swing.JTextField jTextFieldGpsBaudrate;
    private javax.swing.JTextField jTextFieldUdpGpsIpAddress;
    private javax.swing.JTextField jTextFieldUdpGpsPort;
    private javax.swing.JTextField jTextFieldUdpLoggerIpAddress;
    private javax.swing.JTextField jTextFieldUdpLoggerIpPort;
    private javax.swing.JLabel lbl_gps_status;
    private javax.swing.JLabel lbl_gps_time;
    private javax.swing.JLabel lbl_jml_npu;
    private javax.swing.JLabel lbl_msg_file_progress_rx;
    private javax.swing.JLabel lbl_msg_file_progress_tx;
    private javax.swing.JLabel lbl_msg_file_size;
    private javax.swing.JLabel lbl_received_folder1;
    private javax.swing.JLabel lbl_total_time;
    private javax.swing.JLabel lbl_trak_rx_available;
    private javax.swing.JLabel lbl_trak_tx_available;
    private javax.swing.JLabel lnpu1;
    private javax.swing.JLabel lnpu10;
    private javax.swing.JLabel lnpu11;
    private javax.swing.JLabel lnpu12;
    private javax.swing.JLabel lnpu13;
    private javax.swing.JLabel lnpu14;
    private javax.swing.JLabel lnpu15;
    private javax.swing.JLabel lnpu16;
    private javax.swing.JLabel lnpu17;
    private javax.swing.JLabel lnpu18;
    private javax.swing.JLabel lnpu19;
    private javax.swing.JLabel lnpu2;
    private javax.swing.JLabel lnpu20;
    private javax.swing.JLabel lnpu21;
    private javax.swing.JLabel lnpu22;
    private javax.swing.JLabel lnpu23;
    private javax.swing.JLabel lnpu24;
    private javax.swing.JLabel lnpu25;
    private javax.swing.JLabel lnpu26;
    private javax.swing.JLabel lnpu27;
    private javax.swing.JLabel lnpu28;
    private javax.swing.JLabel lnpu29;
    private javax.swing.JLabel lnpu3;
    private javax.swing.JLabel lnpu30;
    private javax.swing.JLabel lnpu4;
    private javax.swing.JLabel lnpu5;
    private javax.swing.JLabel lnpu6;
    private javax.swing.JLabel lnpu7;
    private javax.swing.JLabel lnpu8;
    private javax.swing.JLabel lnpu9;
    private javax.swing.JLabel lnpu_status_rx;
    private javax.swing.JLabel lnpu_status_tx;
    private javax.swing.JTextArea msg_receive_txt;
    private javax.swing.JTextArea msg_send_txt;
    private javax.swing.JPanel panelConstellation;
    private javax.swing.JPanel panelOscilloscope;
    private javax.swing.JTextField rcvraddtxt;
    private javax.swing.JButton sendcrclbtn;
    private javax.swing.JButton sendplbtn;
    private javax.swing.JButton sendtxtBtn;
    private javax.swing.JTextField txt_circle;
    private javax.swing.JTextField txt_datalink_key;
    private javax.swing.JTextField txt_dest_circle;
    private javax.swing.JTextField txt_dest_polyline;
    private javax.swing.JTextField txt_ip_address;
    private javax.swing.JTextField txt_ip_address_remote_generator;
    private javax.swing.JTextField txt_msg_file_filename;
    private javax.swing.JTextField txt_msg_file_rcv_addr;
    private javax.swing.JTextField txt_notes_circle;
    private javax.swing.JTextField txt_notes_polyline;
    private javax.swing.JTextField txt_obj_no;
    private javax.swing.JTextField txt_obj_no_polyline;
    private javax.swing.JTextField txt_own_npu;
    private javax.swing.JTextField txt_polyline_long;
    private javax.swing.JTextField txt_port_udp;
    private javax.swing.JTextField txt_port_udp_remote_generator;
    // End of variables declaration//GEN-END:variables
}
