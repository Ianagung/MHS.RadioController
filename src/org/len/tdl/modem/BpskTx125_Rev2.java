/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.len.tdl.modem;

/**
 *
 * @author ASUS
 */
public class BpskTx125_Rev2 {
    
    private final double FS = 48000.0; // freq sampling
    private final double FC = 1000.0; // freq center
    private final double SR = 125.0;  // symbol rate
    private final int NS = 384; // (FS / SR)
    private final int NS_D2 = 192;
    private final int NS_OFSET = 20;
    private final int NS_M36 = NS - 36;
    private final int NS_36 = 36;
    private final double[][] table_psk = new double[8][NS];

    public BpskTx125_Rev2() {
        double[] sin_1khz = new double[NS];
        double wt = 2 * Math.PI * FC / FS;
        for (int i = 0; i < NS; i++) {
            sin_1khz[i] = Math.sin(wt * i);
        }

        double[] ramp_dn = new double[NS_36];
        double[] ramp_up = new double[NS_36];
        wt = 2 * Math.PI / 72;
        for (int i = 0; i < NS_36; i++) {
            ramp_up[i] = Math.cos(wt * i);
            ramp_dn[i] = Math.cos(wt * (i + NS_36));
        }
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < NS; j++) {
                table_psk[i][j] = sin_1khz[j];
            }
        }
        for (int i = 4; i < 8; i++) {
            for (int j = 0; j < NS; j++) {
                table_psk[i][j] = sin_1khz[j] * -1.0;
            }
        }
        
        // 01
        for (int i = 0; i < NS_36; i++)             
            table_psk[1][i + NS_M36] = ramp_up[i];
        
        // 10
        for (int i = 0; i < NS_36; i++)
            table_psk[2][i] = ramp_up[i];

        // 11
        for (int i = 0; i < NS_36; i++)  {
            table_psk[3][i] = ramp_up[i];
            table_psk[3][i + NS_M36] = ramp_up[i];
        }

        // 01
        for (int i = 0; i < NS_36; i++)             
            table_psk[5][i + NS_M36] = ramp_dn[i];
        
        // 10
        for (int i = 0; i < NS_36; i++)
            table_psk[6][i] = ramp_dn[i];

        // 11
        for (int i = 0; i < NS_36; i++)  {
            table_psk[7][i] = ramp_dn[i];
            table_psk[7][i + NS_M36] = ramp_dn[i];
        }


    }

    private final int Ns = 256;                          // Number of sample
    private final int D = 16;                            // Decimation factor
    private final int Nsym = 4;                          // Number of symbol
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
