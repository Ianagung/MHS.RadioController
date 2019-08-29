/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.len.tdl.view.ryt;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import jssc.SerialPortList;
import org.len.tdl.core.DEF;

/**
 *
 * @author riyanto
 */
public class ConfigurationGui extends javax.swing.JFrame {

    /**
     * Creates new form ConfigurationGui
     */
    
    private final File configFile = new File("config.properties");
    private Properties configProps;
    public int[][] dtdma_scenario = new int[DEF.MAX_NPU][3];
    
    
    
    public ConfigurationGui() {
        initComponents();        
        GUI_addPortList();
        GUI_LoadConfiguration();
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
        defaultProps.setProperty("SILENT_MODE", Boolean.toString(Configuration.silentMode));
        defaultProps.setProperty("AUTORUN", Boolean.toString(Configuration.autorun));
        defaultProps.setProperty("MODE_SYNC", Integer.toString(Configuration.modeSync));
        defaultProps.setProperty("UPD_LOGGER_ON", Boolean.toString(Configuration.udpLoggerOn));
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
        Configuration.silentMode = Boolean.valueOf(configProps.getProperty("SILENT_MODE"));
        Configuration.autorun = Boolean.valueOf(configProps.getProperty("AUTORUN"));
        Configuration.modeSync = Integer.valueOf(configProps.getProperty("MODE_SYNC"));
        Configuration.udpLoggerOn = Boolean.valueOf(configProps.getProperty("UDP_LOGGER_ON"));
        
    }

    private void GUI_LoadConfiguration() {
        // LOAD FILE PROPERTIES
        try {
            loadProperties();   
            jLabelStatus.setForeground(Color.blue);
            jLabelStatus.setText("The config.properties file exist, properties loaded.");
        } catch (IOException ex) {
            System.out.println("The config.properties file does not exist, default properties loaded.");
            jLabelStatus.setForeground(Color.red);
            jLabelStatus.setText("The config.properties file does not exist, default properties loaded.");
        }

        // UPDATE TO GUI
        txt_own_npu.setText(Integer.toString(Configuration.ownNpu));
        txt_boudrate_modem.setText(Integer.toString(Configuration.modemBaudrate));
        jTextFieldComGpsBaudrate.setText(Integer.toString(Configuration.gpsBaudrate));
        txt_datalink_key.setText(Configuration.key);
        txt_data_rate.setText(Integer.toString(Configuration.modemDatarate));
        boolean gpsUpdate = Configuration.gpsUpdateInterval != 0;
        jCheckBoxGpsUpdate.setSelected(gpsUpdate);
        jTextFielGpsUpdateInterval.setEnabled(gpsUpdate);
        if (gpsUpdate) {
            jTextFielGpsUpdateInterval.setText(Integer.toString(Configuration.gpsUpdateInterval));
        }
        jCheckBoxSilentMode.setSelected(Configuration.silentMode);
        jComboBoxModeSync.setSelectedIndex(Configuration.modeSync);
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
            cmb_port_data.setSelectedIndex(indexModemPort);
        }
        if (indexGpsPort >= 0) {
            jComboBoxComGpsPort.setSelectedIndex(indexGpsPort);
        }

        jTextFieldUdpFromTgIpAddress.setText(Configuration.udpRxAddress);
        jTextFieldUdpFromTgIpPort.setText(Integer.toString(Configuration.udpRxPort));
        jTextFieldUdpToTgIpAddress.setText(Configuration.udpTxAddress);
        jTextFieldUdpToTgIpPort.setText(Integer.toString(Configuration.udpTxPort));
        jTextFieldUdpGpsIpAddress.setText(Configuration.udpGpsAddress);
        jTextFieldUdpGpsPort.setText(Integer.toString(Configuration.udpGpsPort));
        jTextFieldUdpLoggerIpAddress.setText(Configuration.udpLoggerAddress);
        jTextFieldUdpLoggerIpPort.setText(Integer.toString(Configuration.udpLoggerPort));
        jCheckBoxAutorun.setSelected(Configuration.autorun);
        jCheckBoxUdpLogger.setSelected(Configuration.udpLoggerOn);
        jLabelUdpLogger.setEnabled(Configuration.udpLoggerOn);
        jLabelUdpLoggerIpAddress.setEnabled(Configuration.udpLoggerOn);
        jLabelUdpLoggerIpPort.setEnabled(Configuration.udpLoggerOn);
        jTextFieldUdpLoggerIpAddress.setEnabled(Configuration.udpLoggerOn);
        jTextFieldUdpLoggerIpPort.setEnabled(Configuration.udpLoggerOn);

        GUI_UpdateModeSync();
        GUI_Update_Scenario();
    }

    private void GUI_SaveConfiguration() {
        // GET DATA CONFIG FROM GUI
        Configuration.ownNpu = Integer.valueOf(txt_own_npu.getText());
        Configuration.modemBaudrate = Integer.valueOf(txt_boudrate_modem.getText());
        Configuration.gpsBaudrate = Integer.valueOf(jTextFieldComGpsBaudrate.getText());
        Configuration.key = txt_datalink_key.getText();
        Configuration.modemDatarate = Integer.valueOf(txt_data_rate.getText());
        Configuration.modemPort = cmb_port_data.getSelectedItem().toString();
        Configuration.gpsPort = jComboBoxComGpsPort.getSelectedItem().toString();
        if (jCheckBoxGpsUpdate.isSelected()) {
            Configuration.gpsUpdateInterval = Integer.valueOf(jTextFielGpsUpdateInterval.getText());
        } else {
            Configuration.gpsUpdateInterval = 0;
        }
        Configuration.silentMode = jCheckBoxSilentMode.isSelected();
        Configuration.udpRxAddress = jTextFieldUdpFromTgIpAddress.getText();
        Configuration.udpTxAddress = jTextFieldUdpToTgIpAddress.getText();
        Configuration.udpRxPort = Integer.valueOf(jTextFieldUdpFromTgIpPort.getText());
        Configuration.udpTxPort = Integer.valueOf(jTextFieldUdpToTgIpPort.getText());
        Configuration.udpGpsAddress = jTextFieldUdpGpsIpAddress.getText();
        Configuration.udpGpsPort = Integer.valueOf(jTextFieldUdpGpsPort.getText());
        Configuration.udpLoggerAddress = jTextFieldUdpLoggerIpAddress.getText();
        Configuration.udpLoggerPort = Integer.valueOf(jTextFieldUdpLoggerIpPort.getText());
        Configuration.udpLoggerOn = jCheckBoxUdpLogger.isSelected();
        Configuration.autorun = jCheckBoxAutorun.isSelected();
        Configuration.modeSync = jComboBoxModeSync.getSelectedIndex();

        GUI_Get_Table_Scenario();
        GUI_Update_Scenario();

        // SAVE TO CONFIGURATION FILE
        try {
            saveProperties();
        } catch (IOException ex) {
            System.out.println("The config.properties file does not exist");
        }
    }
    
    private void GUI_ResetConfiguration() {
        // LOAD FILE PROPERTIES
        try {
            loadProperties();   
            jLabelStatus.setForeground(Color.blue);
            jLabelStatus.setText("The config.properties file exist, properties loaded.");
        } catch (IOException ex) {
            System.out.println("The config.properties file does not exist, default properties loaded.");
            jLabelStatus.setForeground(Color.red);
            jLabelStatus.setText("The config.properties file does not exist, default properties loaded.");
        }

        // UPDATE TO GUI
        txt_own_npu.setText(Integer.toString(Configuration.ownNpu));
        txt_boudrate_modem.setText(Integer.toString(Configuration.modemBaudrate));
        jTextFieldComGpsBaudrate.setText(Integer.toString(Configuration.gpsBaudrate));
        txt_datalink_key.setText(Configuration.key);
        txt_data_rate.setText(Integer.toString(Configuration.modemDatarate));
        boolean gpsUpdate = Configuration.gpsUpdateInterval != 0;
        jCheckBoxGpsUpdate.setSelected(gpsUpdate);
        jTextFielGpsUpdateInterval.setEnabled(gpsUpdate);
        if (gpsUpdate) {
            jTextFielGpsUpdateInterval.setText(Integer.toString(Configuration.gpsUpdateInterval));
        }
        jCheckBoxSilentMode.setSelected(Configuration.silentMode);
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
            cmb_port_data.setSelectedIndex(indexModemPort);
        }
        if (indexGpsPort >= 0) {
            jComboBoxComGpsPort.setSelectedIndex(indexGpsPort);
        }

        jTextFieldUdpFromTgIpAddress.setText(Configuration.udpRxAddress);
        jTextFieldUdpFromTgIpPort.setText(Integer.toString(Configuration.udpRxPort));
        jTextFieldUdpToTgIpAddress.setText(Configuration.udpTxAddress);
        jTextFieldUdpToTgIpPort.setText(Integer.toString(Configuration.udpTxPort));
        jTextFieldUdpGpsIpAddress.setText(Configuration.udpGpsAddress);
        jTextFieldUdpGpsPort.setText(Integer.toString(Configuration.udpGpsPort));
        jTextFieldUdpLoggerIpAddress.setText(Configuration.udpLoggerAddress);
        jTextFieldUdpLoggerIpPort.setText(Integer.toString(Configuration.udpLoggerPort));
        jCheckBoxAutorun.setSelected(Configuration.autorun);
        jCheckBoxUdpLogger.setSelected(Configuration.udpLoggerOn);
        jComboBoxModeSync.setSelectedIndex(Configuration.modeSync);

        GUI_UpdateModeSync();
        GUI_Update_Scenario();
    }
    
    private void GUI_UpdateModeSync()
    {
        switch (Configuration.modeSync)
        {
            case 0:
                jLabelComGpsPort.setEnabled(false);
                jLabelComGpsBaudrate.setEnabled(false);
                jComboBoxComGpsPort.setEnabled(false);
                jTextFieldComGpsBaudrate.setEnabled(false);
                jCheckBoxGpsUpdate.setEnabled(false);
                jTextFielGpsUpdateInterval.setEnabled(false);
                jLabelUdpReceiveFromGps.setEnabled(false);
                jTextFieldUdpGpsIpAddress.setEnabled(false);
                jTextFieldUdpGpsPort.setEnabled(false);
                jLabelUdpGpsIpAddress.setEnabled(false);
                jLabelUdpGpsIpPort.setEnabled(false);                
                break;
                
            case 1:
                jLabelComGpsPort.setEnabled(true);
                jLabelComGpsBaudrate.setEnabled(true);
                jComboBoxComGpsPort.setEnabled(true);
                jTextFieldComGpsBaudrate.setEnabled(true);
                jCheckBoxGpsUpdate.setEnabled(true);
                if (Configuration.gpsUpdateInterval > 0)
                   jTextFielGpsUpdateInterval.setEnabled(true);
                else
                   jTextFielGpsUpdateInterval.setEnabled(false); 
                jLabelUdpReceiveFromGps.setEnabled(false);
                jTextFieldUdpGpsIpAddress.setEnabled(false);
                jTextFieldUdpGpsPort.setEnabled(false);
                jLabelUdpGpsIpAddress.setEnabled(false);
                jLabelUdpGpsIpPort.setEnabled(false);
                break;
                
            case 2:
                jLabelComGpsPort.setEnabled(false);
                jLabelComGpsBaudrate.setEnabled(false);
                jComboBoxComGpsPort.setEnabled(false);
                jTextFieldComGpsBaudrate.setEnabled(false);
                jCheckBoxGpsUpdate.setEnabled(true);
                if (Configuration.gpsUpdateInterval > 0)
                   jTextFielGpsUpdateInterval.setEnabled(true);
                else
                   jTextFielGpsUpdateInterval.setEnabled(false); 
                jLabelUdpReceiveFromGps.setEnabled(true);
                jTextFieldUdpGpsIpAddress.setEnabled(true);
                jTextFieldUdpGpsPort.setEnabled(true);
                jLabelUdpGpsIpAddress.setEnabled(true);
                jLabelUdpGpsIpPort.setEnabled(true); 
                break;
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
     * Method to update Scenario
     */
    private void GUI_Update_Scenario() {


        int row = jTable_scenario.getRowCount();
        int col = jTable_scenario.getColumnCount();

        int count_npu = 0;
        for (int i = 0; i < row; i++) {
            if (jTable_scenario.getModel().getValueAt(i, 0) != null) {
                count_npu++;
                for (int j = 0; j < col; j++) {
                    dtdma_scenario[i][j] = (int) jTable_scenario.getModel().getValueAt(i, j);
                }
            }
        }        
        int dtdma_Tpkcg = (32 * 8 * 1000) / Integer.parseInt(txt_data_rate.getText()) + 1;
        String dtdma_skey = txt_datalink_key.getText();

        int t1 = 0;
        for (int i = 0; i < count_npu; i++) {
            t1 = t1 + dtdma_Tpkcg * dtdma_scenario[i][1] + dtdma_scenario[i][2];
        }

        lbl_jml_npu.setText("Jumlah NPU = " + count_npu);
        lbl_total_time.setText("Total time per cycle = " + t1 + " ms");
    }

    /**
     * Method to show and add port list
     */
    private void GUI_addPortList() {
        cmb_port_data.removeAllItems();
        jComboBoxComGpsPort.removeAllItems();
        String[] portNames = SerialPortList.getPortNames();
        for (int i = 0; i < portNames.length; i++) {
            cmb_port_data.addItem(portNames[i]);
            jComboBoxComGpsPort.addItem(portNames[i]);
        }
        //System.out.println("  " + Arrays.toString(portNames));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel11 = new javax.swing.JPanel();
        txt_own_npu = new javax.swing.JTextField();
        cmb_port_data = new javax.swing.JComboBox();
        txt_boudrate_modem = new javax.swing.JTextField();
        jComboBoxComGpsPort = new javax.swing.JComboBox();
        jTextFieldComGpsBaudrate = new javax.swing.JTextField();
        jTextFielGpsUpdateInterval = new javax.swing.JTextField();
        jCheckBoxGpsUpdate = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabelComGpsPort = new javax.swing.JLabel();
        jLabelComGpsBaudrate = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_datalink_key = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jComboBoxModeSync = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable_scenario = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        txt_data_rate = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        lbl_jml_npu = new javax.swing.JLabel();
        lbl_total_time = new javax.swing.JLabel();
        jButtonUpdateScenario = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        jTextFieldUdpFromTgIpAddress = new javax.swing.JTextField();
        jTextFieldUdpFromTgIpPort = new javax.swing.JTextField();
        jLabelIpPortFromTg = new javax.swing.JLabel();
        jLabelUdpReceiveFromGps = new javax.swing.JLabel();
        jLabelUdpGpsIpAddress = new javax.swing.JLabel();
        jTextFieldUdpGpsIpAddress = new javax.swing.JTextField();
        jLabelUdpGpsIpPort = new javax.swing.JLabel();
        jTextFieldUdpGpsPort = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jTextFieldUdpToTgIpAddress = new javax.swing.JTextField();
        jLabelUdpToTgIpPort = new javax.swing.JLabel();
        jTextFieldUdpToTgIpPort = new javax.swing.JTextField();
        jLabelUdpLogger = new javax.swing.JLabel();
        jLabelUdpLoggerIpAddress = new javax.swing.JLabel();
        jLabelUdpLoggerIpPort = new javax.swing.JLabel();
        jTextFieldUdpLoggerIpAddress = new javax.swing.JTextField();
        jTextFieldUdpLoggerIpPort = new javax.swing.JTextField();
        jLabelIpAddressFromTg = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButtonLoadConfiguration = new javax.swing.JButton();
        jButtonSaveConfiguration = new javax.swing.JButton();
        jButtonResetConfiguration = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jCheckBoxSilentMode = new javax.swing.JCheckBox();
        jCheckBoxUdpLogger = new javax.swing.JCheckBox();
        jCheckBoxAutorun = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jLabelStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MHS Configuration");
        setResizable(false);

        jPanel11.setBackground(new java.awt.Color(204, 204, 204));

        txt_own_npu.setText("4");
        txt_own_npu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_own_npuActionPerformed(evt);
            }
        });

        cmb_port_data.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_port_data.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_port_dataActionPerformed(evt);
            }
        });

        txt_boudrate_modem.setText("9600");

        jComboBoxComGpsPort.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxComGpsPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxComGpsPortActionPerformed(evt);
            }
        });

        jTextFieldComGpsBaudrate.setText("4800");

        jTextFielGpsUpdateInterval.setText("60");
        jTextFielGpsUpdateInterval.setEnabled(false);

        jCheckBoxGpsUpdate.setText("Update (min)");
        jCheckBoxGpsUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxGpsUpdateActionPerformed(evt);
            }
        });

        jLabel1.setText("Own NPU");

        jLabel3.setText("Modem Port");

        jLabel5.setText("Baudrate");

        jLabelComGpsPort.setText("GPS Port");

        jLabelComGpsBaudrate.setText("Baudrate");

        jLabel7.setText("Encryption Key");

        txt_datalink_key.setText("LenIndustri442");

        jLabel12.setText("Time Sync");

        jComboBoxModeSync.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Local PC", "GPS RS232", "GPS UDP" }));
        jComboBoxModeSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxModeSyncActionPerformed(evt);
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
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmb_port_data, 0, 103, Short.MAX_VALUE)
                                    .addComponent(txt_boudrate_modem, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                                    .addComponent(jComboBoxComGpsPort, 0, 103, Short.MAX_VALUE)
                                    .addComponent(jTextFieldComGpsBaudrate, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                                    .addComponent(jTextFielGpsUpdateInterval, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                                    .addComponent(txt_datalink_key, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                                    .addComponent(jComboBoxModeSync, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(txt_own_npu, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabelComGpsPort, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelComGpsBaudrate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jCheckBoxGpsUpdate)
                            .addComponent(jLabel7)
                            .addComponent(jLabel12))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_own_npu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmb_port_data, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_boudrate_modem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxModeSync, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxComGpsPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelComGpsPort))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldComGpsBaudrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelComGpsBaudrate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxGpsUpdate)
                    .addComponent(jTextFielGpsUpdateInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_datalink_key, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(29, 29, 29))
        );

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

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

        jLabel13.setText("Data rate modem");

        txt_data_rate.setText("300");

        jLabel14.setText("bps");

        lbl_jml_npu.setText("Jumlah NPU = ");

        lbl_total_time.setText("Total time percycle =  35432 ms");

        jButtonUpdateScenario.setText("Update");
        jButtonUpdateScenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateScenarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lbl_total_time, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_jml_npu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_data_rate, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14))
                            .addComponent(jButtonUpdateScenario))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txt_data_rate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_jml_npu)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_total_time)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonUpdateScenario, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jLabel45.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel45.setText("UDP Receive From TG");

        jTextFieldUdpFromTgIpAddress.setBackground(new java.awt.Color(204, 204, 204));
        jTextFieldUdpFromTgIpAddress.setText("localhost");
        jTextFieldUdpFromTgIpAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldUdpFromTgIpAddressActionPerformed(evt);
            }
        });

        jTextFieldUdpFromTgIpPort.setText("1112");
        jTextFieldUdpFromTgIpPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldUdpFromTgIpPortActionPerformed(evt);
            }
        });

        jLabelIpPortFromTg.setText("PORT");

        jLabelUdpReceiveFromGps.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelUdpReceiveFromGps.setText("UDP Receive From GPS");

        jLabelUdpGpsIpAddress.setText("IP");

        jTextFieldUdpGpsIpAddress.setBackground(new java.awt.Color(204, 204, 204));
        jTextFieldUdpGpsIpAddress.setText("localhost");

        jLabelUdpGpsIpPort.setText("PORT");

        jTextFieldUdpGpsPort.setText("4242");

        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel44.setText("UDP Transmit To TG");

        jLabel43.setText("IP");

        jTextFieldUdpToTgIpAddress.setText("localhost");

        jLabelUdpToTgIpPort.setText("PORT");

        jTextFieldUdpToTgIpPort.setText("1111");

        jLabelUdpLogger.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelUdpLogger.setText("UDP Transmit To Logger");

        jLabelUdpLoggerIpAddress.setText("IP");

        jLabelUdpLoggerIpPort.setText("PORT");

        jTextFieldUdpLoggerIpAddress.setText("localhost");

        jTextFieldUdpLoggerIpPort.setText("1001");

        jLabelIpAddressFromTg.setText("IP");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelIpAddressFromTg)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextFieldUdpFromTgIpAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelIpPortFromTg)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldUdpFromTgIpPort, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel45)
                            .addComponent(jLabelUdpReceiveFromGps)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelUdpGpsIpPort)
                                    .addComponent(jLabelUdpGpsIpAddress))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldUdpGpsIpAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldUdpGpsPort, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelUdpLogger)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelUdpToTgIpPort)
                                    .addComponent(jLabel43))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldUdpToTgIpAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldUdpToTgIpPort, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel44)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelUdpLoggerIpPort)
                                    .addComponent(jLabelUdpLoggerIpAddress))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextFieldUdpLoggerIpPort, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                                    .addComponent(jTextFieldUdpLoggerIpAddress))))
                        .addContainerGap(86, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel45)
                    .addComponent(jLabel44, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldUdpFromTgIpAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelIpAddressFromTg))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldUdpFromTgIpPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelIpPortFromTg)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldUdpToTgIpAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel43))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldUdpToTgIpPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelUdpToTgIpPort))))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelUdpLogger)
                    .addComponent(jLabelUdpReceiveFromGps))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldUdpGpsIpAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelUdpGpsIpAddress)
                    .addComponent(jLabelUdpLoggerIpAddress)
                    .addComponent(jTextFieldUdpLoggerIpAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldUdpGpsPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelUdpGpsIpPort)
                    .addComponent(jLabelUdpLoggerIpPort)
                    .addComponent(jTextFieldUdpLoggerIpPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));

        jButtonLoadConfiguration.setText("Load");
        jButtonLoadConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoadConfigurationActionPerformed(evt);
            }
        });

        jButtonSaveConfiguration.setText("Save");
        jButtonSaveConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveConfigurationActionPerformed(evt);
            }
        });

        jButtonResetConfiguration.setText("Reset");
        jButtonResetConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetConfigurationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(315, Short.MAX_VALUE)
                .addComponent(jButtonResetConfiguration)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonLoadConfiguration)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSaveConfiguration)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonResetConfiguration, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonSaveConfiguration, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonLoadConfiguration, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));

        jCheckBoxSilentMode.setText("Silent Mode");
        jCheckBoxSilentMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxSilentModeActionPerformed(evt);
            }
        });

        jCheckBoxUdpLogger.setSelected(true);
        jCheckBoxUdpLogger.setText("Udp Logger");
        jCheckBoxUdpLogger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxUdpLoggerActionPerformed(evt);
            }
        });

        jCheckBoxAutorun.setSelected(true);
        jCheckBoxAutorun.setText("Autorun");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxSilentMode)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxUdpLogger)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxAutorun)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxSilentMode)
                    .addComponent(jCheckBoxUdpLogger)
                    .addComponent(jCheckBoxAutorun))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabelStatus.setText("Status: ");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_own_npuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_own_npuActionPerformed

    }//GEN-LAST:event_txt_own_npuActionPerformed

    private void cmb_port_dataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_port_dataActionPerformed

    }//GEN-LAST:event_cmb_port_dataActionPerformed

    private void jComboBoxComGpsPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxComGpsPortActionPerformed

    }//GEN-LAST:event_jComboBoxComGpsPortActionPerformed

    private void jCheckBoxGpsUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxGpsUpdateActionPerformed
        jTextFielGpsUpdateInterval.setEnabled(jCheckBoxGpsUpdate.isSelected());
    }//GEN-LAST:event_jCheckBoxGpsUpdateActionPerformed

    private void jTextFieldUdpFromTgIpAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldUdpFromTgIpAddressActionPerformed

    }//GEN-LAST:event_jTextFieldUdpFromTgIpAddressActionPerformed

    private void jTextFieldUdpFromTgIpPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldUdpFromTgIpPortActionPerformed

    }//GEN-LAST:event_jTextFieldUdpFromTgIpPortActionPerformed

    private void jCheckBoxSilentModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxSilentModeActionPerformed
        
    }//GEN-LAST:event_jCheckBoxSilentModeActionPerformed

    private void jButtonUpdateScenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateScenarioActionPerformed
        GUI_Update_Scenario();
    }//GEN-LAST:event_jButtonUpdateScenarioActionPerformed

    private void jButtonSaveConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveConfigurationActionPerformed
        GUI_SaveConfiguration();
    }//GEN-LAST:event_jButtonSaveConfigurationActionPerformed

    private void jButtonLoadConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLoadConfigurationActionPerformed
        GUI_LoadConfiguration();
    }//GEN-LAST:event_jButtonLoadConfigurationActionPerformed

    private void jCheckBoxUdpLoggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxUdpLoggerActionPerformed
        boolean loggerOn = jCheckBoxUdpLogger.isSelected();
        jLabelUdpLogger.setEnabled(loggerOn);
        jLabelUdpLoggerIpAddress.setEnabled(loggerOn);
        jLabelUdpLoggerIpPort.setEnabled(loggerOn);
        jTextFieldUdpLoggerIpAddress.setEnabled(loggerOn);
        jTextFieldUdpLoggerIpPort.setEnabled(loggerOn);
    }//GEN-LAST:event_jCheckBoxUdpLoggerActionPerformed

    private void jButtonResetConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetConfigurationActionPerformed
        GUI_ResetConfiguration();
    }//GEN-LAST:event_jButtonResetConfigurationActionPerformed

    private void jComboBoxModeSyncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxModeSyncActionPerformed
        Configuration.modeSync = jComboBoxModeSync.getSelectedIndex();
        GUI_UpdateModeSync();
    }//GEN-LAST:event_jComboBoxModeSyncActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ConfigurationGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConfigurationGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConfigurationGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConfigurationGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConfigurationGui().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmb_port_data;
    private javax.swing.JButton jButtonLoadConfiguration;
    private javax.swing.JButton jButtonResetConfiguration;
    private javax.swing.JButton jButtonSaveConfiguration;
    private javax.swing.JButton jButtonUpdateScenario;
    private javax.swing.JCheckBox jCheckBoxAutorun;
    private javax.swing.JCheckBox jCheckBoxGpsUpdate;
    private javax.swing.JCheckBox jCheckBoxSilentMode;
    private javax.swing.JCheckBox jCheckBoxUdpLogger;
    private javax.swing.JComboBox jComboBoxComGpsPort;
    private javax.swing.JComboBox<String> jComboBoxModeSync;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelComGpsBaudrate;
    private javax.swing.JLabel jLabelComGpsPort;
    private javax.swing.JLabel jLabelIpAddressFromTg;
    private javax.swing.JLabel jLabelIpPortFromTg;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelUdpGpsIpAddress;
    private javax.swing.JLabel jLabelUdpGpsIpPort;
    private javax.swing.JLabel jLabelUdpLogger;
    private javax.swing.JLabel jLabelUdpLoggerIpAddress;
    private javax.swing.JLabel jLabelUdpLoggerIpPort;
    private javax.swing.JLabel jLabelUdpReceiveFromGps;
    private javax.swing.JLabel jLabelUdpToTgIpPort;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable_scenario;
    private javax.swing.JTextField jTextFielGpsUpdateInterval;
    private javax.swing.JTextField jTextFieldComGpsBaudrate;
    private javax.swing.JTextField jTextFieldUdpFromTgIpAddress;
    private javax.swing.JTextField jTextFieldUdpFromTgIpPort;
    private javax.swing.JTextField jTextFieldUdpGpsIpAddress;
    private javax.swing.JTextField jTextFieldUdpGpsPort;
    private javax.swing.JTextField jTextFieldUdpLoggerIpAddress;
    private javax.swing.JTextField jTextFieldUdpLoggerIpPort;
    private javax.swing.JTextField jTextFieldUdpToTgIpAddress;
    private javax.swing.JTextField jTextFieldUdpToTgIpPort;
    private javax.swing.JLabel lbl_jml_npu;
    private javax.swing.JLabel lbl_total_time;
    private javax.swing.JTextField txt_boudrate_modem;
    private javax.swing.JTextField txt_data_rate;
    private javax.swing.JTextField txt_datalink_key;
    private javax.swing.JTextField txt_own_npu;
    // End of variables declaration//GEN-END:variables
}
