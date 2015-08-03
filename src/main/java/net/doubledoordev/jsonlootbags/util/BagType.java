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

import net.doubledoordev.jsonlootbags.JsonLootBags;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Dries007
 */
public class BagType
{
    private static final Map<String, BagType> bagTypeMap = new HashMap<>();

    public String itemname;
    public Color color;
    public int invSlotsMin = 36;
    public int invSlotsMax = 36;
    public int amountOfItemsMin = 1;
    public int amountOfItemsMax = 10;
    public WeightedRandomChestContent[] items;
    public EnumRarity rarity = EnumRarity.common;
    private String name;

    public static BagType getFromStack(ItemStack stack)
    {
        NBTTagCompound tagCompound = stack.stackTagCompound;
        if (tagCompound == null) return null;
        return bagTypeMap.get(tagCompound.getString("type"));
    }

    public static void register(String name, BagType bagType)
    {
        bagType.name = name;
        bagTypeMap.put(name, bagType);
    }

    public static Iterable<? extends BagType> getAllTypes()
    {
        return bagTypeMap.values();
    }

    public String getName()
    {
        return name;
    }

    public ItemStack getBagStack()
    {
        ItemStack stack = new ItemStack(JsonLootBags.instance.bag);
        stack.stackTagCompound = new NBTTagCompound();
        stack.stackTagCompound.setString("type", name);
        return stack;
    }

    public List<ItemStack> getRandomItems(Random rand)
    {
        InventoryBasic inventory = new InventoryBasic("", false, Helper.randInt(rand, invSlotsMin, invSlotsMax));
        WeightedRandomChestContent.generateChestContents(rand, items, inventory, Helper.randInt(rand, amountOfItemsMin, amountOfItemsMax));
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null) list.add(stack);
        }
        return list;
    }

    public static class MinMaxWeight
    {
        public int min, max, weight;
    }
}
