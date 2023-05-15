package gamelogic.pieces;

import gamelogic.BoardUtils;
import gamelogic.preemptivecalculators.PieceMovementPreemptiveCalculator;

import java.util.ArrayList;
import java.util.HashMap;

import static gamelogic.BoardUtils.WHITE_PAWN_MOVE_OFFSET;

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

    private static boolean initialized = false;
    private static final BoardUtils utils = new BoardUtils();

    // Precalculate all the moves a piece can do, on each square, using the PieceMovementPreemptiveCalculator class
    public PieceMovement() {
        if (initialized)
            return;

        // Initializing
        for (int i = 0; i < BoardUtils.BOARD_SIZE; i++) {
            ROOK_MOVES.add(new HashMap<>());
            BISHOP_MOVES.add(new HashMap<>());
        }
        PieceMovementPreemptiveCalculator preemptiveCalculator = new PieceMovementPreemptiveCalculator();
        preemptiveCalculator.generateKingMoves(KING_MOVES);
        preemptiveCalculator.generateKnightMoves(KNIGHT_MOVES);
        preemptiveCalculator.generatePawnMoves(WHITE_PAWN_ONLY_MOVES, WHITE_PAWN_CAPTURE, BoardUtils.WHITE);
        preemptiveCalculator.generatePawnMoves(BLACK_PAWN_ONLY_MOVES, BLACK_PAWN_CAPTURE, BoardUtils.BLACK);
        preemptiveCalculator.generateLinePieceMoves(ROOK_MOVES, BISHOP_MOVES);

        initialized = true;
    }


    // Given a piecePosition of a king and a bitboard of all the pieces with the same color
    // return the moves it can do as bitboard
    public long getKingMovement(byte piecePosition, long sameColorPieceBitBoard) {
        // All the moves it can do without piecePosition occupied by same color pieces
        long moves = KING_MOVES[piecePosition];
        return moves & ~sameColorPieceBitBoard;
    }

    // Given a position of a queen and a bitboard of all the pieces with the same color and a bitboard of all pieces
    // Return the moves it can do as bitboard
    public long getQueenMovement(byte piecePosition, long sameColorPieceBitBoard, long allPiecesBitBoard) {
        // Queen moves like a rook and a bishop, and remove square with same color pieces
        return getRookMovement(piecePosition, sameColorPieceBitBoard, allPiecesBitBoard)
                | getBishopMovement(piecePosition, sameColorPieceBitBoard, allPiecesBitBoard);
    }

    // Given a position of a rook and a bitboard of all the pieces with the same color and a bitboard of all pieces
    // Return the moves it can do as bitboard
    public long getRookMovement(byte piecePosition, long allPiecesBitBoard, long sameColorPieceBitBoard) {
        // Calculate the bitBoard & mask val - key to move for that position, and remove square with same color pieces
        long keyVal = PieceMovementPreemptiveCalculator.ROOK_MASK[piecePosition] & allPiecesBitBoard;
        long moves = ROOK_MOVES.get(piecePosition).get(keyVal);
        return moves & ~sameColorPieceBitBoard;
    }

    // Given a position of a bishop and a bitboard of all the pieces with the same color and a bitboard of all pieces
    // Return the moves it can do as bitboard
    public long getBishopMovement(byte piecePosition, long allPiecesBitBoard, long sameColorPieceBitBoard) {
        // Calculate the bitBoard & mask val - key to move for that position, and remove square with same color pieces
        long keyVal = PieceMovementPreemptiveCalculator.BISHOP_MASK[piecePosition] & allPiecesBitBoard;
        long moves = BISHOP_MOVES.get(piecePosition).get(keyVal);
        return moves & ~sameColorPieceBitBoard;
    }

    // Given a position of a knight and a bitboard of all the pieces with the same color,
    // Return the moves it can do as bitboard
    public long getKnightMovement(byte piecePosition, long sameColorPieceBitBoard) {
        // All the moves it can do without position occupied by same color pieces
        long moves = KNIGHT_MOVES[piecePosition];
        return moves & ~sameColorPieceBitBoard;
    }

    // Given a position of a pawn. its color, bitboard of all the pieces and a bitboard of all enemy pieces
    // return the moves it can do as bitboard
    public long getPawnMovement(byte piecePosition, boolean color, long allPiecesBitBoard, long enemyPieceBitBoard) {
        long offset = color ? WHITE_PAWN_MOVE_OFFSET : BoardUtils.BLACK_PAWN_MOVE_OFFSET;
        long squareInfantOfPawn = utils.getSquarePositionAsBitboardPosition(piecePosition + offset);
        // In order to capture an enemy piece must be in the capture square
        long captureSquares = (color ? WHITE_PAWN_CAPTURE[piecePosition] : BLACK_PAWN_CAPTURE[piecePosition])
                & enemyPieceBitBoard;
        long movementSquares = color ? WHITE_PAWN_ONLY_MOVES[piecePosition] : BLACK_PAWN_ONLY_MOVES[piecePosition];

        // If there is a piece informant of the pawn it can only capture
        if ((squareInfantOfPawn & allPiecesBitBoard) != 0)
            return captureSquares;

        return captureSquares | (movementSquares & ~allPiecesBitBoard);
    }

    // Given a color and a square, return the capture moves that pawn color in that square can do
    public long getPawnCaptureSquare(boolean color, byte square) {
        return color ? WHITE_PAWN_CAPTURE[square] : BLACK_PAWN_CAPTURE[square];
    }
}
