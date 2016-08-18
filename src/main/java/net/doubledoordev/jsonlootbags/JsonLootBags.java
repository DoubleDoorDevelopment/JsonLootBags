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

package net.doubledoordev.jsonlootbags;

import net.doubledoordev.jsonlootbags.client.ClientHelper;
import net.doubledoordev.jsonlootbags.item.ItemLootBag;
import net.doubledoordev.jsonlootbags.util.LootTableHook;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static net.doubledoordev.jsonlootbags.util.Constants.*;

@Mod(modid = MODID, name = MODID, updateJSON = UPDATE_URL, guiFactory = MOD_GUI_FACTORY, dependencies = "required-after:D3Core@[1.3,)")
public class JsonLootBags
{
    @Mod.Instance(MODID)
    public static JsonLootBags instance;

    private Logger logger;
    private Configuration config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) throws Exception
    {
        logger = event.getModLog();

        MinecraftForge.EVENT_BUS.register(this);

        config = new Configuration();
        updateConfig();

        File folder = new File(event.getModConfigurationDirectory(), MODID);
        if (!folder.exists()) folder.mkdir();

        LootTableHook.init(new File(folder, "tables"));

        {
            File bagsFolder = new File(folder, "bags");
            if (!bagsFolder.exists()) bagsFolder.mkdir();
            Path basePath = bagsFolder.toPath();
            Set<String> errors = new HashSet<>();
            for (File file : FileUtils.listFiles(bagsFolder, new String[]{"json"}, true))
            {
                if (ItemLootBag.fromFile(file) == null)
                    errors.add(basePath.relativize(file.toPath()).toString());
            }
            if (!errors.isEmpty())
            {
                Exception e =  new Exception("One or more LootBags failed to load. Abort game loading.");
                e.setStackTrace(new StackTraceElement[0]);
                throw e;
            }
            JsonLootBags.getLogger().info("Loaded {} loot bags.", ItemLootBag.getLootBags().length);
        }

        if (event.getSide().isClient()) ClientHelper.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (event.getSide().isClient()) ClientHelper.init();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) updateConfig();
    }

    private void updateConfig()
    {
        if (config.hasChanged()) config.save();
    }

    public static Configuration getConfig()
    {
        return instance.config;
    }

    public static Logger getLogger()
    {
        return instance.logger;
    }
}
