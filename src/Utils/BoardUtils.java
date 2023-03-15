package Utils;

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
}
