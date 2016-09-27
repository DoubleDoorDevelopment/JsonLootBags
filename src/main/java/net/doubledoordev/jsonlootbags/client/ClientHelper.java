/*
 * Copyright (c) 2014-2016, Dries007 & DoubleDoorDevelopment
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
 *  Neither the name of DoubleDoorDevelopment nor the names of its
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
 */

package net.doubledoordev.jsonlootbags.client;

import net.doubledoordev.d3core.events.D3LanguageInjectEvent;
import net.doubledoordev.jsonlootbags.item.ItemLootBag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientHelper
{
    public static final ClientHelper I = new ClientHelper();

    public static void preInit()
    {
        MinecraftForge.EVENT_BUS.register(I);

        for (final ItemLootBag item : ItemLootBag.getLootBags())
        {
            // IDK which one is effective, or which one is recommended. So I'm using them all.
            // The modeling system is confusing...
            ModelLoader.registerItemVariants(item, new ModelResourceLocation(item.texture), new ModelResourceLocation(item.texture, "inventory"));
            ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
                @Override
                public ModelResourceLocation getModelLocation(ItemStack stack)
                {
                    return new ModelResourceLocation(item.texture);
                }
            });
            ModelLoader.registerItemVariants(item, new ModelResourceLocation(item.texture));
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.texture));
        }
    }

    public static void init()
    {
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
            @Override
            public int getColorFromItemstack(ItemStack stack, int tintIndex)
            {
                return ((ItemLootBag) stack.getItem()).getColor(stack, tintIndex);
            }
        }, (Item[]) ItemLootBag.getLootBags());
    }

    @SubscribeEvent
    public void d3LanguageInjectEvent(D3LanguageInjectEvent event)
    {
        for (ItemLootBag axe : ItemLootBag.getLootBags())
        {
            event.map.put(axe.getUnlocalizedName() + ".name", axe.human_name);
        }
    }
}
