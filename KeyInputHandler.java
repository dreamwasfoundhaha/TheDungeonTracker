package com.github.dreamwasfoundhaha.thedungeontracker;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {

        if (TheDungeonTracker.menuKey.isPressed()) {
            // Open the Dungeon Tracker GUI
            Minecraft.getMinecraft().displayGuiScreen(new GuiDungeonTrackerMenu());
        }
    }
}