package com.example.gomoku.model;

import java.util.Arrays;

public class Board {
    public static final char BLACK = 'B';
    public static final char WHITE = 'W';
    public static final char EMPTY = '.';

    private final int size;
    private final char[][] grid;

    public Board(int size) {
        this.size = size;
        this.grid = new char[size][size];
        clear();
    }

    public int getSize() {
        return size;
    }

    public char getSymbolAt(int row, int col) {
        return inBounds(row, col) ? grid[row][col] : EMPTY;
    }

    public char getEmptySymbol() {
        return EMPTY;
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public boolean isValidMove(int row, int col) {
        return inBounds(row, col) && grid[row][col] == EMPTY;
    }

    public boolean placeSymbol(int row, int col, char symbol) {
        if (!isValidMove(row, col)) return false;
        grid[row][col] = symbol;
        return true;
    }

    public void clear() {
        for (char[] row : grid) {
            Arrays.fill(row, EMPTY);
        }
    }

    public boolean isFull() {
        for (char[] row : grid) {
            for (char cell : row) {
                if (cell == EMPTY) return false;
            }
        }
        return true;
    }

    public Board copy() {
        Board b = new Board(size);
        for (int i = 0; i < size; i++) {
            System.arraycopy(this.grid[i], 0, b.grid[i], 0, size);
        }
        return b;
    }

    public void setSymbolAt(int row, int col, char symbol) {
        if (inBounds(row, col)) grid[row][col] = symbol;
    }
}
