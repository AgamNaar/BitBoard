package gameengine;

import gamelogic.ChessGame;
import gamelogic.GameStatusHandler;
import gamelogic.pieces.Piece;

import java.util.LinkedList;

// Class responsible for evaluating a board, the higher the eval, the better it is for white
public class GameEvaluater {

    public static final int WHITE_GET_CHECK_MATE = Integer.MIN_VALUE + 2000;
    public static final int BLACK_GET_CHECK_MATE = Integer.MAX_VALUE - 2000;


    // Given a chess game, return an evaluation of the game
    public int evaluateGame(ChessGame game, int currentDepth) {
        // If white get check mated, return WHITE_GET_CHECK_MATE + currentDepth, that way faster mate will be chosen
        int gameStatus = game.getGameStatus();
        if (gameStatus == GameStatusHandler.CHECKMATE)
            if (game.getPlayerToPlay())
                return WHITE_GET_CHECK_MATE - currentDepth;
            else
                return BLACK_GET_CHECK_MATE + currentDepth;

        // Draw, return 0
        if (gameStatus == GameStatusHandler.DRAW)
            return 0;

        // Evaluate the board
        // TODO: for now only evaluate pieces activity and position
        int eval = 0;
        eval = evaluatePieceValue(game, eval);

        return eval;
    }

    // Evaluate the value of pieces in game
    private int evaluatePieceValue(ChessGame game, int eval) {
        long allPieceBitBoard = game.getAllPieceBitBoard();
        LinkedList<Piece> list = game.getPieceList();

        int gameStage = game.getGameStage();

        // For white pieces add to the eval, for black subtract
        for (Piece piece : list)
            if (piece.getColor())
                eval += piece.getPieceValue(allPieceBitBoard, gameStage);
            else
                eval -= piece.getPieceValue(allPieceBitBoard, gameStage);

        return eval;
    }
}
