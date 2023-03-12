import java.util.HashMap;

// Class that provide movement of pieces given their position, according to the state of the board (board represented as bitboard)
public class PieceMovement {

    private final BitBoards bitBoards;

    private static final long[] KING_MOVES = new long[BoardUtils.BOARD_SIZE];
    private static final HashMap<Long, Long>[] ROOK_MOVES = (HashMap<Long, Long>[]) new HashMap<?, ?>[BoardUtils.BOARD_SIZE];
    private static final HashMap<Long, Long>[] BISHOP_MOVES = (HashMap<Long, Long>[]) new HashMap<?, ?>[BoardUtils.BOARD_SIZE];
    private static final long[] KNIGHT_MOVES = new long[BoardUtils.BOARD_SIZE];
    private static final long[] WHITE_PAWN_ONLY_MOVES = new long[BoardUtils.BOARD_SIZE];
    private static final long[] WHITE_PAWN_CAPTURE = new long[BoardUtils.BOARD_SIZE];
    private static final long[] BLACK_PAWN_ONLY_MOVES = new long[BoardUtils.BOARD_SIZE];
    private static final long[] BLACK_PAWN_CAPTURE = new long[BoardUtils.BOARD_SIZE];
    private static final long[] ROOK_MASK = new long[BoardUtils.BOARD_SIZE];
    private static final long[] BISHOP_MASK = new long[BoardUtils.BOARD_SIZE];

    // builder receive bitboard, build all moves per square piece
    public PieceMovement(BitBoards bitBoards) {
        for (int i = 0; i < BoardUtils.BOARD_SIZE; i++) {
            ROOK_MOVES[i] = new HashMap<>();
            BISHOP_MOVES[i] = new HashMap<>();
        }
        this.bitBoards = bitBoards;
        BitBoardMoveGenerator generator = new BitBoardMoveGenerator();
        generator.generateKingMoves(KING_MOVES);
        generator.generateKnightMoves(KNIGHT_MOVES);
        generator.generatePawnMoves(WHITE_PAWN_ONLY_MOVES, WHITE_PAWN_CAPTURE, BoardUtils.WHITE);
        generator.generatePawnMoves(BLACK_PAWN_ONLY_MOVES, BLACK_PAWN_CAPTURE, BoardUtils.BLACK);
        generator.generateLinePieceMoves(ROOK_MOVES, BISHOP_MOVES);
        generator.generateMaskRook(ROOK_MASK);
        generator.generateMaskBishop(BISHOP_MASK);
    }

    // Given a position of a king, return the moves it can do as bitboard
    public long getKingMovement(byte position, boolean color) {
        // All the moves it can do without position occupied by same color pieces
        long moves = KING_MOVES[position];
        return moves & ~bitBoards.allPiecesWithSameColor(color);
    }

    // Given a position of a queen, return the moves it can do as bitboard
    public long getQueenMovement(byte position, boolean color) {
        // Queen moves like a rook and a bishop, and remove square with same color pieces
        return getRookMovement(position, color) | getBishopMovement(position, color) & ~bitBoards.allPiecesWithSameColor(color);
    }

    // Given a position of a rook, return the moves it can do as bitboard
    public long getRookMovement(byte position, boolean color) {
        // Calculate the bitBoard & mask val - key to move for that position, and remove square with same color pieces
        long keyVal = ROOK_MASK[position] & bitBoards.getAllPieces();
        long moves = ROOK_MOVES[position].get(keyVal);
        return moves & ~bitBoards.allPiecesWithSameColor(color);
    }

    // Given a position of a bishop, return the moves it can do as bitboard
    public long getBishopMovement(byte position, boolean color) {
        // Calculate the bitBoard & mask val - key to move for that position, and remove square with same color pieces
        long keyVal = BISHOP_MASK[position] & bitBoards.getAllPieces();
        long moves = BISHOP_MOVES[position].get(keyVal);
        return moves & ~bitBoards.allPiecesWithSameColor(color);
    }

    // Given a position of a knight, return the moves it can do as bitboard
    public long getKnightMovement(byte position, boolean color) {
        // All the moves it can do without position occupied by same color pieces
        long moves = KNIGHT_MOVES[position];
        return moves & ~bitBoards.allPiecesWithSameColor(color);
    }

    // Given a position of a pawn, return the moves it can do as bitboard
    public long getPawnMovement(byte position, boolean color) {
        // pawn can only capture if there is an enemy piece, and move only if its empty
        if (color == BoardUtils.WHITE)
            return ((WHITE_PAWN_CAPTURE[position] & bitBoards.getBlackPieces())) | ((WHITE_PAWN_ONLY_MOVES[position] & ~bitBoards.getAllPieces()));
        else
            return ((BLACK_PAWN_CAPTURE[position] & bitBoards.getWhitePieces())) | ((BLACK_PAWN_ONLY_MOVES[position] & ~bitBoards.getAllPieces()));
    }
}
