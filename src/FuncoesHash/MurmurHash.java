package FuncoesHash;

import Util.Grafico;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * This is a very fast, non-cryptographic hash suitable for general hash-based
 * lookup. See http://murmurhash.googlepages.com/ fohttpr more details.
 *
 * <p>
 * The C version of MurmurHash 2.0 found at that site was ported to Java by
 * Andrzej Bialecki (ab at getopt org).</p>
 */
public class MurmurHash {

    public static int murmurHash2(byte[] data, int seed) {
        int m = 0x5bd1e995;
        int r = 24;

        int h = seed ^ data.length;

        int len = data.length;
        int len_4 = len >> 2;

        for (int i = 0; i < len_4; i++) {
            int i_4 = i << 2;
            int k = data[i_4 + 3];
            k = k << 8;
            k = k | (data[i_4 + 2] & 0xff);
            k = k << 8;
            k = k | (data[i_4 + 1] & 0xff);
            k = k << 8;
            k = k | (data[i_4 + 0] & 0xff);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }

        int len_m = len_4 << 2;
        int left = len - len_m;

        if (left != 0) {
            if (left >= 3) {
                h ^= (int) data[len - 3] << 16;
            }
            if (left >= 2) {
                h ^= (int) data[len - 2] << 8;
            }
            if (left >= 1) {
                h ^= (int) data[len - 1];
            }

            h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }

    // Testing ...
    static int NUM = 1000;

    public static void main(String[] args) {
        byte[] bytes = new byte[4];
        int[] data;
        data = new int[NUM];
        for (int i = 0; i < NUM; i++) {
            bytes[0] = (byte) (i & 0xff);
            bytes[1] = (byte) ((i & 0xff00) >> 8);
            bytes[2] = (byte) ((i & 0xff0000) >> 16);
            bytes[3] = (byte) ((i & 0xff000000) >> 24);
            System.out.print(Integer.toHexString(i) + " " + Integer.toHexString(murmurHash2(bytes, 1)));
            System.out.println("  " + (Math.abs(murmurHash2(bytes, i))) % (NUM + 1));
            data[i % (NUM + 1)] = Math.abs(murmurHash2(bytes, i));
        }

        int[] data2 = {1, 2, 3, 2, 1};
        Grafico g = new Grafico(data2);
    }

    /*
     *  This code is public domain.
     *
     *  The MurmurHash3 algorithm was created by Austin Appleby and put into the public domain.
     *  See http://code.google.com/p/smhasher/
     *
     *  This java port was authored by
     *  Yonik Seeley and was placed into the public domain per
     *  https://github.com/yonik/java_util/blob/master/src/util/hash/MurmurHash3.java.
     */
    /**
     * <p>
     * This produces exactly the same hash values as the final C+ + * version of
     * MurmurHash3 and is thus suitable for producing the same hash values
     * across platforms.
     * <p>
     * The 32 bit x86 version of this hash should be the fastest variant for
     * relatively short keys like ids.
     * <p>
     * Note - The x86 and x64 versions do _not_ produce the same results, as the
     * algorithms are optimized for their respective platforms.
     * <p>
     * See also http://github.com/yonik/java_util for future updates to this
     * file.
     */
    /**
     * Returns the MurmurHash3_x86_32 hash.
     */
    public static int murmurhash3x8632(byte[] data, int offset, int len, int seed) {

        int c1 = 0xcc9e2d51;
        int c2 = 0x1b873593;

        int h1 = seed;
        int roundedEnd = offset + (len & 0xfffffffc);  // round down to 4 byte block

        for (int i = offset; i < roundedEnd; i += 4) {
            // little endian load order
            int k1 = (data[i] & 0xff) | ((data[i + 1] & 0xff) << 8) | ((data[i + 2] & 0xff) << 16) | (data[i + 3] << 24);
            k1 *= c1;
            k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
            k1 *= c2;

            h1 ^= k1;
            h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
            h1 = h1 * 5 + 0xe6546b64;
        }

        // tail
        int k1 = 0;

        switch (len & 0x03) {
            case 3:
                k1 = (data[roundedEnd + 2] & 0xff) << 16;
            // fallthrough
            case 2:
                k1 |= (data[roundedEnd + 1] & 0xff) << 8;
            // fallthrough
            case 1:
                k1 |= data[roundedEnd] & 0xff;
                k1 *= c1;
                k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                k1 *= c2;
                h1 ^= k1;
            default:
        }

        // finalization
        h1 ^= len;

        // fmix(h1);
        h1 ^= h1 >>> 16;
        h1 *= 0x85ebca6b;
        h1 ^= h1 >>> 13;
        h1 *= 0xc2b2ae35;
        h1 ^= h1 >>> 16;

        return h1;
    }
}
