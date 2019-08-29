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
public class PARAM {
    
    public static final double Fs = 48000;
    public static final double Fs_BB = 8000;
    public static final double Fs_BB_D2 = 4000;
    public static final int FS = 48000;
    public static final int FS_BB = 8000;
    public static final int FS_D2 = 24000;
    public static final int FRAME_BB = 1024;
    public static final int FRAME_IF = 1024 * 6;
    public static final int NUM_BUF = 64;
    public static final int N_FFT_IF = 4096;
    public static final int N_FFT_ZOOM = 512;
    public static final int N_FFT_AVERAGE = 16;
    public static final int N_FFT_BB = 1024;
    public static final int N_FFT_BB_D2 = 512;
    public static final int MAX_CHANNEL = 100;
    public static final int MAX_BIT_DATA = 64 * 1024;
    
    public static final int FRAME_BB_D4 = 256;
    public static final int FRAME_BB_D16 = 64;
    public static final int FRAME_BB_D64 = 16;
    
    public static final int N_SCOPE_BB_48K = 256 * 6;
    
}
