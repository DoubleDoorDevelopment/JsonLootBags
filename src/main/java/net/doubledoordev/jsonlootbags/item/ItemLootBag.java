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

package net.doubledoordev.jsonlootbags.item;

import com.google.gson.*;
import net.doubledoordev.jsonlootbags.JsonLootBags;
import net.doubledoordev.jsonlootbags.util.Constants;
import net.doubledoordev.jsonlootbags.util.Helper;
import net.doubledoordev.jsonlootbags.util.LootTableHook;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemLootBag extends Item
{
    // REQUIRED
    private static final String JSON_NAME = "name"; // String (alphanumerical and underscores)
    private static final String JSON_HUMAN_NAME = "human-name"; // String
    private static final String JSON_LOOT_TABLE = "loot-table"; // String (resource location)
    // OPTIONAL
    private static final String JSON_COLORS = "colors"; // Array of numbers or strings (must be decodable numbers)
    private static final String JSON_TEXTURE = "texture"; // String (resource location) Build in bag texture by default.
    private static final String JSON_RARITY = "rarity"; // String (Common, Uncommon, Rare, or Epic) Common is default.
    private static final String JSON_EFFECT = "effect"; // Boolean (Defaults to true if rarity is not common)

    private static final String DEFAULT_TEXTURE = Constants.MODID.toLowerCase() + ":bag";

    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(ItemLootBag.class, new JsonDeserializer<ItemLootBag>()
            {
                @Override
                public ItemLootBag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
                {
                    final JsonObject object = json.getAsJsonObject();

                    for (String req : new String[]{JSON_NAME, JSON_HUMAN_NAME, JSON_LOOT_TABLE})
                        if (!object.has(req))
                            throw new JsonParseException("Missing required field: " + req);
                    final String name = object.get(JSON_NAME).getAsString();
                    if (!name.matches("^\\w+$"))
                        throw new JsonParseException("Name '" + name + "' has non-word characters in it. Only alphanumerical and underscores are allowed.");
                    final String human_name = object.get(JSON_HUMAN_NAME).getAsString();
                    final String table = object.get(JSON_LOOT_TABLE).getAsString();

                    final int[] colors = object.has(JSON_COLORS) ? Helper.jsonArrayToIntArray(object.getAsJsonArray(JSON_COLORS)) : new int[0];
                    final String texture = object.has(JSON_TEXTURE) ? object.get(JSON_TEXTURE).getAsString() : DEFAULT_TEXTURE;
                    final EnumRarity rarity = object.has(JSON_RARITY) ? Helper.getEnum(object.get(JSON_RARITY).getAsString(), true, EnumRarity.values()) : EnumRarity.COMMON;
                    final boolean effect = object.has(JSON_EFFECT) ? object.get(JSON_EFFECT).getAsBoolean() : rarity != EnumRarity.COMMON;

                    return new ItemLootBag(name, human_name, table, texture, rarity, colors, effect);
                }
            }).setPrettyPrinting().create();
    private static final List<ItemLootBag> LOOT_BAG_LIST = new ArrayList<>();

    public static ItemLootBag fromFile(File file) throws IOException
    {
        try
        {
            return GSON.fromJson(FileUtils.readFileToString(file), ItemLootBag.class);
        }
        catch (Exception e)
        {
            JsonLootBags.getLogger().fatal("An error occurred trying to load {} as LootBag. Will abort loading after the last files is parsed.", file);
            JsonLootBags.getLogger().catching(e);
        }
        return null;
    }

    public static ItemLootBag[] getLootBags()
    {
        return LOOT_BAG_LIST.toArray(new ItemLootBag[LOOT_BAG_LIST.size()]);
    }

    public final String name;
    public final String human_name;
    public final String texture;
    private final ResourceLocation table;
    private final EnumRarity rarity;
    private final int[] colors;
    private final boolean effect;

    public ItemLootBag(String name, String human_name, String table, String texture, EnumRarity rarity, int[] colors, boolean effect)
    {
        this.name = name;
        this.human_name = human_name;
        this.table = new ResourceLocation(table);
        this.texture = texture;
        this.rarity = rarity;
        this.colors = colors;
        this.effect = effect;

        LOOT_BAG_LIST.add(this);

        setCreativeTab(CreativeTabs.MISC);
        setUnlocalizedName(Constants.MODID.toLowerCase() + Character.toUpperCase(name.charAt(0)) + name.substring(1));
        setRegistryName(Constants.MODID.toLowerCase(), name);

        GameRegistry.register(this);

        JsonLootBags.getLogger().info("Successfully loaded {}", this);
    }

    @Override
    public String toString()
    {
        return "ItemLootBag{" +
                "registry_name='" + getRegistryName() + '\'' +
                ", name='" + name + '\'' +
                ", human_name='" + human_name + '\'' +
                ", texture='" + texture + '\'' +
                ", table=" + table +
                ", rarity=" + rarity +
                ", colors=" + Arrays.toString(colors) +
                '}';
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        if (!playerIn.capabilities.isCreativeMode) --itemStackIn.stackSize;
        if (!worldIn.isRemote)
        {
            for (ItemStack stack : LootTableHook.makeLoot((WorldServer) worldIn, playerIn, getLuck(itemStackIn), this.table))
            {
                EntityItem entityitem = new EntityItem(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, stack);
                entityitem.setNoPickupDelay();
                worldIn.spawnEntityInWorld(entityitem);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return rarity;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return super.hasEffect(stack) || effect;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        float luck = getLuck(stack);
        if (luck != 0) tooltip.add("Luck: " + luck);
        super.addInformation(stack, playerIn, tooltip, advanced);
    }

    private float getLuck(ItemStack stack)
    {
        return !stack.hasTagCompound() ? 0 : stack.getTagCompound().getInteger("luck");
    }

    public int getColor(ItemStack stack, int tintIndex)
    {
        return tintIndex < colors.length ? colors[tintIndex] : 0xFFFFFF;
    }
}
