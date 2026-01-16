package com.github.dreamwasfoundhaha.thedungeontracker.dungeon;

import java.util.ArrayList;
import java.util.List;

public class DungeonTracker {
    public static final List<DungeonRun> RECENT_RUNS = new ArrayList<>();

    private static DungeonRun lastRun = null;
    private static long lastRunMillis = 0;

    public static void addRun(DungeonRun newRun) {
        long currentTime = System.currentTimeMillis();


        if (lastRun != null && (currentTime - lastRunMillis < 3000)) {
            if (lastRun.floor.equals(newRun.floor) &&
                    lastRun.score.equals(newRun.score) &&
                    lastRun.time.equals(newRun.time)) {


                return;
            }
        }

        // Add the run to the list
        RECENT_RUNS.add(0, newRun);

        // Update trackers for the next comparison
        lastRun = newRun;
        lastRunMillis = currentTime;

        // Keep the list history to 100 runs
        if (RECENT_RUNS.size() > 100) {
            RECENT_RUNS.remove(RECENT_RUNS.size() - 1);
        }
    }
}