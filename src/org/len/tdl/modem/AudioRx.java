/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.len.tdl.modem;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author ASUS
 */
public final class AudioRx {
    
    
    private final AudioFormat af = new AudioFormat(PARAM.FS, 16, 2, true, false);
    private TargetDataLine targetDataLine;

    private final int Ns_x1 = PARAM.FRAME_IF;
    private final int Ns_x4 = 4 * PARAM.FRAME_IF;

    private final byte[] inpBuffer = new byte[Ns_x4];
    private final double[] iBuffer = new double[Ns_x1];
    private final double[] qBuffer = new double[Ns_x1];
    private boolean stopRequested = false;

    private Decode_ICallback callback;

    public void setCallback(Decode_ICallback cb) {
        callback = cb;
    }

    //private final Thread captureThread = new Thread(new CaptureThread());

    //public AudioRx() {
    //    StartCapture(0);
    // }
    
    
    
private int counter_cek = 0;
private class CaptureThread extends Thread {

        @Override
        public void run() {

            try {
                while (!stopRequested) {
                    int cnt = targetDataLine.read(inpBuffer, 0, Ns_x4);
                    if (cnt > 0) {
                        AudioDevice.PCM_Byte2Double(af, inpBuffer, 0, iBuffer, qBuffer, 0);
                        if (callback != null) {
                            callback.onBufferRx(iBuffer, qBuffer);
                            
                            double max_peak = 0;
                            for (int i = 0; i < iBuffer.length; i++) {
                                if (iBuffer[i] > max_peak)
                                    max_peak = iBuffer[i];
                            }
                            counter_cek++;
                            if (counter_cek > 100) {
                                counter_cek = 0;
                                //System.out.println("peak = " + max_peak);
                            }
                            
                        }
                    }//end if
                    //Thread.sleep(5); 
                }//end while

                targetDataLine.flush();
                targetDataLine.stop();
                targetDataLine.close();
                targetDataLine = null;
            } catch (Exception e) {
                //System.out.println(e);
                System.exit(0);
            }//end catch
        }//end run

    }//end inner class CaptureThread
//===================================//

    public void StartCapture(int index) {
        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(AudioDevice.getSourceLine(index));
            targetDataLine.open(af);
            targetDataLine.start();
            stopRequested = false;
            Thread captureThread = new Thread(new CaptureThread());
            captureThread.setPriority(9);
            //captureThread.setPriority(Thread.MAX_PRIORITY);
            captureThread.start();           
        } catch (LineUnavailableException e) {
            //System.out.println(e);
            System.exit(0);
        }//end catch
    }

    public void StopCapture() {
        //if (stopRequested || targetDataLine == null) {
        //    return;
        //}
        stopRequested = true;
//        captureThread.interrupt();
//        try {
//            captureThread.join();
//        } catch (InterruptedException exp) {
//        }
        //targetDataLine.stop();
        //targetDataLine.close();
        //targetDataLine = null;
    }
    
    
    
    
    
    
    
    
    
}
