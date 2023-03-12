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

    private static final long FIRST_8_BITS = 0XFF;
    private static final byte MAX_DX_DY = 4;

    private static final BoardUtils utils = new BoardUtils();

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
        if (color == BoardUtils.WHITE) {
            generatePawnMoves(movesArray, color);
            generateMoves(atkArray, WHITE_PAWN_OFFSETS_ATK);
        } else {
            generatePawnMoves(movesArray, color);
            generateMoves(atkArray, BLACK_PAWN_OFFSETS_ATK);
        }
    }

    // Generate only the moves a pawn can do (not including capture)
    private void generatePawnMoves(long[] moveArray, boolean color) {
        int offset = (color) ? 8 : -8;
        for (byte square = 8; square < 56; square++) {
            long currSquareMoves = 0L;
            currSquareMoves |= utils.getSquarePositionAsBitboardPosition(offset + square);

            // if on 2nd raw as white, it can move twice
            if (square <= 15 && offset == 8)
                currSquareMoves |= utils.getSquarePositionAsBitboardPosition((offset * 2) + square);

            // if on the 6th row as black, it can move twice
            if (square >= 47 && offset == -8)
                currSquareMoves |= utils.getSquarePositionAsBitboardPosition((offset * 2) + square);

            moveArray[square] = currSquareMoves;
        }
    }

    // Generate moves for each square of the board for none line pieces (King, Knight, Pawn)
    private void generateMoves(long[] moveArray, byte[] offsetArray) {
        for (byte square = 0; square < BoardUtils.BOARD_SIZE; square++) {
            long currSquareMoves = 0L;
            for (byte offset : offsetArray) {
                // check if legal offset, if yes added the possible moves
                if (dxDyCheck(square, offset))
                    currSquareMoves |= utils.getSquarePositionAsBitboardPosition(offset + square);
            }
            moveArray[square] = currSquareMoves;
        }
    }

    // Generate a mask for squares for rook row and columns
    public void generateMaskRook(long[] maskArray) {
        for (byte square = 0; square < BoardUtils.BOARD_SIZE; square++)
            maskArray[square] = toBitMapRook(square, FIRST_8_BITS, FIRST_8_BITS);
    }

    // Generate a mask for squares for bishop row and columns
    public void generateMaskBishop(long[] maskArray) {
        for (byte square = 0; square < BoardUtils.BOARD_SIZE; square++)
            maskArray[square] = toBitMapBishop(square, FIRST_8_BITS, FIRST_8_BITS);
    }

    // Generate moves for each square of the board for line pieces (Rook, Bishop)
    private void generateAllMovesLinePiece(HashMap<Long, Long>[] moveArrayRook, HashMap<Long, Long>[] moveArrayBishop) {
        // For each square, for each possible values of rows and columns, get the bitboard value
        for (byte square = 0; square < BoardUtils.BOARD_SIZE; square++) {
            for (int rowValue = 0; rowValue < 256; rowValue++) {
                for (int columnValue = 0; columnValue < 256; columnValue++) {
                    long rookMap = toBitMapRook(square, rowValue, columnValue);
                    long bishopMap = toBitMapBishop(square, rowValue, columnValue);
                    long movesRook = generateMovesLinePiece(square, ROOK_OFFSETS, rookMap, getDistanceTillEdgeOfBoard(square));
                    long movesBishop = generateMovesLinePiece(square, BISHOP_OFFSETS, bishopMap, getDistanceTillEdgeOfBoardBishop(square));
                    moveArrayRook[square].put(rookMap, movesRook);
                    moveArrayBishop[square].put(bishopMap, movesBishop);
                }
            }
        }
    }

    // Given a square, rowValue. columnValue: set the value of row/column of the square to be rowValue and columnValue
    private long toBitMapRook(byte square, long rowValue, long columnValue) {
        int row = utils.getRowOfSquare(square), column = utils.getColOfSquare(square);
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
        byte[] edgeDistances = getDistanceTillEdgeOfBoardBishop(square);
        long resultNwDiagonal = insertDialogVal(square, nwDiagonalValue, BISHOP_OFFSETS[2], edgeDistances[2]);
        long resultNeDiagonal = insertDialogVal(square, neDiagonalValue, BISHOP_OFFSETS[0], edgeDistances[0]);
        return resultNwDiagonal | resultNeDiagonal;
    }

    // Insert diagonalVal on the diagonal on square, the diagonal with the according offset
    private long insertDialogVal(byte square, long diagonalVal, int offset, int offsetSquareTillEdge) {
        long result = 0;
        // Find the most up square on the diagonal and set curr square to the value
        int currSquare = square + (offset * offsetSquareTillEdge);
        byte[] currEdgeDistances = getDistanceTillEdgeOfBoardBishop((byte) currSquare);
        int diagonalLength = offset == 9 ? currEdgeDistances[3] : currEdgeDistances[1];
        // Run on the entire diagonal from up to down
        for (int i = 0; i < diagonalLength; i++) {
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

    // Given a square, an array of offsets and a long that represent the current board
    // Return a long with all the possible moves a piece with does offset can do on that board
    private long generateMovesLinePiece(byte square, byte[] offsetArray, long bitBoard, byte[] movesTillEdge) {
        long positionBit = utils.getSquarePositionAsBitboardPosition(square), result = 0, temp;
        for (byte i = 0; i < offsetArray.length; i++) {
            // Run until the edge of the board or found a piece
            for (byte j = 1; j <= movesTillEdge[i]; j++) {
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
    private byte[] getDistanceTillEdgeOfBoard(byte position) {
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

    // Given a position, return how many squares the bishop can go from his position to right up, left down,left up and right down
    private byte[] getDistanceTillEdgeOfBoardBishop(byte position) {
        byte[] distances = getDistanceTillEdgeOfBoard(position);
        int rightUp = Math.min(distances[1], distances[2]);
        int leftDown = Math.min(distances[0], distances[3]);
        int leftUp = Math.min(distances[0], distances[2]);
        int rightDown = Math.min(distances[1], distances[3]);

        return new byte[]{(byte) rightUp, (byte) leftDown, (byte) leftUp, (byte) rightDown};
    }

    // Given an offset and a square, check if that offset from that square is not bigger than max dx dy
    // Also check if target y position in range
    private boolean dxDyCheck(byte square, byte offset) {
        // Calculate the row and column of the given position, and of target position
        byte targetSquare = (byte)(square + offset);
        int currX = utils.getColOfSquare(square), currY = utils.getRowOfSquare(square);
        int targetX = utils.getColOfSquare(targetSquare), targetY = utils.getRowOfSquare(targetSquare);
        // Calculate dx dy
        int dx = Math.abs(currX - targetX), dy = Math.abs(currY - targetY);

        return (dx + dy < MAX_DX_DY) && targetSquare < BoardUtils.BOARD_SIZE && targetSquare > -1;
    }
}
