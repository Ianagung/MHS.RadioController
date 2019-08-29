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
public class BCH_127_63_10 {
    
    
    
        private final int[] alpha_to = { 1, 2, 4, 8, 16, 32, 64, 3, 6, 12, 24, 48, 96, 67, 5, 10, 20, 40, 80, 35, 70, 15, 30, 60, 120, 115, 101, 73, 17, 34, 68, 11, 22, 44, 88, 51, 102, 79, 29, 58, 116, 107, 85, 41, 82, 39, 78, 31, 62, 124, 123, 117, 105, 81, 33, 66, 7, 14, 28, 56, 112, 99, 69, 9, 18, 36, 72, 19, 38, 76, 27, 54, 108, 91, 53, 106, 87, 45, 90, 55, 110, 95, 61, 122, 119, 109, 89, 49, 98, 71, 13, 26, 52, 104, 83, 37, 74, 23, 46, 92, 59, 118, 111, 93, 57, 114, 103, 77, 25, 50, 100, 75, 21, 42, 84, 43, 86, 47, 94, 63, 126, 127, 125, 121, 113, 97, 65, 0 };
        private final int[] index_of = { -1, 0, 1, 7, 2, 14, 8, 56, 3, 63, 15, 31, 9, 90, 57, 21, 4, 28, 64, 67, 16, 112, 32, 97, 10, 108, 91, 70, 58, 38, 22, 47, 5, 54, 29, 19, 65, 95, 68, 45, 17, 43, 113, 115, 33, 77, 98, 117, 11, 87, 109, 35, 92, 74, 71, 79, 59, 104, 39, 100, 23, 82, 48, 119, 6, 126, 55, 13, 30, 62, 20, 89, 66, 27, 96, 111, 69, 107, 46, 37, 18, 53, 44, 94, 114, 42, 116, 76, 34, 86, 78, 73, 99, 103, 118, 81, 12, 125, 88, 61, 110, 26, 36, 106, 93, 52, 75, 41, 72, 85, 80, 102, 60, 124, 105, 25, 40, 51, 101, 84, 24, 123, 83, 50, 49, 122, 120, 121 };
        private final int[] g = { 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1 };

        private final int[] recd = new int[128];
        private final int[] data = new int[128];
        private final int[] bb = new int[64];


        public BCH_127_63_10()
        {
            //m = 7;
            //length = 127; // length = panjang data + redudancy  (2^m-1)
            //t = 10;
            //n = 127;
            //k = 64;// k = panjang data
        }

        private void encode_bch(int[] data_in, int[] bb)
        /*
         * Compute redundacy bb[], the coefficients of b(x). The redundancy
         * polynomial b(x) is the remainder after dividing x^(length-k)*data(x)
         * by the generator polynomial g(x).
         */
        {
            int i, j;
            int feedback;
            int n = 127;
            int k = 64;

            for (i = 0; i < n - k; i++)
                bb[i] = 0;
            for (i = k - 1; i >= 0; i--)
            {
                feedback = data_in[i] ^ bb[n - k - 1];
                if (feedback != 0)
                {
                    for (j = n - k - 1; j > 0; j--)
                        if (g[j] != 0)
                            bb[j] = bb[j - 1] ^ feedback;
                        else
                            bb[j] = bb[j - 1];
                    if ((g[0] == 1) && (feedback == 1))
                    {
                        bb[0] = 1;
                    }
                    else
                    {
                        bb[0] = 0;
                    }
                }
                else
                {
                    for (j = n - k - 1; j > 0; j--)
                        bb[j] = bb[j - 1];
                    bb[0] = 0;
                }
            }
        }



        private int decode_bch(int[] recd)
        /*
         * Simon Rockliff's implementation of Berlekamp's algorithm.
         *
         * Assume we have received bits in recd[i], i=0..(n-1).
         *
         * Compute the 2*t syndromes by substituting alpha^i into rec(X) and
         * evaluating, storing the syndromes in s[i], i=1..2t (leave s[0] zero) .
         * Then we use the Berlekamp algorithm to find the error location polynomial
         * elp[i].
         *
         * If the degree of the elp is >t, then we cannot correct all the errors, and
         * we have detected an uncorrectable error pattern. We output the information
         * bits uncorrected.
         *
         * If the degree of elp is <=t, we substitute alpha^i , i=1..n into the elp
         * to get the roots, hence the inverse roots, the error location numbers.
         * This step is usually called "Chien's search".
         *
         * If the number of errors located is not equal the degree of the elp, then
         * the decoder assumes that there are more than t errors and cannot correct
         * them, only detect them. We output the information bits uncorrected.
         */
        {
            int i, j, u, q, t2, count = 0, syn_error = 0;
            int[][] elp = new int[22][20];
            int[] d = new int[23];
            int[] l = new int[23];
            int[] u_lu = new int[23];
            int[] s = new int[22];
            int[] root = new int[10];
            int[] loc = new int[10];
            int[] reg = new int[11];

            int n = 127;
            int t = 10;

            int bit_error = 0;


            t2 = 2 * t;

            /* first form the syndromes */
            for (i = 1; i <= t2; i++)
            {
                s[i] = 0;
                for (j = 0; j < n; j++)
                    if (recd[j] != 0)
                        s[i] ^= alpha_to[(i * j) % n];
                if (s[i] != 0)
                    syn_error = 1; /* set error flag if non-zero syndrome */
                /*
                 * Note:    If the code is used only for ERROR DETECTION, then
                 *          exit program here indicating the presence of errors.
                 */
                /* convert syndrome from polynomial form to index form  */
                s[i] = index_of[s[i]];
            }
            //printf("\n");

            if (syn_error == 1)
            {	/* if there are errors, try to correct them */
                /*
                 * Compute the error location polynomial via the Berlekamp
                 * iterative algorithm. Following the terminology of Lin and
                 * Costello's book :   d[u] is the 'mu'th discrepancy, where
                 * u='mu'+1 and 'mu' (the Greek letter!) is the step number
                 * ranging from -1 to 2*t (see L&C),  l[u] is the degree of
                 * the elp at that step, and u_l[u] is the difference between
                 * the step number and the degree of the elp. 
                 */
                /* initialise table entries */
                d[0] = 0;			/* index form */
                d[1] = s[1];		/* index form */
                elp[0][0] = 0;		/* index form */
                elp[1][0] = 1;		/* polynomial form */
                for (i = 1; i < t2; i++)
                {
                    elp[0][i] = -1;	/* index form */
                    elp[1][i] = 0;	/* polynomial form */
                }
                l[0] = 0;
                l[1] = 0;
                u_lu[0] = -1;
                u_lu[1] = 0;
                u = 0;

                do
                {
                    u++;
                    if (d[u] == -1)
                    {
                        l[u + 1] = l[u];
                        for (i = 0; i <= l[u]; i++)
                        {
                            elp[u + 1][i] = elp[u][i];
                            elp[u][i] = index_of[elp[u][i]];
                        }
                    }
                    else
                    /*
                     * search for words with greatest u_lu[q] for
                     * which d[q]!=0 
                     */
                    {
                        q = u - 1;
                        while ((d[q] == -1) && (q > 0))
                            q--;
                        /* have found first non-zero d[q]  */
                        if (q > 0)
                        {
                            j = q;
                            do
                            {
                                j--;
                                if ((d[j] != -1) && (u_lu[q] < u_lu[j]))
                                    q = j;
                            } while (j > 0);
                        }

                        /*
                         * have now found q such that d[u]!=0 and
                         * u_lu[q] is maximum 
                         */
                        /* store degree of new elp polynomial */
                        if (l[u] > l[q] + u - q)
                            l[u + 1] = l[u];
                        else
                            l[u + 1] = l[q] + u - q;

                        /* form new elp(x) */
                        for (i = 0; i < t2; i++)
                            elp[u + 1][i] = 0;
                        for (i = 0; i <= l[q]; i++)
                            if (elp[q][i] != -1)
                                elp[u + 1][i + u - q] =
                                           alpha_to[(d[u] + n - d[q] + elp[q][i]) % n];
                        for (i = 0; i <= l[u]; i++)
                        {
                            elp[u + 1][i] ^= elp[u][i];
                            elp[u][i] = index_of[elp[u][i]];
                        }
                    }
                    u_lu[u + 1] = u - l[u + 1];

                    /* form (u+1)th discrepancy */
                    if (u < t2)
                    {
                        /* no discrepancy computed on last iteration */
                        if (s[u + 1] != -1)
                            d[u + 1] = alpha_to[s[u + 1]];
                        else
                            d[u + 1] = 0;
                        for (i = 1; i <= l[u + 1]; i++)
                            if ((s[u + 1 - i] != -1) && (elp[u + 1][i] != 0))
                                d[u + 1] ^= alpha_to[(s[u + 1 - i]
                                              + index_of[elp[u + 1][i]]) % n];
                        /* put d[u+1] into index form */
                        d[u + 1] = index_of[d[u + 1]];
                    }
                } while ((u < t2) && (l[u + 1] <= t));

                u++;

                //Debug.WriteLine("JUMLAH ERROR = " + l[u]);
                bit_error = l[u];

                if (l[u] <= t)
                {/* Can correct errors */
                    /* put elp into index form */
                    for (i = 0; i <= l[u]; i++)
                        elp[u][i] = index_of[elp[u][i]];


                    /* Chien search: find roots of the error location polynomial */
                    for (i = 1; i <= l[u]; i++)
                        reg[i] = elp[u][i];
                    count = 0;
                    for (i = 1; i <= n; i++)
                    {
                        q = 1;
                        for (j = 1; j <= l[u]; j++)
                            if (reg[j] != -1)
                            {
                                reg[j] = (reg[j] + j) % n;
                                q ^= alpha_to[reg[j]];
                            }
                        if (q == 0)
                        {	/* store root and error
						         * location number indices */
                            root[count] = i;
                            loc[count] = n - i;
                            count++;
                            //printf("%3d ", n - i);
                        }
                    }
                    //printf("\n");
                    if (count == l[u])
                        /* no. roots = degree of elp hence <= t errors */
                        for (i = 0; i < l[u]; i++)
                            recd[loc[i]] ^= 1;
                    else	/* elp has degree >t hence cannot solve */
                    {
                        
                        //System.out.println("Incomplete decoding: errors detected\n");
                        bit_error = 11;
                    }
                }
            }

            //Debug.WriteLine("CEK");

            return bit_error;
        }



/*
        public int main(int jumlah_error)
        {
            int i;
            int decerror = 0;
            int n = 127;
            int k = 64;

            // Randomly generate DATA 
            int seed = 131073;
            Random rnd = new Random(seed);

            for (i = 0; i < k; i++)
                data[i] = (rnd.Next() & 65536) >> 16;

            encode_bch(ref data, ref bb);           

      
            for (i = 0; i < n - k; i++)
                recd[i] = bb[i];
            for (i = 0; i < k; i++)
                recd[i + n - k] = data[i];

            int pos = data[i] = rnd.Next();
            //int jumlah_error = 15;
            int posisi_error = 0;
            for (i = 0; i < jumlah_error; i++)
            {
                posisi_error = (i * 11 + pos) % n;
                recd[posisi_error] ^= 1;
                Debug.WriteLine("posisi error = " + posisi_error);
            }

            int num_error = decode_bch(ref recd);  // DECODE received codeword recv[]

            Debug.WriteLine("jumlah error yang terdeteksi = " + num_error);

       
            Debug.WriteLine("original data  = ");
            for (i = 0; i < k; i++)
            {
                Debug.WriteLine("data[" + i + "] = " + data[i]);
            }
            Debug.WriteLine("\nrecovered data = ");
            int xx = 0;
            for (i = n - k; i < n; i++)
            {
                Debug.WriteLine("recd[" + xx++ + "] = " + recd[i]);
            }

        
            for (i = n - k; i < n; i++)
                if (data[i - n + k] != recd[i])
                    decerror++;
            if (decerror > 0)
            {
                Debug.WriteLine("There were decoding errors in message positions\n");
                Debug.WriteLine("jumlah error = " + decerror);
            }
            else
            {
                Debug.WriteLine("Succesful decoding\n");
                Debug.WriteLine("jumlah error yang terdeteksi dan terkoreksi = " + num_error);
            }

            return decerror;
        }
*/


        public void encode_data(byte[] x, byte[] y, int nx)
        {
            int i;
            int n = 127;
            int k = 64;

            int np = 0; int no = 0;
            int nblock = nx / 8;
            int sync = 0;
            for (int nb = 0; nb < nblock; nb++)
            {
                int nk = 0;
                for (int nd = 0; nd < 8; nd++)
                {
                    int data_tmp = x[np++];
                    for (int ns = 7; ns >= 0; ns--)
                    {
                        data[nk++] = (data_tmp >> ns) & 1;
                    }
                }

                encode_bch(data, bb);           /* encode data */


                /*
                 * recd[] are the coefficients of c(x) = x**(length-k)*data(x) + b(x)
                 */
                for (i = 0; i < n - k; i++)
                    recd[i] = bb[i];
                for (i = 0; i < k; i++)
                    recd[i + n - k] = data[i];
                recd[127] = sync;
                sync ^= 1;

                nk = 0;
                for (int nd = 0; nd < 16; nd++)
                {
                    int tmp = 0;
                    for (int ns = 0; ns < 8; ns++)
                    {
                        tmp = (tmp << 1) | (recd[nk++] & 1);
                    }
                    y[no++] = (byte)tmp;
                }
            }
        }

        public int decode_data(byte[] x, byte[] y)
        {
            //int i;
            //int n = 127;
            //int k = 64;
            int num_error = 0;

            int np = 0; int no = 0;
            int nblock = x.length / 16;
            for (int nb = 0; nb < nblock; nb++)
            {
                int nk = 0;
                for (int nd = 0; nd < 16; nd++)
                {
                    int data_tmp = x[np++];
                    for (int ns = 7; ns >= 0; ns--)
                    {
                        recd[nk++] = (data_tmp >> ns) & 1;
                    }
                }

                num_error += decode_bch(recd);  // DECODE received codeword recv[]

                nk = 0;
                for (int nd = 0; nd < 8; nd++)
                {
                    int tmp = 0;
                    for (int ns = 0; ns < 8; ns++)
                    {
                        tmp = (tmp << 1) | (recd[63 + nk] & 1);
                        nk++;
                    }
                    y[no++] = (byte)tmp;
                }
            }
            return num_error;
        }

        public void get_data(byte[] x, byte[] y, int nx)
        {
            int np = 0; int no = 0;
            int nblock = nx / 16;
            for (int nb = 0; nb < nblock; nb++)
            {
                int nk = 0;
                for (int nd = 0; nd < 16; nd++)
                {
                    int data_tmp = x[np++];
                    for (int ns = 7; ns >= 0; ns--)
                    {
                        recd[nk++] = (data_tmp >> ns) & 1;
                    }
                }

                //num_error += decode_bch(ref recd);  // DECODE received codeword recv[]

                nk = 0;
                for (int nd = 0; nd < 8; nd++)
                {
                    int tmp = 0;
                    for (int ns = 0; ns < 8; ns++)
                    {
                        tmp = (tmp << 1) | (recd[63 + nk] & 1);
                        nk++;
                    }
                    y[no++] = (byte)tmp;
                }
            }
        }
    
    
    
    
}
