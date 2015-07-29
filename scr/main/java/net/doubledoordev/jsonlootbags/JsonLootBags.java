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

package net.doubledoordev.jsonlootbags;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.doubledoordev.jsonlootbags.items.ItemBag;
import net.doubledoordev.jsonlootbags.util.BagType;
import net.doubledoordev.jsonlootbags.util.Constants;
import net.doubledoordev.jsonlootbags.util.JsonLootBagsCommand;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

import static net.minecraftforge.common.ChestGenHooks.*;

/**
 * @author Dries007
 */
@Mod(modid = Constants.MODID)
public class JsonLootBags
{
    @Mod.Instance(Constants.MODID)
    public static JsonLootBags instance;

    private File folder;
    public ItemBag bag;
    private boolean makeexamples;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        folder = new File(event.getModConfigurationDirectory(), Constants.MODID);
        if (!folder.exists())
        {
            makeexamples = true;
            folder.mkdir();
        }

        bag = new ItemBag();
        GameRegistry.registerItem(bag, "ItemLootBag");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) throws IOException, NoSuchFieldException, IllegalAccessException
    {
        if (makeexamples)
        {
            Random random = new Random();
            Field contents = ChestGenHooks.class.getDeclaredField("contents");
            contents.setAccessible(true);
            for (String name : new String[] {DUNGEON_CHEST, BONUS_CHEST, VILLAGE_BLACKSMITH, STRONGHOLD_CROSSING, STRONGHOLD_LIBRARY, STRONGHOLD_CORRIDOR, PYRAMID_JUNGLE_DISPENSER, PYRAMID_JUNGLE_CHEST, PYRAMID_DESERT_CHEST, MINESHAFT_CORRIDOR})
            {
                //noinspection unchecked
                List<WeightedRandomChestContent> list = (List<WeightedRandomChestContent>) contents.get(ChestGenHooks.getInfo(name));
                BagType bagType = new BagType();
                bagType.color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
                bagType.itemname = name + " bag";
                bagType.amountOfItemsMin = 0;
                bagType.amountOfItemsMax = 36;
                bagType.invSlotsMin = 36;
                bagType.amountOfItemsMax = 36 * 2;
                bagType.items = list.toArray(new WeightedRandomChestContent[list.size()]);
                bagType.rarity = EnumRarity.values()[random.nextInt(EnumRarity.values().length)];
                FileUtils.writeStringToFile(new File(folder, name + ".json"), Constants.GSON.toJson(bagType));
            }
        }

        File[] bagFiles = folder.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".json");
            }
        });

        for (File bagFile : bagFiles)
        {
            BagType.register(FilenameUtils.getBaseName(bagFile.getName()), Constants.GSON.fromJson(FileUtils.readFileToString(bagFile), BagType.class));
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new JsonLootBagsCommand());
    }
}
