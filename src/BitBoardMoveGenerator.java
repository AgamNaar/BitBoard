import java.util.HashMap;

// This class is responsible, to generate for each piece, for each square on the board (0-63)
// All the possible moves it can do - regardless of the position of the board
// For each piece it returns an array of 64, each position has a bitboard of the move co-responding for that position
public class BitBoardMoveGenerator {

    private static final byte[] KING_OFFSETS = {-9, -8, -7, -1, 1, 7, 8, 9};
    private static final byte[] ROOK_OFFSETS = {1, -1, 8, -8};
    private static final byte[] BISHOP_OFFSETS = {7, -7, 9, -9};
    private static final byte[] KNIGHT_OFFSETS = {-17, -15, -10, -6, 6, 10, 15, 17};
    private static final byte[] WHITE_PAWN_OFFSETS_ATK = {7, 9};
    private static final byte[] BLACK_PAWN_OFFSETS_ATK = {-7, -9};
    private static final int[] DIAGONAL_LENGTH_BY_POSITION = new int[64];
    private static final long FIRST_8_BITS = 0XFF;

    public static final boolean WHITE = true;
    private static final byte MAX_DX_DY = 4;

    public BitBoardMoveGenerator() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i + j < 8)
                    DIAGONAL_LENGTH_BY_POSITION[i * 8 + j] = i + 1;
                else
                    DIAGONAL_LENGTH_BY_POSITION[i * 8 + j] = i - j + 1;
            }
        }
    }

    // Change moves array that in each position of the array, it has a bitboard that represent the moves a king can do in the position
    public void generateKingMoves(long[] movesArray) {
        generateMoves(movesArray, KING_OFFSETS);
    }

    // Change rook/bishop moves that, in each cell of the array, there is a hashmap that has the bitBoard movement for each row+column or diagonal
    // The value of row+column or diagonal is the key to that value of bitBoard movement
    public void generateLinePieceMoves(HashMap<Long, Long>[] rookMoves, HashMap<Long, Long>[] bishopMoves) {
        generateAllMovesLinePiece(rookMoves, bishopMoves);
    }

    // Change moves array that in each position of the array, it has a bitboard that represent the moves a knight can do in the position
    public void generateKnightMoves(long[] movesArray) {
        generateMoves(movesArray, KNIGHT_OFFSETS);
    }

    // Change moves array and atk array, that in each position of the array, it has a bitboard that represent the moves/capture a pawn can do in the position
    public void generatePawnMoves(long[] movesArray, long[] atkArray, boolean color) {
        if (color == WHITE) {
            generatePawnMoves(movesArray, color);
            generateMoves(atkArray, WHITE_PAWN_OFFSETS_ATK);
        } else {
            generatePawnMoves(movesArray, color);
            generateMoves(atkArray, BLACK_PAWN_OFFSETS_ATK);
        }
    }

    // Generate a mask for squares for rook row and columns
    public void generateMaskRook(long[] maskArray) {
        for (byte square = 0; square < 64; square++)
            maskArray[square] = toBitMapRook(square, FIRST_8_BITS, FIRST_8_BITS);
    }

    // Generate a mask for squares for bishop row and columns
    public void generateMaskBishop(long[] maskArray) {
        for (byte square = 0; square < 64; square++)
            maskArray[square] = toBitMapBishop(square, FIRST_8_BITS, FIRST_8_BITS);
    }

    // Generate only the moves a pawn can do (not including capture)
    private void generatePawnMoves(long[] moveArray, boolean color) {
        int offset = (color) ? 8 : -8;
        for (byte square = 8; square < 56; square++) {
            long currSquareMoves = 0L;
            currSquareMoves |= squareToBitboard(offset + square);

            // if on 2nd raw as white, it can move twice
            if (square <= 15 && offset == 8)
                currSquareMoves |= squareToBitboard((offset * 2) + square);

            // if on the 6th row as black, it can move twice
            if (square >= 47 && offset == -8)
                currSquareMoves |= squareToBitboard((offset * 2) + square);

            moveArray[square] = currSquareMoves;
        }
    }

    // Generate moves for each square of the board for none line pieces (King, Knight, Pawn)
    private void generateMoves(long[] moveArray, byte[] offsetArray) {
        for (byte square = 0; square < 64; square++) {
            long currSquareMoves = 0L;
            for (byte offset : offsetArray) {
                // check if legal offset, if yes added the possible moves
                if (dxDyCheck(square, offset))
                    currSquareMoves |= squareToBitboard(offset + square);
            }
            moveArray[square] = currSquareMoves;
        }
    }

    // Generate moves for each square of the board for line pieces (Rook, Bishop)
    private void generateAllMovesLinePiece(HashMap<Long, Long>[] moveArrayRook, HashMap<Long, Long>[] moveArrayBishop) {
        // For each square, for each possible values of rows and columns, get the bitboard value
        for (byte square = 0; square < 64; square++) {
            for (int rowValue = 0; rowValue < 256; rowValue++) {
                for (int columnValue = 0; columnValue < 256; columnValue++) {
                    long rookMap = toBitMapRook(square, rowValue, columnValue);
                    long bishopMap = toBitMapBishop(square, rowValue, columnValue);
                    long movesRook = generateMovesLinePiece(square, ROOK_OFFSETS, rookMap);
                    long movesBishop = generateMovesLinePiece(square, BISHOP_OFFSETS, bishopMap);
                    moveArrayRook[square].put(rookMap, movesRook);
                    moveArrayBishop[square].put(bishopMap, movesBishop);
                }
            }
        }
    }

    // Given a square, rowValue. columnValue: set the value of row/column of the square to be rowValue and columnValue
    private long toBitMapRook(byte square, long rowValue, long columnValue) {
        int row = square / 8, column = square % 8;
        long rowMask = rowValue << (8 * row), columnMask = 0;

        for (int i = 0; i < 8; i++) {
            // Extract i'th bit value from columnValue, move it the 0 bit position
            long bitValue = extractBit(i, columnValue);
            // Add it by moving it to its right row
            columnMask = columnMask | (bitValue << (column + (i * 8)));
        }
        return rowMask | columnMask;
    }

    // Given a square, nwDiagonalValue. neDiagonalValue: set the value of nwDiagonalValue/neDiagonalValue of the square to be nwDiagonalValue/neDiagonalValue
    private long toBitMapBishop(byte square, long nwDiagonalValue, long neDiagonalValue) {
        long result = 0;
        byte[] edgeDistances = getBitTillEdgeOfBoard(square);

        long position = square + (9 * (Math.min(edgeDistances[0], edgeDistances[2])));
        int diagonalLength = 8 - DIAGONAL_LENGTH_BY_POSITION[(int) position];
        // First diagonal
        for (int i = 0; i < diagonalLength; i++) {
            long bitVal = extractBit(i, neDiagonalValue);
            result |= bitVal << position;
            position = position - 9;
        }

        // Second diagonal
        position = square + (7 * (Math.min(edgeDistances[1], edgeDistances[2])));
        diagonalLength = DIAGONAL_LENGTH_BY_POSITION[(int) position];
        for (int i = 0; i < diagonalLength; i++) {
            long bitVal = extractBit(i, nwDiagonalValue);
            result |= bitVal << position;
            position = position - 7;
        }
        return result;
    }

    // Extract the bit position from val, and put that bit in the most right position
    private long extractBit(long position, long val) {
        return (val & (1L << position)) >>> position;
    }

    // Given a square, an array of offsets and a long that represent the current board
    // Return a long with all the possible moves a piece with does offset can do on that board
    private long generateMovesLinePiece(byte square, byte[] offsetArray, long bitBoard) {
        long positionBit = squareToBitboard(square), result = 0, temp;
        byte[] edgeDistances = getBitTillEdgeOfBoard(square);

        for (byte i = 0; i < offsetArray.length; i++) {
            // Run until the edge of the board or found a piece
            for (byte j = 1; j <= edgeDistances[i]; j++) {
                // Check if offset is negative or positive to offset right or left
                if (offsetArray[i] > 0)
                    temp = positionBit << j * offsetArray[i];
                else
                    temp = positionBit >>> j * -offsetArray[i];

                // if not 0, piece on that position, 0 mean position empty
                if ((temp & bitBoard) != 0) {
                    result |= temp;
                    break;
                } else
                    result |= temp;
            }
        }
        return result;
    }

    // Given a position, return how many bits till the end of the board from the position to left, right, up, down
    private byte[] getBitTillEdgeOfBoard(byte position) {
        byte[] distances = new byte[4];
        // Left
        distances[0] = (byte) (7 - (position % 8));
        // Right
        distances[1] = (byte) (position % 8);
        // Up
        distances[2] = (byte) (7 - (position / 8));
        // Down
        distances[3] = (byte) (position / 8);

        return distances;
    }

    // Given an offset and a square, check if that offset from that square is not bigger than max dx dy
    // Also check if target y position in range
    private boolean dxDyCheck(long position, byte offset) {
        // Calculate the row and column of the given position, and of target position
        long currX = position % 8, currY = position / 8, targetSquare = position + offset;
        long targetX = (targetSquare) % 8, targetY = (targetSquare) / 8;
        // Calculate dx dy
        long dx = Math.abs(currX - targetX), dy = Math.abs(currY - targetY);

        return (dx + dy < MAX_DX_DY) && targetSquare < 64 && targetSquare > -1;
    }

    // Convert position into bit position
    private long squareToBitboard(long square) {
        return 1L << square;
    }

}
