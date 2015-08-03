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

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author Dries007
 */
public class JsonLootBagsCommand extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "jsonlootbags";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "'/jsonlootbags dumpNBT' to dump what you are holding.";
    }

    @Override
    public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_)
    {
        try
        {
            EntityPlayerMP player = getCommandSenderAsPlayer(p_71515_1_);
            ItemStack stack = player.getHeldItem();
            if (stack == null) throw new CommandException("You aren't holding anything.");

            FileUtils.write(new File("JsonLootBags-NBTdump.txt"), "// Dump from " + p_71515_1_.getCommandSenderName() +
                    "\n// Item name: " + stack.getDisplayName() +
                    "\n// Timestamp: " + new Date().toString() +
                    '\n' + Constants.GSON.toJson(stack) + '\n', true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new CommandException(e.getMessage());
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_)
    {
        return p_71519_1_ instanceof EntityPlayer && (MinecraftServer.getServer().isSinglePlayer() || MinecraftServer.getServer().getConfigurationManager().func_152596_g(((EntityPlayer) p_71519_1_).getGameProfile()));
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_)
    {
        return getListOfStringsMatchingLastWord(p_71516_2_, "dumpNBT");
    }
}
