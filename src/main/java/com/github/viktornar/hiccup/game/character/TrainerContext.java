package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.data.Item;
import com.github.viktornar.hiccup.game.data.Quest;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class TrainerContext {
    private String gameId;
    private int level = 0;
    private int lives = 3;
    private int gold = 0;
    private int score = 0;
    private int turn = 0;
    private int people = 0;
    private int state = 0;
    private int underworld = 0;
    private int highScore = 0;
    private int expiresInCount = 0;
    private boolean gameOver = false;
    private List<Quest> quests = Collections.emptyList();
    private List<Item> purchasedItems = new ArrayList<>();

    public void from(TrainerContext ctx) {
        gameId = ctx.gameId == null ? gameId : ctx.gameId;
        level = ctx.level == 0 ? level : ctx.level;
        lives = ctx.lives == 0 ? lives : ctx.lives;
        gold = ctx.gold == 0 ? gold : ctx.gold;
        score = ctx.score == 0 ? score : ctx.score;
        turn = ctx.turn == 0 ? turn : ctx.turn;
        people = ctx.people == 0 ? people : ctx.people;
        state = ctx.state == 0 ? state : ctx.state;
        underworld = ctx.underworld == 0 ? underworld : ctx.underworld;
        highScore = ctx.highScore == 0 ? highScore : ctx.highScore;
        quests = ctx.quests == null ? quests : ctx.quests;
    }
}
