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
public class CarrierRecovery {
        // Decision Aided Carrier Phase Recovery Loop
    
    private final double limit = 4.0 * Math.PI;
    private final double toleransi = Math.PI;
    private  final double symbol_rate = 125.0;
    private  final double twopi = 2.0 * Math.PI;    
    private  double t3 = 0.0;
    private  double p6 = 0.0;
    private  double pr = 0.0;
    private  double fr = 0.0;
    
    private  double or = 0.0;
    private  double oi = 0.0;
    
    private  double sign(double x)
    {
        return (x >= 0 ? 1.0 : -1.0);
    }
    
    public  double step(double[] xr, double[] xi, int ns)
    {

        for (int i = 0; i < ns; i++)
        {           
            // Multiply complex conjugate
            double ar = xr[i];
            double ai = xi[i];           
            double br = or;
            double bi = oi;            
            double cr = ar * br + ai * bi;
            double ci = ai * br - ar * bi;  
            
            xr[i] = cr;
            xi[i] = ci;
            
            // Slicer
            ar = cr;
            ai = ci;            
            br = sign(ar);           
            cr = ar * br;
            ci = -1.0 * ai * br;
            double phase = 4.0 * Math.atan2(ci, cr);

            // PI Loop Filter
            double a = 2.5 * phase;
            p6 = p6 - (2 * a);
            double p7 = p6 - 25 * a;       
            if ( Math.abs(pr) > 300.0 )
                p6 = 0.0;
            pr = p7;
            if ( p7 > 200 )
                p7 = 200;
            if ( p7 < -200 )
                p7 = -200;
            
            // Voltage Control Oscillator
            fr = p7;
            double t1 = fr + t3;
            double ph = t1 / 1024.0;
            ph = ph - Math.floor(ph);
            t3 = ph * 1024.0;

            or = Math.cos(twopi * ph);
            oi = Math.sin(twopi * ph);
        }

        return (pr / 1024.0 * symbol_rate);  
    }
}
