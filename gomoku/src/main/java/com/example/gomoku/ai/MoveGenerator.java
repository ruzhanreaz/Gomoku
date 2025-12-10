package com.example.gomoku.ai;

import com.example.gomoku.game.WinChecker;
import com.example.gomoku.model.Board;
import com.example.gomoku.model.GomokuMove;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
    private final int radius;

    public MoveGenerator(int radius) {
        this.radius = radius;
    }

    public List<GomokuMove> generateMoves(Board board, char player) {
        int n = board.getSize();
        if (isEmpty(board, n)) {
            return List.of(new GomokuMove(n / 2, n / 2, player));
        }

        boolean[][] considered = new boolean[n][n];
        List<GomokuMove> moves = new ArrayList<>();

        addNeighborMoves(board, n, player, considered, moves);
        addTacticalMoves(board, n, player, considered, moves);

        if (moves.isEmpty()) {
            addAllEmptyMoves(board, n, player, moves);
        }

        return moves;
    }

    private boolean isEmpty(Board board, int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board.getSymbolAt(i, j) != Board.EMPTY) return false;
            }
        }
        return true;
    }

    private void addNeighborMoves(Board board, int n, char player, boolean[][] considered, List<GomokuMove> moves) {
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (board.getSymbolAt(r, c) != Board.EMPTY) {
                    for (int dr = -radius; dr <= radius; dr++) {
                        for (int dc = -radius; dc <= radius; dc++) {
                            int nr = r + dr, nc = c + dc;
                            if (board.inBounds(nr, nc) && board.getSymbolAt(nr, nc) == Board.EMPTY && !considered[nr][nc]) {
                                considered[nr][nc] = true;
                                moves.add(new GomokuMove(nr, nc, player));
                            }
                        }
                    }
                }
            }
        }
    }

    private void addTacticalMoves(Board board, int n, char player, boolean[][] considered, List<GomokuMove> moves) {
        char opponent = (player == Board.BLACK) ? Board.WHITE : Board.BLACK;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (board.getSymbolAt(r, c) == Board.EMPTY && !considered[r][c]) {
                    if (isWinningMove(board, r, c, player) || isBlockingMove(board, r, c, opponent)) {
                        considered[r][c] = true;
                        moves.add(new GomokuMove(r, c, player));
                    }
                }
            }
        }
    }

    private boolean isWinningMove(Board board, int r, int c, char player) {
        board.setSymbolAt(r, c, player);
        boolean win = WinChecker.checkWin(board, r, c, player);
        board.setSymbolAt(r, c, Board.EMPTY);
        return win;
    }

    private boolean isBlockingMove(Board board, int r, int c, char opponent) {
        board.setSymbolAt(r, c, opponent);
        boolean block = WinChecker.checkWin(board, r, c, opponent);
        board.setSymbolAt(r, c, Board.EMPTY);
        return block;
    }

    private void addAllEmptyMoves(Board board, int n, char player, List<GomokuMove> moves) {
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (board.getSymbolAt(r, c) == Board.EMPTY) {
                    moves.add(new GomokuMove(r, c, player));
                }
            }
        }
    }
}

