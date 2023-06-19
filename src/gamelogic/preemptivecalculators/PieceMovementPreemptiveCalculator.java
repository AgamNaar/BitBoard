package gamelogic.preemptivecalculators;

import gamelogic.GameLogicUtilities;
import gamelogic.MovementData;

import java.util.ArrayList;
import java.util.HashMap;

/*
 This class is responsible to generate for each piece, for each square on the board (0-63),
 bitboards that represent the moves it can do
 For line pieces - their moves depend on the pieces on their moving line,
 so it calculates for each square all the possible combination on the moving lines
 Save it on a 64 array of hashmap, each cell of the array is a square,
 and for each hashmap the key for the movement is the mask value of the pieces on the movement line
 For none line pieces - simply calculate for each square what are the possible movements and save it on an array.
 The movement is represented as a bitboard.
 */
public class PieceMovementPreemptiveCalculator extends PreemptiveCalculator {

    private static final byte[] KING_OFFSETS = {-9, -8, -7, -1, 1, 7, 8, 9};
    private static final byte[] ROOK_OFFSETS = {1, -1, 8, -8};
    private static final byte[] KNIGHT_OFFSETS = {-17, -15, -10, -6, 6, 10, 15, 17};
    private static final byte[] WHITE_PAWN_OFFSETS_ATK = {7, 9};
    private static final byte[] BLACK_PAWN_OFFSETS_ATK = {-7, -9};

    private static final byte MAX_DX_DY = 4;

    private static final byte LAST_SQUARE_ON_2ND_ROW = 15;
    private static final byte LAST_SQUARE_ON_6TH_ROW = 48;

    // Change moves array that in each position of the array,
    // it has a bitboard that represent the moves a king can do in the position
    public void generateKingMoves(long[] movesArray) {
        generateMoves(movesArray, KING_OFFSETS);
    }

    // Change rook/bishop moves that, in each cell of the array,
    // there is a hashmap that has the bitBoard movement for each row+column or diagonal
    // The value of row+column or diagonal is the key to that value of bitBoard movement
    public void generateLinePieceMoves(ArrayList<HashMap<Long, MovementData>> rookMoves,
                                       ArrayList<HashMap<Long, MovementData>> bishopMoves) {

        generateAllMovesLinePiece(rookMoves, bishopMoves);
    }

    // Change moves array that in each position of the array,
    // it has a bitboard that represent the moves a knight can do in the position
    public void generateKnightMoves(long[] movesArray) {
        generateMoves(movesArray, KNIGHT_OFFSETS);
    }

    // Change moves array and atk array, that in each position of the array,
    // it has a bitboard that represent the moves/capture a pawn can do in the position
    public void generatePawnMoves(long[] movesArray, long[] atkArray, boolean color) {
        if (color == GameLogicUtilities.WHITE) {
            generatePawnMoves(movesArray, color);
            generateMoves(atkArray, WHITE_PAWN_OFFSETS_ATK);
        } else {
            generatePawnMoves(movesArray, color);
            generateMoves(atkArray, BLACK_PAWN_OFFSETS_ATK);
        }
    }

    // Generate only the moves a pawn can do (not including capture)
    private void generatePawnMoves(long[] moveArray, boolean color) {
        int offset = color ? GameLogicUtilities.WHITE_PAWN_MOVE_OFFSET : GameLogicUtilities.BLACK_PAWN_MOVE_OFFSET;
        byte firstSquareOn2NdtRow = LAST_SQUARE_ON_2ND_ROW - GameLogicUtilities.BOARD_EDGE_SIZE + 1;
        byte lastSquareOn6ThRow = LAST_SQUARE_ON_6TH_ROW + GameLogicUtilities.BOARD_EDGE_SIZE + 1;
        for (byte pieceSquare = firstSquareOn2NdtRow; pieceSquare < lastSquareOn6ThRow; pieceSquare++) {
            long currSquareMoves = 0L;
            currSquareMoves |= GameLogicUtilities.squareAsBitBoard(offset + pieceSquare);

            // if on 2nd raw as white, it can move twice
            if (pieceSquare <= LAST_SQUARE_ON_2ND_ROW && offset == GameLogicUtilities.WHITE_PAWN_MOVE_OFFSET)
                currSquareMoves |= GameLogicUtilities.squareAsBitBoard((offset * 2) + pieceSquare);

            // if on the 6th row as black, it can move twice
            if (pieceSquare >= LAST_SQUARE_ON_6TH_ROW && offset == GameLogicUtilities.BLACK_PAWN_MOVE_OFFSET)
                currSquareMoves |= GameLogicUtilities.squareAsBitBoard((offset * 2) + pieceSquare);

            moveArray[pieceSquare] = currSquareMoves;
        }
    }

    // Generate moves for each square of the board for none line pieces (King, Knight, Pawn)
    private void generateMoves(long[] moveArray, byte[] offsetArray) {
        for (byte square = 0; square < GameLogicUtilities.BOARD_SIZE; square++) {
            long currSquareMoves = 0L;
            for (byte offset : offsetArray) {
                // check if legal offset, if yes added the possible moves
                if (dxDyCheck(square, offset))
                    currSquareMoves |= GameLogicUtilities.squareAsBitBoard(offset + square);
            }
            moveArray[square] = currSquareMoves;
        }
    }

    // Generate moves for each square of the board for line pieces (Rook, Bishop)
    private void generateAllMovesLinePiece(ArrayList<HashMap<Long, MovementData>> moveListRook,
                                           ArrayList<HashMap<Long, MovementData>> moveListBishop) {

        // For each square, for each possible values of rows and columns, get the movement it can do
        for (byte pieceSquare = 0; pieceSquare < GameLogicUtilities.BOARD_SIZE; pieceSquare++) {
            for (int rowValue = 0; rowValue < NUMBER_OF_POSSIBLE_VALUES_PER_EDGE; rowValue++) {
                for (int columnValue = 0; columnValue < NUMBER_OF_POSSIBLE_VALUES_PER_EDGE; columnValue++) {
                    long rookMap = toBitMapRook(pieceSquare, rowValue, columnValue);
                    long bishopMap = toBitMapBishop(pieceSquare, rowValue, columnValue);
                    MovementData movesRook = generateMovesLinePiece(pieceSquare, ROOK_OFFSETS, rookMap,
                            getDistanceTillEdgeOfBoard(pieceSquare));
                    MovementData movesBishop = generateMovesLinePiece(pieceSquare, BISHOP_OFFSETS, bishopMap,
                            getDistanceTillEdgeOfBoardBishop(pieceSquare));
                    moveListRook.get(pieceSquare).put(rookMap, movesRook);
                    moveListBishop.get(pieceSquare).put(bishopMap, movesBishop);
                }
            }
        }
    }

    // Given a pieceSquare, an array of offsets and a long that represent the current board
    // Return a long with all the possible moves a piece with does offset can do on that board
    private MovementData generateMovesLinePiece(byte pieceSquare, byte[] offsetArray, long bitBoard, byte[] movesTillEdge) {
        long positionBit = GameLogicUtilities.squareAsBitBoard(pieceSquare), result = 0;
        int counter = 0;
        for (byte i = 0; i < offsetArray.length; i++) {
            // Run until the edge of the board or found a piece
            for (byte j = 1; j <= movesTillEdge[i]; j++) {
                long currentBit = GameLogicUtilities.shiftNumberLeft(positionBit, j * offsetArray[i]);

                // if not 0, piece on that position, 0 mean position empty
                if ((currentBit & bitBoard) != 0) {
                    result |= currentBit;
                    counter++;
                    break;
                } else {
                    counter++;
                    result |= currentBit;
                }
            }
        }
        return new MovementData(result, counter);
    }

    // Given an offset and a pieceSquare, check if that offset from that pieceSquare is not bigger than max dx dy
    // Also check if target y position in range
    private boolean dxDyCheck(byte pieceSquare, byte offset) {
        // Calculate the row and column of the given position, and of target position
        byte targetSquare = (byte) (pieceSquare + offset);
        int currX = GameLogicUtilities.getColOfSquare(pieceSquare),
                currY = GameLogicUtilities.getRowOfSquare(pieceSquare),
                targetX = GameLogicUtilities.getColOfSquare(targetSquare),
                targetY = GameLogicUtilities.getRowOfSquare(targetSquare);
        // Calculate dx dy
        int dx = Math.abs(currX - targetX), dy = Math.abs(currY - targetY);

        return (dx + dy < MAX_DX_DY) && targetSquare < GameLogicUtilities.BOARD_SIZE && targetSquare > -1;
    }
}
