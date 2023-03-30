package PreemptiveCalculators;

import Utils.BoardUtils;

import java.util.ArrayList;
import java.util.HashMap;

import static PreemptiveCalculators.PreemptiveCalculatorUtils.FIRST_8_BITS;
import static PreemptiveCalculators.PreemptiveCalculatorUtils.NUMBER_OF_POSSIBLE_VALUES_PER_EDGE;

/*
 This class is responsible to generate for each piece, for each square on the board (0-63), bitboards that represent the moves it can do
 For line pieces - their moves depend on the pieces on their moving line, so it calculates for each square all the possible combination on the moving lines
 Save it on a 64 array of hashmap, each cell of the array is a square, and for each hashmap the key for the movement is the mask value of the pieces on the movement line
 For none line pieces - simply calculate for each square what are the possible movements and save it on an array
 The movement is represented as a bitboard
 */
public class PieceMovementPreemptiveCalculator {

    private static final byte[] KING_OFFSETS = {-9, -8, -7, -1, 1, 7, 8, 9};
    private static final byte[] ROOK_OFFSETS = {1, -1, 8, -8};
    private static final byte[] KNIGHT_OFFSETS = {-17, -15, -10, -6, 6, 10, 15, 17};
    private static final byte[] WHITE_PAWN_OFFSETS_ATK = {7, 9};
    private static final byte[] BLACK_PAWN_OFFSETS_ATK = {-7, -9};

    private static final byte MAX_DX_DY = 4;

    private static final BoardUtils boardUtilises = new BoardUtils();
    private static final PreemptiveCalculatorUtils preemptiveCalculatorUtils = new PreemptiveCalculatorUtils();
    private static final byte LAST_SQUARE_ON_2ND_ROW = 15;
    private static final byte LAST_SQUARE_ON_6TH_ROW = 47;

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
        byte firstSquareOn2NdtRow = LAST_SQUARE_ON_2ND_ROW - BoardUtils.BOARD_EDGE_SIZE + 1;
        byte lastSquareOn6ThRow = LAST_SQUARE_ON_6TH_ROW + BoardUtils.BOARD_EDGE_SIZE + 1;
        for (byte pieceSquare = firstSquareOn2NdtRow; pieceSquare < lastSquareOn6ThRow; pieceSquare++) {
            long currSquareMoves = 0L;
            currSquareMoves |= boardUtilises.getSquarePositionAsBitboardPosition(offset + pieceSquare);

            // if on 2nd raw as white, it can move twice
            if (pieceSquare <= LAST_SQUARE_ON_2ND_ROW && offset == BoardUtils.WHITE_PAWN_MOVE_OFFSET)
                currSquareMoves |= boardUtilises.getSquarePositionAsBitboardPosition((offset * 2) + pieceSquare);

            // if on the 6th row as black, it can move twice
            if (pieceSquare >= LAST_SQUARE_ON_6TH_ROW && offset == BoardUtils.BLACK_PAWN_MOVE_OFFSET)
                currSquareMoves |= boardUtilises.getSquarePositionAsBitboardPosition((offset * 2) + pieceSquare);

            moveArray[pieceSquare] = currSquareMoves;
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

    // TODO: move to utils
    // Generate a mask for squares for rook row and columns
    public void generateMaskRook(long[] maskArray) {
        for (byte square = 0; square < BoardUtils.BOARD_SIZE; square++)
            maskArray[square] = preemptiveCalculatorUtils.toBitMapRook(square, FIRST_8_BITS, FIRST_8_BITS);
    }

    // TODO: move to utils
    // Generate a mask for squares for bishop row and columns
    public void generateMaskBishop(long[] maskArray) {
        for (byte square = 0; square < BoardUtils.BOARD_SIZE; square++)
            maskArray[square] = preemptiveCalculatorUtils.toBitMapBishop(square, FIRST_8_BITS, FIRST_8_BITS);
    }

    // Generate moves for each square of the board for line pieces (Rook, Bishop)
    private void generateAllMovesLinePiece(ArrayList<HashMap<Long, Long>> moveListRook, ArrayList<HashMap<Long, Long>> moveListBishop) {
        // For each square, for each possible values of rows and columns, get the movement it can do
        for (byte pieceSquare = 0; pieceSquare < BoardUtils.BOARD_SIZE; pieceSquare++) {
            for (int rowValue = 0; rowValue < NUMBER_OF_POSSIBLE_VALUES_PER_EDGE; rowValue++) {
                for (int columnValue = 0; columnValue < NUMBER_OF_POSSIBLE_VALUES_PER_EDGE; columnValue++) {
                    long rookMap = preemptiveCalculatorUtils.toBitMapRook(pieceSquare, rowValue, columnValue);
                    long bishopMap = preemptiveCalculatorUtils.toBitMapBishop(pieceSquare, rowValue, columnValue);
                    long movesRook = generateMovesLinePiece(pieceSquare, ROOK_OFFSETS, rookMap, preemptiveCalculatorUtils.getDistanceTillEdgeOfBoard(pieceSquare));
                    long movesBishop = generateMovesLinePiece(pieceSquare, PreemptiveCalculatorUtils.BISHOP_OFFSETS, bishopMap, preemptiveCalculatorUtils.getDistanceTillEdgeOfBoardBishop(pieceSquare));
                    moveListRook.get(pieceSquare).put(rookMap, movesRook);
                    moveListBishop.get(pieceSquare).put(bishopMap, movesBishop);
                }
            }
        }
    }

    // Given a pieceSquare, an array of offsets and a long that represent the current board
    // Return a long with all the possible moves a piece with does offset can do on that board
    private long generateMovesLinePiece(byte pieceSquare, byte[] offsetArray, long bitBoard, byte[] movesTillEdge) {
        long positionBit = boardUtilises.getSquarePositionAsBitboardPosition(pieceSquare), result = 0;
        for (byte i = 0; i < offsetArray.length; i++) {
            // Run until the edge of the board or found a piece
            for (byte j = 1; j <= movesTillEdge[i]; j++) {
                // Check if offset is negative or positive to offset right or left
                long currentBit;
                if (offsetArray[i] > 0)
                    currentBit = positionBit << j * offsetArray[i];
                else
                    currentBit = positionBit >>> j * -offsetArray[i];

                // if not 0, piece on that position, 0 mean position empty
                if ((currentBit & bitBoard) != 0) {
                    result |= currentBit;
                    break;
                } else
                    result |= currentBit;
            }
        }
        return result;
    }

    // Given an offset and a pieceSquare, check if that offset from that pieceSquare is not bigger than max dx dy
    // Also check if target y position in range
    private boolean dxDyCheck(byte pieceSquare, byte offset) {
        // Calculate the row and column of the given position, and of target position
        byte targetSquare = (byte) (pieceSquare + offset);
        int currX = boardUtilises.getColOfSquare(pieceSquare), currY = boardUtilises.getRowOfSquare(pieceSquare);
        int targetX = boardUtilises.getColOfSquare(targetSquare), targetY = boardUtilises.getRowOfSquare(targetSquare);
        // Calculate dx dy
        int dx = Math.abs(currX - targetX), dy = Math.abs(currY - targetY);

        return (dx + dy < MAX_DX_DY) && targetSquare < BoardUtils.BOARD_SIZE && targetSquare > -1;
    }
}
