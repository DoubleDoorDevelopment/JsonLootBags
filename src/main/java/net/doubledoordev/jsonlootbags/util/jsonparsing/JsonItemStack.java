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
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Type;

/**
 * @author Dries007
 */
public class JsonItemStack implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack>
{
    public static final Type TYPE = new TypeToken<ItemStack>()
    {
    }.getType();

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();
        GameRegistry.UniqueIdentifier id = new GameRegistry.UniqueIdentifier(object.get("item").getAsString());
        ItemStack stack = GameRegistry.findItemStack(id.modId, id.name, object.has("stacksize") ? object.get("stacksize").getAsInt() : 1);
        if (stack == null) throw new JsonParseException("Invalid item: " + json.toString());
        if (object.has("damage")) stack.setItemDamage(object.get("damage").getAsInt());
        if (object.has("nbt")) stack.stackTagCompound = context.deserialize(object.get("nbt"), JsonNBT.TYPE);
        return stack;
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject object = new JsonObject();
        GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(src.getItem());
        object.addProperty("item", id.toString());
        object.addProperty("stacksize", src.stackSize);
        object.addProperty("damage", src.getItemDamage());
        if (src.stackTagCompound != null) object.add("nbt", context.serialize(src.stackTagCompound));
        return object;
    }
}
