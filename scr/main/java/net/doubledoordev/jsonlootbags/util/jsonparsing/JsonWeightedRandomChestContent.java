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
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;

import java.lang.reflect.Type;

/**
 * @author Dries007
 *
 * json object with properties:
 * - itemstack: The itemstack
 * @see net.doubledoordev.jsonlootbags.util.jsonparsing.JsonItemStack
 * - min: The minimum number of these items.
 * - max: The maximum number of these items.
 * - weight: How often the item is chosen. (Higher is more)
 */
public class JsonWeightedRandomChestContent implements JsonSerializer<WeightedRandomChestContent>, JsonDeserializer<WeightedRandomChestContent>
{
    public static final Type TYPE = new TypeToken<WeightedRandomChestContent>(){}.getType();

    @Override
    public WeightedRandomChestContent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();
        ItemStack stack = context.deserialize(object.get("itemstack"), JsonItemStack.TYPE);
        int min = object.get("min").getAsInt();
        int max = object.get("max").getAsInt();
        int weight = object.get("weight").getAsInt();
        return new WeightedRandomChestContent(stack, min, max, weight);
    }

    @Override
    public JsonElement serialize(WeightedRandomChestContent src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject object = new JsonObject();
        object.addProperty("min", src.theMinimumChanceToGenerateItem);
        object.addProperty("max", src.theMaximumChanceToGenerateItem);
        object.addProperty("weight", src.itemWeight);
        object.add("itemstack", context.serialize(src.theItemId));
        return object;
    }
}
