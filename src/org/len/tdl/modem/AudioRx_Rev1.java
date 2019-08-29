/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.len.tdl.modem;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;

/**
 *
 * @author ASUS
 */
public class AudioRx_Rev1 {
    private final AudioFormat af = new AudioFormat(PARAM.FS, 16, 2, true, false);
    
    // Save the array of Mixers for when the user
    // is trying to determine which one they  want to
    // use for recording
    private Mixer.Info[] mixerInfo;

    // We need all these to help us determine capabilities of lines
    private Line.Info sourceDLInfo = new Line.Info(SourceDataLine.class);
    private Line.Info targetDLInfo = new Line.Info(TargetDataLine.class);
    private Line.Info clipInfo = new Line.Info(Clip.class);
    private Line.Info portInfo = new Line.Info(Port.class);

    // For opening data lines
    private TargetDataLine targetRecordLine;
    private Port targetRecordPort;

    // For controlling the inner classes thread
    boolean stopCapture = false;
    boolean threadEnded = true;

    private final int Ns_x1 = PARAM.FRAME_IF;
    private final int Ns_x4 = 4 * PARAM.FRAME_IF;

    private final byte[] inpBuffer = new byte[Ns_x4];
    private final double[] iBuffer = new double[Ns_x1];
    private final double[] qBuffer = new double[Ns_x1];
    private boolean stopRequested = false;
    
    private int r_level = 0;
    private int r_tick = 0;
    private int m_tick = 0;
    private int r_counter = 0;
    private int m_time_elapsed = 0;
    private long m_time_start = 0;
    private int r_time_process = 0;
    private int r_time_sample = 0;
    
    

    protected Decode_ICallback callback;

    public void setCallback(Decode_ICallback cb) {
        callback = cb;
    }

    private Thread captureThread;

    public List<String> ListAudioInputDevices(int selected_soundcard) {
        List<String> returnList = new ArrayList<String>();

        mixerInfo = AudioSystem.getMixerInfo();
        System.out.println("Available mixers:");
        for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
            Mixer currentMixer = AudioSystem.getMixer(mixerInfo[cnt]);

            // Because this is for a recording application, we only care about audio INPUT so we just
            // care if TargetDataLine is supported.
            // currentMixer.isLineSupported(targetDLInfo) && currentMixer.isLineSupported(portInfo)
            if (currentMixer.isLineSupported(targetDLInfo)) {
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
            StartMonitoringLevelsOnMixer(returnList.get(selected_soundcard));
        } else {
            StartMonitoringLevelsOnMixer(returnList.get(0));
        }
        return returnList;
    }
    
    public void StartCapture(int index) {

        
        
        
    }

    public void StartMonitoringLevelsOnMixer(String _MixerName) {

        //DetermineConfigFileIndexOfMixer(_MixerName);

        // Locate the target mixer
        for (int cnt = 0; cnt < mixerInfo.length; cnt++) {

            // Mixer currentMixer = AudioSystem.getMixer(mixerInfo[cnt]);
            // Because this is for a recording application, we only care about audio INPUT so we just
            // care if TargetDataLine is supported.
            if (mixerInfo[cnt].getName().equals(_MixerName)) {
                System.out.println("SoundInputDeviceControl - found target mixer: " + _MixerName);

                // This may freeze the UI but oh well, we have to wait for the inner thread to stop from the
                // previous selection
                stopCapture = true;
                while (threadEnded == false) {
                    try {
                        sleep(100);
                    } catch (InterruptedException ex) {                        
                    }
                }

                Mixer currentMixer = AudioSystem.getMixer(mixerInfo[cnt]);
                try {
                    targetRecordLine = (TargetDataLine) currentMixer.getLine(targetDLInfo);
                    targetRecordLine.open(af);
                    targetRecordLine.start();
                    captureThread = new CaptureThread();
                    //Thread captureThread = new CaptureThread();
                    captureThread.start();
                } catch (LineUnavailableException e) {
                    System.out.println("SoundInputDeviceControl - StartMonitorlingLevelsOnMixer -" + e);
                }
            }
        }
    }



    

private int counter_cek = 0;
    //Inner class to capture data from the selected input mixer
private  class CaptureThread extends Thread {

        //An arbitrary-size temporary holding buffer
        //byte tempBuffer[] = new byte[8*1024];

        public void run() {
            System.out.println("SoundInputDeviceControl - Starting thread");
            threadEnded = false;
            stopCapture = false;
            try {
                while (!stopCapture) {
                    //Read data from the internal buffer of
                    // the data line.
                    int cnt = targetRecordLine.read(inpBuffer, 0, Ns_x4);
                    if (cnt > 0) {
                        
                        m_tick = (m_tick + 1) & 0xffff;
                        int tick = m_tick;
                        double peak;

                        long start = System.nanoTime();  
                        int time_sample = (int) (start - m_time_start);
                        //long cpu_load = 1000 * m_time_elapsed / time_sample;
                        m_time_start = start;
                        // 
                        
                        int level = 0;
                        //System.out.println("N="+cnt);
                        AudioDevice.PCM_Byte2Double(af, inpBuffer, 0, iBuffer, qBuffer, 0);
                        if (callback != null) {
                            callback.onBufferRx(iBuffer, qBuffer);
                            
                            double max_peak = 0;
                            for (int i = 0; i < iBuffer.length; i++) {
                                if (iBuffer[i] > max_peak)
                                    max_peak = iBuffer[i];
                            }
                            level = (int) ( 100.0 * max_peak);
                            counter_cek++;
                            if (counter_cek > 100) {
                                counter_cek = 0;
                                //System.out.println("peak = " + max_peak);
                            }
                            
                        }
                        else
                        {
                            //System.out.println("NULL");                        
                        }
                    
                        r_level = level;
                        r_tick = tick;
                        r_counter = counter_cek;
                        r_time_sample = time_sample;
                        r_time_process = m_time_elapsed;
                    
                        long finish = System.nanoTime();
                        m_time_elapsed = (int) (finish - start);
                    
                    
                    }//end if
                }//end while

                targetRecordLine.close();
                threadEnded = true;
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }//end catch
        }//end run
    }//end inner class CaptureThread





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


        





}
