/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.len.tdl.modem;

/**
 *
 * @author APS
 */
public class BpskTx2000 {
    
    
private final double FS = 48000.0; // freq sampling
    private final double FC = 2000.0; // freq center
    private final double SR = 2000.0;  // symbol rate
    private final int NS = 24; // (FS / SR)
    //private final int NS_D2 = 48;
    //private final int NS_OFSET = 20;
    private final int NS_M18 = NS - 6;
    private final int NS_18 = 6;
    private final double[][] table_psk = new double[8][NS];

    public BpskTx2000() 
    {
        double[] sin_2k = new double[NS];
        double wt = 2 * Math.PI * FC / FS;
        for (int i = 0; i < NS; i++) {
            sin_2k[i] = Math.sin(wt * i);
        }

        double[] ramp_dn = new double[NS_18];
        double[] ramp_up = new double[NS_18];
        double[] ramp_dn_p = new double[NS_18];
        double[] ramp_up_p = new double[NS_18];
        //wt = 2 * Math.PI / 36;
        //for (int i = 0; i < NS_18; i++) {
        //    ramp_up[i] = Math.cos(wt * i);
        //    ramp_dn[i] = Math.cos(wt * (i + NS_18));
        //}
        /*
        wt = 2 * Math.PI / 24;
        for (int i = 0; i < NS_18; i++) {
            ramp_up[i] = Math.cos(wt * i);
            ramp_dn[i] = Math.cos(wt * (i + 12));
        }
        ramp_up[12] = -1.0;
        ramp_up[13] = -0.9866;
        ramp_up[14] = -0.95;
        ramp_up[15] = -0.9;
        ramp_up[16] = -0.85;
        ramp_up[17] = -0.8134;
        
        ramp_dn[0] = -0.8;
        ramp_dn[1] = -0.8134;
        ramp_dn[2] = -0.85;
        ramp_dn[3] = -0.9;
        ramp_dn[4] = -0.95;
        ramp_dn[5] = -0.9866;
        */
        
  
        ramp_up[0] = -1.0;
        ramp_up[1] = -0.9866;
        ramp_up[2] = -0.95;
        ramp_up[3] = -0.9;
        ramp_up[4] = -0.85;
        ramp_up[5] = -0.8134;
        
        ramp_dn[0] = -0.8;
        ramp_dn[1] = -0.8134;
        ramp_dn[2] = -0.85;
        ramp_dn[3] = -0.9;
        ramp_dn[4] = -0.95;
        ramp_dn[5] = -0.9866;
        
        ramp_dn_p[0] = 1.0;
        ramp_dn_p[1] = 0.9866;
        ramp_dn_p[2] = 0.95;
        ramp_dn_p[3] = 0.9;
        ramp_dn_p[4] = 0.85;
        ramp_dn_p[5] = 0.8134;
        
        ramp_up_p[0] = 0.8;
        ramp_up_p[1] = 0.8134;
        ramp_up_p[2] = 0.85;
        ramp_up_p[3] = 0.9;
        ramp_up_p[4] = 0.95;
        ramp_up_p[5] = 0.9866;
        
        
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < NS; j++) {
                table_psk[i][j] = sin_2k[j];
            }
        }
        for (int i = 4; i < 8; i++) {
            for (int j = 0; j < NS; j++) {
                table_psk[i][j] = sin_2k[j] * -1.0;
            }
        }
        
        // 01
        for (int i = 0; i < NS_18; i++)             
            table_psk[1][i + NS_M18] = ramp_up[i];
        
        // 10
        for (int i = 0; i < NS_18; i++)
            table_psk[2][i] = ramp_up_p[i];

        // 11
        for (int i = 0; i < NS_18; i++)  {
            table_psk[3][i] = ramp_up_p[i];
            table_psk[3][i + NS_M18] = ramp_up[i];
        }

        // 01
        for (int i = 0; i < NS_18; i++)             
            table_psk[5][i + NS_M18] = ramp_dn_p[i];
        
        // 10
        for (int i = 0; i < NS_18; i++)
            table_psk[6][i] = ramp_dn[i];

        // 11
        for (int i = 0; i < NS_18; i++)  {
            table_psk[7][i] = ramp_dn[i];
            table_psk[7][i + NS_M18] = ramp_dn_p[i];
        }


    }

    //private final int Ns = 256;                          // Number of sample
    //private final int D = 16;                            // Decimation factor
    private final int Nsym = 64;                          // Number of symbol
    private final int Ns_x6 = 256 * 6;
    private int counter = 0;
    private static final int ZERO_SYMBOL = 0; // phase reversal
    private static final int ONE_SYMBOL = 1; // no phase reversal
    private int currentSymbol = 0;
    private int nextSymbol = 0;
    // if true output phase zero, if false output phase 180
    private int outputPhaseZero = 1;

    public void transmit(int[] x, double[] out, double[] out2) {

        int i, j;
        int k = 0;
        for (i = 0; i < Nsym; i++) 
        {
            currentSymbol = nextSymbol;
            nextSymbol = x[i];

            int rampUp = 0;
            int rampDown = 0;

            if (currentSymbol == ZERO_SYMBOL) {
                // current symbol is a phase invert so need to ramp up
                outputPhaseZero = outputPhaseZero ^ 1;
                rampUp = 1;
            }

            if (nextSymbol == ZERO_SYMBOL) {
                // next symbol is a phase invert so need to ramp down
                rampDown = 1;
            }
            
            int code = 4 * outputPhaseZero + 2 * rampUp + rampDown;
            for (j = 0; j < NS; j++) {
                out[k++] = table_psk[code][j];
            }
        }

        for (i = 0; i < Ns_x6; i++) {
            out2[i] = out[i];
        }
    }

    public void transmit_reset() {
        outputPhaseZero = 1;
        currentSymbol = ZERO_SYMBOL;
        nextSymbol = ZERO_SYMBOL;
    }

    public void setFrequencyOfset(double fo) {
        //frequency_ofset = fo;
    }

    public void setCounter(int count) {
        counter = 0;
    }

    public int getCounter() {
        return counter;
    }    
    
}
