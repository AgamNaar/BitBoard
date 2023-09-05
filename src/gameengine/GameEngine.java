package gameengine;

import Gui.ChessBoardGui;
import gamelogic.ChessGame;

import java.util.LinkedList;

// Main class of the game engine, responsible for calculating the best move for a player
// This version, is a thread, when built with game, startingDepthSearch and searchTimeSecond, will run findBestMove,
// Every time increasing the depth search by 2, until the time is over, and will return the best move found of the last
// fully completed search
public class GameEngine extends Thread {

    ChessGame gameToEvaluate;
    private PieceMove bestMove;
    private int depthSearch;
    GameEngineMonitor monitor;

    private static final TranspositionTableHandler transpositionTableHandler = new TranspositionTableHandler();

    private int counter = 0;

    public static final GameEvaluater gameEvaluater = new GameEvaluater();

    // Empty builder
    public GameEngine() {
    }

    // Builder, create monitor to stop search when time is over
    public GameEngine(ChessGame game, ChessBoardGui gui, int startingDepthSearch, int searchTimeSecond, boolean withTimeLimit) {
        gameToEvaluate = game;
        this.depthSearch = startingDepthSearch;
        // If there is a time limit, add monitor to stop it the run after set time
        if (withTimeLimit)
            monitor = new GameEngineMonitor(this, gui, searchTimeSecond);
    }

    // Run function of thread, will run searches in deeper depth as long as not interrupted
    @Override
    public void run() {
        // Run monitor to tell it when time is over to stop it
        if (monitor != null)
            monitor.start();

        int successfulCounter = 0, successfulDepthSearch = 0;
        PieceMove currentPieceMove;

        // Run as long as the thread is not interrupted
        try {
            while (!this.isInterrupted()) {
                counter = 0;
                // Find the best move in the current depth
                currentPieceMove = findBestMove(gameToEvaluate, depthSearch);

                // If interrupted before current search was completed, current search is not good
                if (!this.isInterrupted()) {
                    // Search was finished successfully, save last completed search info
                    bestMove = currentPieceMove;
                    successfulDepthSearch = depthSearch;
                    successfulCounter = counter;
                }
                depthSearch += 2;
            }
        } finally {
            System.out.println("Depth search: " + successfulDepthSearch + ". Position evaluated: " + successfulCounter);
            System.out.println("Move found: " + bestMove.toStringWithMoveValue());
        }
    }

    // Find the best move in the game at the depth given, using alpha beta pruning - initialize the fist recursive call
    public PieceMove findBestMove(ChessGame game, int depth) {
        return alphaBeta(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, game.getPlayerToPlay(), null);
    }

    /*
    Search for the best move using alpha beta pruning algorithm, in each recursive call, check if it's maximizing player,
    white, or minimizer player, black. For each possible move, get it value using the recursive call, anc chose the best
    according to the player (white want high number black wants low).
    Alpha represents the best score found so far for white (big numbers), and beta represent the best score found for
    black (low number), in the case that beta <= alpha we know a better move has already been found, and there is no
    reason to keep searching that branch of the tree
     */

    private PieceMove alphaBeta(ChessGame game, int depth, int alpha, int beta, boolean maximizingPlayer,
                                PieceMove preMove) {
        int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        // Check if the search is over or not
        if (currentThread().isInterrupted())
            return null;

        // Check if the position was already evaluated with a greater depth
        PieceMove bestMove = transpositionTableHandler.checkIfCalculatedAlready(game, depth, alpha, beta);
        if (bestMove != null)
            return bestMove;

        // Base case: if depth is 0 or game is over, return the evaluated score
        if (depth == 0 || game.isGameOver()) {
            preMove.setMoveValue(gameEvaluater.evaluateGame(game, depth));
            counter++;
            return preMove;
        }

        // Get all move possible in this position, and sort them using move order
        LinkedList<PieceMove> pieceMovesList = GameEngineUtilities.getAllPossibleMoves(game);
        MoveOrderingHandler.sortMoveByOrderValue(game, pieceMovesList);

        // Evaluate and find the best move
        for (PieceMove move : pieceMovesList) {
            ChessGame newGame = new ChessGame(game);
            newGame.executeMove(move.getCurrentPieceSquare(), move.getTargetSquare(), move.getTypeOfPieceToPromoteTo());

            PieceMove current = alphaBeta(newGame, depth - 1, alpha, beta, !maximizingPlayer, move);
            if (current == null)
                return null;

            // Check if we found a better score and check if we found a better alpha/beta
            if (maximizingPlayer) {
                if (current.getMoveValue() > bestScore) {
                    bestScore = current.getMoveValue();
                    bestMove = move;
                }
                alpha = Math.max(alpha, bestScore);
            } else {
                if (current.getMoveValue() < bestScore) {
                    bestScore = current.getMoveValue();
                    bestMove = move;
                }
                beta = Math.min(beta, bestScore);
            }
            // Alpha beta cut off
            if (beta <= alpha)
                break;
        }
        transpositionTableHandler.updateTranspositionTable(bestMove, depth, bestScore, alpha, beta, game);
        // Return the best move (there's always at least 1 move, otherwise the game would've been over)
        assert bestMove != null;
        bestMove.setMoveValue(bestScore);
        return bestMove;
    }

    // Return the best move from the last successful search
    public PieceMove getBestMove() {
        return bestMove;
    }

    // Reset the game engine for a new game
    public void resetEngine(){
        // Stop all running searches
        this.interrupt();
        // Reset transposition table
        transpositionTableHandler.clearTable();
    }
}
