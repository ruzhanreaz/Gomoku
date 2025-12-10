package com.example.gomoku.ai;

import com.example.gomoku.game.WinChecker;
import com.example.gomoku.model.Board;
import com.example.gomoku.model.GomokuMove;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MinimaxSearch {
    private final EvaluationFunction eval;
    private final MoveGenerator generator;

    public MinimaxSearch(EvaluationFunction eval, MoveGenerator generator) {
        this.eval = eval;
        this.generator = generator;
    }

    public MoveEvaluation search(Board board, int maxDepth, char maxPlayer, char minPlayer, long timeLimitMs, AtomicBoolean abortFlag) {
        MoveEvaluation best = new MoveEvaluation(null, MoveEvaluation.minimum());
        long deadline = System.currentTimeMillis() + timeLimitMs;

        for (int depth = 1; depth <= maxDepth; depth++) {
            try {
                MoveEvaluation result = minimax(board, depth, MoveEvaluation.minimum(), MoveEvaluation.maximum(), true, maxPlayer, minPlayer, deadline, abortFlag);
                if (result != null && result.move != null) best = result;
                else if (result != null) best = new MoveEvaluation(result.score);
            } catch (SearchTimeoutException e) {
                break;
            }
            if (abortFlag != null && abortFlag.get()) break;
        }

        if (best.move == null) {
            List<GomokuMove> moves = generator.generateMoves(board, maxPlayer);
            if (!moves.isEmpty()) {
                return new MoveEvaluation(moves.get(0), eval.evaluate(board, maxPlayer, minPlayer));
            }
            return findAnyMove(board, maxPlayer, minPlayer);
        }

        return best;
    }

    private MoveEvaluation findAnyMove(Board board, char maxPlayer, char minPlayer) {
        int n = board.getSize();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (board.getSymbolAt(r, c) == Board.EMPTY) {
                    return new MoveEvaluation(new GomokuMove(r, c, maxPlayer), eval.evaluate(board, maxPlayer, minPlayer));
                }
            }
        }
        return new MoveEvaluation(MoveEvaluation.minimum());
    }

    private MoveEvaluation minimax(Board board, int depth, int alpha, int beta, boolean maximizing, char maxPlayer, char minPlayer, long deadline, AtomicBoolean abortFlag) {
        checkTimeout(deadline, abortFlag);

        if (WinChecker.checkWinAnywhere(board, maxPlayer)) return new MoveEvaluation(MoveEvaluation.maximum());
        if (WinChecker.checkWinAnywhere(board, minPlayer)) return new MoveEvaluation(MoveEvaluation.minimum());
        if (depth == 0 || board.isFull()) return new MoveEvaluation(eval.evaluate(board, maxPlayer, minPlayer));

        char current = maximizing ? maxPlayer : minPlayer;
        List<GomokuMove> moves = generator.generateMoves(board, current);

        GomokuMove winningMove = checkImmediateWin(board, moves, current, deadline, abortFlag);
        if (winningMove != null) {
            return new MoveEvaluation(winningMove, maximizing ? MoveEvaluation.maximum() : MoveEvaluation.minimum());
        }

        List<ScoredMove> scoredMoves = scoreMoves(board, moves, maxPlayer, minPlayer, deadline, abortFlag);
        if (scoredMoves.isEmpty()) return new MoveEvaluation(eval.evaluate(board, maxPlayer, minPlayer));

        sortMoves(scoredMoves, maximizing);

        return maximizing ?
            maximizeScore(board, depth, alpha, beta, maxPlayer, minPlayer, deadline, abortFlag, scoredMoves) :
            minimizeScore(board, depth, alpha, beta, maxPlayer, minPlayer, deadline, abortFlag, scoredMoves);
    }

    private void checkTimeout(long deadline, AtomicBoolean abortFlag) {
        if ((abortFlag != null && abortFlag.get()) || System.currentTimeMillis() >= deadline) {
            throw new SearchTimeoutException();
        }
    }

    private GomokuMove checkImmediateWin(Board board, List<GomokuMove> moves, char player, long deadline, AtomicBoolean abortFlag) {
        for (GomokuMove move : moves) {
            checkTimeout(deadline, abortFlag);
            move.execute(board);
            boolean wins = WinChecker.checkWin(board, move.getRow(), move.getCol(), player);
            move.undo(board);
            if (wins) return move;
        }
        return null;
    }

    private List<ScoredMove> scoreMoves(Board board, List<GomokuMove> moves, char maxPlayer, char minPlayer, long deadline, AtomicBoolean abortFlag) {
        List<ScoredMove> scored = new ArrayList<>();
        for (GomokuMove move : moves) {
            checkTimeout(deadline, abortFlag);
            move.execute(board);
            int h = eval.evaluate(board, maxPlayer, minPlayer);
            scored.add(new ScoredMove(move, h));
            move.undo(board);
        }
        return scored;
    }

    private void sortMoves(List<ScoredMove> moves, boolean maximizing) {
        moves.sort(maximizing ? Comparator.comparingInt((ScoredMove m) -> m.score).reversed() : Comparator.comparingInt(m -> m.score));
    }

    private MoveEvaluation maximizeScore(Board board, int depth, int alpha, int beta, char maxPlayer, char minPlayer, long deadline, AtomicBoolean abortFlag, List<ScoredMove> moves) {
        int bestVal = MoveEvaluation.minimum();
        GomokuMove bestMove = null;
        List<GomokuMove> equalMoves = new ArrayList<>();

        for (ScoredMove sm : moves) {
            checkTimeout(deadline, abortFlag);
            sm.move.execute(board);
            MoveEvaluation child = minimax(board, depth - 1, alpha, beta, false, maxPlayer, minPlayer, deadline, abortFlag);
            sm.move.undo(board);

            int score = child != null ? child.score : MoveEvaluation.minimum();
            if (score > bestVal) {
                bestVal = score;
                bestMove = sm.move;
                equalMoves.clear();
                equalMoves.add(sm.move);
            } else if (score == bestVal && bestMove != null) {
                equalMoves.add(sm.move);
            }
            alpha = Math.max(alpha, bestVal);
            if (alpha >= beta) break;
        }

        if (!equalMoves.isEmpty() && equalMoves.size() > 1) {
            bestMove = equalMoves.get((int)(Math.random() * equalMoves.size()));
        }

        return bestMove != null ? new MoveEvaluation(bestMove, bestVal) : new MoveEvaluation(bestVal);
    }

    private MoveEvaluation minimizeScore(Board board, int depth, int alpha, int beta, char maxPlayer, char minPlayer, long deadline, AtomicBoolean abortFlag, List<ScoredMove> moves) {
        int bestVal = MoveEvaluation.maximum();
        GomokuMove bestMove = null;

        for (ScoredMove sm : moves) {
            checkTimeout(deadline, abortFlag);
            sm.move.execute(board);
            MoveEvaluation child = minimax(board, depth - 1, alpha, beta, true, maxPlayer, minPlayer, deadline, abortFlag);
            sm.move.undo(board);

            int score = child != null ? child.score : MoveEvaluation.maximum();
            if (score < bestVal) {
                bestVal = score;
                bestMove = sm.move;
            }
            beta = Math.min(beta, bestVal);
            if (alpha >= beta) break;
        }


        return bestMove != null ? new MoveEvaluation(bestMove, bestVal) : new MoveEvaluation(bestVal);
    }

    private static class ScoredMove {
        final GomokuMove move;
        final int score;

        ScoredMove(GomokuMove move, int score) {
            this.move = move;
            this.score = score;
        }
    }

    static class SearchTimeoutException extends RuntimeException {}
}

