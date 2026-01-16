package com.github.dreamwasfoundhaha.thedungeontracker;

import com.github.dreamwasfoundhaha.thedungeontracker.dungeon.DungeonRun;
import com.github.dreamwasfoundhaha.thedungeontracker.dungeon.DungeonTracker;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GuiDungeonTrackerMenu extends GuiScreen {

    private final int boxWidth = 380;
    private final int boxHeight = 280;
    private final int lineHeight = 16;

    // --- SESSION SETTINGS ---
    public static boolean showDate = true;
    public static boolean showTime = true;
    public static boolean showScore = true;
    public static boolean showFloor = true;
    public static boolean useColorMode = false;

    // SCROLLING VARIABLES
    private int scrollOffset = 0;
    private boolean isScrolling = false;

    private boolean showSettings = false;
    private boolean selectingFloor = false;
    private String activeFilter = null;

    // ANIMATION VARS
    private float toggleAnim = 0f;
    private int toggleX, toggleY;
    private final int toggleWidth = 26;
    private final int toggleHeight = 12;

    private final List<String> floors = Arrays.asList(
            "Entrance", "F1", "F2", "F3", "F4", "F5", "F6", "F7",
            "M1", "M2", "M3", "M4", "M5", "M6", "M7", "BACK"
    );

    // --- HELPER METHODS ---
    private String getRoman(String input) {
        if (input == null || input.equalsIgnoreCase("Entrance")) return "Entrance";
        String num = input.replaceAll("[^0-9]", "");
        switch (num) {
            case "1": return "I";
            case "2": return "II";
            case "3": return "III";
            case "4": return "IV";
            case "5": return "V";
            case "6": return "VI";
            case "7": return "VII";
            default: return input;
        }
    }

    private String getBossName(String floor) {
        if (floor == null) return "Unknown";
        String f = floor.toUpperCase();
        if (f.contains("ENTRANCE")) return "Watcher";
        if (f.endsWith("VII") || f.contains("7")) return "Wither Lords";
        if (f.endsWith("VI") || f.contains("6")) return "Sadan";
        if (f.endsWith("V") || f.contains("5")) return "Livid";
        if (f.endsWith("IV") || f.contains("4")) return "Thorn";
        if (f.endsWith("III") || f.contains("3")) return "Professor";
        if (f.endsWith("II") || f.contains("2")) return "Scarf";
        if (f.endsWith("I") || f.contains("1")) return "Bonzo";
        return "Unknown";
    }

    private List<DungeonRun> getFilteredRuns() {
        if (activeFilter == null || activeFilter.equals("BACK")) return DungeonTracker.RECENT_RUNS;
        final String target = getRoman(activeFilter);
        return DungeonTracker.RECENT_RUNS.stream()
                .filter(run -> run.floor.trim().equalsIgnoreCase(target))
                .collect(Collectors.toList());
    }

    // --- DRAW SCREEN ---
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int x = (width - boxWidth) / 2;
        int y = (height - boxHeight) / 2;

        // Calculate Colors
        int rainbow = getRainbowColor();
        int titleColor = useColorMode ? rainbow : 0xFFFFFF; // White if off
        int subtitleColor = useColorMode ? rainbow : 0xAAAAAA; // Gray if off

        // 1. Draw main background box
        drawRect(x, y, x + boxWidth, y + boxHeight, 0xCC000000);

        // 2. Header
        drawCenteredString(fontRendererObj, "The Dungeon Tracker", width / 2, y + 10, titleColor);
        drawCenteredString(fontRendererObj, "By dreamwasfoundhaha", width / 2, y + 25, subtitleColor);

        // TOP LINE - Always uses titleColor (White or Rainbow)
        drawHorizontalLine(x + 10, x + boxWidth - 10, y + 40, titleColor);

        // 3. Render content (Settings, Floor Grid, or Main List)
        if (showSettings) {
            drawSettingsMenu(x, y, mouseX, mouseY);
        } else if (selectingFloor) {
            drawFloorGrid(x, y, mouseX, mouseY);
        } else {
            renderMainView(x, y, mouseX, mouseY);
        }

        // 4. Footer
        // BOTTOM LINE - Always uses titleColor (White or Rainbow)
        drawHorizontalLine(x + 10, x + boxWidth - 10, y + boxHeight - 35, titleColor);

        int footerY = y + boxHeight - 25;

        // Settings Link
        boolean hoverSet = mouseX >= x + 15 && mouseX <= x + 15 + fontRendererObj.getStringWidth("Settings") && mouseY >= footerY - 2 && mouseY <= footerY + 10;
        int settingsTextColor = useColorMode ? rainbow : (hoverSet ? 0xFFFFFF55 : 0xAAAAAA);
        drawString(fontRendererObj, "Settings", x + 15, footerY, settingsTextColor);

        // Total Runs
        drawCenteredString(fontRendererObj, "Total Runs: " + getFilteredRuns().size(), width / 2, footerY, subtitleColor);

        // Copyright
        String copy = "© dreamwasfoundhaha";
        drawString(fontRendererObj, copy, x + boxWidth - fontRendererObj.getStringWidth(copy) - 15, footerY, subtitleColor);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void renderMainView(int x, int y, int mouseX, int mouseY) {
        String title = (activeFilter == null ? "Recent Dungeon Data" : "Floor " + activeFilter + " Data");
        drawString(fontRendererObj, title, x + 15, y + 55, 0xFFFFFF);

        toggleX = x + boxWidth - toggleWidth - 15;
        toggleY = y + 57;
        boolean isFilterActive = (activeFilter != null);
        drawRect(toggleX, toggleY, toggleX + toggleWidth, toggleY + toggleHeight, isFilterActive ? 0xFF00AA00 : 0xFFAA0000);
        toggleAnim += ((isFilterActive ? 1f : 0f) - toggleAnim) * 0.2f;
        int knobX = (int) (toggleX + (toggleWidth - toggleHeight) * toggleAnim);
        drawRect(knobX, toggleY, knobX + toggleHeight, toggleY + toggleHeight, 0xFFFFFFFF);

        String modeText = "Floor Mode";
        drawString(fontRendererObj, modeText, toggleX - fontRendererObj.getStringWidth(modeText) - 5, y + 59, isFilterActive ? 0xFFFFD700 : 0xFFFFFF);

        renderScrollableList(x, y, mouseX, mouseY);
    }

    private void renderScrollableList(int x, int y, int mouseX, int mouseY) {
        List<DungeonRun> runs = getFilteredRuns();
        int listTop = y + 80;
        int listBottom = y + boxHeight - 40;
        int listHeight = listBottom - listTop;
        int maxVisible = listHeight / lineHeight;

        if (runs.isEmpty()) {
            drawCenteredString(fontRendererObj, "No runs found.", x + boxWidth / 2, listTop + 40, 0x777777);
            return;
        }

        // --- VISUAL SCROLLBAR LOGIC ---
        int scrollBarX = x + boxWidth - 15;
        int scrollBarWidth = 5;
        // Draw Track
        drawRect(scrollBarX, listTop, scrollBarX + scrollBarWidth, listBottom, 0xFF444444);

        if (runs.size() > maxVisible) {
            // Calculate Thumb Size & Position
            int maxScroll = runs.size() - maxVisible;
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

            float ratio = (float) listHeight / (runs.size() * lineHeight);
            int thumbHeight = Math.max(20, (int) (listHeight * ratio));
            int trackHeight = listHeight - thumbHeight;
            int thumbY = listTop + (int) (trackHeight * ((float) scrollOffset / maxScroll));


            boolean hoverThumb = mouseX >= scrollBarX && mouseX <= scrollBarX + scrollBarWidth && mouseY >= thumbY && mouseY <= thumbY + thumbHeight;
            int thumbColor = (isScrolling || hoverThumb) ? 0xFFFFFFFF : 0xFFAAAAAA;
            drawRect(scrollBarX, thumbY, scrollBarX + scrollBarWidth, thumbY + thumbHeight, thumbColor);
        } else {
            scrollOffset = 0;
        }

        // --- SCISSOR & DRAW LIST ---
        ScaledResolution res = new ScaledResolution(mc);
        int f = res.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x * f, (mc.displayHeight - (listBottom * f)), boxWidth * f, listHeight * f);

        GlStateManager.pushMatrix();

        float textScale = 1.0f;
        GlStateManager.scale(textScale, textScale, 1);

        for (int i = 0; i < runs.size(); i++) {

            int drawY = listTop + ((i - scrollOffset) * lineHeight);


            if (drawY < listTop - lineHeight || drawY > listBottom) continue;

            drawRunLine(runs.get(i), (int) ((x + 15) / textScale), (int) (drawY / textScale));
        }

        GlStateManager.popMatrix();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void drawRunLine(DungeonRun run, int dx, int dy) {
        StringBuilder sb = new StringBuilder();
        if (showDate) sb.append("Date: §7").append(run.date).append(" §r| ");
        if (showFloor) sb.append("Floor: §e").append(run.floor).append(" §r| ");
        if (showScore) sb.append("Score: §6").append(run.score).append(" (").append(run.rank).append(") §r| ");
        if (showTime) sb.append("Time: §c").append(run.time).append(" §f(").append(getBossName(run.floor)).append(")");
        String text = sb.toString();
        if (text.endsWith(" | ")) text = text.substring(0, text.length() - 3);
        drawString(fontRendererObj, text, dx, dy, 0xFFFFFF);
    }

    // --- INTERACTION ---
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (showSettings || selectingFloor) return;

        // MOUSE WHEEL LOGIC
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            List<DungeonRun> runs = getFilteredRuns();
            int listHeight = (boxHeight - 40) - 80;
            int maxVisible = listHeight / lineHeight;

            if (runs.size() > maxVisible) {
                if (wheel > 0) scrollOffset--;
                else scrollOffset++;

                // Clamp limits
                scrollOffset = Math.max(0, Math.min(scrollOffset, runs.size() - maxVisible));
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int x = (width - boxWidth) / 2;
        int y = (height - boxHeight) / 2;
        int footerY = y + boxHeight - 25;

        if (mouseButton == 0) {
            // 1. Settings Click
            if (mouseX >= x + 15 && mouseX <= x + 15 + fontRendererObj.getStringWidth("Settings") && mouseY >= footerY - 2 && mouseY <= footerY + 10) {
                showSettings = true;
                return;
            }

            if (showSettings) {
                int startY = y + 80;
                if (mouseX >= x + 20 && mouseX <= x + 150) {
                    if (mouseY >= startY && mouseY <= startY + 10) showDate = !showDate;
                    else if (mouseY >= startY + 25 && mouseY <= startY + 35) showTime = !showTime;
                    else if (mouseY >= startY + 50 && mouseY <= startY + 60) showScore = !showScore;
                    else if (mouseY >= startY + 75 && mouseY <= startY + 85) showFloor = !showFloor;
                    else if (mouseY >= startY + 100 && mouseY <= startY + 110) useColorMode = !useColorMode;
                }
                if (mouseX >= x + 15 && mouseX <= x + 65 && mouseY >= y + boxHeight - 60 && mouseY <= y + boxHeight - 45) showSettings = false;
                return;
            }

            // 2. Floor Mode Toggle
            if (mouseX >= toggleX && mouseX <= toggleX + toggleWidth && mouseY >= toggleY && mouseY <= toggleY + toggleHeight) {
                if (activeFilter != null) activeFilter = null;
                else selectingFloor = !selectingFloor;
                return;
            }

            // 3. Floor Grid Selection
            if (selectingFloor) {
                int startX = x + 35;
                int startY = y + 85;
                for (int i = 0; i < floors.size(); i++) {
                    int bx = startX + (i % 4 * 82);
                    int by = startY + (i / 4 * 37);
                    if (mouseX >= bx && mouseX <= bx + 70 && mouseY >= by && mouseY <= by + 25) {
                        activeFilter = floors.get(i).equals("BACK") ? null : floors.get(i);
                        selectingFloor = false;
                        scrollOffset = 0;
                        break;
                    }
                }
                return;
            }


            int listTop = y + 80;
            int listBottom = y + boxHeight - 40;
            int scrollBarX = x + boxWidth - 15;


            if (mouseX >= scrollBarX && mouseX <= scrollBarX + 15 && mouseY >= listTop && mouseY <= listBottom) {
                isScrolling = true;
                return;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        // 5. DRAGGING LOGIC
        if (isScrolling && clickedMouseButton == 0) {
            int x = (width - boxWidth) / 2;
            int y = (height - boxHeight) / 2;
            int listTop = y + 80;
            int listBottom = y + boxHeight - 40;
            int listHeight = listBottom - listTop;

            List<DungeonRun> runs = getFilteredRuns();
            int maxVisible = listHeight / lineHeight;
            int maxScroll = runs.size() - maxVisible;

            if (maxScroll > 0) {

                float val = (float) (mouseY - listTop) / listHeight;
                val = Math.max(0, Math.min(1, val));

                scrollOffset = (int) (val * maxScroll);
            }
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        //6. Dragging Stop
        if (state == 0) {
            isScrolling = false;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    // Settings Menu Helper
    private void drawSettingsMenu(int x, int y, int mouseX, int mouseY) {
        drawString(fontRendererObj, "Settings", x + 15, y + 55, 0xFFFFFF);
        int startY = y + 80;
        drawCheckbox(x + 20, startY, "Show Date", showDate);
        drawCheckbox(x + 20, startY + 25, "Show Time", showTime);
        drawCheckbox(x + 20, startY + 50, "Show Score", showScore);
        drawCheckbox(x + 20, startY + 75, "Show Floor", showFloor);
        drawCheckbox(x + 20, startY + 100, "Turn on/off Color Mode", useColorMode);

        int backX = x + 15;
        int backY = y + boxHeight - 60;
        boolean hoverBack = mouseX >= backX && mouseX <= backX + 50 && mouseY >= backY && mouseY <= backY + 15;
        drawRect(backX, backY, backX + 50, backY + 15, hoverBack ? 0xFF777777 : 0xFF444444);
        drawCenteredString(fontRendererObj, "DONE", backX + 25, backY + 4, 0xFFFFFF);
    }

    private void drawCheckbox(int x, int y, String label, boolean checked) {
        drawRect(x, y, x + 10, y + 10, 0xFFAAAAAA);
        if (checked) drawRect(x + 2, y + 2, x + 8, y + 8, 0xFF00FF00);
        drawString(fontRendererObj, label, x + 15, y + 1, 0xFFFFFF);
    }

    // Floor Grid Helper
    private void drawFloorGrid(int x, int y, int mouseX, int mouseY) {
        int startX = x + 35;
        int startY = y + 85;
        int rainbow = getRainbowColor(); // Get current color once per frame

        for (int i = 0; i < floors.size(); i++) {
            int bx = startX + (i % 4 * 82);
            int by = startY + (i / 4 * 37);
            boolean hover = mouseX >= bx && mouseX <= bx + 70 && mouseY >= by && mouseY <= by + 25;


            drawRect(bx, by, bx + 70, by + 25, hover ? 0xCCAAAAAA : 0x66AAAAAA);
            drawCenteredString(fontRendererObj, floors.get(i), bx + 35, by + 9, useColorMode ? rainbow : (hover ? 0xFF000000 : 0xFFFFFFFF));
        }
    }
    private int getRainbowColor() {
        // 3000ms for a full cycle.
        float hue = (System.currentTimeMillis() % 3000) / 3000f;
        return java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
    }

    @Override
    public boolean doesGuiPauseGame() { return true; }
}
