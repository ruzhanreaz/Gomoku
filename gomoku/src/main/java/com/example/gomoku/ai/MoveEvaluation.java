package com.example.gomoku.ai;

import com.example.gomoku.model.GomokuMove;

public class MoveEvaluation {
    public final GomokuMove move;
    public final int score;

    public MoveEvaluation(GomokuMove move, int score) {
        this.move = move;
        this.score = score;
    }

    public MoveEvaluation(int score) {
        this(null, score);
    }

    public static int maximum() {
        return Integer.MAX_VALUE / 4;
    }

    public static int minimum() {
        return -maximum();
    }
}

