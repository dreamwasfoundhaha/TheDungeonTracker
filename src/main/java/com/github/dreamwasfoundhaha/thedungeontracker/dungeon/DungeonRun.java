package com.github.dreamwasfoundhaha.thedungeontracker.dungeon;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DungeonRun {
    public final String floor;
    public final String score;
    public final String rank;
    public final String time;
    public final String date;


    public DungeonRun(String floor, String score, String rank, String time) {
        this.floor = floor;
        this.score = score;
        this.rank = rank;
        this.time = time;


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE dd");
        this.date = LocalDateTime.now().format(formatter);
    }
}