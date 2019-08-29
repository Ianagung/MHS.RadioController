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
public class ConstellationProcessor {
    private final double[] magnitude = new double[PARAM.FRAME_BB_D16];
    private final int LENGTH_SNR = 64;
    private final double[] magnitude_snr = new double[LENGTH_SNR];
    private int counter_snr = 0;
    
    
    private boolean carrierRecoveryEnable = true;
    private boolean agcEnable = true;
    
    private final CarrierRecovery carrierRecovery = new CarrierRecovery();
    private final AGC_IQ agc = new AGC_IQ();
    
    
    private double gain_agc = 1.0;
    private double frequencyOfset = 0.0;
    private double snr_approx = 1.0;
    private double mag_peak = -3.0;
    private double[] mag_symbol = new double[PARAM.FRAME_BB_D64];
    
    
    public ConstellationProcessor()
    {
        agc.setLevelReference(0.5);
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
    
    private void getMagnitude(double[] xr, double[] xi, double[] y, int nx)
    {
        for (int i = 0; i < nx; i++) {
            y[i] = Math.sqrt(xr[i] * xr[i] + xi[i] * xi[i]);
        }
    }
    
    private void getMagnitudeDb(double[] xr, double[] xi, double[] y, int nx)
    {
        for (int i = 0; i < nx; i++) {
            double tmp = xr[i] * xr[i] + xi[i] * xi[i];
            y[i] = 10 * Math.log10(tmp / (2048));
        }
    }
   // ------------------------------------------------------------------------
   // variance
   // var = (1/(n-1)) * sum((x - mean(x)).^2);

    private double getSnr(double[] s_mag, int nx)
    {
            int i;
            double tmp;
            double mean, var;
            double snr, snr_db;

            mean = 0.0;
            for (i = 0; i < nx; i++)
            {
                mean += s_mag[i];
            }
            mean = mean / (double)nx;

            var = 0.0;
            for (i = 0; i < nx; i++)
            {
                tmp = s_mag[i] - mean;
                var += (tmp * tmp);
            }
            var = var / (double)(nx - 1);

            // snr = signal / noise;
            snr = mean * mean / var;
            snr_db = 10.0 * Math.log10(snr);

            return snr_db;
    }
    
    private double measureSnr(double[] xr, double[] xi, int ns) {
        getMagnitude(xr, xi, magnitude, ns);

        for (int i = 0; i < ns; i++) {
            magnitude_snr[counter_snr] = magnitude[i];
            counter_snr = (counter_snr + 1) % LENGTH_SNR;
        }
        double snr_val = getSnr(magnitude_snr, LENGTH_SNR);

        return snr_val;
    }
    
    
    
    public void phaseVectorNormalize(double[] sr, double[] si, int nx)
    {
        
       //snr_approx = measureSnr(sr, si, nx);
        getMagnitude(sr, si, mag_symbol, PARAM.FRAME_BB_D64);
        mag_peak = getPeak(mag_symbol, PARAM.FRAME_BB_D64);
        
        // Carrier Recovery
       if (carrierRecoveryEnable) {
            frequencyOfset = carrierRecovery.step(sr, si, PARAM.FRAME_BB_D64);
       }

       // AGC
       if (agcEnable)
           gain_agc = agc.step_log(sr, si, PARAM.FRAME_BB_D64);
       else
           gain_agc = 0.0;
       
       
       snr_approx = measureSnr(sr, si, nx);
    }
    
    
    public double getGainAgc()
    {
        return gain_agc;
    }
    
    public double getFrequencyOfset()
    {
        return frequencyOfset;
    }
    
    public double getSnrApprox()
    {
        return snr_approx;
    }
    
    public double getPeakMagnitude()
    {
        return (10.0 * Math.log10(mag_peak));
    }
    
    public void setEnableAgc(boolean val)
    {
        agcEnable = val;
    }
    
    public void setEnablePll(boolean val)
    {
        carrierRecoveryEnable = val;
    }
}
