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
public interface Decode_ICallback {
    public void onBufferTx(double[] buf);
    public void onBufferRx(double[] buf_i, double[] buf_q);
    public void onPacketRx(byte[] buf, int ch);
}

