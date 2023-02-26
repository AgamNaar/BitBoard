// Class that provide movement of pieces given their position, according to the state of the board (board represented as bitboard)
public class PieceMovement {

    private static final byte BOARD_SIZE = 64;
    private static final int A = (int)Math.pow(2,8);

    private final BitBoards bitBoards;
    private static BitBoardMoveGenerator generator;

    private static final long[] KING_MOVES = new long[BOARD_SIZE];
    private static final long[][][] QUEEN_MOVES = new long[BOARD_SIZE][A][A];
    private static final long[][][] ROOK_MOVES = new long[BOARD_SIZE][A][A];
    private static final long[][][] BISHOP_MOVES = new long[BOARD_SIZE][A][A];
    private static final long[] KNIGHT_MOVES = new long[BOARD_SIZE];
    private static final long[] WHITE_PAWN_ONLY_MOVES = new long[BOARD_SIZE];
    private static final long[] WHITE_PAWN_CAPTURE = new long[BOARD_SIZE];
    private static final long[] BLACK_PAWN_ONLY_MOVES = new long[BOARD_SIZE];
    private static final long[] BLACK_PAWN_CAPTURE = new long[BOARD_SIZE];

    private static final boolean WHITE = true;

    // builder receive bitboard
    public PieceMovement(BitBoards bitBoards) {
        this.bitBoards = bitBoards;
        generator = new BitBoardMoveGenerator();
        generator.generateKingMoves(KING_MOVES);
        generator.generateKnightMoves(KNIGHT_MOVES);
        generator.generatePawnMoves(WHITE_PAWN_ONLY_MOVES,WHITE_PAWN_CAPTURE,WHITE);
        generator.generatePawnMoves(BLACK_PAWN_ONLY_MOVES,BLACK_PAWN_CAPTURE,!WHITE);
    }

    // Given a position of a king, return the moves it can do as bitboard
    public long getKingMovement(byte position, boolean color) {
        // All the moves it can do without position occupied by same color pieces
        if (color == WHITE)
            return KING_MOVES[position] & ~bitBoards.getWhitePieces();
        else
            return KING_MOVES[position] & ~bitBoards.getBlackPieces();
    }

    /*
    // Given a position of a rook, return the moves it can do as bitboard
    public long getRookMovement(byte position, boolean color) {
        int rowValue = getRowValue(), columnValue = getColumnValue();
        // All the moves it can do without position occupied by same color pieces
        if (color == WHITE)
            return ROOK_MOVES[position][rowValue][columnValue] & ~bitBoards.getWhitePieces();
        else
            return ROOK_MOVES[position][rowValue][columnValue] & ~bitBoards.getBlackPieces();
    }

     */

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
