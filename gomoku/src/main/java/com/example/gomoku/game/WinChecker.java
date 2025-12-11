package com.example.gomoku.game;

import com.example.gomoku.model.Board;

public class WinChecker {
    private static final int WIN_LENGTH = 5;

    public static boolean checkWin(Board board, int row, int col, char symbol) {
        if (!board.inBounds(row, col)) return false;
        return checkLine(board, row, col, symbol, 0, 1) ||
               checkLine(board, row, col, symbol, 1, 0) ||
               checkLine(board, row, col, symbol, 1, 1) ||
               checkLine(board, row, col, symbol, 1, -1);
    }

    private static boolean checkLine(Board board, int row, int col, char symbol, int dRow, int dCol) {
        int count = 1 + countDirection(board, row, col, symbol, dRow, dCol) +
                        countDirection(board, row, col, symbol, -dRow, -dCol);
        return count >= WIN_LENGTH;
    }

    private static int countDirection(Board board, int row, int col, char symbol, int dRow, int dCol) {
        int count = 0;
        int r = row + dRow;
        int c = col + dCol;
        while (board.inBounds(r, c) && board.getSymbolAt(r, c) == symbol) {
            count++;
            r += dRow;
            c += dCol;
        }
        return count;
    }

    public static boolean checkWinAnywhere(Board board, char symbol) {
        int size = board.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.getSymbolAt(i, j) == symbol && checkWin(board, i, j, symbol)) {
                    return true;
                }
            }
        }
        return false;
    }
}

