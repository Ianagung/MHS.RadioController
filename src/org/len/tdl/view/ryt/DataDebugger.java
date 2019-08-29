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
import javax.swing.JOptionPane;
import org.len.tdl.common.DataListener;
import org.len.tdl.common.StructCircle;
import org.len.tdl.common.StructPolyline;
import org.len.tdl.common.StructTrack;
import org.len.tdl.core.DEF;
import org.len.tdl.thread_mc.*;

/**
 *
 * @author datalink2
 */
/**
 *
 * This is DataDebugger Jframe Form class
 */
public class DataDebugger extends javax.swing.JFrame {

    /**
     * Reference Variables
     */
    private UdpRx udpReceive = new UdpRx();
    //private final DEF DEF = new DEF();
    private final int[][] trak_life = new int[DEF.MAX_NPU + 1][DEF.MAX_TRAK];
    private final byte[][][] trackRx = new byte[DEF.MAX_NPU + 1][DEF.MAX_TRAK][DEF.PACK_LENGTH];
    private byte[][] trackTx = new byte[DEF.MAX_TRAK][DEF.PACK_LENGTH];

    /**
     * Method to set time out of received tracks in table RX
     */
    javax.swing.Timer tmout = new javax.swing.Timer(500, new ActionListener() {
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

    /**
     * Method of DataDisplayerTester to receive data listeners from UDP
     */
    public DataDebugger() {
        initComponents();
        udpReceive.addListener(new DataListener() {
            @Override
            public void dataReceive(byte[] aData, int aType) {

                if (aData.length > 16) {

                    if (aData[0] == DEF.TOPIC_CORE2LOGGER) {

                        byte topic = aData[0];
                        byte type = aData[1];
                        byte subType = aData[2];
                        byte sender = aData[3];
                        byte jam = aData[4];
                        byte menit = aData[5];
                        byte detik = aData[6];
                        
                        if (type == DEF.TYPE_TX_INFO)
                        {
                            int npu = aData[8 + 2];                            
                            GUI_Show_NPU_Tick(npu);
                            //System.out.println("TIK: " + npu);
                        }
                        
                        

                        String time = "[" + jam + ":" + menit + ":" + detik + ":] ";

                        if (aData[1] == DEF.TYPE_RX_TRACK) {
                            byte[] readBuffer = new byte[DEF.PACK_LENGTH];
                            System.arraycopy(aData, 16, readBuffer, 0, readBuffer.length);
                            int no_track_m1 = ((readBuffer[DEF.TRAKNO1_IDX] & 0xff) + ((readBuffer[DEF.TRAKNO2_IDX] & 0xff) * 0x100));
                            int lastnpu = (readBuffer[DEF.OWNNPU1_IDX] & 0xFF + (readBuffer[DEF.OWNNPU2_IDX] & 0xff) * 0x100);
                            trak_life[lastnpu][no_track_m1] = 0;
                            System.arraycopy(aData, 16, trackRx[lastnpu][no_track_m1], 0, DEF.PACK_LENGTH);
                        }

                        if (aData[1] == DEF.TYPE_TX_TRACK) {
                            byte[] readBuffer = new byte[DEF.PACK_LENGTH];
                            System.arraycopy(aData, 16, readBuffer, 0, readBuffer.length);
                            int no_track_m1 = ((readBuffer[DEF.TRAKNO1_IDX] & 0xff) + ((readBuffer[DEF.TRAKNO2_IDX] & 0xff) * 0x100));
                            int lastnpu = (readBuffer[DEF.OWNNPU1_IDX] & 0xFF + (readBuffer[DEF.OWNNPU2_IDX] & 0xff) * 0x100);
                            trak_life[lastnpu][no_track_m1] = 0;
                            System.arraycopy(aData, 16, trackRx[lastnpu][no_track_m1], 0, DEF.PACK_LENGTH);
                        }

                        if (aData[1] == DEF.TYPE_RX_MSG) {
                            byte[] data = new byte[aData.length - 8];
                            System.arraycopy(aData, 8, data, 0, data.length);
                            GUI_Show_Msg_Rx(data, time);
                        }

                        if (aData[1] == DEF.TYPE_TX_MSG) {
                            byte[] data = new byte[aData.length - 8];
                            System.arraycopy(aData, 8, data, 0, data.length);
                            GUI_Show_Msg_Tx(data, time);
                        }
                    }
                }
            }

            @Override
            public void infoReceive(byte[] aData) {
            }
        });
    }

    /**
     * Method to show received messages
     *
     * @param data
     * @param time
     */
    private void GUI_Show_Msg_Rx(byte[] data, String time) {

        byte[] header_msg = new byte[8];
        byte[] data_msg = new byte[data.length - 8];

        System.arraycopy(data, 0, header_msg, 0, 8);
        System.arraycopy(data, 8, data_msg, 0, data_msg.length);
        int sender_address = header_msg[0];
        int msg_data_type = header_msg[2];
        int msg_no_rx = header_msg[3] * 256 + header_msg[4];
        String display_string = "";

        switch (msg_data_type) {
            case DEF.MSG_TYPE_NACK:
                display_string = " Recv not ack from NPU " + sender_address + " : " + new String(data_msg);
                break;

            case DEF.MSG_TYPE_ACK:
                display_string = " Recv ack from NPU " + sender_address + " : " + new String(data_msg);
                break;

            case DEF.MSG_TYPE_DRAW_CIRCLE:
                StructCircle circle = new StructCircle(data_msg);
                display_string = " Recv draw circle from NPU " + sender_address + " Longitude = " + Double.toString(circle.getLongitude()) + ", Latitude " + circle.getLatitude() + ", Range = " + circle.getRange();
                if (circle.getNotes().length() > 0) {
                    display_string = display_string + ", notes : " + circle.getNotes();
                }
                break;

            case DEF.MSG_TYPE_DEL_CIRCLE:
                display_string = " Recv delete circle from NPU " + sender_address;
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

                break;

            case DEF.MSG_TYPE_DEL_POLYLINE:
                display_string = " Recv delete polyline from NPU " + sender_address;
                break;

            case DEF.MSG_TYPE_UPDATE_PARAM:
                display_string = " Recv update param from NPU " + sender_address;
                break;

            case DEF.MSG_TYPE_TEXT:
                display_string = " Recv text(" + msg_no_rx + ") from NPU " + sender_address + " : " + new String(data_msg);
                break;

            case DEF.MSG_TYPE_FTP:
                display_string = " Recv file from NPU " + sender_address;
                break;
        }

        jTextAreaLogger.append(time + display_string + "\n");
    }

    /**
     * Method to show Transmitted messages
     *
     * @param data
     * @param time
     */
    private void GUI_Show_Msg_Tx(byte[] data, String time) {

        byte[] header_msg = new byte[8];
        byte[] data_msg = new byte[data.length - 8];

        System.arraycopy(data, 0, header_msg, 0, 8);
        System.arraycopy(data, 8, data_msg, 0, data_msg.length);
        int sender_address = header_msg[0];
        int msg_data_type = header_msg[2];
        int msg_no_rx = header_msg[3] * 256 + header_msg[4];
        String display_string = "";

        switch (msg_data_type) {
            case DEF.MSG_TYPE_NACK:
                display_string = " Send not ack from NPU " + sender_address + " : " + new String(data_msg);
                break;

            case DEF.MSG_TYPE_ACK:
                display_string = " Send ack from NPU " + sender_address + " : " + new String(data_msg);
                break;

            case DEF.MSG_TYPE_DRAW_CIRCLE:
                StructCircle circle = new StructCircle(data_msg);

                display_string = " Send draw circle from NPU " + sender_address + " Longitude = " + Double.toString(circle.getLongitude()) + ", Latitude " + circle.getLatitude() + ", Range = " + circle.getRange();
                if (circle.getNotes().length() > 0) {
                    display_string = display_string + ", notes : " + circle.getNotes();
                }
                break;

            case DEF.MSG_TYPE_DEL_CIRCLE:
                display_string = " Send delete circle from NPU " + sender_address;
                break;

            case DEF.MSG_TYPE_DRAW_POLYLINE:
                StructPolyline polyline = new StructPolyline(data_msg);
                display_string = " Send draw polyline from NPU " + sender_address + " : ";
                for (int i = 0; i < polyline.getNum_point(); i++) {
                    display_string += ", Longitude = " + polyline.getLongitudes()[i] + ", Latitude = " + polyline.getLatitudes()[i];
                }
                if (polyline.getNote_length() > 0) {
                    display_string += ", notes : " + polyline.getNotes();
                }

                break;

            case DEF.MSG_TYPE_DEL_POLYLINE:
                display_string = " Send delete polyline from NPU " + sender_address;
                break;

            case DEF.MSG_TYPE_UPDATE_PARAM:
                display_string = " Send update param from NPU " + sender_address;
                break;

            case DEF.MSG_TYPE_TEXT:
                display_string = " Send text(" + msg_no_rx + ") from NPU " + sender_address + " : " + new String(data_msg);
                break;

            case DEF.MSG_TYPE_FTP:
                display_string = " Send file from NPU " + sender_address;
                break;
        }

        jTextAreaLogger.append(time + display_string + "\n");
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
                int attribute_idx = data[DEF.ATTB1_IDX] + data[DEF.ATTB2_IDX] * 256;

                if (attribute_idx > 0) {

                    StructTrack aTrack = new StructTrack(data);
                    int nomor_track = aTrack.getNumber();
                    jTable_trak_rx.getModel().setValueAt((double) nomor_pu, k, 0);
                    jTable_trak_rx.getModel().setValueAt((double) nomor_track, k, 1);
                    jTable_trak_rx.getModel().setValueAt(aTrack.getLongitude(), k, 2);
                    jTable_trak_rx.getModel().setValueAt(aTrack.getLatitude(), k, 3);
                    jTable_trak_rx.getModel().setValueAt(aTrack.getSpeed(), k, 4);
                    jTable_trak_rx.getModel().setValueAt(aTrack.getCourse(), k, 5);
                    jTable_trak_rx.getModel().setValueAt(aTrack.getHeight(), k, 6);
                    jTable_trak_rx.getModel().setValueAt(aTrack.getAttribute(), k, 7);
                    k++;
                }
            }
        }
    }
    
    private void GUI_Show_NPU_Tick(int npu) {
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

        switch (npu) {
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
     * method to scanning data tracks in table
     */
    javax.swing.Timer dataScan = new javax.swing.Timer(300, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            GUI_Show_Trak_Rcv();
        }
    });

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt_port_udp = new javax.swing.JTextField();
        txt_ip_address = new javax.swing.JTextField();
        jButtonStartRx = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable_trak_rx = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaLogger = new javax.swing.JTextArea();
        clearSend_button = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Data Debugger");
        setResizable(false);

        jLabel1.setText("IP Address");

        jLabel2.setText("Port");

        txt_port_udp.setText("1001");
        txt_port_udp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_port_udpActionPerformed(evt);
            }
        });

        txt_ip_address.setText("localhost");
        txt_ip_address.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_ip_addressActionPerformed(evt);
            }
        });

        jButtonStartRx.setText("Start Rx");
        jButtonStartRx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartRxActionPerformed(evt);
            }
        });

        jTable_trak_rx.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "From NPU", "Trak no", "Longitude", "Latitude", "Speed", "Csr", "Height", "Attribute"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane6.setViewportView(jTable_trak_rx);

        jTextAreaLogger.setColumns(20);
        jTextAreaLogger.setRows(5);
        jScrollPane2.setViewportView(jTextAreaLogger);

        clearSend_button.setText("Clear Sent");
        clearSend_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearSend_buttonActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lnpu15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lnpu30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
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
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(36, 36, 36)
                .addComponent(txt_ip_address, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_port_udp, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonStartRx)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(clearSend_button)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txt_ip_address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txt_port_udp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonStartRx))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(clearSend_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_port_udpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_port_udpActionPerformed
    }//GEN-LAST:event_txt_port_udpActionPerformed

    private void txt_ip_addressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_ip_addressActionPerformed
    }//GEN-LAST:event_txt_ip_addressActionPerformed

    private void jButtonStartRxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartRxActionPerformed
        udpReceive.setSocket(Integer.valueOf(txt_port_udp.getText()), txt_ip_address.getText());
        udpReceive.receiveStart();
        dataScan.start();
        tmout.start();
        jButtonStartRx.setEnabled(false);
    }//GEN-LAST:event_jButtonStartRxActionPerformed

    private void clearSend_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearSend_buttonActionPerformed
        int opsi = JOptionPane.showConfirmDialog(null, "Benarkah anda ingin menghapus data ini?", "Penghapusan Data", JOptionPane.YES_NO_OPTION);
        if (opsi == JOptionPane.YES_OPTION) {
            jTextAreaLogger.setText("");
        } else if (opsi == JOptionPane.NO_OPTION) {
        }
    }//GEN-LAST:event_clearSend_buttonActionPerformed

    private void lnpu4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lnpu4MouseClicked

    }//GEN-LAST:event_lnpu4MouseClicked

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
            java.util.logging.Logger.getLogger(DataDebugger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DataDebugger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DataDebugger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DataDebugger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DataDebugger().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearSend_button;
    private javax.swing.JButton jButtonStartRx;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTable jTable_trak_rx;
    private javax.swing.JTextArea jTextAreaLogger;
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
    private javax.swing.JTextField txt_ip_address;
    private javax.swing.JTextField txt_port_udp;
    // End of variables declaration//GEN-END:variables
}
