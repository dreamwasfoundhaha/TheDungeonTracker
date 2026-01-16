package com.github.dreamwasfoundhaha.thedungeontracker;
import com.github.dreamwasfoundhaha.thedungeontracker.dungeon.ChatListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = "thedungeontracker", useMetadata = true)
public class TheDungeonTracker {

    public static KeyBinding menuKey;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // 1. Register Chat Listener
        MinecraftForge.EVENT_BUS.register(new ChatListener());

        // 2. Register Key Input Handler
        FMLCommonHandler.instance().bus().register(new KeyInputHandler());

        // 3. Setup Keybinding
        menuKey = new KeyBinding("Open Dungeon Tracker", Keyboard.KEY_O, "The Dungeon Tracker");
        ClientRegistry.registerKeyBinding(menuKey);


    }
}
