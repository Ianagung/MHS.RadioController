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
public class SymbolTimingPskCore {
        // bit sync half distance table
    // index any position 0 to 15 and it returns a position halfway from the index
    private final int[] HALF_TBL = {
        7, // 0
        8, // 1
        9, // 2
        10, // 3
        11, // 4
        12, // 5
        13, // 6
        14, // 7
        0, // 8
        1, // 9
        2, // 10
        3, // 11
        4, // 12
        5, // 13
        6, // 14
        7, // 15
    };

    private final double m_SampleFreq = 8000.0;
    private final double Ts = 0.008;//0.032;			// Ts == symbol period

    private double[] m_SyncAve = new double[21];
    private int[] m_SyncArray = new int[16];

    private int m_BitPos = 0;
    private int m_PkPos = 0;
    private int m_NewPkPos = 5;
    private double m_BitPhasePos = 0.0;
    private double m_BitPhaseInc = Ts / 16.0;//16.0 / m_SampleFreq;
    private boolean m_SQOpen = false; // squelch

    private int m_LastPkPos = 0;
    private int m_ClkErrCounter = 0;
    private int m_ClkErrTimer = 0;
    private int m_ClkError = 0;
    
    
    private double fmod(double x, double y)
    {
        return ( x - y * (Math.floor(x / y)) );       
    }

    //////////////////////////////////////////////////////////////////////
    // Called at Fs/16 rate to calculate the symbol sync position
    // Returns TRUE if at center of symbol.
    // Sums up the energy at each sample time, averages it, and picks the
    //   sample time with the highest energy content.
    //////////////////////////////////////////////////////////////////////
    public int process(double[] xr, double[] xi, int nx) 
    {
        boolean Trigger = false;
        double max;
        double energy;
        int BitPos = m_BitPos;

        int k = 0;
        for (int n = 0; n < nx; n++) 
        {
            double ar = xr[n];
            double ai = xi[n];

            if (BitPos < 16) {
                energy = ar * ar + ai * ai;
                m_SyncAve[BitPos] = (1.0 - 1.0 / 82.0) * m_SyncAve[BitPos] + (1.0 / 82.0) * energy;
                if (BitPos == m_PkPos) // see if at middle of symbol
                {
                    Trigger = true;
                    m_SyncArray[m_PkPos] = (int) (900.0 * m_SyncAve[m_PkPos]);
                } else {
                    Trigger = false;
                    m_SyncArray[BitPos] = (int) (750.0 * m_SyncAve[BitPos]);
                }
                if (BitPos == HALF_TBL[m_NewPkPos]) //don't change pk pos until halfway into next bit.
                {
                    m_PkPos = m_NewPkPos;
                }
                BitPos++;
            }

            m_BitPhasePos += (m_BitPhaseInc);
            if (m_BitPhasePos >= Ts) {			// here every symbol time
                m_BitPhasePos = fmod(m_BitPhasePos, Ts);	//keep phase bounded
                if ((BitPos == 15) && (m_PkPos == 15)) //if missed the 15 bin before rollover
                {
                    Trigger = true;
                }
                BitPos = 0;
                max = -1e10;
                for (int i = 0; i < 16; i++) //find maximum energy pk
                {
                    energy = m_SyncAve[i];
                    if (energy > max) {
                        m_NewPkPos = i;
                        max = energy;
                    }
                }
                if (m_SQOpen) {
                    if (m_PkPos == m_LastPkPos + 1) //calculate clock error
                    {
                        m_ClkErrCounter++;
                    } else if (m_PkPos == m_LastPkPos - 1) {
                        m_ClkErrCounter--;
                    }
                    if (m_ClkErrTimer++ > 313) // every 10 seconds sample clk drift
                    {
                        m_ClkError = m_ClkErrCounter * 200;	//each count is 200ppm
                        m_ClkErrCounter = 0;
                        m_ClkErrTimer = 0;
                        //::PostMessage(m_hWnd, MSG_CLKERROR, m_ClkError, m_RxChannel);
                    }
                } else {
                    m_ClkError = 0;
                    m_ClkErrCounter = 0;
                    m_ClkErrTimer = 0;
                }
                m_LastPkPos = m_PkPos;
            }
            m_BitPos = BitPos;

            if (Trigger == true)
            {
                xr[k] = ar;
                xi[k] = ai;
                k++;
            }
        }
        return k;
    }
}
