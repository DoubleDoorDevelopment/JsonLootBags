/*
 * Copyright (c) 2014,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the {organization} nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 */

package net.doubledoordev.jsonlootbags.util;

import com.google.gson.JsonObject;

import java.util.Random;

/**
 * @author Dries007
 */
public class Helper
{
    public static String getPossibleAsString(JsonObject object, String def, String... names)
    {
        for (String name : names)
        {
            if (object.has(name)) return object.getAsJsonPrimitive(name).getAsString();
        }
        return def;
    }

    public static float[] parseFloats(String... strings) throws NumberFormatException
    {
        float[] ints = new float[strings.length];
        for (int i = 0; i < strings.length; i++)
        {
            ints[i] = Float.parseFloat(strings[i]);
        }
        return ints;
    }

    public static int[] parseInts(String... strings) throws NumberFormatException
    {
        int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; i++)
        {
            ints[i] = Integer.parseInt(strings[i]);
        }
        return ints;
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(Random random, int min, int max)
    {
        if (min == max) return min;
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return random.nextInt((max - min) + 1) + min;
    }
}
