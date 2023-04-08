package PreemptiveCalculators;

import Utils.BoardUtils;

import java.util.ArrayList;
import java.util.HashMap;

/*
 Class responsible for precalculating the treating lines for the lines piece, per square position, per enemy king position and per key val
 Key value is the bitboard of the board & mask of the piece
 treat line - if the piece threat the king, or if an enemy piece block the piece from threading the king
 the treat line will be all the square from the piece to the king as bitboards
 if more than 1 piece block a piece from treating, or the king is not even on the attack line, return 0
 */
public class ThreateningLinePreemptiveCalculator extends PreemptiveCalculator {

    private static final BoardUtils boardUtils = new BoardUtils();
    private static final int MAX_NUMBER_OF_PIECE_ON_THREAT_LINE = 1;

    // Fill the list with all the possible threaten moves of a bishop and a rook
    public void calculateThreateningLines(ArrayList<ArrayList<HashMap<Long, Long>>> rookTreatingLinesList, ArrayList<ArrayList<HashMap<Long, Long>>> bishopTreatingLinesList) {
        for (byte square = 0; square < BoardUtils.BOARD_SIZE; square++)
            for (byte kingPosition = 0; kingPosition < BoardUtils.BOARD_SIZE; kingPosition++)
                for (int rowValue = 0; rowValue < NUMBER_OF_POSSIBLE_VALUES_PER_EDGE; rowValue++)
                    for (int columnValue = 0; columnValue < NUMBER_OF_POSSIBLE_VALUES_PER_EDGE; columnValue++)
                        addThreatenLineToList(square, kingPosition, rowValue, columnValue, rookTreatingLinesList, bishopTreatingLinesList);
    }

    // Given the square of the piece, enemy king square, value of the row and column of the bitboard of all piece, add new threaten line to list of not 0
    private void addThreatenLineToList(byte pieceSquare, byte kingPosition, int rowValue, int columnValue, ArrayList<ArrayList<HashMap<Long, Long>>> rookTreatingLinesList, ArrayList<ArrayList<HashMap<Long, Long>>> bishopTreatingLinesList) {
        // Calculate the value of the bitboard of the pieces according to row/column value per line of movement of bishop/rook
        long rookBitBoardMap = toBitMapRook(pieceSquare, rowValue, columnValue);
        long bishopBitBoardMap = toBitMapBishop(pieceSquare, rowValue, columnValue);
        long rookMask = toBitMapRook(pieceSquare, FIRST_8_BITS, FIRST_8_BITS);
        long bishopMask = toBitMapBishop(pieceSquare, FIRST_8_BITS, FIRST_8_BITS);
        byte[] rookEdgeDistance = getDistanceTillEdgeOfBoard(pieceSquare);
        byte[] bishopEdgeDistances = getDistanceTillEdgeOfBoardBishop(pieceSquare);
        long rookThreateningLine = checkIfValidThanCreateThreatenLine(pieceSquare, kingPosition, rookBitBoardMap, rookMask, ROOK_OFFSETS, rookEdgeDistance);
        long bishopThreateningLine = checkIfValidThanCreateThreatenLine(pieceSquare, kingPosition, bishopBitBoardMap, bishopMask, BISHOP_OFFSETS, bishopEdgeDistances);
        // Add threaten line if not empty, meaning not 0
        if (rookThreateningLine != 0)
            rookTreatingLinesList.get(pieceSquare).get(kingPosition).put(rookBitBoardMap, rookThreateningLine);
        if (bishopThreateningLine != 0)
            bishopTreatingLinesList.get(pieceSquare).get(kingPosition).put(bishopBitBoardMap, bishopThreateningLine);
    }

    // Check if the king position can be threatened by the piece, and the piece is on one the values of the row/column
    private long checkIfValidThanCreateThreatenLine(byte pieceSquare, byte kingPosition, long boardBitBoard, long mask, byte[] movementOffsetArray, byte[] distancesTillEdge) {
        long kingPositionAsBitBoard = boardUtils.getSquarePositionAsBitboardPosition(kingPosition);
        long piecePositionAsBitBoard = boardUtils.getSquarePositionAsBitboardPosition(pieceSquare);
        // King position is on the mask of the piece movement
        if ((mask & kingPositionAsBitBoard) == 0)
            return 0;

        // The piece position is on the bitboard (i.e. piece position is on a bit that is on the bitboard of pieces)
        if ((piecePositionAsBitBoard & boardBitBoard) == 0)
            return 0;

        return createThreatenLine(pieceSquare, kingPosition, boardBitBoard, movementOffsetArray, distancesTillEdge);
    }

    // Given the square of the piece, position of enemy king, bitboard of all the pieces, movement offset of the piece and moves till the edge
    // Calculate the threatening line, if no threatening line return 0
    private long createThreatenLine(byte pieceSquare, byte kingPosition, long bitBoard, byte[] offsetArray, byte[] movesTillEdge) {
        long positionBit = boardUtils.getSquarePositionAsBitboardPosition(pieceSquare);
        long kingPositionAsBitBoard = boardUtils.getSquarePositionAsBitboardPosition(kingPosition);
        for (byte i = 0; i < offsetArray.length; i++) {
            long currentLine = positionBit;
            int numberOfPiecesOnLine = 0;
            // Run until the edge of the board or till the enemy king
            for (byte j = 1; j <= movesTillEdge[i]; j++) {
                // Check if offset is negative or positive to offset right or left
                long currentBitPosition;
                if (offsetArray[i] > 0)
                    currentBitPosition = positionBit << j * offsetArray[i];
                else
                    currentBitPosition = positionBit >>> j * -offsetArray[i];

                // Check if it's on a piece
                if ((currentBitPosition & bitBoard) != 0) {
                    // the piece is a king
                    if ((currentBitPosition & kingPositionAsBitBoard) != 0) {
                        return currentLine;
                    }
                    numberOfPiecesOnLine++;
                    // Too many piece, check next offset
                    if (numberOfPiecesOnLine > MAX_NUMBER_OF_PIECE_ON_THREAT_LINE)
                        break;
                }
                currentLine |= currentBitPosition;
            }
        }
        return 0;
    }
}
