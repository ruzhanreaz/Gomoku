package com.example.gomoku.ai;

import com.example.gomoku.game.WinChecker;
import com.example.gomoku.model.Board;

import java.util.HashSet;
import java.util.Set;

public class EvaluationFunction {
    private static final int WIN_SCORE = 100000;
    private static final int FOUR_OPEN = 10000;
    private static final int FOUR_BLOCKED = 1000;
    private static final int THREE_OPEN = 500;
    private static final int THREE_BLOCKED = 50;
    private static final int TWO_OPEN = 50;
    private static final int TWO_BLOCKED = 5;

    public int evaluate(Board board, char maxPlayer, char minPlayer) {
        if (WinChecker.checkWinAnywhere(board, maxPlayer)) return WIN_SCORE;
        if (WinChecker.checkWinAnywhere(board, minPlayer)) return -WIN_SCORE;

        int maxScore = evaluatePlayer(board, maxPlayer);
        int minScore = evaluatePlayer(board, minPlayer);

        return maxScore - minScore;
    }

    public int evaluatePlayer(Board board, char player) {
        int score = 0;
        int size = board.getSize();
        Set<String> countedPatterns = new HashSet<>();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.getSymbolAt(i, j) == player) {
                    int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
                    for (int[] dir : directions) {
                        String key = getPatternKey(i, j, dir[0], dir[1]);
                        if (!countedPatterns.contains(key)) {
                            countedPatterns.add(key);
                            score += evaluateDirection(board, i, j, player, dir[0], dir[1]);
                        }
                    }
                }
            }
        }
        return score;
    }

    private String getPatternKey(int row, int col, int dRow, int dCol) {
        int minRow = row;
        int minCol = col;
        while (minRow > 0 || minCol > 0) {
            int newRow = minRow - dRow;
            int newCol = minCol - dCol;
            if (newRow < 0 || newCol < 0) break;
            minRow = newRow;
            minCol = newCol;
        }
        return minRow + "," + minCol + "," + dRow + "," + dCol;
    }

    private int evaluateDirection(Board board, int row, int col, char player, int dRow, int dCol) {
        int count = 1;
        int openEnds = 0;

        int r = row + dRow, c = col + dCol;
        while (board.inBounds(r, c) && board.getSymbolAt(r, c) == player) {
            count++;
            r += dRow;
            c += dCol;
        }
        if (board.inBounds(r, c) && board.getSymbolAt(r, c) == board.getEmptySymbol()) openEnds++;

        r = row - dRow;
        c = col - dCol;
        while (board.inBounds(r, c) && board.getSymbolAt(r, c) == player) {
            count++;
            r -= dRow;
            c -= dCol;
        }
        if (board.inBounds(r, c) && board.getSymbolAt(r, c) == board.getEmptySymbol()) openEnds++;

        if (count >= 5) return WIN_SCORE;
        if (count == 4) return openEnds == 2 ? FOUR_OPEN : FOUR_BLOCKED;
        if (count == 3) return openEnds == 2 ? THREE_OPEN : THREE_BLOCKED;
        if (count == 2) return openEnds == 2 ? TWO_OPEN : TWO_BLOCKED;
        return 0;
    }
}

