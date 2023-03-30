package PreemptiveCalculators;

import Utils.BoardUtils;

import java.util.ArrayList;
import java.util.HashMap;

import static PreemptiveCalculators.PreemptiveCalculatorUtils.FIRST_8_BITS;

// This class is responsible to generate for each piece, for each square on the board (0-63), bitboards that represent the moves it can do
// For line pieces - their moves depend on the pieces on their moving line, so it calculates for each square all the possible combination on the moving lines
// Save it on a 64 array of hashmap, each cell of the array is a square, and for each hashmap the key for the movement is the mask value of the pieces on the movement line
// For none line pieces - simply calculate for each square what are the possible movements and save it on an array
// The movement is represented as a bitboard
public class PieceMovementPreemptiveCalculator {
    // Movement offset for each piece
    private static final byte[] KING_OFFSETS = {-9, -8, -7, -1, 1, 7, 8, 9};
    private static final byte[] ROOK_OFFSETS = {1, -1, 8, -8};
    private static final byte[] KNIGHT_OFFSETS = {-17, -15, -10, -6, 6, 10, 15, 17};
    private static final byte[] WHITE_PAWN_OFFSETS_ATK = {7, 9};
    private static final byte[] BLACK_PAWN_OFFSETS_ATK = {-7, -9};

    private static final byte MAX_DX_DY = 4;

    private static final BoardUtils boardUtilises = new BoardUtils();
    private static final PreemptiveCalculatorUtils preemptiveCalculatorUtils = new PreemptiveCalculatorUtils();

    // Change moves array that in each position of the array, it has a bitboard that represent the moves a king can do in the position
    public void generateKingMoves(long[] movesArray) {
        generateMoves(movesArray, KING_OFFSETS);
    }

    // Change rook/bishop moves that, in each cell of the array, there is a hashmap that has the bitBoard movement for each row+column or diagonal
    // The value of row+column or diagonal is the key to that value of bitBoard movement
    public void generateLinePieceMoves(ArrayList<HashMap<Long, Long>> rookMoves, ArrayList<HashMap<Long, Long>> bishopMoves) {
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
        int offset = color ? BoardUtils.WHITE_PAWN_MOVE_OFFSET : BoardUtils.BLACK_PAWN_MOVE_OFFSET;
        for (byte square = 8; square < 56; square++) {
            long currSquareMoves = 0L;
            currSquareMoves |= boardUtilises.getSquarePositionAsBitboardPosition(offset + square);

            // if on 2nd raw as white, it can move twice
            if (square <= 15 && offset == BoardUtils.WHITE_PAWN_MOVE_OFFSET)
                currSquareMoves |= boardUtilises.getSquarePositionAsBitboardPosition((offset * 2) + square);

            // if on the 6th row as black, it can move twice
            if (square >= 47 && offset == BoardUtils.BLACK_PAWN_MOVE_OFFSET)
                currSquareMoves |= boardUtilises.getSquarePositionAsBitboardPosition((offset * 2) + square);

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
                    currSquareMoves |= boardUtilises.getSquarePositionAsBitboardPosition(offset + square);
            }
            moveArray[square] = currSquareMoves;
        }
    }

    // Generate a mask for squares for rook row and columns
    public void generateMaskRook(long[] maskArray) {
        for (byte square = 0; square < BoardUtils.BOARD_SIZE; square++)
            maskArray[square] = preemptiveCalculatorUtils.toBitMapRook(square, FIRST_8_BITS, FIRST_8_BITS);
    }

    // Generate a mask for squares for bishop row and columns
    public void generateMaskBishop(long[] maskArray) {
        for (byte square = 0; square < BoardUtils.BOARD_SIZE; square++)
            maskArray[square] = preemptiveCalculatorUtils.toBitMapBishop(square, FIRST_8_BITS, FIRST_8_BITS);
    }

    // Generate moves for each square of the board for line pieces (Rook, Bishop)
    private void generateAllMovesLinePiece(ArrayList<HashMap<Long, Long>> moveListRook, ArrayList<HashMap<Long, Long>> moveListBishop) {
        // For each square, for each possible values of rows and columns, get the movement it can do
        for (byte square = 0; square < BoardUtils.BOARD_SIZE; square++) {
            for (int rowValue = 0; rowValue < 256; rowValue++) {
                for (int columnValue = 0; columnValue < 256; columnValue++) {
                    long rookMap = preemptiveCalculatorUtils.toBitMapRook(square, rowValue, columnValue);
                    long bishopMap = preemptiveCalculatorUtils.toBitMapBishop(square, rowValue, columnValue);
                    long movesRook = generateMovesLinePiece(square, ROOK_OFFSETS, rookMap, preemptiveCalculatorUtils.getDistanceTillEdgeOfBoard(square));
                    long movesBishop = generateMovesLinePiece(square, PreemptiveCalculatorUtils.BISHOP_OFFSETS, bishopMap, preemptiveCalculatorUtils.getDistanceTillEdgeOfBoardBishop(square));
                    moveListRook.get(square).put(rookMap, movesRook);
                    moveListBishop.get(square).put(bishopMap, movesBishop);
                }
            }
        }
    }

    // Given a square, an array of offsets and a long that represent the current board
    // Return a long with all the possible moves a piece with does offset can do on that board
    private long generateMovesLinePiece(byte square, byte[] offsetArray, long bitBoard, byte[] movesTillEdge) {
        long positionBit = boardUtilises.getSquarePositionAsBitboardPosition(square), result = 0, temp;
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

    // Given an offset and a square, check if that offset from that square is not bigger than max dx dy
    // Also check if target y position in range
    private boolean dxDyCheck(byte square, byte offset) {
        // Calculate the row and column of the given position, and of target position
        byte targetSquare = (byte) (square + offset);
        int currX = boardUtilises.getColOfSquare(square), currY = boardUtilises.getRowOfSquare(square);
        int targetX = boardUtilises.getColOfSquare(targetSquare), targetY = boardUtilises.getRowOfSquare(targetSquare);
        // Calculate dx dy
        int dx = Math.abs(currX - targetX), dy = Math.abs(currY - targetY);

        return (dx + dy < MAX_DX_DY) && targetSquare < BoardUtils.BOARD_SIZE && targetSquare > -1;
    }
}
