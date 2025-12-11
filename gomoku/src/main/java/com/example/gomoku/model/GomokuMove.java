package com.example.gomoku.model;

public class GomokuMove {
    private final int row;
    private final int col;
    private final char symbol;

    public GomokuMove(int row, int col, char symbol) {
        this.row = row;
        this.col = col;
        this.symbol = symbol;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char getSymbol() {
        return symbol;
    }

    public boolean isValid(Board board) {
        return board.isValidMove(row, col);
    }

    public boolean execute(Board board) {
        return board.placeSymbol(row, col, symbol);
    }

    public boolean undo(Board board) {
        if (!board.inBounds(row, col) || board.getSymbolAt(row, col) != symbol) return false;
        board.setSymbolAt(row, col, Board.EMPTY);
        return true;
    }
}

