package gameengine;

import gamelogic.ChessGame;
import gamelogic.pieces.Pawn;
import gamelogic.pieces.Piece;

import java.util.LinkedList;

// General utilities class for the game engines
public class GameEngineUtilities {

    // Given a ChessGame, return a list of all the possible moves in that position
    public static LinkedList<PieceMove> getAllPossibleMoves(ChessGame game) {
        LinkedList<PieceMove> pieceMoveList = new LinkedList<>();
        LinkedList<Piece> pieceList = game.getPieceList();
        for (Piece piece : pieceList) {
            // Check if the piece color is the game as the player turn color
            if (piece.getColor() == game.getPlayerToPlay()) {
                byte piecePosition = piece.getSquare();
                pieceMoveList.addAll(transferFromBitBoardMovesToMoves(piece,
                        game.getLegalMovesAsBitBoard(piecePosition), piecePosition));

            }
        }
        return pieceMoveList;
    }

    // Given a bitboard and the piece position, return as a list all the piece moves based on the pieceMovesBitBoards
    public static LinkedList<PieceMove> transferFromBitBoardMovesToMoves(Piece piece,
                                                                         long pieceMovesBitBoards, byte piecePosition) {

        LinkedList<PieceMove> pieceMoveList = new LinkedList<>();
        for (byte i = 0; i < 64; i++) {
            if (((1L << i) & pieceMovesBitBoards) != 0) {
                boolean isAPawn = piece instanceof Pawn;
                PieceMove pieceMove = new PieceMove(piecePosition, i, ChessGame.PROMOTE_TO_QUEEN, isAPawn);
                pieceMoveList.add(pieceMove);
                if (pieceMove.isItPromotionMove() && isAPawn) {
                    pieceMoveList.add(new PieceMove(piecePosition, i, ChessGame.PROMOTE_TO_ROOK, true));
                    pieceMoveList.add(new PieceMove(piecePosition, i, ChessGame.PROMOTE_TO_KNIGHT, true));
                    pieceMoveList.add(new PieceMove(piecePosition, i, ChessGame.PROMOTE_TO_BISHOP, true));
                }
            }
        }
        return pieceMoveList;
    }
}
