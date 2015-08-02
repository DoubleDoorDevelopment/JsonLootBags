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

package net.doubledoordev.jsonlootbags.util.jsonparsing;

import com.google.gson.*;
import net.doubledoordev.jsonlootbags.util.Helper;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author Dries007
 */
public class JsonColor implements JsonSerializer<Color>, JsonDeserializer<Color>
{
    @Override
    public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        try
        {
            if (json.isJsonPrimitive())
            {
                Color color = Color.getColor(json.getAsString());
                if (color != null) return color;
                Field field = Color.class.getDeclaredField(json.getAsString().toUpperCase());
                if (field.getType() == Color.class)
                {
                    return (Color) field.get(null);
                }
            }
            else if (json.isJsonArray())
            {
                JsonArray array = json.getAsJsonArray();
                if (array.size() == 3)
                {
                    String red = array.get(0).getAsJsonPrimitive().getAsString();
                    String green = array.get(1).getAsJsonPrimitive().getAsString();
                    String blue = array.get(2).getAsJsonPrimitive().getAsString();

                    return getColorFromStrings(red, green, blue);
                }
                else if (array.size() == 4)
                {
                    String red = array.get(0).getAsJsonPrimitive().getAsString();
                    String green = array.get(1).getAsJsonPrimitive().getAsString();
                    String blue = array.get(2).getAsJsonPrimitive().getAsString();
                    String alpha = array.get(3).getAsJsonPrimitive().getAsString();

                    return getColorFromStrings(red, green, blue, alpha);
                }
            }
            else if (json.isJsonObject())
            {
                JsonObject object = json.getAsJsonObject();

                String red = Helper.getPossibleAsString(object, "0", "r", "red");
                String green = Helper.getPossibleAsString(object, "0", "g", "green");
                String blue = Helper.getPossibleAsString(object, "0", "b", "blue");
                String alpha = Helper.getPossibleAsString(object, "255", "a", "alpha");

                return getColorFromStrings(red, green, blue, alpha);
            }
        }
        catch (NumberFormatException | NoSuchFieldException | IllegalAccessException e)
        {
            throw new JsonParseException("Color format not parsable: " + json.toString(), e);
        }
        throw new JsonParseException("Color format not parsable: " + json.toString());
    }

    private Color getColorFromStrings(String red, String green, String blue)
    {
        try
        {
            int[] colors = Helper.parseInts(red, green, blue);
            return new Color(colors[0], colors[1], colors[2]);
        }
        catch (NumberFormatException e)
        {
            float[] colors = Helper.parseFloats(red, green, blue);
            return new Color(colors[0], colors[1], colors[2]);
        }
    }

    private Color getColorFromStrings(String red, String green, String blue, String alpha)
    {
        try
        {
            int[] colors = Helper.parseInts(red, green, blue, alpha);
            return new Color(colors[0], colors[1], colors[2], colors[3]);
        }
        catch (NumberFormatException e)
        {
            float[] colors = Helper.parseFloats(red, green, blue, alpha);
            if (colors[3] > 1) colors[3] = 1; // to avoid issues with default of 255
            return new Color(colors[0], colors[1], colors[2], colors[3]);
        }
    }

    @Override
    public JsonElement serialize(Color color, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonArray a = new JsonArray();
        a.add(new JsonPrimitive(color.getRed()));
        a.add(new JsonPrimitive(color.getGreen()));
        a.add(new JsonPrimitive(color.getBlue()));
        a.add(new JsonPrimitive(color.getAlpha()));
        return a;
    }
}
