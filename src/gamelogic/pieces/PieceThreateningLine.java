package gamelogic.pieces;

import gamelogic.GameLogicUtilities;
import gamelogic.preemptivecalculators.ThreateningLinePreemptiveCalculator;

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

    private static final long NO_VALUE = 0;

    private static boolean initialized = false;

    // precalculate all the threatening lines using the ThreateningLinePreemptiveCalculator class
    public PieceThreateningLine() {
        if (initialized)
            return;

        // Initializing
        ThreateningLinePreemptiveCalculator threateningLineCalculator = new ThreateningLinePreemptiveCalculator();

        for (int i = 0; i < GameLogicUtilities.BOARD_SIZE; i++) {
            rookThreateningLinesDB.add(new ArrayList<>());
            bishopThreateningLinesDB.add(new ArrayList<>());
            for (int j = 0; j < GameLogicUtilities.BOARD_SIZE; j++) {
                rookThreateningLinesDB.get(i).add(new HashMap<>());
                bishopThreateningLinesDB.get(i).add(new HashMap<>());
            }
        }
        // Fill the db with the possible threatening lines
        threateningLineCalculator.calculateThreateningLines(rookThreateningLinesDB, bishopThreateningLinesDB);
        initialized = true;
    }

    // Given the piece position, enemy king position and bitboard of the board, return the threat line of the queen
    public long getQueenThreateningLine(byte piecePosition, byte enemyKingSquare, Long allPiecesBitBoard) {
        // The queen can have only 1 treat line as a piece on the enemy king (either as a bishop or rook)
        // Either both 0 or just one of them is 0
        long rookTreatLine = getRookThreateningLine(piecePosition, enemyKingSquare, allPiecesBitBoard);
        long bishopTreatLine = getBishopThreateningLine(piecePosition, enemyKingSquare, allPiecesBitBoard);
        return rookTreatLine + bishopTreatLine;
    }

    // Given the piece position, enemy king position and bitboard of the board, return the threat line of the rook
    public long getRookThreateningLine(byte piecePosition, byte enemyKingSquare, Long allPiecesBitBoard) {
        long keyVal = ThreateningLinePreemptiveCalculator.ROOK_MASK[piecePosition] & allPiecesBitBoard;
        return rookThreateningLinesDB.get(piecePosition).get(enemyKingSquare).getOrDefault(keyVal, NO_VALUE);
    }

    // Given the piece position, enemy king position and bitboard of the board, return the threat line of the bishop
    public long getBishopThreateningLine(byte piecePosition, byte enemyKingSquare, Long allPiecesBitBoard) {
        long keyVal = ThreateningLinePreemptiveCalculator.BISHOP_MASK[piecePosition] & allPiecesBitBoard;
        return bishopThreateningLinesDB.get(piecePosition).get(enemyKingSquare).getOrDefault(keyVal, NO_VALUE);
    }
}
