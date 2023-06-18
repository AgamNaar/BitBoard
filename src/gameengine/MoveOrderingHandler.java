package gameengine;

import gamelogic.ChessGame;
import gamelogic.GameLogicUtilities;
import gamelogic.GameStatusHandler;
import gamelogic.pieces.King;
import gamelogic.pieces.Pawn;
import gamelogic.pieces.Piece;

import java.util.Comparator;
import java.util.LinkedList;

// Class responsible for finding the most likely good moves to play
public class MoveOrderingHandler {

    // Receive a list of moves and current game
    // Return a list where likely good moves are at the top of the list and bad at the end
    public static void sortMoveByOrderValue(ChessGame game, LinkedList<PieceMove> pieceMoveList) {
        long pawnThreatenedSquare = 0, threatenedSquare = 0, allPieceBitBoard = game.getAllPieceBitBoard(),
                sameColorPieceBitBoard = game.getSameColorPieceBitBoard();

        // Get the squares threatened by a pawn, and square threatened by pieces
        boolean playerTurn = game.getPlayerToPlay();
        for (Piece piece : game.getPieceList()) {
            if (piece.getColor() != playerTurn)
                if (piece instanceof Pawn)
                    pawnThreatenedSquare |= ((Pawn) piece).getPawnAttackSquare();
                else
                    threatenedSquare |= piece.getMovesAsBitBoard(allPieceBitBoard, sameColorPieceBitBoard);
        }

        for (PieceMove pieceMove : pieceMoveList)
            pieceMove.setMoveAssumedValue(-assumeMoveValue(pieceMove, pawnThreatenedSquare, threatenedSquare, game));

        pieceMoveList.sort(Comparator.comparingInt(PieceMove::getMoveAssumedValue));
    }

    // try to guess how good a move will be, and set pieceMove moveOrderValue to that value
    private static int assumeMoveValue(PieceMove pieceMove, long pawnThreatenedSquare, long threatenedSquare,
                                       ChessGame game) {
        int assumedValue = 0;

        if (pieceMove.getPieceToMove() instanceof King)
            return assumeKingMoveValue(pieceMove, game, pawnThreatenedSquare & threatenedSquare);

        if (pieceMove.isItPromotionMove())
            if (pieceMove.getTypeOfPieceToPromoteTo() == ChessGame.PROMOTE_TO_QUEEN)
                assumedValue = 800;
            else
                // It's almost never a good idea to promote to something that's not a queen
                assumedValue = -1000;

        assumedValue += assumeCaptureAndIfPieceIsThreaten(pieceMove, game, pawnThreatenedSquare, threatenedSquare);

        // Usually going back is not good
        if (isBackwardMove(pieceMove) && game.getGameStage() != GameStatusHandler.END_GAME)
            assumedValue -= 200;

        return assumedValue;
    }

    // Check if it's a backwards move
    private static boolean isBackwardMove(PieceMove pieceMove) {
        // Check if the piece moved more than 8 blocks, if yes it's a backward move
        boolean isDistancePositive = pieceMove.getCurrentPieceSquare() - (pieceMove.getTargetSquare() + 8) >= 0;
        if (pieceMove.getPieceToMove().getColor())
            return isDistancePositive;
        else
            return !isDistancePositive;
    }

    // Return the assumed value of moving the piece from its position to the target square based on if the piece is
    // threatened and if it can take a piece and if it's likely that it will be taken bck
    private static int assumeCaptureAndIfPieceIsThreaten(PieceMove pieceMove, ChessGame game,
                                                         long pawnThreatenedSquare, long threatenedSquare) {
        int assumedValue = 0;
        long enemyBitBoard = game.getEnemyBitBoard();
        long targetSquareBitBoard = GameLogicUtilities.getSquarePositionAsBitboardPosition(pieceMove.getTargetSquare());
        long currentSquareBitBoard = GameLogicUtilities.getSquarePositionAsBitboardPosition(pieceMove.getCurrentPieceSquare());

        // Check if the move capture enemy piece, the higher the piece value the better
        if ((enemyBitBoard & targetSquareBitBoard) != 0)
            assumedValue += game.getPiece(pieceMove.getTargetSquare()).getPieceValue(game.getAllPieceBitBoard(),
                    game.getGameStage());

        // Moving to a square threatened by a pawn is very bad
        if ((targetSquareBitBoard & pawnThreatenedSquare) != 0)
            assumedValue -= pieceMove.getPieceToMoveValue() * 1.35;

        // Moving to a square threatened by a piece is bad
        if ((targetSquareBitBoard & threatenedSquare) != 0)
            assumedValue -= pieceMove.getPieceToMoveValue();

        // If piece is threatened by a pawn, it's likely very good to move it
        if ((currentSquareBitBoard & pawnThreatenedSquare) != 0)
            assumedValue += pieceMove.getPieceToMoveValue() * 1.25;

        // If piece is threatened by a piece, it's likely good to move it
        if ((currentSquareBitBoard & threatenedSquare) != 0)
            assumedValue += pieceMove.getPieceToMoveValue();

        return assumedValue;
    }

    // Assume the move value of the king
    private static int assumeKingMoveValue(PieceMove pieceMove, ChessGame game, long threatenSquare) {
        boolean isCastlingMove = game.isCastlingMove(pieceMove.getTargetSquare(), pieceMove.getPieceToMove());

        // If check, probably good idea to move with the king
        if (game.getGameStatus() == GameStatusHandler.CHECK)
            return 500;

        // If it's not end game, and not a castling move, probably bad idea to move the king
        if (isCastlingMove)
            return 70;

        // If it's end game, might be good idea to move the king
        if (game.getGameStage() == GameStatusHandler.END_GAME)
            return 50;

        // If it's a free capture, probably good idea
        if (freeCapture(pieceMove, game, threatenSquare))
            return game.getPiece(pieceMove.getTargetSquare()).getPieceValue(game.getAllPieceBitBoard()
                    , game.getGameStage());

        // Any other case, moving a piece is a bad idea
        return -2000;
    }

    // Check if this move makes a free capture
    private static boolean freeCapture(PieceMove pieceMove, ChessGame game, long threatenSquare) {
        long currentSquareBitBoard = GameLogicUtilities.getSquarePositionAsBitboardPosition
                (pieceMove.getCurrentPieceSquare());
        long targetSquareBitBoard = GameLogicUtilities.getSquarePositionAsBitboardPosition
                (pieceMove.getTargetSquare());
        // Check if the move capture enemy piece, the higher the piece value the better
        return (((game.getEnemyBitBoard() & targetSquareBitBoard) != 0)
                && ((currentSquareBitBoard & threatenSquare) != 0));
    }
}
