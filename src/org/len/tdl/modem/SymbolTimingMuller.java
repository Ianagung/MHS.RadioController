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
public class SymbolTimingMuller {
    
    
    // --------------------------------------------------------- Muller Mueller ------------------------------------------

        private final double m_K1 = -0.0029394007;
        private final double m_K2 = -1.1757603e-05;

        //private double m_zr = 0;
        //private double m_zi = 0;
        //private double m_z1 = 0;
        //private double m_z2 = 0;
        private double m_vi = 0;
        private double m_ye = 0;
        private double m_ac = 0;
        private double m_mu = 0;
        private double m_c1 = 0;
        private double m_c2 = 0;
        //private double zloop = zeros(30,1);
        //private double m_vp = 0;
        //private double m_z3 = 0;
        //private double m_z4 = 0;

//        private double del1_re = 0;
//        private double del2_re = 0;
//        private double del1_im = 0;
//        private double del2_im = 0;
//        private double del3_re = 0;
//        private double del3_im = 0;

        private int strobe = 0;
        private int delayStrobe = 0;

        private double zr1 = 0;
        private double zr2 = 0;
        private double zar1 = 0;
        private double zar2 = 0;
        private double zi1 = 0;
        private double zi2 = 0;
        private double zai1 = 0;
        private double zai2 = 0;
        
        private final double[] z_re = new double[4];
        private final double[] z_im = new double[4];
        
        private int select_interpolation = 1; // 0:linier, 1:parabolic, 2:cubic
        private int select_loop_filter = 0; // 0:P, 1:PI, 2:PID
        
        private double zv = 0.0;
        private double clock_err = 0.0;

        private double signx(double x)
        {
            return x > 0 ? 1 : -1;
        }
        
        // ref: M,Rice pp.470
        private double linier_interpolation(double x, double[] z, double mu)
        {
            double tmp = z[0] + mu * (x - z[0]);
            z[0] = x;        
            return tmp;
        }

        private double parabolic_interpolation(double x, double[] z, double mu)
        { 
            double tmp = (((x + z[1] + z[2]) * -0.5 + 1.5 * z[0]) * mu + z[1]) + (z[0] + z[1] - x - z[2]) * -0.5 * mu * mu;

            z[2] = z[1];
            z[1] = z[0];
            z[0] = x;
            return tmp;    
        }

        private double cubic_interpolation(double x, double[] z, double mu) {
            double tmp;

            z[3] = z[2];
            z[2] = z[1];
            z[1] = z[0];
            z[0] = x;
            tmp = ((((z[0] - z[3]) * 0.16666666 + (z[2] - z[1]) * 0.5) * mu
                    + ((z[0] + z[2]) * 0.5 - z[1])) * mu + (((0.33333333 * z[0]
                    + 0.5 * z[1]) - z[2]) + 0.16666666 * z[3])) * mu + z[1];

            return tmp;
        }

        public int process(double[] xr, double[] xi, int nx)
        {
            int i, k;
            double ar, ai, qr, qi, er, ei, e, vp;


            k = 0;
            for (i = 0; i < nx; i+=12)
            {
                ar = xr[i];
                ai = xi[i];

                // interpolation linear
                //     qr = m_zr + m_mu * (ar - m_zr);
                //     qi = m_zi + m_mu * (ai - m_zi);
                //     m_zr = ar;
                //     m_zi = ai;

                // interpolation parabolic
//                qr = (((ar + del2_re + del3_re) * -0.5f + 1.5f * del1_re) * m_mu + del2_re) + (del1_re + del2_re - ar - del3_re) * -0.5f * m_mu * m_mu;
//                qi = (((ai + del2_im + del3_im) * -0.5f + 1.5f * del1_im) * m_mu + del2_im) + (del1_im + del2_im - ai - del3_im) * -0.5f * m_mu * m_mu;
//
//                del3_re = del2_re;
//                del2_re = del1_re;
//                del1_re = ar;
//
//                del3_im = del2_im;
//                del2_im = del1_im;
//                del1_im = ai;
                
                
                qr = ar;
                qi = ai;
                if (select_interpolation == 0)
                {
                    qr = linier_interpolation(ar, z_re, m_mu);
                    qi = linier_interpolation(ai, z_im, m_mu);
                }
                                
                if (select_interpolation == 1)
                {
                    qr = parabolic_interpolation(ar, z_re, m_mu);
                    qi = parabolic_interpolation(ai, z_im, m_mu);
                }
                
                if (select_interpolation == 2)
                {
                    qr = cubic_interpolation(ar, z_re, m_mu);
                    qi = cubic_interpolation(ai, z_im, m_mu);
                }

                // TED
                if ((strobe == 1) && (delayStrobe != strobe))
                {
                    ar = signx(qr);
                    ai = signx(qi);

                    er = qr * zar2 - ar * zr2;
                    ei = qi * zai2 - ai * zi2;

                    e = signx(er) + signx(ei);
                    //e = er + ei;
                }
                else
                {
                    e = 0;
                }

                if (delayStrobe != strobe)
                {
                    zr2 = zr1;
                    zr1 = qr;
                    zar2 = zar1;
                    zar1 = ar;

                    zi2 = zi1;
                    zi1 = qi;
                    zai2 = zai1;
                    zai1 = ai;
                }
                else if (strobe == 1)
                {
                    zr2 = 0;
                    zr1 = 0;
                    zar2 = 0;
                    zar1 = 0;

                    zi2 = zi1;
                    zi1 = qi;
                    zai2 = zai1;
                    zai1 = ai;
                }

                delayStrobe = strobe;


                //         vp = g_K1 * e;
                //         g_vi = g_vi + g_K2 * e;
                //         g_ye = vp + g_vi;
                ////          zloop = [zloop(2:end);e];
                ////          g_ye = mean(zloop) * -0.001;
                //         g_vp = vp;


                // PI Loop
                if (m_c1 == 1)
                {
                    if (select_loop_filter == 0) // P
                    {
                        m_ye = m_K1 * e;
                    }
                    
                    if (select_loop_filter == 1) // PI
                    {
                        vp = m_K1 * e;
                        m_vi = m_vi + m_K2 * e;
                        m_ye = vp + m_vi;
                    }
                    
                    if (select_loop_filter == 2) // PID (belum dikerjakan)
                    {
                        vp = m_K1 * e;
                        m_vi = m_vi + m_K2 * e;
                        m_ye = vp + m_vi;
                    }
                }


                // NCO
                m_ac = m_ac - m_ye - 0.5f;
                m_c2 = m_c1;
                if (m_ac < 0.0f)
                {
                    m_mu = m_ac * 2 + 1;
                    m_ac = m_ac + 1;
                    m_c1 = 1;
                    strobe = 1;
                }
                else
                {
                    m_c1 = 0;
                    strobe = 0;
                }

                // out
                if (m_c2 == 1)
                {
                    xr[k] = qr;
                    xi[k] = qi;
                    //clock[i] = 1;
                    k++;
                }
                else
                {
                    //xr[k] = 0;
                    //xi[k] = 0;
                    //clock[i] = 0;
                }

                //err[i] = g_ye;
                //mu[i] = g_mu;
                
                // symbol timing error
                zv = 0.0005 * m_ye + 0.9995 * zv;
                clock_err = 2 * 100 * zv;
            }

            return k;
        }   
    
        public void reset()
        {
            m_vi = 0;
            m_ye = 0;
            m_ac = 0;
            m_mu = 0;
            m_c1 = 0;
            m_c2 = 0;

            strobe = 0;
            delayStrobe = 0;

            zr1 = 0;
            zr2 = 0;
            zar1 = 0;
            zar2 = 0;
            zi1 = 0;
            zi2 = 0;
            zai1 = 0;
            zai2 = 0;

            for (int i = 0; i < 4; i++) {
              z_re[i] = 0;
              z_im[i] = 0;
            }

            zv = 0.0;
            clock_err = 0.0;
        }
    
        public void setLoopFilterType(int val)
        {
            select_loop_filter = val;
        }

        public void setInterpolationType(int val)
        {
            select_interpolation = val;
        }

        public double getTimingError()
        {
            return clock_err;
        }
    
    
}
