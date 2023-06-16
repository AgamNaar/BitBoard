package gameengine;

import gamelogic.ChessGame;
import gamelogic.GameLogicUtilities;
import gamelogic.pieces.Pawn;
import gamelogic.pieces.Piece;

import java.util.Comparator;
import java.util.LinkedList;

public class MoveOrderingHandler {

    // Receive a list of moves and current game
    // Return a list where likely good moves are at the top of the list and bad at the end
    public static void sortMoveByOrderValue(ChessGame game, LinkedList<PieceMove> pieceMoveList, int gameStage) {
        long pawnThreatenedSquare = 0, threatenedSquare = 0, allPieceBitBoard = game.getAllPieceBitBoard(),
                sameColorPieceBitBoard = game.getSameColorPieceBitBoard();

        // Get the squares threatened by a pawn, and square threatened by pieces
        boolean playerTurn = game.getPlayerToPlay();
        for (Piece piece : game.getPieceList()) {
            if (piece.getColor() != playerTurn)
                if (piece instanceof Pawn)
                    pawnThreatenedSquare |= ((Pawn) piece).getPawnAttackSquare();
                else {
                    threatenedSquare |= piece.getMovesAsBitBoard(allPieceBitBoard, sameColorPieceBitBoard);
                }
        }

        for (PieceMove pieceMove : pieceMoveList)
            assumeMoveValue(pieceMove, pawnThreatenedSquare, threatenedSquare, game, gameStage, allPieceBitBoard);

        pieceMoveList.sort(Comparator.comparingInt(PieceMove::getMoveAssumedValue));
    }

    // try to guess how good a move will be, and set pieceMove moveOrderValue to that value
    private static void assumeMoveValue(PieceMove pieceMove, long pawnThreatenedSquare,
                                        long threatenedSquare, ChessGame game, int gameStage, long allPieceBitBoard) {

        long enemyBitBoard = game.getEnemyBitBoard();
        long targetSquareBitBoard = GameLogicUtilities.getSquarePositionAsBitboardPosition(pieceMove.getTargetSquare());
        long currentSquareBitBoard = GameLogicUtilities.getSquarePositionAsBitboardPosition(pieceMove.getCurrentPieceSquare());
        int assumedValue = 0;

        // Promotion is usually good, best to a queen
        if (pieceMove.isItPromotionMove()) {
            if (pieceMove.getTypeOfPieceToPromoteTo() == ChessGame.PROMOTE_TO_QUEEN)
                pieceMove.setMoveAssumedValue(assumedValue + 800);
            else
                pieceMove.setMoveAssumedValue(assumedValue + 300);
        }

        // Check if the move capture enemy piece, the higher the piece value the better
        if ((enemyBitBoard & targetSquareBitBoard) != 0)
            assumedValue += game.getPiece(pieceMove.getTargetSquare()).getPieceValue(allPieceBitBoard, gameStage);

        // Moving to a square threatened by a pawn is very bad
        if ((targetSquareBitBoard & pawnThreatenedSquare) != 0)
            assumedValue -= pieceMove.getPieceToMoveValue() * 1.5;

        // Moving to a square threatened by a pawn is bad
        if ((targetSquareBitBoard & threatenedSquare) != 0)
            assumedValue -= pieceMove.getPieceToMoveValue();

        // If piece is threatened by a pawn, it's likely very good to move it
        if ((currentSquareBitBoard & pawnThreatenedSquare) != 0)
            assumedValue += pieceMove.getPieceToMoveValue();

        // If piece is threatened by a piece, it's likely good to move it
        if ((currentSquareBitBoard & threatenedSquare) != 0)
            assumedValue += pieceMove.getPieceToMoveValue() * 1.5;

        pieceMove.setMoveAssumedValue(-assumedValue);
    }
}
