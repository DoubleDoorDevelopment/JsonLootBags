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

package net.doubledoordev.jsonlootbags.util;

import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.doubledoordev.jsonlootbags.JsonLootBags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.ForgeHooks;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is a wrapper around the existing {@link LootTableManager}, with its own cache, loaded from the tables folder.
 */
public class LootTableHook extends LootTableManager
{
    private static final Gson GSON_INSTANCE = new GsonBuilder().registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntry.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
    private static LootTableManager fallback;
    private final LoadingCache<ResourceLocation, LootTable> cache = CacheBuilder.newBuilder().build(new Loader());
    private File tablesFolder;

    private static LootTableHook instance;

    public LootTableHook(File folder)
    {
        // Only possible because we kill reloadLootTables
        // noinspection ConstantConditions
        super(null);
        tablesFolder = folder;
        // Custom reload, because we can't access super's folder. Otherwise this would be so much simpler.
        reload();
    }

    @Override
    public void reloadLootTables()
    {
        // Fuck no
    }

    public static void init(File tables)
    {
        instance = new LootTableHook(tables);
    }

    //todo: call this when stuff reloads...
    private void reload()
    {
        cache.invalidateAll();

        if (!tablesFolder.exists()) tablesFolder.mkdir();
        Path basePath = tablesFolder.toPath();
        Set<String> errors = new HashSet<>();
        for (File file : FileUtils.listFiles(tablesFolder, new String[]{"json"}, true))
        {
            String name = FilenameUtils.separatorsToUnix(FilenameUtils.removeExtension(basePath.relativize(file.toPath()).toString()));
            if (cache.getUnchecked(new ResourceLocation(Constants.MODID.toLowerCase(), name)) == null)
                errors.add(name);
        }
        if (!errors.isEmpty())
        {
            RuntimeException e =  new RuntimeException("One or more LootTables failed to load. Abort game loading.");
            e.setStackTrace(new StackTraceElement[0]);
            throw e;
        }

        JsonLootBags.getLogger().info("Loaded {} custom loot tables.", cache.size());
    }

    public static List<ItemStack> makeLoot(WorldServer worldIn, EntityPlayer playerIn, float luck, ResourceLocation location)
    {
        LootContext lootContext = new LootContext(luck, worldIn, instance, null, playerIn, null);
        fallback = worldIn.getLootTableManager();
        LootTable table = instance.cache.getIfPresent(location);
        if (table == null) table = fallback.getLootTableFromLocation(location);
        return table.generateLootForPools(worldIn.rand, lootContext);
    }

    @Override
    public LootTable getLootTableFromLocation(ResourceLocation location)
    {
        LootTable table = cache.getIfPresent(location);
        if (table == null) table = fallback.getLootTableFromLocation(location);
        return table;
    }

    private class Loader extends CacheLoader<ResourceLocation, LootTable>
    {
        @Override
        public LootTable load(ResourceLocation key) throws Exception
        {
            File file = new File(tablesFolder, key.getResourcePath() + ".json");
            if (!file.exists()) return null;
            if (!file.isFile()) throw new IllegalArgumentException(file + " has to be a file, not a folder.");

            return ForgeHooks.loadLootTable(GSON_INSTANCE, key, Files.toString(file, Charsets.UTF_8), true, LootTableHook.instance);
        }
    }
}
