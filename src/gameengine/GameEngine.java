package gameengine;

import gamelogic.ChessGame;

import java.util.LinkedList;

// Main class of the game engine, responsible for calculating the best move for a player
public class GameEngine {

    public static final GameEvaluater gameEvaluater = new GameEvaluater();

    // Return an evaluation of the current board
    public void evalPosition(ChessGame game) {
        System.out.println("Position val: " + gameEvaluater.evaluateGame(game));
    }

    // Given a game and a depth, find the best move for the current player
    public void findBestMove(ChessGame game, int depth) {
        // For black, search the lowest evaluation, for white the highest
        int bestScore = game.getPlayerToPlay() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        PieceMove bestMove = new PieceMove(bestScore);

        LinkedList<PieceMove> pieceMovesList = GameEngineUtilities.getAllPossibleMoves(game);

        for (PieceMove move : pieceMovesList) {
            ChessGame newGame = new ChessGame(game);
            newGame.executeMove(move.getPiecePosition(), move.getTargetSquare(),
                    move.getTypeOfPieceToPromoteTo());

            move.setMoveValue(alphaBeta(newGame, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false));

            if (game.getPlayerToPlay() && move.getMoveValue() > bestMove.getMoveValue())
                bestMove = move;
            if (!game.getPlayerToPlay() && move.getMoveValue() < bestMove.getMoveValue())
                bestMove = move;
        }

        bestMove.setMoveValue(bestScore);
        System.out.println(bestMove.toStringWithMoveValue());
    }

    // Find the best move using alpha beta pruning algorithm
    private int alphaBeta(ChessGame game, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || game.isGameOver())
            return gameEvaluater.evaluateGame(game); // Evaluate the position

        if (maximizingPlayer) {
            int maxScore = Integer.MIN_VALUE;

            LinkedList<PieceMove> pieceMovesList = GameEngineUtilities.getAllPossibleMoves(game);
            for (PieceMove move : pieceMovesList) {
                ChessGame newGame = new ChessGame(game);
                newGame.executeMove(move.getPiecePosition(), move.getTargetSquare(),
                        move.getTypeOfPieceToPromoteTo());

                int score = alphaBeta(newGame, depth - 1, alpha, beta, false);

                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);

                if (beta <= alpha)
                    break; // Beta cutoff
            }

            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;

            LinkedList<PieceMove> pieceMovesList = GameEngineUtilities.getAllPossibleMoves(game);
            for (PieceMove move : pieceMovesList) {
                ChessGame newGame = new ChessGame(game);
                newGame.executeMove(move.getPiecePosition(), move.getTargetSquare(),
                        move.getTypeOfPieceToPromoteTo());

                int score = alphaBeta(newGame, depth - 1, alpha, beta, true);

                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);

                if (beta <= alpha)
                    break; // Alpha cutoff
            }
            return minScore;
        }
    }


}
