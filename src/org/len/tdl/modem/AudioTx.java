/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.len.tdl.modem;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author ASUS
 */
public class AudioTx {
    
    
    
    
    
    private boolean running = true;
    private AudioFormat audioFormat;
    private SourceDataLine sourceDataLine;
    //private final Thread playThread = new Thread(new PlayThread());
    
    private final AudioFormat af = new AudioFormat(PARAM.FS, 16, 2, true, false);
    private final float Fs = 48000;
    private final int Ns = 256 * 6; // 32 ms
    private final int Ns2 = 1024 * 6;
    private final byte[] outBuffer = new byte[Ns2];
    private final double[] spkBuffer = new double[Ns];
    private final double[] spkBuffer2 = new double[Ns];
    private final int[] bit_inp = new int[4];
    private double volume = 0.8;
    
    private final BpskTx125_Rev2 bpsk = new BpskTx125_Rev2();
    //private final BpskTx125_Rev1 bpsk = new BpskTx125_Rev1();
    //private final BpskTx125 bpsk = new BpskTx125();
    private int counter_tx = 0;
    private int data_tx_length = 0;
    private final byte[] data_tx = new byte[PARAM.MAX_BIT_DATA + 32];
    private int r_level = 0;
    private int r_tick = 0;
    private int m_tick = 0;
    private int r_counter = 0;
    private int m_time_elapsed = 0;
    private long m_time_start = 0;
    private int r_time_process = 0;
    private int r_time_sample = 0;
    
    private double getPeak(double[] x, int nx)
    {
        double max = 0;
        for (int i = 0; i < nx; i++) {
            double inp = x[i];
            if (inp > max) max = inp;
        }
        return max;
    }
    
    private AudioFormat getAudioFormat() {
        float sampleRate = 48000;  //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16; //8,16
        int channels = 2; //1,2
        boolean signed = true; //true,false
        boolean bigEndian = false; //true,false
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }//end getAudioFormat
    
    public void playAudio() {
        try {
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, af);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(af, Ns2 * 4);
            //System.out.println("BUFFFFERRR: " + sourceDataLine.getBufferSize());
            sourceDataLine.start();
            Thread playThread = new Thread(new PlayThread());
            playThread.setPriority(Thread.MAX_PRIORITY);
            playThread.start();
        } catch (LineUnavailableException e) {
            //System.out.println(e);
            System.exit(0);
        }//end catch
    }//end playAudio
    
    private class PlayThread extends Thread {

        @Override
        public void run() {
            try {
                while (running) {
                    m_tick = (m_tick + 1) & 0xffff;
                    int tick = m_tick;
                    double peak;
                    
                    long start = System.nanoTime();  
                    int time_sample = (int) (start - m_time_start);
                    //long cpu_load = 1000 * m_time_elapsed / time_sample;
                    m_time_start = start;
                    // 
                    for (int i = 0; i < 4; i++) {
                        bit_inp[i] = data_tx[counter_tx];
                        counter_tx = counter_tx + 1;
                        if (counter_tx > PARAM.MAX_BIT_DATA)
                            counter_tx = PARAM.MAX_BIT_DATA;
                    }
                    
                    
                    if (counter_tx <= data_tx_length) {
                        
                        //System.out.println("cekkkk " + counter_tx);
                        
                        bpsk.transmit(bit_inp, spkBuffer, spkBuffer2); 
                        
                        for (int i = 0; i < Ns; i++) {
                            spkBuffer[i] *= volume;
                            spkBuffer2[i] *= volume;
                        }
                        
                        peak = getPeak(spkBuffer, spkBuffer.length);
                    } else {
                        bpsk.transmit_reset();
                        for (int i = 0; i < Ns; i++) {
                            spkBuffer[i] = 0.0;
                            spkBuffer2[i] = 0.0;
                        }
                        counter_tx = data_tx_length;
                        peak = 0;
                    }
                    int level = (int) ( 100.0 * peak);
                    //counter_tx = bpsk.getCounter();
                    //progressBarLevel.setValue((int) (100 * peak));
                    r_level = level;
                    r_tick = tick;
                    r_counter = counter_tx;
                    r_time_sample = time_sample;
                    r_time_process = m_time_elapsed;
                    
                    //System.out.println("CEK");
                    
                    int k = 0;
                    for (int i = 0; i < Ns2; i += 4) {
                        int nSample = (int) Math.round(spkBuffer[k] * 32767.0);
                        outBuffer[i + 1] = (byte) ((nSample >> 8) & 0xFF);
                        outBuffer[i] = (byte) (nSample & 0xFF);
                        
                        nSample = (int) Math.round(spkBuffer2[k] * 32767.0);
                        outBuffer[i + 3] = (byte) ((nSample >> 8) & 0xFF);
                        outBuffer[i + 2] = (byte) (nSample & 0xFF);
                        
                        k++;
                    }
                    long finish = System.nanoTime();
                    m_time_elapsed = (int) (finish - start);
                    sourceDataLine.write(outBuffer, 0, Ns2);
                }//end while
                //Block and wait for internal buffer of the data line to empty.
                sourceDataLine.drain();
                sourceDataLine.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }//end catch
        }//end run
    }//end inner class PlayThread
    //===================================//
        
        public int getLevel()
        {
            return r_level;
        }
        
        public int getTick()
        {
            return r_tick;
        }
        
        public int getTimeProcess()
        {
            return r_time_process;
        }
        
        public int getTimeSample()
        {
            return r_time_sample;
        }
        
        public int getCounter()
        {
            return r_counter;
        }
        
        public void setVolume(int vol_persen)
        {
            volume = vol_persen * 0.01;
        }
        
        //======================================================================
        
        
    private static final int PATTERN_LENGTH = 32;
    private static final int PREAMBLE_LENGTH = 32;
    private static final int POSTAMBLE_LENGTH = 32;
    private static final byte[] PATTERN_START = new byte[] {1,1,1,1,0,0,0,0, 1,1,1,0,0,1,0,1, 0,1,0,0,1,1,0,0, 1,1,1,1,0,0,0,0};
    private static final byte[] PATTERN_STOP  = new byte[] {0,0,0,0,1,1,1,1, 0,0,0,1,1,0,1,0, 1,0,1,1,0,0,1,1, 0,0,0,0,1,1,1,1};
        
    
    public int bit_packet(byte[] x, int nx)
    {    
        // CEK TOTAL BIT
        if (nx <= 0)
            return 0;               
        
        int nb = PATTERN_LENGTH + PREAMBLE_LENGTH + POSTAMBLE_LENGTH + nx * 8;
        if (nb > PARAM.MAX_BIT_DATA)
            return -1;       
       
        // PREAMBLE
        int k = 0;
        for (int i = 0; i < PREAMBLE_LENGTH; i++) {
            data_tx[k++] = 0;
        }
        
        // PATTERN START
        for (int i = 0; i < PATTERN_LENGTH; i++) {
            data_tx[k++] = PATTERN_START[i];
        }
        
        // DATA
        for (int i = 0; i < nx; i++) {
            int tmp = (int) x[i];
            for (int j = 7; j >= 0; j--) {
                data_tx[k++] = (byte)(( tmp >> j ) & 1);
            }                
        }
        
        // PATTERN STOP
        for (int i = 0; i < PATTERN_LENGTH; i++) {
            data_tx[k++] = PATTERN_STOP[i];
        }
        
        // POSTAMBLE
        for (int i = 0; i < POSTAMBLE_LENGTH; i++) {
            data_tx[k++] = 1;
        }
        int ret = k;
        
        // START TX
        counter_tx = 0;
        data_tx_length = k;
        
        return ret;
    }
        
        

}
