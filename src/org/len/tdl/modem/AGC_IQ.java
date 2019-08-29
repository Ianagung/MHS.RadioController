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
public class AGC_IQ {
    private double agc_gain = 1.0;
    private double alpha = 0.1;
    private double reference = 0.5;
    
    
    public void setStep(double val)
    {
        alpha = val;
    }
    
    public void setLevelReference(double val)
    {
        reference = val;
        ref = val;
    }
    
    

    // loop method : linear
    public double step(double[] xr, double[] xi, int nx)
    {
        double gain;
        
        for (int i = 0; i < nx; i++)
        {
            xr[i] = xr[i] * agc_gain;
            xi[i] = xi[i] * agc_gain;            
            double magnitude = Math.sqrt(xr[i] * xr[i] + xi[i] * xi[i]);
            agc_gain = agc_gain + alpha * (reference - magnitude); 
        }
        gain = agc_gain;
        
        return gain;
    }
    
    // loop method : logaritmic
    private double agc_g = 0.0;
    private double ref = 1.0;
    public double step_log(double[] xr, double[] xi, int nx)
    {
        double gain = 1.0;
        double tmp = 1.0;
        
        for (int i = 0; i < nx; i++)
        {
            //gain = Math.pow(10.0,agc_g);
            gain = Math.exp(agc_g);
            xr[i] = xr[i] * gain;
            xi[i] = xi[i] * gain;            
            double magnitude = Math.sqrt(xr[i] * xr[i] + xi[i] * xi[i]);
            if (magnitude > 0.0) {
                agc_g = agc_g + alpha * Math.log(ref / magnitude); 
                //agc_g = agc_g + alpha * (Math.log(reference)- Math.log(magnitude)); 
                //System.out.println("GAin = " + Math.log(reference / magnitude));
            }
        }
        //System.out.println("GAin = " + agc_g);
        double db_gain = 10.0 * Math.log10(gain);
        return db_gain;
    }
}
