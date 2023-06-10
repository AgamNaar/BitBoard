package gameengine;

import gamelogic.ChessGame;
import gamelogic.pieces.Piece;

import java.util.LinkedList;

// Class responsible for evaluating a board, the higher the eval, the better it is for white
public class GameEvaluater {

    // Given a chess game, return an evaluation of the game
    public int evaluateGame(ChessGame game) {
        // If game status is check mate, return MAX_VALUE for the winner
        int gameStatus = game.getGameStatus();
        if (gameStatus == ChessGame.CHECKMATE)
            if (game.getPlayerToPlay())
                return Integer.MIN_VALUE;
            else
                return Integer.MAX_VALUE;

        int eval = 0;
        eval = evaluatePieceValue(new ChessGame(game), eval);

        return eval;
    }

    // Evaluate the value of pieces in game
    private int evaluatePieceValue(ChessGame game, int eval) {
        long allPieceBitBoard = game.getAllPieceBitBoard();
        LinkedList<Piece> list = game.getPieceList();

        // For white pieces add to the eval, for black subtract
        for (Piece piece : list)
            if (piece.getColor())
                eval += piece.getPieceValue(allPieceBitBoard);
            else
                eval -= piece.getPieceValue(allPieceBitBoard);

        return eval;
    }
}
