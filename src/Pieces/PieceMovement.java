package Pieces;

import Utils.BoardUtils;

import java.util.ArrayList;
import java.util.HashMap;

// Class that provide movement for pieces given their position, and bitboards that represent the state of the board
public class PieceMovement {
    private static final long[] KING_MOVES = new long[BoardUtils.BOARD_SIZE];
    private static final ArrayList<HashMap<Long, Long>> ROOK_MOVES = new ArrayList<>();
    private static final ArrayList<HashMap<Long, Long>> BISHOP_MOVES = new ArrayList<>();
    private static final long[] KNIGHT_MOVES = new long[BoardUtils.BOARD_SIZE];
    private static final long[] WHITE_PAWN_ONLY_MOVES = new long[BoardUtils.BOARD_SIZE];
    private static final long[] WHITE_PAWN_CAPTURE = new long[BoardUtils.BOARD_SIZE];
    private static final long[] BLACK_PAWN_ONLY_MOVES = new long[BoardUtils.BOARD_SIZE];
    private static final long[] BLACK_PAWN_CAPTURE = new long[BoardUtils.BOARD_SIZE];
    private static final long[] ROOK_MASK = new long[BoardUtils.BOARD_SIZE];
    private static final long[] BISHOP_MASK = new long[BoardUtils.BOARD_SIZE];

    // precalculate all the moves a piece can do , on each square, using the PieceMovementPreemptiveCalculator class
    public PieceMovement() {
        for (int i = 0; i < BoardUtils.BOARD_SIZE; i++) {
            ROOK_MOVES.add(new HashMap<>());
            BISHOP_MOVES.add(new HashMap<>());
        }
        PieceMovementPreemptiveCalculator generator = new PieceMovementPreemptiveCalculator();
        generator.generateKingMoves(KING_MOVES);
        generator.generateKnightMoves(KNIGHT_MOVES);
        generator.generatePawnMoves(WHITE_PAWN_ONLY_MOVES, WHITE_PAWN_CAPTURE, BoardUtils.WHITE);
        generator.generatePawnMoves(BLACK_PAWN_ONLY_MOVES, BLACK_PAWN_CAPTURE, BoardUtils.BLACK);
        generator.generateLinePieceMoves(ROOK_MOVES, BISHOP_MOVES);
        generator.generateMaskRook(ROOK_MASK);
        generator.generateMaskBishop(BISHOP_MASK);
    }

    // Given a position of a king and a bitboard of all the pieces with the same color, return the moves it can do as bitboard
    public long getKingMovement(byte position, long allSameColorPiecesBitBoard) {
        // All the moves it can do without position occupied by same color pieces
        long moves = KING_MOVES[position];
        return moves & ~allSameColorPiecesBitBoard;
    }

    // Given a position of a queen and a bitboard of all the pieces with the same color and a bitboard of all pieces
    // return the moves it can do as bitboard
    public long getQueenMovement(byte position, long allSameColorPiecesBitBoard, long allPiecesBitBoard) {
        // Queen moves like a rook and a bishop, and remove square with same color pieces
        return getRookMovement(position, allSameColorPiecesBitBoard, allPiecesBitBoard) | getBishopMovement(position, allSameColorPiecesBitBoard, allPiecesBitBoard);
    }

    // Given a position of a rook and a bitboard of all the pieces with the same color and a bitboard of all pieces
    // return the moves it can do as bitboard
    public long getRookMovement(byte position, long allPiecesBitBoard, long allSameColorPiecesBitBoard) {
        // Calculate the bitBoard & mask val - key to move for that position, and remove square with same color pieces
        long keyVal = ROOK_MASK[position] & allPiecesBitBoard;
        long moves = ROOK_MOVES.get(position).get(keyVal);
        return moves & ~allSameColorPiecesBitBoard;
    }

    // Given a position of a bishop and a bitboard of all the pieces with the same color and a bitboard of all pieces
    // return the moves it can do as bitboard
    public long getBishopMovement(byte position, long allPiecesBitBoard, long allSameColorPiecesBitBoard) {
        // Calculate the bitBoard & mask val - key to move for that position, and remove square with same color pieces
        long keyVal = BISHOP_MASK[position] & allPiecesBitBoard;
        long moves = BISHOP_MOVES.get(position).get(keyVal);
        return moves & ~allSameColorPiecesBitBoard;
    }

    // Given a position of a knight and a bitboard of all the pieces with the same color, return the moves it can do as bitboard
    public long getKnightMovement(byte position, long allSameColorPiecesBitBoard) {
        // All the moves it can do without position occupied by same color pieces
        long moves = KNIGHT_MOVES[position];
        return moves & ~allSameColorPiecesBitBoard;
    }

    // Given a position of a pawn. its color, bitboard of all the pieces and a bitboard of all enemy pieces
    // return the moves it can do as bitboard
    public long getPawnMovement(byte position, boolean color, long allPiecesBitBoard, long enemyPieceBitBoard) {
        // pawn can only capture if there is an enemy piece, and move only if its empty
        if (color == BoardUtils.WHITE)
            return ((WHITE_PAWN_CAPTURE[position] & enemyPieceBitBoard)) | ((WHITE_PAWN_ONLY_MOVES[position] & ~allPiecesBitBoard));
        else
            return ((BLACK_PAWN_CAPTURE[position] & enemyPieceBitBoard)) | ((BLACK_PAWN_ONLY_MOVES[position] & ~allPiecesBitBoard));
    }

    // Given a color and a square, return the capture moves that pawn color in that square can do
    public long getPawnCaptureSquare(boolean color, byte square) {
        return color ? WHITE_PAWN_CAPTURE[square] : BLACK_PAWN_CAPTURE[square];
    }
}
