/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.tools_ryt;

/**
 *
 * @author riyanto
 */

/**
 * 
 * This is aes128 class
 */
public class aes128_class {

        private int[] sbox = new int[]   { 

        0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76, 
        0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0, 
        0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
        0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75, 
        0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84, 
        0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf, 
        0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8, 
        0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2, 
        0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73, 
        0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb, 
        0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79, 
        0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08, 
        0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a, 
        0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e, 
        0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf, 
        0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16 };    

        private int[] rsbox = new int[]
        { 0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb
        , 0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb
        , 0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e
        , 0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25
        , 0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92
        , 0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84
        , 0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06
        , 0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b
        , 0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73
        , 0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e
        , 0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b
        , 0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4
        , 0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f
        , 0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef
        , 0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61
        , 0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d };

        private int[] Rcon = new int[] {0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36};

         /**
         * Method to expand the key
         * @param eKey
         * @param key 
         */
        private void expandKey(int[] eKey, int[] key)
        {
          int ii, buf1;
          for (ii = 0; ii < 16;ii++)  eKey[ii] = key[ii];
  
          for (ii = 1; ii < 11; ii++){
            buf1 = eKey[ii*16 - 4];
            eKey[ii*16 + 0] = sbox[eKey[ii*16 - 3]]^eKey[(ii-1)*16 + 0]^Rcon[ii];
            eKey[ii*16 + 1] = sbox[eKey[ii*16 - 2]]^eKey[(ii-1)*16 + 1];
            eKey[ii*16 + 2] = sbox[eKey[ii*16 - 1]]^eKey[(ii-1)*16 + 2];
            eKey[ii*16 + 3] = sbox[buf1           ]^eKey[(ii-1)*16 + 3];
    
            eKey[ii*16 + 4] = eKey[(ii-1)*16 + 4]^eKey[ii*16 + 0];
            eKey[ii*16 + 5] = eKey[(ii-1)*16 + 5]^eKey[ii*16 + 1];
            eKey[ii*16 + 6] = eKey[(ii-1)*16 + 6]^eKey[ii*16 + 2];
            eKey[ii*16 + 7] = eKey[(ii-1)*16 + 7]^eKey[ii*16 + 3];
            eKey[ii*16 + 8] = eKey[(ii-1)*16 + 8]^eKey[ii*16 + 4];
            eKey[ii*16 + 9] = eKey[(ii-1)*16 + 9]^eKey[ii*16 + 5];
            eKey[ii*16 +10] = eKey[(ii-1)*16 +10]^eKey[ii*16 + 6];
            eKey[ii*16 +11] = eKey[(ii-1)*16 +11]^eKey[ii*16 + 7];
            eKey[ii*16 +12] = eKey[(ii-1)*16 +12]^eKey[ii*16 + 8];
            eKey[ii*16 +13] = eKey[(ii-1)*16 +13]^eKey[ii*16 + 9];
            eKey[ii*16 +14] = eKey[(ii-1)*16 +14]^eKey[ii*16 +10];
            eKey[ii*16 +15] = eKey[(ii-1)*16 +15]^eKey[ii*16 +11];
          }
        }
        /**
         * 
         * @param value
         * @return 
         */
        private int gmul2(int value)
        {
	        value &= 0xff; 
	        if ((value >> 7) > 0) {
		        value = value << 1;
		        return (value ^ 0x1b);
	        } else
		        return (value << 1) & 0xff;
        }
        /**
         * Method to encrypt aes eKey
         * @param state
         * @param eKey 
         */
        void aes_encr(int[] state, int[] eKey)
        {
          int buf1, buf2, buf3, round;
    
          for (round = 0; round < 9; round++){

            state[ 0]  = sbox[(state[ 0] ^ eKey[(round*16)     ]) & 0xff];
            state[ 4]  = sbox[(state[ 4] ^ eKey[(round*16) +  4]) & 0xff];
            state[ 8]  = sbox[(state[ 8] ^ eKey[(round*16) +  8]) & 0xff];
            state[12]  = sbox[(state[12] ^ eKey[(round*16) + 12]) & 0xff];
       
            buf1 = (state[1] ^ eKey[(round*16)+1]) & 0xff ;
            state[ 1]  = sbox[(state[ 5] ^ eKey[(round*16) +  5]) & 0xff];
            state[ 5]  = sbox[(state[ 9] ^ eKey[(round*16) +  9]) & 0xff];
            state[ 9]  = sbox[(state[13] ^ eKey[(round*16) + 13]) & 0xff];
            state[13]  = sbox[buf1];
          
            buf1 = (state[2] ^ eKey[(round*16) + 2]) & 0xff;
            buf2 = (state[6] ^ eKey[(round*16) + 6]) & 0xff;
            state[ 2]  = sbox[(state[10] ^ eKey[(round*16) + 10]) & 0xff];
            state[ 6]  = sbox[(state[14] ^ eKey[(round*16) + 14]) & 0xff];
            state[10]  = sbox[buf1];
            state[14]  = sbox[buf2];
          
            buf1 = (state[15] ^ eKey[(round*16) + 15]) & 0xff;
            state[15]  = sbox[(state[11] ^ eKey[(round*16) + 11]) & 0xff];
            state[11]  = sbox[(state[ 7] ^ eKey[(round*16) +  7]) & 0xff];
            state[ 7]  = sbox[(state[ 3] ^ eKey[(round*16) +  3]) & 0xff];
            state[ 3]  = sbox[buf1];
    
            buf1 = (state[0] ^ state[1] ^ state[2] ^ state[3]) & 0xff ;
            buf2 = state[0];
            buf3 = (state[0]^state[1]) & 0xff; buf3=gmul2(buf3);
            state[0] = (state[0] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[1]^state[2]) & 0xff; buf3=gmul2(buf3);
            state[1] = (state[1] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[2]^state[3]) & 0xff; buf3=gmul2(buf3); 
            state[2] = (state[2] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[3]^buf2) & 0xff;     buf3=gmul2(buf3); 
            state[3] = (state[3] ^ buf3 ^ buf1) & 0xff;
      
            buf1 = (state[4] ^ state[5] ^ state[6] ^ state[7]) & 0xff;
            buf2 = state[4];
            buf3 = (state[4]^state[5]) & 0xff; buf3=gmul2(buf3); 
            state[4] = (state[4] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[5]^state[6]) & 0xff; buf3=gmul2(buf3); 
            state[5] = (state[5] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[6]^state[7]) & 0xff; buf3=gmul2(buf3); 
            state[6] = (state[6] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[7]^buf2) & 0xff;     buf3=gmul2(buf3); 
            state[7] = (state[7] ^ buf3 ^ buf1) & 0xff;
         
            buf1 = (state[8] ^ state[9] ^ state[10] ^ state[11]) & 0xff;
            buf2 = state[8];
            buf3 = (state[8]^state[9]) & 0xff;   buf3=gmul2(buf3); 
            state[8] = (state[8] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[9]^state[10]) & 0xff;  buf3=gmul2(buf3); 
            state[9] = (state[9] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[10]^state[11]) & 0xff; buf3=gmul2(buf3); 
            state[10] = (state[10] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[11]^buf2) & 0xff;      buf3=gmul2(buf3); 
            state[11] = (state[11] ^ buf3 ^ buf1) & 0xff;
       
            buf1 = (state[12] ^ state[13] ^ state[14] ^ state[15]) & 0xff;
            buf2 = state[12];
            buf3 = (state[12]^state[13]) & 0xff; buf3=gmul2(buf3); 
            state[12] = (state[12] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[13]^state[14]) & 0xff; buf3=gmul2(buf3); 
            state[13] = (state[13] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[14]^state[15]) & 0xff; buf3=gmul2(buf3); 
            state[14] = (state[14] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[15]^buf2) & 0xff;      buf3=gmul2(buf3); 
            state[15] = (state[15] ^ buf3 ^ buf1) & 0xff;    

          }
    
          state[ 0]  = sbox[(state[ 0] ^ eKey[(round*16)     ]) & 0xff];
          state[ 4]  = sbox[(state[ 4] ^ eKey[(round*16) +  4]) & 0xff];
          state[ 8]  = sbox[(state[ 8] ^ eKey[(round*16) +  8]) & 0xff];
          state[12]  = sbox[(state[12] ^ eKey[(round*16) + 12]) & 0xff];
      
          buf1 = (state[1] ^ eKey[(round*16) + 1]) & 0xff;
          state[ 1]  = sbox[(state[ 5] ^ eKey[(round*16) +  5]) & 0xff];
          state[ 5]  = sbox[(state[ 9] ^ eKey[(round*16) +  9]) & 0xff];
          state[ 9]  = sbox[(state[13] ^ eKey[(round*16) + 13]) & 0xff];
          state[13]  = sbox[buf1];
       
          buf1 = (state[2] ^ eKey[(round*16) + 2]) & 0xff;
          buf2 = (state[6] ^ eKey[(round*16) + 6]) & 0xff;
          state[ 2]  = sbox[(state[10] ^ eKey[(round*16) + 10]) & 0xff];
          state[ 6]  = sbox[(state[14] ^ eKey[(round*16) + 14]) & 0xff];
          state[10]  = sbox[buf1];
          state[14]  = sbox[buf2];
    
          buf1 = (state[15] ^ eKey[(round*16) + 15]) & 0xff;
          state[15]  = sbox[(state[11] ^ eKey[(round*16) + 11]) & 0xff];
          state[11]  = sbox[(state[ 7] ^ eKey[(round*16) +  7]) & 0xff];
          state[ 7]  = sbox[(state[ 3] ^ eKey[(round*16) +  3]) & 0xff];
          state[ 3]  = sbox[buf1];
     
          state[ 0] ^= eKey[160];
          state[ 1] ^= eKey[161];
          state[ 2] ^= eKey[162];
          state[ 3] ^= eKey[163];
          state[ 4] ^= eKey[164];
          state[ 5] ^= eKey[165];
          state[ 6] ^= eKey[166];
          state[ 7] ^= eKey[167];
          state[ 8] ^= eKey[168];
          state[ 9] ^= eKey[169];
          state[10] ^= eKey[170];
          state[11] ^= eKey[171];
          state[12] ^= eKey[172];
          state[13] ^= eKey[173];
          state[14] ^= eKey[174]; 
          state[15] ^= eKey[175];
        } 
        /**
         * Method to decrypt aes eKey
         * @param state
         * @param eKey 
         */
        void aes_decr(int[] state, int[] eKey)
        {
          int buf1, buf2, buf3;
          int round;
          round = 9;
   
          state[ 0] ^= eKey[160];
          state[ 1] ^= eKey[161];
          state[ 2] ^= eKey[162];
          state[ 3] ^= eKey[163];
          state[ 4] ^= eKey[164];
          state[ 5] ^= eKey[165];
          state[ 6] ^= eKey[166];
          state[ 7] ^= eKey[167];
          state[ 8] ^= eKey[168];
          state[ 9] ^= eKey[169];
          state[10] ^= eKey[170];
          state[11] ^= eKey[171];
          state[12] ^= eKey[172];
          state[13] ^= eKey[173];
          state[14] ^= eKey[174]; 
          state[15] ^= eKey[175];

          state[ 0]  = (rsbox[state[ 0]] ^ eKey[(round*16)     ]) & 0xff;
          state[ 4]  = (rsbox[state[ 4]] ^ eKey[(round*16) +  4]) & 0xff;
          state[ 8]  = (rsbox[state[ 8]] ^ eKey[(round*16) +  8]) & 0xff;
          state[12]  = (rsbox[state[12]] ^ eKey[(round*16) + 12]) & 0xff;

          buf1 =       (rsbox[state[13]] ^ eKey[(round*16) +  1]) & 0xff;
          state[13]  = (rsbox[state[ 9]] ^ eKey[(round*16) + 13]) & 0xff;
          state[ 9]  = (rsbox[state[ 5]] ^ eKey[(round*16) +  9]) & 0xff;
          state[ 5]  = (rsbox[state[ 1]] ^ eKey[(round*16) +  5]) & 0xff;
          state[ 1]  = buf1;

          buf1 =       (rsbox[state[ 2]] ^ eKey[(round*16) + 10]) & 0xff;
          buf2 =       (rsbox[state[ 6]] ^ eKey[(round*16) + 14]) & 0xff;
          state[ 2]  = (rsbox[state[10]] ^ eKey[(round*16) +  2]) & 0xff;
          state[ 6]  = (rsbox[state[14]] ^ eKey[(round*16) +  6]) & 0xff;
          state[10]  = buf1;
          state[14]  = buf2;

          buf1 =       (rsbox[state[ 3]] ^ eKey[(round*16) + 15]) & 0xff;
          state[ 3]  = (rsbox[state[ 7]] ^ eKey[(round*16) +  3]) & 0xff;
          state[ 7]  = (rsbox[state[11]] ^ eKey[(round*16) +  7]) & 0xff;
          state[11]  = (rsbox[state[15]] ^ eKey[(round*16) + 11]) & 0xff;
          state[15]  = buf1;

          for (round = 8; round >= 0; round--){

            buf1 = gmul2(gmul2(state[0]^state[2]) & 0xff) & 0xff;
            buf2 = gmul2(gmul2(state[1]^state[3]) & 0xff) & 0xff;
            state[0] ^= buf1; state[1] ^= buf2; state[2] ^= buf1; state[3] ^= buf2;
     
            buf1 = gmul2(gmul2(state[4]^state[6]) & 0xff) & 0xff;
            buf2 = gmul2(gmul2(state[5]^state[7]) & 0xff) & 0xff;
            state[4] ^= buf1; state[5] ^= buf2; state[6] ^= buf1; state[7] ^= buf2;
       
            buf1 = gmul2(gmul2(state[8]^state[10]) & 0xff) & 0xff;
            buf2 = gmul2(gmul2(state[9]^state[11]) & 0xff) & 0xff;
            state[8] ^= buf1; state[9] ^= buf2; state[10] ^= buf1; state[11] ^= buf2;
      
            buf1 = gmul2(gmul2(state[12]^state[14]) & 0xff) & 0xff;
            buf2 = gmul2(gmul2(state[13]^state[15]) & 0xff) & 0xff;
            state[12] ^= buf1; state[13] ^= buf2; state[14] ^= buf1; state[15] ^= buf2;
    
            buf1 = (state[0] ^ state[1] ^ state[2] ^ state[3]) & 0xff;
            buf2 = state[0];
            buf3 = (state[0]^state[1]) & 0xff; buf3=gmul2(buf3) & 0xff; 
            state[0] = (state[0] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[1]^state[2]) & 0xff; buf3=gmul2(buf3) & 0xff; 
            state[1] = (state[1] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[2]^state[3]) & 0xff; buf3=gmul2(buf3) & 0xff; 
            state[2] = (state[2] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[3]^buf2) & 0xff;     buf3=gmul2(buf3) & 0xff; 
            state[3] = (state[3] ^ buf3 ^ buf1) & 0xff;
    
            buf1 = (state[4] ^ state[5] ^ state[6] ^ state[7]) & 0xff;
            buf2 = state[4];
            buf3 = (state[4]^state[5]) & 0xff; buf3=gmul2(buf3) & 0xff; 
            state[4] = (state[4] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[5]^state[6]) & 0xff; buf3=gmul2(buf3) & 0xff; 
            state[5] = (state[5] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[6]^state[7]) & 0xff; buf3=gmul2(buf3) & 0xff; 
            state[6] = (state[6] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[7]^buf2) & 0xff;     buf3=gmul2(buf3) & 0xff; 
            state[7] = (state[7] ^ buf3 ^ buf1) & 0xff;
       
            buf1 = (state[8] ^ state[9] ^ state[10] ^ state[11]) & 0xff;
            buf2 = state[8];
            buf3 = (state[8]^state[9]) & 0xff;   buf3=gmul2(buf3) & 0xff; 
            state[8] = (state[8] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[9]^state[10]) & 0xff;  buf3=gmul2(buf3) & 0xff; 
            state[9] = (state[9] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[10]^state[11]) & 0xff; buf3=gmul2(buf3) & 0xff; 
            state[10] = (state[10] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[11]^buf2) & 0xff;      buf3=gmul2(buf3) & 0xff; 
            state[11] = (state[11] ^ buf3 ^ buf1) & 0xff;
      
            buf1 = (state[12] ^ state[13] ^ state[14] ^ state[15]) & 0xff;
            buf2 = state[12];
            buf3 = (state[12]^state[13]) & 0xff; buf3=gmul2(buf3) & 0xff; 
            state[12] = (state[12] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[13]^state[14]) & 0xff; buf3=gmul2(buf3) & 0xff; 
            state[13] = (state[13] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[14]^state[15]) & 0xff; buf3=gmul2(buf3) & 0xff; 
            state[14] = (state[14] ^ buf3 ^ buf1) & 0xff;
            buf3 = (state[15]^buf2) & 0xff;      buf3=gmul2(buf3) & 0xff; 
            state[15] = (state[15] ^ buf3 ^ buf1) & 0xff;    

            state[ 0]  = (rsbox[state[ 0]] ^ eKey[(round*16)     ]) & 0xff;
            state[ 4]  = (rsbox[state[ 4]] ^ eKey[(round*16) +  4]) & 0xff;
            state[ 8]  = (rsbox[state[ 8]] ^ eKey[(round*16) +  8]) & 0xff;
            state[12]  = (rsbox[state[12]] ^ eKey[(round*16) + 12]) & 0xff;
    
            buf1 =       (rsbox[state[13]] ^ eKey[(round*16) +  1]) & 0xff;
            state[13]  = (rsbox[state[ 9]] ^ eKey[(round*16) + 13]) & 0xff;
            state[ 9]  = (rsbox[state[ 5]] ^ eKey[(round*16) +  9]) & 0xff;
            state[ 5]  = (rsbox[state[ 1]] ^ eKey[(round*16) +  5]) & 0xff;
            state[ 1]  = buf1;
      
            buf1 =       (rsbox[state[ 2]] ^ eKey[(round*16) + 10]) & 0xff;
            buf2 =       (rsbox[state[ 6]] ^ eKey[(round*16) + 14]) & 0xff;
            state[ 2]  = (rsbox[state[10]] ^ eKey[(round*16) +  2]) & 0xff;
            state[ 6]  = (rsbox[state[14]] ^ eKey[(round*16) +  6]) & 0xff;
            state[10]  = buf1;
            state[14]  = buf2;
     
            buf1 =       (rsbox[state[ 3]] ^ eKey[(round*16) + 15]) & 0xff;
            state[ 3]  = (rsbox[state[ 7]] ^ eKey[(round*16) +  3]) & 0xff;
            state[ 7]  = (rsbox[state[11]] ^ eKey[(round*16) +  7]) & 0xff;
            state[11]  = (rsbox[state[15]] ^ eKey[(round*16) + 11]) & 0xff;
            state[15]  = buf1;
          }
        } 

    /**
     * Method to block encrypt key
     * @param data
     * @param kunci
     * @return out
     */
    public byte[] block_encrypt(byte[] data, byte[] kunci)
        {
            int[] state = new int[16];
            int[] key = new int[16];         
            int[] eKey = new int[176];
            
            for (int i = 0; i < 16; i++) {
                state[i] =  data[i] & 0xff;
            }
            
            for (int i = 0; i < kunci.length; i++) {
                key[i] = kunci[i] & 0xff;
            }

            expandKey(eKey, key);
            aes_encr(state, eKey);
            
            byte[] out = new byte[32];
            for (int i = 0; i < state.length; i++) {
                out[i] = (byte) state[i];
            }   
            
            
            for (int i = 0; i < 16; i++) {
                state[i] =  data[i+16] & 0xff;
            }

            aes_encr(state, eKey);

            for (int i = 0; i < state.length; i++) {
                out[i+16] = (byte) state[i];
            }   
            
            
            
            return out;
        }

    /**
     * Method to block decrypt key
     * @param data
     * @param kunci
     * @return out
     */
    public byte[] block_decrypt(byte[] data, byte[] kunci)
        {
            int[] state = new int[16];
            int[] key = new int[16];         
            int[] eKey = new int[176];
            
            for (int i = 0; i < 16; i++) {
                state[i] = data[i] & 0xff;
            }
            
            for (int i = 0; i < kunci.length; i++) {
                key[i] = kunci[i] & 0xff;
            }

            expandKey(eKey, key);
            aes_decr(state, eKey);
            
            byte[] out = new byte[32];
            for (int i = 0; i < state.length; i++) {
                out[i] = (byte) state[i];
            } 
            
            
            for (int i = 0; i < 16; i++) {
                state[i] = data[i+16] & 0xff;
            }
            
            aes_decr(state, eKey);
            for (int i = 0; i < state.length; i++) {
                out[i+16] = (byte) state[i];
            } 
            
            
            return out;
        }  
        
    /**
     * method to encrypt the file
     * @param data
     * @param kunci
     * @return out
     */
    public byte[] file_encrypt(byte[] data, byte[] kunci)
        {          
            int num_block = (data.length / 16) + 1;
            int r = num_block * 16 - data.length;                                    
            byte[] out = new byte[16 * num_block];    
            int[] key = new int[16];         
            int[] eKey = new int[176]; 
            int[] state = new int[16];
            

            for (int i = 0; i < kunci.length; i++) {
                key[i] = kunci[i] & 0xff;
            }
            expandKey(eKey, key);                  
            
            int q = 0;
            int p = 0;
            for (int k = 0; k < num_block; k++) {            
                for (int i = 0; i < 16; i++) {
                    state[i] = q < data.length ? (data[q] & 0xff):r;
                    q++;
                }
                aes_encr(state, eKey);                
                for (int i = 0; i < 16; i++) {
                    out[p++] = (byte) state[i];
                } 
            }         
            
            return out;
        }        
        
    /**
     * Method to decrypt the file
     * @param data
     * @param kunci
     * @return y
     */
    public byte[] file_decrypt(byte[] data, byte[] kunci)
        {          
            int num_block = data.length / 16;                                   
            byte[] out = new byte[16 * num_block];    
            int[] key = new int[16];         
            int[] eKey = new int[176]; 
            int[] state = new int[16];  
            
            for (int i = 0; i < kunci.length; i++) {
                key[i] = kunci[i] & 0xff;
            }
            expandKey(eKey, key);                  
            
            int q = 0;
            int p = 0;
            for (int k = 0; k < num_block; k++) {            
                for (int i = 0; i < 16; i++) {
                    state[i] = data[q++] & 0xff;
                }
                aes_decr(state, eKey);                
                for (int i = 0; i < 16; i++) {
                    out[p++] = (byte) state[i];
                } 
            }
            
            byte[] y = new byte[p - state[15]];
            System.arraycopy(out, 0, y, 0, y.length);
            return y;
        }          
}