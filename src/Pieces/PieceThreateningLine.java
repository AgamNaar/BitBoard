package Pieces;

import PreemptiveCalculators.PieceMovementPreemptiveCalculator;
import PreemptiveCalculators.ThreateningLinePreemptiveCalculator;
import Utils.BoardUtils;

import java.util.ArrayList;
import java.util.HashMap;

/*
 Class that provide threatening line of line pieces, given their position, enemy king position the board as bitboard
 Save it as a linked list of linked list of hash maps
 Index of first list - position of piece
 Index of second list - position of king
 Key in the hash map - mask of the according piece & bitboard of the board
 Mask of each piece is their as bitboard movement if the board was empty
 */
public class PieceThreateningLine {

    private static final ArrayList<ArrayList<HashMap<Long, Long>>> rookThreateningLinesDB = new ArrayList<>();
    private static final ArrayList<ArrayList<HashMap<Long, Long>>> bishopThreateningLinesDB = new ArrayList<>();
    //TODO: has duplicate in PieceMovement
    private static final long[] ROOK_MASK = new long[BoardUtils.BOARD_SIZE];
    private static final long[] BISHOP_MASK = new long[BoardUtils.BOARD_SIZE];

    private static final long NO_VALUE = 0;

    // precalculate all the threatening lines using the ThreateningLinePreemptiveCalculator class
    public PieceThreateningLine() {
        ThreateningLinePreemptiveCalculator threateningLinePreemptiveCalculator = new ThreateningLinePreemptiveCalculator();
        PieceMovementPreemptiveCalculator pieceMovementPreemptiveCalculator = new PieceMovementPreemptiveCalculator();
        pieceMovementPreemptiveCalculator.generateMaskRook(ROOK_MASK);
        pieceMovementPreemptiveCalculator.generateMaskBishop(BISHOP_MASK);
        // Initializing
        for (int i = 0; i < BoardUtils.BOARD_SIZE; i++) {
            rookThreateningLinesDB.add(new ArrayList<>());
            bishopThreateningLinesDB.add(new ArrayList<>());
            for (int j = 0; j < BoardUtils.BOARD_SIZE; j++) {
                rookThreateningLinesDB.get(i).add(new HashMap<>());
                bishopThreateningLinesDB.get(i).add(new HashMap<>());
            }
        }
        // Fill the db with the possible threatening lines
        threateningLinePreemptiveCalculator.calculateThreateningLines(rookThreateningLinesDB, bishopThreateningLinesDB);
    }

    // Given a position of a piece, enemy king position and bitboard of the board, return the threat line of the queen
    public long getQueenThreateningLine(byte piecePosition, byte enemyKingSquare, Long allPiecesBitBoard) {
        // The queen can have only 1 treat line as a piece on the enemy king (either as a bishop or rook)
        // Either both 0 or just one of them is 0
        long rookTreatLine = getRookThreateningLine(piecePosition, enemyKingSquare, allPiecesBitBoard);
        long bishopTreatLine = getBishopThreateningLine(piecePosition, enemyKingSquare, allPiecesBitBoard);
        return rookTreatLine + bishopTreatLine;
    }

    // Given a position of a piece, enemy king position and bitboard of the board, return the threat line of the rook
    public long getRookThreateningLine(byte piecePosition, byte enemyKingSquare, Long allPiecesBitBoard) {
        long keyVal = ROOK_MASK[piecePosition] & allPiecesBitBoard;
        return rookThreateningLinesDB.get(piecePosition).get(enemyKingSquare).getOrDefault(keyVal, NO_VALUE);
    }

    // Given a position of a piece, enemy king position and bitboard of the board, return the threat line of the bishop
    public long getBishopThreateningLine(byte piecePosition, byte enemyKingSquare, Long allPiecesBitBoard) {
        long keyVal = BISHOP_MASK[piecePosition] & allPiecesBitBoard;
        return bishopThreateningLinesDB.get(piecePosition).get(enemyKingSquare).getOrDefault(keyVal, NO_VALUE);
    }
}
