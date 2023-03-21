package Utils;

import Pieces.Piece;

import java.util.LinkedList;

// Util class for all the bit boards operation
public class BoardUtils {
    public static final boolean WHITE = true;
    public static final boolean BLACK = false;
    public static final int BOARD_SIZE = 64;
    public static final int BOARD_EDGE_SIZE = 8;

    public static final byte INITIAL_WHITE_KING_SQUARE = 3;
    public static final byte INITIAL_BLACK_KING_SQUARE = 59;
    public static final byte INITIAL_WHITE_ROOK_SQUARE_SHORT = 0;
    public static final byte INITIAL_WHITE_ROOK_SQUARE_LONG = 7;
    public static final byte INITIAL_BLACK_ROOK_SQUARE_SHORT = 56;
    public static final byte INITIAL_BLACK_ROOK_SQUARE_LONG = 63;

    public static final byte WHITE_PAWN_MOVE_OFFSET = 8;
    public static final byte BLACK_PAWN_MOVE_OFFSET = -8;

    // Given a square, return its row number
    public int getRowOfSquare(byte square) {
        return square / BOARD_EDGE_SIZE;
    }

    // Given a square, return its col number
    public int getColOfSquare(byte square) {
        return square % BOARD_EDGE_SIZE;
    }

    // Convert a square represented as a position (i.e. number from 0 to 63), turn it into bit position (i.e. only the bit on the position is 1)
    public long getSquarePositionAsBitboardPosition(long square) {
        return 1L << square;
    }

    // for testing, print long as 8x8 of it bits value
    public void print(long toPrint) {
        String binaryString = String.format("%64s", Long.toBinaryString(toPrint)).replace(' ', '0');
        for (int i = 0; i < 64; i += 8) {
            System.out.println(binaryString.substring(i, i + 8));
        }
        System.out.println();
    }

    // Update piece Position from its current square to the target square
    public void updatePiecePosition(byte targetSquare, byte currentSquare, Piece[] board, LinkedList<Piece> pieceList) {
        Piece pieceToMove = board[currentSquare];
        Piece pieceToRemove = board[targetSquare];

        // Update the position of the piece, on the board and of the piece
        pieceToMove.setSquare(targetSquare);
        board[targetSquare] = pieceToMove;
        board[currentSquare] = null;

        // Remove the piece on the target square and remove from the board the piece from the previous position
        pieceList.remove(pieceToRemove);
    }
}
