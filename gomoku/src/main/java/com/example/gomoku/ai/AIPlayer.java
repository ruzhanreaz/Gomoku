package com.example.gomoku.ai;

import com.example.gomoku.model.Board;
import com.example.gomoku.model.GomokuMove;

import java.util.concurrent.atomic.AtomicBoolean;

public class AIPlayer {
    private final char aiSymbol;
    private final char humanSymbol;
    private final int maxDepth;
    private final int timeLimitMs;
    private final MinimaxSearch search;

    public AIPlayer(char aiSymbol, char humanSymbol, int maxDepth, int timeLimitMs, MinimaxSearch search) {
        this.aiSymbol = aiSymbol;
        this.humanSymbol = humanSymbol;
        this.maxDepth = maxDepth;
        this.timeLimitMs = timeLimitMs;
        this.search = search;
    }

    public GomokuMove getBestMove(Board board, AtomicBoolean abortFlag) {
        Board copy = board.copy();
        MoveEvaluation result = search.search(copy, maxDepth, aiSymbol, humanSymbol, timeLimitMs, abortFlag);
        return result != null ? result.move : null;
    }
}

