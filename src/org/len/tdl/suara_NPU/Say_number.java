/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.suara_NPU;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.len.tdl.general_var.model_variabel;

/**
 *
 * @author Solveware
 */

/**
 * 
 * This is Say_number class
 */
public class Say_number {

    /**
     * Reference Variables
     */
    AudioInputStream sound;
    File soundFile;
    AudioInputStream soundIStream;
    AudioInputStream[] soundArray;
    DataLine.Info info;
    Clip clip;
    int Last_say = 0;
    model_variabel mod_var;

    public Say_number() {
    }

 /**
 * 
 * Method to input audio stream sayNPU
 */
    public Say_number(model_variabel model) {
        mod_var = model;
        soundArray = new AudioInputStream[30];
        
        
        for (int i = 0; i < soundArray.length; i++) {
            
            try {
                File a = new File(System.getProperty("user.home") + "/Documents/tools_datalink/sayNPU/" + (i + 1) + ".wav");
                soundArray[i] = AudioSystem.getAudioInputStream(a);
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(Say_number.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Say_number.class.getName()).log(Level.SEVERE, null, ex);
            }
        }       
    }

    /**
     * Method to stop say_number
     */
    public void stop_say() {
        clip.close();
    }

    /**
     * set to last say NPU
     * @param NPU 
     */
    public void set_last_say(int NPU) {
        Last_say = NPU;
    }
 
    /**
     * set init say NPU
     * @param NPU 
     */
    public void init_say(int NPU) {
        if (Last_say != NPU && !mod_var.isMute()) {
            soundIStream = soundArray[NPU-1];
            start_say();
            Last_say = NPU;
        }
    }

    /**
     * Method to start say_number 
     */
    public void start_say() {

        sound = null;
        sound = soundIStream;
        info = new DataLine.Info(Clip.class, sound.getFormat());
        clip = null;
        try {
            clip = (Clip) AudioSystem.getLine(info);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Say_number.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            clip.open(sound);
        } catch (IOException ex) {
            Logger.getLogger(Say_number.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Say_number.class.getName()).log(Level.SEVERE, null, ex);
        }
        clip.addLineListener(new LineListener() {
            public void update(LineEvent event) {
                if (event.getType() == LineEvent.Type.STOP) {
                    event.getLine().close();
                }
            }
        });
        clip.start();
    }
}