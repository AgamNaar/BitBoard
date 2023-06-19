package gamelogic;

import gamelogic.pieces.Piece;

import java.util.LinkedList;

// Util class for all the bit boards operation
public class GameLogicUtilities {
    public static final boolean WHITE = true;
    public static final boolean BLACK = false;
    public static final int BOARD_SIZE = 64;
    public static final int BOARD_EDGE_SIZE = 8;

    public static final byte WHITE_PAWN_MOVE_OFFSET = 8;
    public static final byte BLACK_PAWN_MOVE_OFFSET = -8;

    public static final long EMPTY_BOARD = 0;

    // Given a square, return its row number
    public static int getRowOfSquare(byte square) {
        return square / BOARD_EDGE_SIZE;
    }

    // Given a square, return its col number
    public static int getColOfSquare(byte square) {
        return square % BOARD_EDGE_SIZE;
    }

    // Convert a square represented as a position (i.e. number from 0 to 63), turn it into bit position
    // i.e. only the bit on the position is 1
    public static long squareAsBitBoard(long square) {
        return 1L << square;
    }

    // Update piece Position from its current square to the target square
    public static void updatePiecePosition(byte targetSquare, byte currentSquare,
                                           Piece[] board, LinkedList<Piece> pieceList) {

        Piece pieceToMove = board[currentSquare];
        Piece pieceToRemove = board[targetSquare];

        // Update the position of the piece, on the board and of the piece
        pieceToMove.setSquare(targetSquare);
        board[targetSquare] = pieceToMove;
        board[currentSquare] = null;

        // Remove the piece on the target square and remove from the board the piece from the previous position
        pieceList.remove(pieceToRemove);
    }

    // Shift a number left, if a number is negative, shift it right
    public static long shiftNumberLeft(long num, int offset) {
        if (offset > 0)
            return num << offset;
        else
            return num >>> -offset;
    }
}
