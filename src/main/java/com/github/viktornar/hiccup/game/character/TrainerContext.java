package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.data.Item;
import com.github.viktornar.hiccup.game.data.Quest;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TrainerContext {
    private String gameId;
    private Integer level;
    private Integer lives;
    private Integer gold;
    private Integer score;
    private Integer turn;
    private Integer people;
    private Integer state;
    private Integer underworld;
    private Integer highScore;
    private boolean gameOver = false;
    private List<Quest> quests = new ArrayList<>();
    private List<Item> purchasedItems = new ArrayList<>();

    public void from(TrainerContext ctx) {
        gameId = ctx.gameId == null ? gameId : ctx.gameId;
        level = ctx.level == null ? level : ctx.level;
        lives = ctx.lives == null ? lives : ctx.lives;
        gold = ctx.gold == null ? gold : ctx.gold;
        score = ctx.score == null ? score : ctx.score;
        turn = ctx.turn == null ? turn : ctx.turn;
        people = ctx.people == null ? people : ctx.people;
        state = ctx.state == null ? state : ctx.state;
        underworld = ctx.underworld == null ? underworld : ctx.underworld;
        highScore = ctx.highScore == null ? highScore : ctx.highScore;
    }
}
