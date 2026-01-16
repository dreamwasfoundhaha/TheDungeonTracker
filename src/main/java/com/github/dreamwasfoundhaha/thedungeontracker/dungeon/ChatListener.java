package com.github.dreamwasfoundhaha.thedungeontracker.dungeon;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener {

    private static final Pattern FLOOR_PATTERN =
            Pattern.compile("The Catacombs - Floor ([IVX]+)");

    private static final Pattern SCORE_PATTERN =
            Pattern.compile("Team Score: (\\d+) \\((S\\+|S|A|B|C|D)\\)");

    private static final Pattern TIME_PATTERN =
            Pattern.compile("Defeated.*?(\\d+m \\d+s)");

    private String floor;
    private String score;
    private String rank;

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {


        String msg = event.message.getUnformattedText().trim();

        Matcher f = FLOOR_PATTERN.matcher(msg);
        if (f.find()) {
            floor = f.group(1);
            System.out.println("[Tracker] Floor detected: " + floor);
        }

        Matcher s = SCORE_PATTERN.matcher(msg);
        if (s.find()) {
            score = s.group(1);
            rank  = s.group(2);
            System.out.println("[Tracker] Score detected: " + score + " (" + rank + ")");
        }

        Matcher t = TIME_PATTERN.matcher(msg);
        if (t.find()) {
            String time = t.group(1);

            if (floor != null && score != null && rank != null) {
                DungeonTracker.addRun(
                        new DungeonRun(floor, score, rank, time)
                );
                System.out.println("[Tracker] Dungeon run SAVED");
            }

            reset();
        }
    }

    private void reset() {
        floor = null;
        score = null;
        rank  = null;
    }
}
