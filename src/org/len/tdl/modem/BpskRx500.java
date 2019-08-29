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
public class BpskRx500 {
    
    
    private final int Ns = 256;                          // Number of sample
    private final int Norde = 65;                        // RRC Orde
    private final int D = 16;                            // Decimation factor
    private final int Nsym = 4;                          // Number of symbol
    private final double Fs = 8000.0;                    // Sampling frequency
    private final double twopi = 6.2831853071795862;     // 2 * pi

    private final int Ns_x6 = 256*6;  
    private final int Norde_up = 96; 
    private final double[] h_up = {-0.000372741292935656,-0.000699300330538713,-0.00105502397534662,-0.00141075686095526,-0.00173096357895102,-0.00197616904045692,-0.00210618432488512,-0.00208391454226439,-0.00187946470159399,-0.00147419690631717,-0.000864354893969112,-6.38658063042128e-05,0.000894042433235467,0.00195670604354756,0.00305357408653523,0.00409951887526627,0.00499984379790009,0.00565677407043133,0.00597706995725404,0.00588027296450662,0.00530699559543648,0.00422660501807428,0.00264363837245136,0.000602326928710093,-0.00181130163531621,-0.00447013276243769,-0.00720969475071496,-0.00983490664354306,-0.0121296176250591,-0.0138684892247539,-0.0148305695012094,-0.0148137427951371,-0.0136491241728458,-0.0112144155425879,-0.00744525747580910,-0.00234369879213199,0.00401693863004133,0.0114913084648000,0.0198656518885638,0.0288657366968776,0.0381687940362950,0.0474188436885607,0.0562445538530304,0.0642785894420928,0.0711772790014080,0.0766393846813892,0.0804227967788220,0.0823580927474076,0.0823580927474076,0.0804227967788220,0.0766393846813892,0.0711772790014080,0.0642785894420928,0.0562445538530304,0.0474188436885607,0.0381687940362950,0.0288657366968776,0.0198656518885638,0.0114913084648000,0.00401693863004133,-0.00234369879213199,-0.00744525747580910,-0.0112144155425879,-0.0136491241728458,-0.0148137427951371,-0.0148305695012094,-0.0138684892247539,-0.0121296176250591,-0.00983490664354306,-0.00720969475071496,-0.00447013276243769,-0.00181130163531621,0.000602326928710093,0.00264363837245136,0.00422660501807428,0.00530699559543648,0.00588027296450662,0.00597706995725404,0.00565677407043133,0.00499984379790009,0.00409951887526627,0.00305357408653523,0.00195670604354756,0.000894042433235467,-6.38658063042128e-05,-0.000864354893969112,-0.00147419690631717,-0.00187946470159399,-0.00208391454226439,-0.00210618432488512,-0.00197616904045692,-0.00173096357895102,-0.00141075686095526,-0.00105502397534662,-0.000699300330538713,-0.000372741292935656};
    private final double[] z_up_re = new double[Norde_up];
    private final double[] z_up_im = new double[Norde_up];
    
    private final double[] map = {-1.0,1.0};
    //private final double[] cosx = {1.0,0.7071,0.0,-0.7071,-1.0,-0.7071,0.0,0.7071};

    // Transmiter global variable :
    private double theta = 1;
    private final double[] state1 = new double[Norde];
    private final double[] state2 = new double[Norde];
    private final double[] out_bb = new double[Ns];
    private final double[] out_re = new double[Ns];
    private final double[] out_im = new double[Ns];
    private double frequency_ofset = 1000.0;
    private double t = 0.0;
    private int counter = 0;
    
    // -------------------------------- up sampler ----------------------------
    // note : 
    //   length(z) = nh/nu -> integer
    private void upSample(double[] x, double[] y, double[] z, double[] h, int nh, int nu, int nx) {
        int i, j, k, m, n;
        double tmp;
        double gain;

        gain = (double) (nu);
        n = 0;
        for (i = 0; i < nx; i++) {

            m = (nh / nu) - 1;
            for (j = m; j > 0; j--) {
                z[j] = z[j - 1];
            }
            z[0] = x[i];

            for (j = 0; j < nu; j++) {
                m = 0;
                tmp = 0;
                for (k = 0; k < nh; k += nu) {
                    tmp += z[m] * h[k + j];
                    m++;
                }
                y[n] = tmp * gain;
                n++;
            }
        }

        //return n;
    }
    
    // -------------------------------- down sample ---------------------------
    // note :
    //   length(z) = nh
    //   nh/nd -> integer
    private void downSample(double[] x, double[] y, double[] z, double[] h, int nh, int nd, int nx) {
        int i, j, k;
        double tmp;

        k = 0;
        for (i = (nd - 1); i < nx; i += nd) {

            for (j = (nh - 1); j > (nd - 1); j--) {
                z[j] = z[j - nd];
            }

            for (j = 0; j < nd; j++) {
                z[j] = x[i - j];
            }

            tmp = 0;
            for (j = 0; j <= (nh - nd); j++) {
                tmp += z[j + (nd - 1)] * h[j];
            }
            y[k] = tmp;
            k++;
        }
    }
    
    private void fir(double[] x, double[] y, double[] z, double[] h, int nh, int ny) {
        int i, j;
        double tmp;

        for (i = 0; i < ny; i++) {
            for (j = (nh - 1); j > 0; j--) {
                z[j] = z[j - 1];
            }
            z[0] = x[i];

            tmp = 0;
            for (j = 0; j < nh; j++) {
                tmp += z[j] * h[j];
            }
            y[i] = tmp;
        }
    }
    

    private final int N_RRC = 65;
    private final int N_RRC_M1 = 64;
    //private final double Fs = 8000.0;
    
    private final double[] coeff1 = {2.5267937e-05, 0.00013323825, 0.00033131737, 0.00065536838, 0.0010557838, 0.0014237450, 0.0015750466, 0.0013021753, 0.00045293992, -0.00096717261, -0.0027143452, -0.0042911577, -0.0050341892, -0.0043096785, -0.0017781900, 0.0023543830, 0.0072056265, 0.011298533, 0.012895946, 0.010542255, 0.0036774313, -0.0068729380, -0.018731864, -0.028262362, -0.031322107, -0.024335079, -0.0054064542, 0.024848916, 0.063049532, 0.10350566, 0.13933672, 0.16399086, 0.17277636, 0.16399086, 0.13933672, 0.10350566, 0.063049532, 0.024848916, -0.0054064542, -0.024335079, -0.031322107, -0.028262362, -0.018731864, -0.0068729380, 0.0036774313, 0.010542255, 0.012895946, 0.011298533, 0.0072056265, 0.0023543830, -0.0017781900, -0.0043096785, -0.0050341892, -0.0042911577, -0.0027143452, -0.00096717261, 0.00045293992, 0.0013021753, 0.0015750466, 0.0014237450, 0.0010557838, 0.00065536838, 0.00033131737, 0.00013323825, 2.5267937e-05};
    private final double[] coeff2 = {0.0026525825, 0.0026448339, 0.0023742125, 0.0018166678, 0.00096676126, -0.00015951286, -0.0015230400, -0.0030612336, -0.0046891477, -0.0063020946, -0.0077797230, -0.0089913998, -0.0098026665, -0.010082452, -0.0097106537, -0.0085856738, -0.0066314558, -0.0038035938, -9.4113595e-05, 0.0044654110, 0.0098026665, 0.015805479, 0.022324812, 0.029179784, 0.036164530, 0.043056574, 0.049626328, 0.055647224, 0.060905959, 0.065212354, 0.068408228, 0.070374876, 0.071038738, 0.070374876, 0.068408228, 0.065212354, 0.060905959, 0.055647224, 0.049626328, 0.043056574, 0.036164530, 0.029179784, 0.022324812, 0.015805479, 0.0098026665, 0.0044654110, -9.4113595e-05, -0.0038035938, -0.0066314558, -0.0085856738, -0.0097106537, -0.010082452, -0.0098026665, -0.0089913998, -0.0077797230, -0.0063020946, -0.0046891477, -0.0030612336, -0.0015230400, -0.00015951286, 0.00096676126, 0.0018166678, 0.0023742125, 0.0026448339, 0.0026525825};

    // -------------------------------------------------------------------------
    private final double[] ys = new double[PARAM.FRAME_BB];
    private final double[] yr = new double[PARAM.FRAME_BB];
    private final double[] yi = new double[PARAM.FRAME_BB];

    private final double[] ar = new double[PARAM.FRAME_BB_D4];
    private final double[] ai = new double[PARAM.FRAME_BB_D4];
    
    private final double[] zar = new double[N_RRC];
    private final double[] zai = new double[N_RRC];
    private final double[] sr = new double[PARAM.FRAME_BB];
    private final double[] si = new double[PARAM.FRAME_BB];
    private final int[] bit_rx = new int[PARAM.FRAME_BB];
    private final double[] symbol_re = new double[PARAM.FRAME_BB_D64];
    private final double[] symbol_im = new double[PARAM.FRAME_BB_D64];

    private final double[] zsr = new double[N_RRC];
    private final double[] zsi = new double[N_RRC];


 
    private final SymbolTimingPskCore symbolTimingPskCore = new SymbolTimingPskCore();

    private double frequency = 1500;
    private double[] peak_testpoint = new double[4];

    
    private double getPeak(double[] x, int nx) {        
        double max = x[0];
        for (int i = 1; i < nx; i++) {
            if (x[i] > max) {
                max = x[i];
            }
        }
        return max;
    }
    
    private double fcor = 0.0;
    private double td = 0.0;

    public void downConvertion(double[] x, double[] yr, double[] yi, double fcenter, double fsample, int nx) {
        int i;
        double wt;
        double tmp;

        wt = twopi * (fcenter + fcor) / fsample;
        for (i = 0; i < nx; i++) {
            //tmp = 2.0 * x[i] / fscale;
            tmp = 2.0 * x[i];
            yr[i] = tmp * Math.cos(td);
            yi[i] = tmp * Math.sin(td);
            td += wt;
            if (td > twopi) {
                td -= twopi;
            }
        }
    }
    
    private double ph_prev = 0;
    //private double dec = 0;
    private final double ph_ofst = 0;
    private final int M = 2;

    private double mod1(double x) {
        return (x - Math.floor(x));
    }

    private void bpsk_demod(double[] xr, double[] xi, int[] dec, int nx) {
        double qr, qi, ph, tmp, ph_dif;

        for (int i = 0; i < nx; i++) {
            qr = xr[i];
            qi = xi[i];

            ph = Math.atan2(qi, qr) / twopi;
            tmp = ph + ((0.5 - ph_ofst) / M);
            ph_dif = mod1(tmp - ph_prev);
            ph_prev = ph;

            if (ph_dif < 0.5) {
                dec[i] = 1;
            } else {
                dec[i] = 0;
            }
        }
    }
    
    private static final int HEADER_LENGTH = 24;
    private static final int BARKER_LENGTH = 26;
    private static final int BARKER_LENGTH_M1 = 25;
    private static final byte[] BARKER  = new byte[] {0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,1,1,0,0,1,1,0,0};
    private static final byte[] PATTERN = new byte[] {1,1,1,1,0,0,0,0, 1,1,1,0,0,1,0,1, 0,1,0,0,1,1,0,0, 1,1,1,1,0,0,0,0};
    private final int[] buffer = new int[BARKER_LENGTH+HEADER_LENGTH];
    private int m_start_detect = 0;
    private int m_finish_detect = 0;
    private int m_length_rx = 0;
    private int m_counter_bit = 0;
    private int m_counter_word = 0;
    private int m_word = 0;
    private int m_rcv_ready = 0;
    private static final int MAX_DATA = 64 * 64;
    private final byte[] buffer_rx = new byte[MAX_DATA];
    private final byte[] buffer_rx2 = new byte[MAX_DATA];
    private int r_start_detect = 0;
    private int r_finish_detect = 0;
    private int r_preamble_detect = 0;
    private int r_postamble_detect = 0;
    private int r_status_rx = 0;
    private int sync_preamble = 0;
    private int m_preamble_detect = 0;
    private int m_postamble_detect = 0;
    private int m_tick_info = 0;
    private int m_tick_on = 0;
    private int r_length_detect = 0;
    private int m_info_start_detect = 0;
    private int m_info_status_rx = 0;
    private int r_info_count_byte = 0;
    private int m_length_byte = 0;
    private final Golay golay = new Golay();  
    private final int[] tmp_golay = new int[24];
    
    private final int[] reg = new int[7];
    private int bit_scramble(int x)
    {
        int tmpi,y;
        int j;

        tmpi = (reg[3] ^ reg[6]) & 1;
        for (j = 6; j > 0; j--)
            reg[j] = reg[j-1];
        reg[0] = tmpi;
        y = (x ^ tmpi) & 1;
        return y;
    }
    private int frame_synchronization(int[] x, int nx)
    {
        for (int i = 0; i < nx; i++)
        {
            int bit = x[i];
            
            if (m_start_detect == 1)
            {
                m_word = 2 * m_word + bit_scramble(bit);
                m_counter_bit++;
                if (m_counter_bit == 8)
                {
                    m_counter_bit = 0;
                    byte word = (byte) m_word;
                    m_word = 0;
                    buffer_rx[m_counter_word] = word;
                    m_counter_word++;
                    //if ((m_finish_detect == 1) || (m_counter_word >= m_length_byte))
                    if (m_counter_word >= m_length_byte)
                    {
                        m_finish_detect = 0;
                        m_start_detect = 0;
                        m_length_rx = m_counter_word;
                        if (m_length_rx > 0)
                        {
                           m_rcv_ready = 1;
                           System.arraycopy(buffer_rx, 0, buffer_rx2, 0, m_length_rx);                            
                        }
                    }
                    
                    if (m_counter_word >= MAX_DATA)
                    {                        
                        m_length_rx = MAX_DATA; //m_counter_word;                        
                        m_counter_word = 0;
                        m_rcv_ready = 1;
                        System.arraycopy(buffer_rx, 0, buffer_rx2, 0, m_length_rx); 
                        //System.out.println("RX 1024");
                    }                  
                }            
            }
            
            // SEARCH PATTERN
            for (int j = 0; j < (BARKER_LENGTH_M1+HEADER_LENGTH); j++)
                buffer[j] = buffer[j+1];
            buffer[BARKER_LENGTH_M1+HEADER_LENGTH] = bit;
            
            int sync = 0;
            for (int j = 0; j < BARKER_LENGTH; j++)
                sync += (buffer[j] ^ BARKER[j]) & 1;

            //if (sync == 0)
            if (sync <= 4) // toleransi 15%
            {
                m_info_start_detect = 1;
                
                for (int j = 0; j < 24; j++)
                {
                    tmp_golay[j] = buffer[BARKER_LENGTH + j];
                }
                
                int[] y = golay.Decode(tmp_golay, 0);
                
                int length_a = (y[0] << 5) | (y[1] << 4) | (y[2] << 3) | (y[3] << 2) | (y[4] << 1) | y[5];
                int length_b =  ((y[6] << 5) | (y[7] << 4) | (y[8] << 3) | (y[9] << 2) | (y[10] << 1) | y[11]) ^ 0x3F;
                
                if (length_a == length_b)
                {
                    //System.out.println("START OF MESSAGE" + length_a);
                    m_length_byte = length_a * 64;
                    r_length_detect = m_length_byte;
                    
                    m_start_detect = 1;  
                    m_counter_bit = 0;
                    m_counter_word = 0;
                    m_word = 0;

                    m_info_start_detect = 2;
                    m_info_status_rx = 1;
                    
                    // reset scramble
                    for (int j = 0; j < 6; j++) {
                        reg[j] = 0;
                    }
                    reg[6] = 1;
                    
                    
                }
                
                
            }
            
            //if (sync == BARKER_LENGTH)
            //{
            //    m_finish_detect = 1;  
            //    m_stop_detect = 1;
            //    m_info_status_rx = 0;
            //    //System.out.println("EOM" + sync);
            //}            
            
            sync_preamble = ( (sync_preamble << 1) | bit ) & 0xFFFFFF;
            if (sync_preamble == 0)
            {
                m_preamble_detect++;                
            }
            
            if (sync_preamble == 0xFFFFFF)
            {
                //m_finish_detect = 1;  
                //m_stop_detect = 1;
                m_info_status_rx = 0;
                
                
                
                m_postamble_detect++;                
                m_tick_info = 16;
                m_tick_on = 1;
                
            }            
        }
        
        // INFO FOR DEBUG ONLY
        if (m_tick_on == 1)
        {
            m_tick_info--;
            if (m_tick_info < 0)
            {
                m_tick_info = 0;
                // reset
                m_preamble_detect = 0;
                m_postamble_detect = 0;
                //m_stop_detect = 0;
                m_tick_on = 0;
                m_info_start_detect = 0;
                m_finish_detect = 0; 
            }
        }
        
        r_start_detect = m_info_start_detect;
        //r_finish_detect = r_length_detect;
        r_preamble_detect = m_preamble_detect;
        r_postamble_detect = m_postamble_detect;
        r_status_rx = m_info_status_rx;
        r_info_count_byte = m_counter_word;
        
        return 0;
    }
    
    public int[] getInfoRx()
    {
        int[] tmp = new int[6];
        tmp[0] = r_preamble_detect;
        tmp[1] = r_start_detect;
        tmp[2] = r_length_detect;
        tmp[3] = r_postamble_detect;
        tmp[4] = r_status_rx;
        tmp[5] = r_info_count_byte;
        return tmp;
    }
    
    public boolean isDataReceived()
    {
        boolean ret = m_rcv_ready == 1;
        return ret;
    }
    
    public byte[] getDataReceived()
    {
        m_rcv_ready = 0;
        //m_counter_word = 0;
        byte[] tmp = new byte[m_length_rx];
        System.arraycopy(buffer_rx2, 0, tmp, 0, m_length_rx);
        return tmp;
    }
    
    
    
    public void received(double[] x) {
        
        
        // downsample
        downSample(x, ys, z_up_im, h_up, Norde_up, 6, PARAM.FRAME_IF);
        
        // mixer
        downConvertion(ys, yr, yi, frequency, Fs, PARAM.FRAME_BB);

        // down sample 4x
        //downSample(yr, ar, zar, coeff1, N_RRC_M1, 4, PARAM.FRAME_BB);
        //downSample(yi, ai, zai, coeff1, N_RRC_M1, 4, PARAM.FRAME_BB);

        // down sample 4x
        fir(yr, sr, zsr, coeff2, N_RRC_M1, PARAM.FRAME_BB);
        fir(yi, si, zsi, coeff2, N_RRC_M1, PARAM.FRAME_BB);
    
        int ns = symbolTimingPskCore.process(sr, si, PARAM.FRAME_BB);

        System.arraycopy(sr, 0, symbol_re, 0, PARAM.FRAME_BB_D64);
        System.arraycopy(si, 0, symbol_im, 0, PARAM.FRAME_BB_D64);

        bpsk_demod(sr, si, bit_rx, ns);
        frame_synchronization(bit_rx, ns);
        
        
        //System.out.println("ns " + ns);
    }
    
    public double[] getSymbolReal()
    {
        double[] tmp = new double[PARAM.FRAME_BB_D64];
        System.arraycopy(symbol_re, 0, tmp, 0, PARAM.FRAME_BB_D64);
        return tmp;        
    }
    
    public double[] getSymbolImaginary()
    {
        double[] tmp = new double[PARAM.FRAME_BB_D64];
        System.arraycopy(symbol_im, 0, tmp, 0, PARAM.FRAME_BB_D64);
        return tmp;        
    }   
    
    
}
