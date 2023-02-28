import java.util.HashMap;

// Class that provide movement of pieces given their position, according to the state of the board (board represented as bitboard)
public class PieceMovement {

    private static final byte BOARD_SIZE = 64;

    private final BitBoards bitBoards;

    private static final long[] KING_MOVES = new long[BOARD_SIZE];
    private static final HashMap<Long,Long>[]ROOK_MOVES = (HashMap<Long, Long>[]) new HashMap<?,?>[BOARD_SIZE];
    private static final HashMap<Long,Long>[]BISHOP_MOVES =  (HashMap<Long, Long>[]) new HashMap<?,?>[BOARD_SIZE];
    private static final long[] KNIGHT_MOVES = new long[BOARD_SIZE];
    private static final long[] WHITE_PAWN_ONLY_MOVES = new long[BOARD_SIZE];
    private static final long[] WHITE_PAWN_CAPTURE = new long[BOARD_SIZE];
    private static final long[] BLACK_PAWN_ONLY_MOVES = new long[BOARD_SIZE];
    private static final long[] BLACK_PAWN_CAPTURE = new long[BOARD_SIZE];
    private static final long[] ROOK_MASK = new long[BOARD_SIZE];
    private static final long[] BISHOP_MASK = new long[BOARD_SIZE];

    private static final boolean WHITE = true;

    // builder receive bitboard
    public PieceMovement(BitBoards bitBoards) {
        this.bitBoards = bitBoards;
        BitBoardMoveGenerator generator = new BitBoardMoveGenerator();
        generator.generateKingMoves(KING_MOVES);
        generator.generateKnightMoves(KNIGHT_MOVES);
        generator.generatePawnMoves(WHITE_PAWN_ONLY_MOVES,WHITE_PAWN_CAPTURE,WHITE);
        generator.generatePawnMoves(BLACK_PAWN_ONLY_MOVES,BLACK_PAWN_CAPTURE,!WHITE);
        generator.generateLinePieceMoves(ROOK_MOVES,BISHOP_MOVES);
        generator.generateMaskRook(ROOK_MASK);
        generator.generateMaskBishop(BISHOP_MASK);
    }

    // Given a position of a king, return the moves it can do as bitboard
    public long getKingMovement(byte position, boolean color) {
        // All the moves it can do without position occupied by same color pieces
        if (color == WHITE)
            return KING_MOVES[position] & ~bitBoards.getWhitePieces();
        else
            return KING_MOVES[position] & ~bitBoards.getBlackPieces();
    }

    // Given a position of a queen, return the moves it can do as bitboard
    public long getQueenMovement(byte position, boolean color) {
        // All the moves it can do, without the last piece on the end of each line, if its same color
        if (color == WHITE)
            return getRookMovement(position,color) | getBishopMovement(position,color) & ~bitBoards.getWhitePieces();
        else
            return getRookMovement(position,color) | getBishopMovement(position,color) & ~bitBoards.getBlackPieces();
    }

    // Given a position of a rook, return the moves it can do as bitboard
    public long getRookMovement(byte position, boolean color) {
        // All the moves it can do, without the last piece on the end of each line, if its same color
        if (color == WHITE)
            return ROOK_MOVES[position].get(ROOK_MASK[position]) & ~bitBoards.getWhitePieces();
        else
            return ROOK_MOVES[position].get(ROOK_MASK[position]) & ~bitBoards.getBlackPieces();
    }

    // Given a position of a bishop, return the moves it can do as bitboard
    public long getBishopMovement(byte position, boolean color) {
        // All the moves it can do, without the last piece on the end of each line, if its same color
        if (color == WHITE)
            return BISHOP_MOVES[position].get(BISHOP_MASK[position]) & ~bitBoards.getWhitePieces();
        else
            return BISHOP_MOVES[position].get(BISHOP_MASK[position]) & ~bitBoards.getBlackPieces();
    }

    // Given a position of a knight, return the moves it can do as bitboard
    public long getKnightMovement(byte position, boolean color) {
        // All the moves it can do without position occupied by same color pieces
        if (color == WHITE)
            return KNIGHT_MOVES[position] & ~bitBoards.getWhitePieces();
        else
            return KNIGHT_MOVES[position] & ~bitBoards.getBlackPieces();
    }

    // Given a position of a pawn, return the moves it can do as bitboard
    public long getPawnMovement(byte position, boolean color) {
        // pawn can only capture if there is an enemy piece, and move only if its empty
        if (color == WHITE)
            return (WHITE_PAWN_CAPTURE[position] & bitBoards.getBlackPieces()) | (WHITE_PAWN_ONLY_MOVES[position] & ~bitBoards.getAllPieces());
        else
            return (BLACK_PAWN_CAPTURE[position] & bitBoards.getWhitePieces()) | (BLACK_PAWN_ONLY_MOVES[position] & ~bitBoards.getAllPieces());
    }



}
