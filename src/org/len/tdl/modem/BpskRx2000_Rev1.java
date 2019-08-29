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
public class BpskRx2000_Rev1 {
    
    
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
    
    
    
    private final double[] h_rrc = {0.00012744224,-2.0806330e-05,-0.00017237118,-0.00032136220,-0.00046164458,-0.00058705424,-0.00069162709,-0.00076983462,-0.00081681821,-0.00082861388,-0.00080235902,-0.00073647354,-0.00063080690,-0.00048674503,-0.00030727035,-9.6970856e-05,0.00013800563,0.00039005341,0.00065026712,0.00090865477,0.0011544011,0.0013761774,0.0015624886,0.0017020515,0.0017841915,0.0017992486,0.0017389815,0.0015969531,0.0013688917,0.0010530088,0.00065026712,0.00016458480,-0.00039703160,-0.0010244338,-0.0017043634,-0.0024205728,-0.0031540347,-0.0038832414,-0.0045845876,-0.0052328310,-0.0058016242,-0.0062641008,-0.0065935119,-0.0067638876,-0.0067507168,-0.0065316213,-0.0060870121,-0.0054007038,-0.0044604787,-0.0032585755,-0.0017920948,-6.3303094e-05,0.0019201718,0.0041452632,0.0065935119,0.0092412382,0.012059841,0.015016211,0.018073266,0.021190589,0.024325142,0.027432082,0.030465612,0.033379875,0.036129877,0.038672384,0.040966831,0.042976134,0.044667482,0.046013039,0.046990495,0.047583580,0.047782380,0.047583580,0.046990495,0.046013039,0.044667482,0.042976134,0.040966831,0.038672384,0.036129877,0.033379875,0.030465612,0.027432082,0.024325142,0.021190589,0.018073266,0.015016211,0.012059841,0.0092412382,0.0065935119,0.0041452632,0.0019201718,-6.3303094e-05,-0.0017920948,-0.0032585755,-0.0044604787,-0.0054007038,-0.0060870121,-0.0065316213,-0.0067507168,-0.0067638876,-0.0065935119,-0.0062641008,-0.0058016242,-0.0052328310,-0.0045845876,-0.0038832414,-0.0031540347,-0.0024205728,-0.0017043634,-0.0010244338,-0.00039703160,0.00016458480,0.00065026712,0.0010530088,0.0013688917,0.0015969531,0.0017389815,0.0017992486,0.0017841915,0.0017020515,0.0015624886,0.0013761774,0.0011544011,0.00090865477,0.00065026712,0.00039005341,0.00013800563,-9.6970856e-05,-0.00030727035,-0.00048674503,-0.00063080690,-0.00073647354,-0.00080235902,-0.00082861388,-0.00081681821,-0.00076983462,-0.00069162709,-0.00058705424,-0.00046164458,-0.00032136220,-0.00017237118,-2.0806330e-05,0.00012744224};
    private final double[] z_rrc_re = new double[145];
    private final double[] z_rrc_im = new double[145];
    
    
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
    

    //------------------------------------------------------
    private final double[] ys = new double[PARAM.FRAME_IF];
    private final double[] yr = new double[PARAM.FRAME_IF];
    private final double[] yi = new double[PARAM.FRAME_IF];

    private final double[] sr = new double[PARAM.FRAME_IF];
    private final double[] si = new double[PARAM.FRAME_IF];
    private final int[] bit_rx = new int[1024];
    private final double[] symbol_re = new double[PARAM.FRAME_BB_D64];
    private final double[] symbol_im = new double[PARAM.FRAME_BB_D64];

    private final SymbolTimingPskCore symbolTimingPskCore = new SymbolTimingPskCore();

    private final SymbolTimingMuller symbolTiming = new SymbolTimingMuller();

    private double frequency = 2000;
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
        
        

        // mixer
        downConvertion(x, yr, yi, 2000, 48000, 6 * 1024);

        // rrc
        fir(yr, sr, z_rrc_re, h_rrc, 145, 6 * 1024);
        fir(yi, si, z_rrc_im, h_rrc, 145, 6 * 1024);

        int ns = symbolTiming.process(sr, si, PARAM.FRAME_IF);

        System.arraycopy(sr, 0, symbol_re, 0, PARAM.FRAME_BB_D64);
        System.arraycopy(si, 0, symbol_im, 0, PARAM.FRAME_BB_D64);

        bpsk_demod(sr, si, bit_rx, ns);
        frame_synchronization(bit_rx, ns);
        
        
        //System.out.println("ns " + ns);
    }
    
    /*
    public void received(double[] x) {
        
        
        // downsample
        downSample(x, ys, z_up_im, h_up, Norde_up, 3, PARAM.FRAME_IF);
        
        // mixer
        downConvertion(ys, yr, yi, frequency, 16000, PARAM.FRAME_BB * 2);

        // dow sample 2x
        upSample(yr, sr, zar, coeff1, N_RRC_M1, 2, PARAM.FRAME_BB * 2);
        upSample(yi, si, zai, coeff1, N_RRC_M1, 2, PARAM.FRAME_BB * 2);

        int ns = symbolTimingPskCore.process(sr, si, PARAM.FRAME_BB * 4);

        System.arraycopy(sr, 0, symbol_re, 0, PARAM.FRAME_BB_D64);
        System.arraycopy(si, 0, symbol_im, 0, PARAM.FRAME_BB_D64);

        bpsk_demod(sr, si, bit_rx, ns);
        frame_synchronization(bit_rx, ns);
        
        
        //System.out.println("ns " + ns);
    }
    */
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
