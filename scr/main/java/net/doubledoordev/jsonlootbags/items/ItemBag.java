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

package net.doubledoordev.jsonlootbags.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.doubledoordev.jsonlootbags.util.BagType;
import net.doubledoordev.jsonlootbags.util.Constants;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author Dries007
 */
public class ItemBag extends Item
{
    public ItemBag()
    {
        setUnlocalizedName("ItemLootBag");
        setTextureName(Constants.MODID + ":bag");
        setCreativeTab(CreativeTabs.tabMisc);
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        BagType bagType = BagType.getFromStack(stack);
        if (bagType == null) return "Invalid loot bag!";
        return bagType.itemname;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            BagType bagType = BagType.getFromStack(stack);
            if (bagType == null) return stack;
            player.inventory.decrStackSize(player.inventory.currentItem, 1);
            for (ItemStack lootStack : bagType.getRandomItems(world.rand))
            {
                world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, lootStack));
            }
        }
        return stack;
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (BagType type : BagType.getAllTypes())
        {
            list.add(type.getBagStack());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
        BagType bagType = BagType.getFromStack(stack);
        if (bagType == null) return super.getColorFromItemStack(stack, pass);
        return bagType.color.getRGB();
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        BagType bagType = BagType.getFromStack(stack);
        if (bagType == null) return EnumRarity.common;
        return bagType.rarity;
    }
}
