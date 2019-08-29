/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.len.tdl.modem;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import javax.swing.JPanel;

/**
 *
 * @author riyanto
 */
public class ConstellationPanel extends JPanel {
    
    //private static final Color COLOR_BACKGROUND = new Color(50,50,50);
    private static final Color COLOR_BACKGROUND = new Color(0,0,50);
    private static final Color COLOR_SPECTRUM = new Color(255,255,255,255);
    private static final Color COLOR_FILTER = new Color(0,0,0,100);
    private static final Color COLOR_GRID = new Color(0,255,255,100);
    private static final Color COLOR_CURSOR_TEXT = new Color(0,255,255,200);
    private static final Color COLOR_CURSOR_LINE = new Color(255,255,0,255);
    
    
//    private double[] sample_re;
//    private double[] sample_im;
    
    private final double[] sample_re = new double[PARAM.FRAME_BB_D64];
    private final double[] sample_im = new double[PARAM.FRAME_BB_D64];
    private double max = 1;
    private boolean enable_plot = true;

    public ConstellationPanel() {
        this.setBackground(COLOR_BACKGROUND);
    }
    
    public void setEnablePlot(boolean val)
    {
        enable_plot = val;
    }
        
    public void displayData(double[] buf_re, double[] buf_im, double max_mag) {
        
        if (enable_plot == true)
        {
            System.arraycopy(buf_re, 0, sample_re, 0, PARAM.FRAME_BB_D64);
            System.arraycopy(buf_im, 0, sample_im, 0, PARAM.FRAME_BB_D64);
        }
        
        
        //this.sample_re = buf_re;
        //this.sample_im = buf_im;
        this.max = max_mag;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        
        Graphics2D g2 = (Graphics2D) g;
        int wp = getSize().width;
        int hp = getSize().height;
        int wp2 = wp / 2;
        int hp2 = hp / 2;
        
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(0, hp2, wp, hp2);
        g.drawLine(wp2, 0, wp2, hp);
        
        
        g.setColor(COLOR_GRID);
        g.drawOval(0, 0, wp, hp);
        
        
        
        g.setColor(Color.YELLOW);
        int n = sample_re.length;
        for (int i = 0; i < n; i++) {
            int pos_x = (int) Math.floor(wp2 * (sample_re[i]+1));
            int pos_y = (int) Math.floor(hp2 * (-1*sample_im[i]+1));    
            g.drawOval(pos_x, pos_y, 4, 4);
        }
        
        g2.setColor(COLOR_CURSOR_TEXT);
        DecimalFormat df = new DecimalFormat("0.00");
        g2.drawString("SNR = " + df.format(snr_approx) + " dB", 6, 20);
        g2.drawString("Rot = " + df.format(rotasi) + " Hz", 6, 35);
        //g2.drawString("Magnitude = " + df.format(magnitude), 10, 35);
        
        
            
        g2.dispose();
    }
    
    private double snr_approx = 50;
    private double rotasi = 0;
    private double magnitude = 1;
    
    public void setInfo(double snr, double rot, double mag)
    {
        snr_approx = snr;
        rotasi = rot;
        magnitude = mag;        
    }
}
