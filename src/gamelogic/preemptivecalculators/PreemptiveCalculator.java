package gamelogic.preemptivecalculators;

import gamelogic.GameLogicUtilities;

import static gamelogic.GameLogicUtilities.BOARD_EDGE_SIZE;

// General class for preemptive calculating things before the game start
public class PreemptiveCalculator {

    public static final byte[] ROOK_OFFSETS = {1, -1, 8, -8};
    public static final byte[] BISHOP_OFFSETS = {7, -7, 9, -9};
    public static final long FIRST_8_BITS = 0XFF;

    public static final long[] ROOK_MASK = new long[GameLogicUtilities.BOARD_SIZE];
    public static final long[] BISHOP_MASK = new long[GameLogicUtilities.BOARD_SIZE];

    // for a row or a column, number of possible value as bitboard of all pieces of a row/column
    public static final int NUMBER_OF_POSSIBLE_VALUES_PER_EDGE = (int) Math.pow(2, GameLogicUtilities.BOARD_EDGE_SIZE);
    public static final int EDGE_DISTANCE_LEFT = 0;
    public static final int EDGE_DISTANCE_RIGHT = 1;
    public static final int EDGE_DISTANCE_UP = 2;
    public static final int EDGE_DISTANCE_DOWN = 3;
    public static final int BISHOP_LEFT_UP = 0;
    public static final int BISHOP_LEFT_DOWN = 1;
    public static final int BISHOP_RIGHT_UP = 2;
    public static final int BISHOP_RIGHT_DOWN = 3;

    private static boolean initialized = false;

    public PreemptiveCalculator() {
        // If not initialized, initialize the bishop/rook mask
        if (initialized)
            return;

        for (byte square = 0; square < GameLogicUtilities.BOARD_SIZE; square++)
            BISHOP_MASK[square] = toBitMapBishop(square, FIRST_8_BITS, FIRST_8_BITS);

        for (byte square = 0; square < GameLogicUtilities.BOARD_SIZE; square++)
            ROOK_MASK[square] = toBitMapRook(square, FIRST_8_BITS, FIRST_8_BITS);

        initialized = true;

    }

    // Given a position, return how many bits till the end of the board from the position to left, right, up, down
    public byte[] getDistanceTillEdgeOfBoard(byte position) {
        byte[] distances = new byte[4];
        // Left
        distances[EDGE_DISTANCE_LEFT] = (byte) (BOARD_EDGE_SIZE - 1 - (position % BOARD_EDGE_SIZE));
        // Right
        distances[EDGE_DISTANCE_RIGHT] = (byte) (position % BOARD_EDGE_SIZE);
        // Up
        distances[EDGE_DISTANCE_UP] = (byte) (BOARD_EDGE_SIZE - 1 - (position / BOARD_EDGE_SIZE));
        // Down
        distances[EDGE_DISTANCE_DOWN] = (byte) (position / BOARD_EDGE_SIZE);

        return distances;
    }

    // Given a position, return how many squares the bishop can go from his position to
    // left up, left down, right up, right down
    public byte[] getDistanceTillEdgeOfBoardBishop(byte position) {
        byte[] distances = getDistanceTillEdgeOfBoard(position);
        int leftUp = Math.min(distances[EDGE_DISTANCE_RIGHT], distances[EDGE_DISTANCE_UP]);
        int leftDown = Math.min(distances[EDGE_DISTANCE_LEFT], distances[EDGE_DISTANCE_DOWN]);
        int rightUp = Math.min(distances[EDGE_DISTANCE_LEFT], distances[EDGE_DISTANCE_UP]);
        int rightDown = Math.min(distances[EDGE_DISTANCE_RIGHT], distances[EDGE_DISTANCE_DOWN]);

        return new byte[]{(byte) leftUp, (byte) leftDown, (byte) rightUp, (byte) rightDown};
    }

    // Given a square, rowValue. columnValue: set the value of row/column of the square to be rowValue and columnValue
    public long toBitMapRook(byte square, long rowValue, long columnValue) {
        int row = GameLogicUtilities.getRowOfSquare(square), column = GameLogicUtilities.getColOfSquare(square);
        long rowMask = rowValue << (BOARD_EDGE_SIZE * row), columnMask = 0;

        for (int i = 0; i < BOARD_EDGE_SIZE; i++) {
            // Extract i'th bit value from columnValue, move it the 0 bit position
            long bitValue = extractBit(i, columnValue);
            // Add it by moving it to its right row
            columnMask = columnMask | (bitValue << (column + (i * BOARD_EDGE_SIZE)));
        }
        return rowMask | columnMask;
    }

    // Given a square, nwDiagonalValue. neDiagonalValue:
    // set the value of nwDiagonalValue/neDiagonalValue of the square to be nwDiagonalValue/neDiagonalValue
    public long toBitMapBishop(byte square, long nwDiagonalValue, long neDiagonalValue) {
        byte[] edgeDistances = getDistanceTillEdgeOfBoardBishop(square);
        long resultNwDiagonal = insertDialogVal(square, nwDiagonalValue, BISHOP_OFFSETS[BISHOP_RIGHT_UP],
                edgeDistances[BISHOP_RIGHT_UP]);

        long resultNeDiagonal = insertDialogVal(square, neDiagonalValue, BISHOP_OFFSETS[BISHOP_LEFT_UP],
                edgeDistances[BISHOP_LEFT_UP]);

        return resultNwDiagonal | resultNeDiagonal;
    }

    // Insert diagonalVal on the diagonal on square, the diagonal with the according offset
    private long insertDialogVal(byte square, long diagonalVal, int offset, int offsetSquareTillEdge) {
        long result = 0;
        // Find the most up square on the diagonal and set curr square to the value
        int currSquare = square + (offset * offsetSquareTillEdge);
        byte[] currEdgeDistances = getDistanceTillEdgeOfBoardBishop((byte) currSquare);
        int diagonalLength = offset == BISHOP_OFFSETS[BISHOP_RIGHT_UP] ?
                currEdgeDistances[BISHOP_RIGHT_DOWN] : currEdgeDistances[BISHOP_LEFT_DOWN];

        // Run on the entire diagonal from up to down
        for (int i = 0; i <= diagonalLength; i++) {
            long bitVal = extractBit(i, diagonalVal);
            // Move the bit to the curr square, then move curr square by -offset
            result |= bitVal << currSquare;
            currSquare = currSquare - offset;
        }
        return result;
    }

    // Extract the bit position from val, and put that bit in the most right position
    private long extractBit(long position, long val) {
        return (val & (1L << position)) >>> position;
    }
}
