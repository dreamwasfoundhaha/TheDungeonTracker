package com.github.dreamwasfoundhaha.thedungeontracker.mixin;

import com.github.dreamwasfoundhaha.thedungeontracker.GuiDungeonTrackerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Collections;

@Mixin(GuiIngameMenu.class)
public abstract class MixinGuiIngameMenu extends GuiScreen {

    private static final int DUNGEON_BUTTON_ID = 999;
    private GuiButton dungeonButton;

    // ADD BUTTON
    @Inject(method = "initGui", at = @At("RETURN"))
    private void dungeontracker$init(CallbackInfo ci) {
        dungeonButton = new GuiButton(
                DUNGEON_BUTTON_ID,
                5,
                this.height - 25,
                90,
                20,
                "Dungeon"
        );
        this.buttonList.add(dungeonButton);
    }

    // HANDLE CLICK  (THIS WAS MISSING / WRONG)
    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    private void dungeontracker$click(GuiButton button, CallbackInfo ci) throws IOException {
        if (button.id == DUNGEON_BUTTON_ID) {
            Minecraft.getMinecraft().displayGuiScreen(
                    new GuiDungeonTrackerMenu()
            );
            ci.cancel(); // VERY IMPORTANT
        }
    }

    // TOOLTIP
    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void dungeontracker$tooltip(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (dungeonButton != null && dungeonButton.isMouseOver()) {
            this.drawHoveringText(
                    Collections.singletonList("Open Dungeon Tracker"),
                    mouseX,
                    mouseY
            );
        }
    }
}
