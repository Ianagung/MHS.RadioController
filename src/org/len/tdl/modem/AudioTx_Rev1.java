/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.len.tdl.modem;


import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author ASUS
 */
public class AudioTx_Rev1 {
    
    
    
    
    
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
    private final int[] bit_inp = new int[64];
    private double volume = 0.95;
    
    private final BpskTx2000_Rev1 bpsk2000 = new BpskTx2000_Rev1();
    private final BpskTx500 bpsk500 = new BpskTx500();
    private final BpskTx125_Rev2 bpsk = new BpskTx125_Rev2();
    //private final BpskTx125_Rev1 bpsk = new BpskTx125_Rev1();
    //private final BpskTx125 bpsk = new BpskTx125();
    private int counter_tx = 0;
    private int data_tx_length = 0;
    private final byte[] data_tx = new byte[PARAM.MAX_BIT_DATA + 64];
    private int r_level = 0;
    private int r_tick = 0;
    private int m_tick = 0;
    private int r_counter = 0;
    private int r_persen_tx = 0;
    private int m_time_elapsed = 0;
    private long m_time_start = 0;
    private int r_time_process = 0;
    private int r_time_sample = 0;
    private int r_status_tx = 0;
    private int DATARATE = 0;
    private int N_SYM = 4;
    
    private Mixer.Info[] mixerInfo;
    private final Line.Info sourceDLInfo = new Line.Info(SourceDataLine.class);
    // For controlling the inner classes thread
    boolean stopPlay = false;
    boolean threadEnd = true;
    private Thread playerThread;
    //private SourceDataLine sourcedataLine;
    
    public List<String> ListAudioOutputDevices(int selected_soundcard) {
        List<String> returnList = new ArrayList<String>();

        mixerInfo = AudioSystem.getMixerInfo();
        System.out.println("Available out mixers:");
        for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
            Mixer currentMixer = AudioSystem.getMixer(mixerInfo[cnt]);

            // Because this is for a recording application, we only care about audio INPUT so we just
            // care if TargetDataLine is supported.
            // currentMixer.isLineSupported(targetDLInfo) && currentMixer.isLineSupported(portInfo)
            if (currentMixer.isLineSupported(sourceDLInfo)) {
                //if( currentMixer.isLineSupported(portInfo) ) {
                System.out.println("mixer name: " + mixerInfo[cnt].getName() + " index:" + cnt);
                returnList.add(mixerInfo[cnt].getName());
                //}
            }
        }

        // Save the valid list of mixers
        //SavedValidRecordingDevList = returnList;

        // Automatically start listening on the device read for the config file
        // or default to the first item
        if (returnList.size() >= selected_soundcard) {
            StartPlayingSound(returnList.get(selected_soundcard));
        } else {
            StartPlayingSound(returnList.get(0));
        }
        return returnList;
    }
    
    public void StartPlayingSound(String _MixerName) {

        //DetermineConfigFileIndexOfMixer(_MixerName);

        // Locate the target mixer
        for (int cnt = 0; cnt < mixerInfo.length; cnt++) {

            // Mixer currentMixer = AudioSystem.getMixer(mixerInfo[cnt]);
            // Because this is for a recording application, we only care about audio INPUT so we just
            // care if TargetDataLine is supported.
            if (mixerInfo[cnt].getName().equals(_MixerName)) {
                System.out.println("SoundOutputDeviceControl - found target mixer: " + _MixerName);

                // This may freeze the UI but oh well, we have to wait for the inner thread to stop from the
                // previous selection
                stopPlay = true;
                while (threadEnd == false) {
                    try {
                        sleep(100);
                    } catch (InterruptedException ex) {                        
                    }
                }

                Mixer currentMixer = AudioSystem.getMixer(mixerInfo[cnt]);
                try {
                    
                    //DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, af);
                    //sourceDataLine = (SourceDataLine) currentMixer.getLine(dataLineInfo);
                    sourceDataLine = (SourceDataLine) currentMixer.getLine(sourceDLInfo);
                    //sourceDataLine.open(af);
                    sourceDataLine.open(af, Ns2 * 4);
                    sourceDataLine.start();
                    playerThread = new Thread(new PlayThread());
                    playerThread.setPriority(Thread.MAX_PRIORITY);                    
                    playerThread.start();
                } catch (LineUnavailableException e) {
                    System.out.println("SoundOutputDeviceControl - StartPlayingSoundOnMixer -" + e);
                }
            }
        }
    }
    
    
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
    /*
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
    */
    private class PlayThread extends Thread {

        @Override
        public void run() {
            System.out.println("SoundOutputDeviceControl - Starting thread");
            threadEnd = false;
            stopPlay = false;
            try {
                while (!stopPlay) {
                    m_tick = (m_tick + 1) & 0xffff;
                    int tick = m_tick;
                                      
                    long start = System.nanoTime();  
                    int time_sample = (int) (start - m_time_start);
                    //long cpu_load = 1000 * m_time_elapsed / time_sample;
                    m_time_start = start;
                    
                    
                    
                    // 
                    for (int i = 0; i < N_SYM; i++) {
                        bit_inp[i] = data_tx[counter_tx];
                        counter_tx = counter_tx + 1;
                        if (counter_tx > PARAM.MAX_BIT_DATA)
                            counter_tx = PARAM.MAX_BIT_DATA;
                    }
                    
                    
                    if (counter_tx <= data_tx_length) {
                        
                        switch (DATARATE)
                        {
                            case 0:                        
                                bpsk.transmit(bit_inp, spkBuffer, spkBuffer2); 
                                break;
                                
                            case 1:
                                bpsk500.transmit(bit_inp, spkBuffer, spkBuffer2); 
                                break;
                            case 2:
                                bpsk2000.transmit(bit_inp, spkBuffer, spkBuffer2); 
                                break;
                        }
                        for (int i = 0; i < Ns; i++) {
                            spkBuffer[i] *= volume;
                            spkBuffer2[i] *= volume;
                        }
                        
                        
                        r_status_tx = 1;
                    } else {
                        bpsk.transmit_reset();
                        bpsk500.transmit_reset();
                        bpsk2000.transmit_reset();
                        for (int i = 0; i < Ns; i++) {
                            spkBuffer[i] = 0.0;
                            spkBuffer2[i] = 0.0;
                        }
                        counter_tx = data_tx_length;                        
                        r_status_tx = 0;
                    }
                    
                    
                    double peak = getPeak(spkBuffer, spkBuffer.length);
                    int level = (int) ( 100.0 * peak);
                    //counter_tx = bpsk.getCounter();
                    //progressBarLevel.setValue((int) (100 * peak));
                    r_level = level;
                    r_tick = tick;
                    r_counter = counter_tx;
                    r_time_sample = time_sample;
                    r_time_process = m_time_elapsed;
                    //r_persen_tx = 100 * counter_tx / data_tx_length;
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
                threadEnd = true;
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
        
        public int getPersenTx()
        {
            int persen_tx = 0;
            if ( data_tx_length > 0)
                persen_tx = 100 * counter_tx / data_tx_length;
            return persen_tx;
        }
        
        public void setVolume(int vol_persen)
        {
            volume = vol_persen * 0.01;
        }
        
        public int getStatusTx()
        {
            int ret = r_status_tx;
            return ret;
        }
        
        public void setPreambleLength(int val)
        {
            PREAMBLE_LENGTH = val;
        }
        
        public void setPostambleLength(int val)
        {
            POSTAMBLE_LENGTH = val;
        }
        
        private final int[] TABLE_NS = {4,16,64};
        public void setDataRate(int val)
        {
            DATARATE = val;
            N_SYM = TABLE_NS[DATARATE];
        }
        
        //======================================================================
        
        
    private static final int PATTERN_LENGTH = 32;
    private int PREAMBLE_LENGTH = 32;
    private int POSTAMBLE_LENGTH = 32;
    private static final int BARKER_LENGTH = 26;
    private static final int GUARD_LENGTH = 6;
    private static final byte[] PATTERN_START = new byte[] {1,1,1,1,0,0,0,0, 1,1,1,0,0,1,0,1, 0,1,0,0,1,1,0,0, 1,1,1,1,0,0,0,0};
    private static final byte[] PATTERN_STOP  = new byte[] {0,0,0,0,1,1,1,1, 0,0,0,1,1,0,1,0, 1,0,1,1,0,0,1,1, 0,0,0,0,1,1,1,1};
        
    //-1 -1 -1 -1 -1 1 1 -1 -1 1 -1 1 -1
    private static final byte[] BARKER  = new byte[] {0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,1,1,0,0,1,1,0,0};
    private static final byte[] GUARD  = new byte[] {1,1,1,0,0,0};
    
    private final Golay golay = new Golay();    
    
    private int[] create_header(int rx_length)
    {
        int[] x = new int[12];
        int tmp = rx_length / 64;
        int j = 0;
        for (int i = 5; i >= 0; i--)
            x[j++] = (tmp >> i) & 1;
        for (int i = 0; i < 6; i++)
            x[i+6] = x[i] ^ 1;
        int[] y = golay.Encode(x, 0);
        return y;
    }
    
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
    
    
    public int bit_packet(byte[] x, int nx)
    {    
        // CEK TOTAL BIT
        if (nx <= 0)
            return 0;  
        
        // CREATE HEADER
        int[] header = create_header(nx);
        
        
        int nb = PATTERN_LENGTH + PREAMBLE_LENGTH + POSTAMBLE_LENGTH + nx * 8;
        if (nb > PARAM.MAX_BIT_DATA)
            return -1;       
       
        // PREAMBLE
        int k = 0;
        for (int i = 0; i < PREAMBLE_LENGTH; i++) {
            data_tx[k++] = 0;
        }
        
        // GUARD
        for (int i = 0; i < GUARD_LENGTH; i++) {
            data_tx[k++] = GUARD[i];
        }
        
        // BARKER
        for (int i = 0; i < BARKER_LENGTH; i++) {
            data_tx[k++] = BARKER[i];
        }
        
        // HEADER
        for (int i = 0; i < 24; i++) {
            data_tx[k++] = (byte) header[i];
        }
        
        // DATA
        for (int i = 0; i < 6; i++) {
            reg[i] = 0;
        }
        reg[6] = 1;
        
        for (int i = 0; i < nx; i++) {
            int tmp = (int) x[i];
            for (int j = 7; j >= 0; j--) {
                int bin = (( tmp >> j ) & 1);
                data_tx[k++] = (byte) bit_scramble(bin);
            }                
        }
        
        // GUARD
        for (int i = 0; i < GUARD_LENGTH; i++) {
            data_tx[k++] = GUARD[i];
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
