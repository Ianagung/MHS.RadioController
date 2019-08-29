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
public class BpskTx125_Rev1 {

    private final double FS = 48000.0; // freq sampling
    private final double FC = 1000.0; // freq center
    private final double SR = 125.0;  // symbol rate
    private final int NS = 384; // (FS / SR)
    private final int NS_D2 = 192;
    private final int NS_OFSET = 20;
    private final double[][] table_psk = new double[4][NS];

    public BpskTx125_Rev1() {
        double[] sin_1khz = new double[NS];
        double wt = 2 * Math.PI * FC / FS;
        for (int i = 0; i < NS; i++) {
            sin_1khz[i] = Math.sin(wt * i);
        }

        double[] ramp_dn = new double[NS_D2];
        double[] ramp_up = new double[NS_D2];
        wt = 2 * Math.PI * 125.0 / FS;
        for (int i = 0; i < NS_D2; i++) {
            ramp_dn[i] = 0.5 * (1 + Math.cos(wt * i));
            ramp_up[i] = 0.5 * (1 + Math.cos(wt * (i + NS_D2)));
        }
        
        double[] cos_dn = new double[NS_D2];
        double[] cos_up = new double[NS_D2];
        
        for (int i = 0; i < (NS_D2 - NS_OFSET); i++) {
            cos_dn[i + NS_OFSET] = ramp_dn[i];
            cos_up[i] = ramp_up[i + NS_OFSET];
        }
        
        for (int i = 0; i < NS_OFSET; i++) {
            cos_dn[i] = 1.0;
            cos_up[i + NS_D2 - NS_OFSET] = 1.0;            
        }

        for (int i = 0; i < NS_D2; i++) {
            table_psk[2][i] = sin_1khz[i] * cos_up[i];
            table_psk[2][i + NS_D2] = sin_1khz[i + NS_D2];
        }

        for (int i = 0; i < NS_D2; i++) {
            table_psk[1][i] = sin_1khz[i];
            table_psk[1][i + NS_D2] = sin_1khz[i + NS_D2] * cos_dn[i];
        }

        for (int i = 0; i < NS_D2; i++) {
            table_psk[3][i] = sin_1khz[i] * cos_up[i];
            table_psk[3][i + NS_D2] = sin_1khz[i + NS_D2] * cos_dn[i];
        }

        for (int i = 0; i < NS; i++) {
            table_psk[0][i] = sin_1khz[i];
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
    private boolean outputPhaseZero = true;

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
                outputPhaseZero = !outputPhaseZero;
                rampUp = 1;
            }

            if (nextSymbol == ZERO_SYMBOL) {
                // next symbol is a phase invert so need to ramp down
                rampDown = 1;
            }
            int inv = outputPhaseZero ? 1 : -1;
            int code = 2 * rampUp + rampDown;
            for (j = 0; j < NS; j++) {
                out[k++] = table_psk[code][j] * inv;
            }
        }

        for (i = 0; i < Ns_x6; i++) {
            out2[i] = out[i];
        }
    }

    public void transmit_reset() {
        outputPhaseZero = true;
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
