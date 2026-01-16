package com.github.dreamwasfoundhaha.thedungeontracker;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Arrays;
import java.util.List;

public class CommandDungeonTracker extends CommandBase {

    private boolean shouldOpenGui = false;

    @Override
    public String getCommandName() {
        return "dungeontracker";
    }

    @Override
    public List<String> getCommandAliases() {
        // ./dt as a shortcut
        return Arrays.asList("dt", "dungeon");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/dungeontracker";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {

        shouldOpenGui = true;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (shouldOpenGui) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiDungeonTrackerMenu());
            shouldOpenGui = false;

            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }
}