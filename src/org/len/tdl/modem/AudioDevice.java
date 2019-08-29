/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.len.tdl.modem;

import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;



/**
 *
 * @author ASUS
 */
public class AudioDevice {
    
    public static ArrayList<String> getSourceLines() {
        ArrayList<String> listLineIn = new ArrayList<String>();
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
            Mixer m = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = m.getTargetLineInfo();
            if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)) {//Only prints out info is it is a Microphone
                System.out.println("Line Name: " + info.getName());//The name of the AudioDevice
                System.out.println("Line Description: " + info.getDescription());//The type of audio device
                listLineIn.add(info.getName());
            }
        }
        
        return listLineIn;
    }
    
    
    public static Line.Info getSourceLine(int index) {
        
        int i = 0;
        Line.Info lineInfo = null;
        
        ArrayList<String> listLineIn = new ArrayList<String>();
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
            Mixer m = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = m.getTargetLineInfo();
            if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)) {
                if (i == index)
                    lineInfo = lineInfos[0];
                i++;
            }
        }

        return lineInfo;
    }
    
    
    
    public static int PCM_Byte2Double(AudioFormat af, byte[] buf, int offset, double[] dest_i, double[] dest_q, int destoff) {

        int sb = af.getSampleSizeInBits();
        int samp;
        int samp1, samp2;
        double[] dsamp = new double[2];

        int chs = af.getChannels();

        for (int i = 0; i < dest_i.length; i++) {

            for (int ch = 0; ch < chs; ch++) {
                if (sb == 16) {
                    samp1 = (int) buf[offset++] & 0xFF;
                    samp2 = (int) buf[offset++] & 0xFF;
                    if (af.isBigEndian()) {
                        samp = (samp1 << 8) | samp2;
                    } else {
                        samp = (samp2 << 8) | samp1;
                    }
                    if (af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED && ((samp & 0x8000) == 0x8000)) {
                        samp |= 0xFFFF0000;
                    }
                    dsamp[ch] = (double) samp / 32768.0d;
                } else if (sb == 8) {
                    samp = (int) buf[offset++] & 0xFF;
                    if (af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED && ((samp & 0x80) == 0x80)) {
                        samp |= 0xFFFFFF00;
                    }
                    dsamp[ch] = (double) samp / 128.0d;
                } else {
                    offset++;
                }
            }
            dest_i[destoff] = dsamp[0];
            dest_q[destoff] = dsamp[1];
            destoff++;
        }

        return offset;
    }
    
    
    
    
    public static ArrayList<String> getDestinationLines() {
        ArrayList<String> listLineIn = new ArrayList<String>();
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
            Mixer m = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = m.getSourceLineInfo();
            if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(SourceDataLine.class)) {//Only prints out info is it is a Microphone
                System.out.println("Line Name: " + info.getName());//The name of the AudioDevice
                System.out.println("Line Description: " + info.getDescription());//The type of audio device
                listLineIn.add(info.getName());
            }
        }
        
        return listLineIn;
    }
    
    
    
    public static Line.Info getDestinationLine(int index) {
        
        int i = 0;
        Line.Info lineInfo = null;
        
        ArrayList<String> listLineIn = new ArrayList<String>();
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
            Mixer m = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = m.getSourceLineInfo();
            if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(SourceDataLine.class)) {
                if (i == index)
                    lineInfo = lineInfos[0];
                i++;
            }
        }

        return lineInfo;
    }
    
    
    
    
}
