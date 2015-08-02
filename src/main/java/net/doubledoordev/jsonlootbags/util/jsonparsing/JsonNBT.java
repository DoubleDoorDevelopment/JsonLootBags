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
import com.google.gson.reflect.TypeToken;
import net.minecraft.nbt.*;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author Dries007
 */
public class JsonNBT implements JsonSerializer<NBTBase>, JsonDeserializer<NBTBase>
{
    public static final Type TYPE = new TypeToken<NBTBase>() {}.getType();

    @Override
    public NBTBase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        if (json.isJsonNull()) return null;
        if (json.isJsonObject())
        {
            NBTTagCompound compound = new NBTTagCompound();
            JsonObject object = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet())
            {
                NBTBase nbtBase = context.deserialize(entry.getValue(), TYPE);
                compound.setTag(entry.getKey(), nbtBase);
            }
            return compound;
        }
        if (json.isJsonArray())
        {
            NBTTagList nbtList = new NBTTagList();
            JsonArray jsonList = json.getAsJsonArray();
            for (JsonElement element : jsonList)
            {
                NBTBase nbtBase = context.deserialize(element, TYPE);
                nbtList.appendTag(nbtBase);
            }
            return nbtList;
        }
        if (json.isJsonPrimitive())
        {
            String string = json.getAsString();

            if (string.matches("[-+]?[0-9]*\\.?[0-9]+[d|D]"))
            {
                return new NBTTagDouble(Double.parseDouble(string.substring(0, string.length() - 1)));
            }
            else if (string.matches("[-+]?[0-9]*\\.?[0-9]+[f|F]"))
            {
                return new NBTTagFloat(Float.parseFloat(string.substring(0, string.length() - 1)));
            }
            else if (string.matches("[-+]?[0-9]+[b|B]"))
            {
                return new NBTTagByte(Byte.parseByte(string.substring(0, string.length() - 1)));
            }
            else if (string.matches("[-+]?[0-9]+[l|L]"))
            {
                return new NBTTagLong(Long.parseLong(string.substring(0, string.length() - 1)));
            }
            else if (string.matches("[-+]?[0-9]+[s|S]"))
            {
                return new NBTTagShort(Short.parseShort(string.substring(0, string.length() - 1)));
            }
            else if (string.matches("[-+]?[0-9]+"))
            {
                return new NBTTagInt(Integer.parseInt(string.substring(0, string.length())));
            }
            else if (string.matches("[-+]?[0-9]*\\.?[0-9]+"))
            {
                return new NBTTagDouble(Double.parseDouble(string.substring(0, string.length())));
            }
            else if (!string.equalsIgnoreCase("true") && !string.equalsIgnoreCase("false"))
            {
                if (string.startsWith("[") && string.endsWith("]"))
                {
                    if (string.length() > 2)
                    {
                        String s = string.substring(1, string.length() - 1);
                        String[] astring = s.split(",");

                        try
                        {
                            if (astring.length <= 1)
                            {
                                return new NBTTagIntArray(new int[] {Integer.parseInt(s.trim())});
                            }
                            else
                            {
                                int[] aint = new int[astring.length];

                                for (int i = 0; i < astring.length; ++i)
                                {
                                    aint[i] = Integer.parseInt(astring[i].trim());
                                }

                                return new NBTTagIntArray(aint);
                            }
                        }
                        catch (NumberFormatException numberformatexception)
                        {
                            return new NBTTagString(string);
                        }
                    }
                    else
                    {
                        return new NBTTagIntArray(new int[0]);
                    }
                }
                else
                {
                    if (string.startsWith("\"") && string.endsWith("\"") && string.length() > 2)
                    {
                        string = string.substring(1, string.length() - 1);
                    }

                    string = string.replaceAll("\\\\\"", "\"");
                    return new NBTTagString(string);
                }
            }
            else
            {
                return new NBTTagByte((byte)(Boolean.parseBoolean(string) ? 1 : 0));
            }
        }
        throw new JsonParseException("Invalid NBT: " + json.toString());

    }

    @Override
    public JsonElement serialize(NBTBase src, Type typeOfSrc, JsonSerializationContext context)
    {
        if (src instanceof NBTBase.NBTPrimitive)
        {
            return new JsonPrimitive(src.toString());
        }
        if (src instanceof NBTTagString)
        {
            return new JsonPrimitive(((NBTTagString) src).func_150285_a_());
        }
        if (src instanceof NBTTagCompound)
        {
            NBTTagCompound compound = ((NBTTagCompound) src);
            JsonObject object = new JsonObject();
            for (Object name : compound.func_150296_c())
            {
                object.add((String) name, context.serialize(compound.getTag((String) name)));
            }
            return object;
        }
        if (src instanceof NBTTagList)
        {
            NBTTagList list = ((NBTTagList) src.copy());
            JsonArray array = new JsonArray();
            while (list.tagCount() != 0)
            {
                array.add(context.serialize(list.removeTag(0)));
            }
            return array;
        }
        if (src instanceof NBTTagIntArray)
        {
            JsonArray array = new JsonArray();
            for (int val : ((NBTTagIntArray) src).func_150302_c()) array.add(new JsonPrimitive(val));
            return array;
        }
        throw new IllegalArgumentException("Not supported by MC's JSON code. Type: " + src.getClass() + " Value: " + src);
    }
}
