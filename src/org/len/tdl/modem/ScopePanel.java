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

public class ScopePanel extends JPanel {

    //private static final Color COLOR_BACKGROUND = new Color(50, 50, 50);
    private static final Color COLOR_BACKGROUND = new Color(0, 0, 50);
    private static final Color COLOR_SCOPE = new Color(255, 255, 255, 255);
    private static final Color COLOR_GRID = new Color(0, 255, 255, 100);
    private static final Color COLOR_CURSOR_TEXT = new Color(0, 255, 255, 255);
    //private static final Color COLOR_CURSOR_LINE = new Color(255, 255, 0, 255);
    private final double[] sample_data = new double[PARAM.N_SCOPE_BB_48K];
    //private final double[] sample_data = new double[64];
    
    //private double max_mag;
    //private int length;
    private boolean enable_plot = true;

    public ScopePanel() {
        this.setBackground(COLOR_BACKGROUND);
    }
    
    public void setEnablePlot(boolean val)
    {
        enable_plot = val;
    }

    public void displayData(double[] data) {
        if (enable_plot == true)
        {
            System.arraycopy(data, 0, sample_data, 0, PARAM.N_SCOPE_BB_48K);
            //System.arraycopy(data, 0, sample_data, 0, 64);
        }
        //this.sample_data = data;
        repaint();
    }
    
    private double getPeak(double[] x, int nx) {        
        double max = x[0];
        for (int i = 1; i < nx; i++) {
            if (x[i] > max) {
                max = x[i];
            }
        }
        return max;
    }
    
    private double getMinimumValue(double[] x, int nx) {        
        double min = x[0];
        for (int i = 1; i < nx; i++) {
            if (x[i] < min) {
                min = x[i];
            }
        }
        return min;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // ========================== DRAW SCOPE ============================
        g2.setColor(COLOR_SCOPE);

        int wp = getSize().width;
        int hp = getSize().height;
        int hp2 = hp / 2;
        int n = sample_data.length;
        int[] xp = new int[n];
        int[] yp = new int[n];
        float p = 0.0f;
        for (int i = 0; i < n; i++) {
            xp[i] = (int) Math.round(p * wp / n);
            yp[i] = (int) (hp2 * sample_data[i]) + hp2;
            p = p + 1.0f;
        }
        g2.drawPolyline(xp, yp, n);

        // ============================== DRAW GRID ============================
        // sumbu-Y Garis
        g2.setColor(COLOR_GRID);
        g2.drawLine(0, hp / 2, wp - 1, hp / 2);
        g2.drawLine(0, hp / 4, wp - 1, hp / 4);
        g2.drawLine(0, 3 * hp / 4, wp - 1, 3 * hp / 4);

        // sumbu-X Garis
        for (int i = 1; i < 8; i++) {
            int f = i * 500;
            int x = (int) (wp * f / 4000);
            g2.drawLine(x, 0, x, hp - 1);
        }

//            // Sumbu-Y String Label
//            g2.setColor(Color.ORANGE);
//            g2.drawString(" 1.0", 2, 10);
//            g2.drawString(" 0.5", 2, hp/4 + 5);
//            g2.drawString(" 0.0", 2, hp/2);
//            g2.drawString("-0.5", 2, 3*hp/4 + 5);            
//            g2.drawString("-1.0", 2, hp-1);

        // ========================= DRAW INFO TEXT ============================
        
        g2.setColor(COLOR_CURSOR_TEXT);
        double max_val = getPeak(sample_data, n);
        double min_val = getMinimumValue(sample_data, n);
        DecimalFormat df = new DecimalFormat("0.00000");
        g2.drawString("Max value = " + df.format(max_val), 10, 20);
        g2.drawString("Min value = " + df.format(min_val), 10, 35);

        g2.dispose();
    }

}

