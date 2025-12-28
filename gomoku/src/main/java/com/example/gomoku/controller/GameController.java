package com.example.gomoku.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import com.example.gomoku.ai.*;
import com.example.gomoku.game.*;
import com.example.gomoku.model.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController implements Initializable {
    @FXML
    private GridPane boardGrid;

    @FXML
    private Label turnLabel;

    @FXML
    private Button backButton;

    @FXML
    private Label blackScoreLabel;

    @FXML
    private Label whiteScoreLabel;

    @FXML
    private Label lastMoveScoreLabel;

    @FXML
    private HBox gameOverButtons;

    @FXML
    private Button tryAgainButton;

    @FXML
    private Button closeGameButton;

    private Board board;
    private AIPlayer aiPlayer;
    private EvaluationFunction evaluationFunction;
    private final String gameMode = "PVAI";
    private boolean blackTurn = true;
    private boolean gameOver = false;
    private final AtomicBoolean abortFlag = new AtomicBoolean(false);
    private int previousBlackScore = 0;
    private int previousWhiteScore = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        board = new Board(10);
        evaluationFunction = new EvaluationFunction();
        MoveGenerator moveGenerator = new MoveGenerator(2);
        MinimaxSearch minimaxSearch = new MinimaxSearch(evaluationFunction, moveGenerator);
        aiPlayer = new AIPlayer(Board.WHITE, Board.BLACK, 5, 3000, minimaxSearch);

        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(50, 50);
                cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                cell.setStyle("-fx-background-color: transparent; -fx-border-width: 0.5; -fx-border-color: lightgray;");

                final int r = row, c = col;
                cell.setOnMouseClicked(e -> handleCellClick(r, c, cell));

                boardGrid.add(cell, col, row);
            }
        }

        updateTurnLabel();
        updateScoreboard();
    }

    private void handleCellClick(int row, int col, StackPane cell) {
        if (gameOver || !board.isValidMove(row, col)) return;

        char currentPlayer = blackTurn ? Board.BLACK : Board.WHITE;
        if (!board.placeSymbol(row, col, currentPlayer)) return;

        placeStoneWithAnimation(cell, blackTurn);

        // Update scoreboard after placing a stone
        updateScoreboard();

        if (WinChecker.checkWin(board, row, col, currentPlayer)) {
            gameOver = true;
            showWinDialog(blackTurn ? "Black" : "White");
            return;
        }

        if (board.isFull()) {
            gameOver = true;
            showDrawDialog();
            return;
        }

        blackTurn = !blackTurn;
        updateTurnLabel();

        if (gameMode.equals("PVAI") && !blackTurn && !gameOver) {
            startAIMove();
        }
    }

    private void placeStoneWithAnimation(StackPane cell, boolean isBlack) {
        Circle stone = new Circle(20);
        stone.setFill(isBlack ? Color.BLACK : Color.WHITE);
        if (!isBlack) {
            stone.setStroke(Color.BLACK);
            stone.setStrokeWidth(1);
        }

        stone.setScaleX(0);
        stone.setScaleY(0);
        stone.setOpacity(0);
        cell.getChildren().add(stone);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), stone);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), stone);
        fadeTransition.setToValue(1.0);

        scaleTransition.play();
        fadeTransition.play();
    }

    private void startAIMove() {
        boardGrid.setDisable(true);
        boardGrid.setCursor(Cursor.WAIT);

        abortFlag.set(false);
        Task<GomokuMove> aiTask = new Task<>() {
            @Override
            protected GomokuMove call() {
                return aiPlayer.getBestMove(board, abortFlag);
            }
        };

        aiTask.setOnSucceeded(event -> {
            aiMoveWithAnimation(aiTask.getValue());
            boardGrid.setDisable(false);
            boardGrid.setCursor(Cursor.DEFAULT);
        });

        aiTask.setOnFailed(event -> {
            boardGrid.setDisable(false);
            boardGrid.setCursor(Cursor.DEFAULT);
        });

        Thread t = new Thread(aiTask, "AI-Thread");
        t.setDaemon(true);
        t.start();
    }

    private void aiMoveWithAnimation(GomokuMove bestMove) {
        if (gameOver || bestMove == null) return;

        int row = bestMove.getRow();
        int col = bestMove.getCol();

        if (!board.placeSymbol(row, col, Board.WHITE)) return;

        StackPane cell = getNodeFromGridPane(row, col);
        if (cell != null) {
            placeStoneWithAnimation(cell, false);
        }

        // Update scoreboard after AI move
        updateScoreboard();

        if (WinChecker.checkWin(board, row, col, Board.WHITE)) {
            gameOver = true;
            Platform.runLater(() -> showWinDialog("AI (White)"));
            return;
        }

        if (board.isFull()) {
            gameOver = true;
            Platform.runLater(this::showDrawDialog);
            return;
        }

        blackTurn = true;
        updateTurnLabel();
    }

    private StackPane getNodeFromGridPane(int row, int col) {
        for (javafx.scene.Node node : boardGrid.getChildren()) {
            if (node instanceof StackPane) {
                Integer r = GridPane.getRowIndex(node);
                Integer c = GridPane.getColumnIndex(node);
                if ((r == null ? 0 : r) == row && (c == null ? 0 : c) == col) {
                    return (StackPane) node;
                }
            }
        }
        return null;
    }

    private void updateTurnLabel() {
        turnLabel.setText(gameOver ? "Game Over" :
            (blackTurn ? "Black's Turn" : (gameMode.equals("PVAI") ? "AI's Turn" : "White's Turn")));
    }

    private void showWinDialog(String winner) {
        updateTurnLabel();
        gameOverButtons.setVisible(true);
        gameOverButtons.setManaged(true);
        boardGrid.setDisable(true);
        
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(winner + " Wins!");
            alert.setContentText("Congratulations! " + winner + " has won the game with 5 in a row!");
            alert.show();
        });
    }

    private void showDrawDialog() {
        updateTurnLabel();
        gameOverButtons.setVisible(true);
        gameOverButtons.setManaged(true);
        boardGrid.setDisable(true);
        
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("It's a Draw!");
            alert.setContentText("The board is full. No one wins!");
            alert.show();
        });
    }

    private void resetGame() {
        board.clear();
        gameOver = false;
        blackTurn = true;
        previousBlackScore = 0;
        previousWhiteScore = 0;

        for (javafx.scene.Node node : boardGrid.getChildren()) {
            if (node instanceof StackPane) {
                ((StackPane) node).getChildren().clear();
            }
        }

        gameOverButtons.setVisible(false);
        gameOverButtons.setManaged(false);
        boardGrid.setDisable(false);
        updateTurnLabel();
        updateScoreboard();
    }

    @FXML
    private void handleTryAgain() {
        resetGame();
    }

    @FXML
    private void handleCloseGame() {
        Platform.exit();
        System.exit(0);
    }

    private void updateScoreboard() {
        // Calculate scores using the evaluation function
        int blackScore = evaluationFunction.evaluatePlayer(board, Board.BLACK);
        int whiteScore = evaluationFunction.evaluatePlayer(board, Board.WHITE);

        // Calculate score changes
        int blackChange = blackScore - previousBlackScore;
        int whiteChange = whiteScore - previousWhiteScore;

        // Update the score labels
        blackScoreLabel.setText(String.format("%,d", blackScore));
        whiteScoreLabel.setText(String.format("%,d", whiteScore));

        // Update last move score change
        if (blackChange != 0 || whiteChange != 0) {
            String lastMoveText;
            if (!blackTurn && whiteChange != 0) {
                // AI just moved (White)
                lastMoveText = String.format("White: %+,d", whiteChange);
            } else if (blackTurn && blackChange != 0) {
                // Player just moved (Black)
                lastMoveText = String.format("Black: %+,d", blackChange);
            } else {
                lastMoveText = "N/A";
            }
            lastMoveScoreLabel.setText(lastMoveText);
        }

        // Store current scores for next comparison
        previousBlackScore = blackScore;
        previousWhiteScore = whiteScore;
    }
}
