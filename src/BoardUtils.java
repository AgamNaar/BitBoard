// Util class for all the bit boards operation
public class BoardUtils {
    public static final boolean WHITE = true;
    public static final boolean BLACK = false;
    public static final int BOARD_SIZE = 64;
    public static final int BOARD_EDGE_SIZE = 8;

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
