package gamelogic.pieces;

// Responsible for evaluation pieces given their position and state of the board
// The power of lines pieces depends on their activity - how many squares they attack, and their position on the board
public class PieceEvaluation {

    private static final int BISHOP_MOVEMENT_MULTIPLIER = 5;
    private static final int ROOK_MOVEMENT_MULTIPLIER = 10;
    private static final int QUEEN_MOVEMENT_MULTIPLIER = 12;

    private static final int PAWN_INITIAL_POWER = 100;
    private static final int KNIGHT_INITIAL_POWER = 200;
    private static final int BISHOP_INITIAL_POWER = 200;
    private static final int ROOK_INITIAL_POWER = 350;
    private static final int QUEEN_INITIAL_POWER = 600;


    private static final int[] ROOK_MAP = {15, 15, 15, 15, 15, 15, 15, 15,
            5, -5, -15, -20, -20, -15, -5, 5,
            5, -5, -15, -20, -20, -15, -5, 5,
            5, -5, -15, -20, -20, -15, -5, 5,
            5, -5, -15, -20, -20, -15, -5, 5,
            5, -5, -15, -20, -20, -15, -5, 5,
            30, 30, 30, 30, 30, 30, 30, 30,
            20, 20, 20, 20, 20, 20, 20, 20};

    private static final int[] BISHOP_MAP = {15, 10, 5, 0, 0, 5, 10, 15,
            15, 15, 10, 5, 5, 10, 15, 15,
            0, 5, 10, 10, 10, 10, 5, 0,
            0, 5, 20, 15, 15, 20, 5, 0,
            0, 5, 20, 15, 15, 20, 5, 0,
            0, 5, 10, 10, 10, 10, 5, 0,
            15, 15, 10, 5, 5, 10, 15, 15,
            15, 10, 5, 0, 0, 5, 10, 15};

    private static final int[] KNIGHT_MAP = {0, 5, 15, 25, 25, 15, 5, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            10, 60, 80, 100, 100, 80, 60, 10,
            20, 80, 100, 130, 130, 100, 80, 20,
            20, 80, 100, 130, 130, 100, 80, 10,
            10, 60, 80, 100, 100, 80, 60, 20,
            0, 5, 15, 25, 25, 15, 5, 0,
            0, 0, 0, 0, 0, 0, 0, 0};

    private static final int[] PAWN_MAP = {0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            70, 60, 60, 60, 60, 60, 60, 70,
            55, 50, 50, 25, 25, 50, 50, 55,
            0, 0, 15, 20, 20, 0, 0, 0,
            0, 0, 10, 15, 15, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0};

    private static final int[] NEGATIVE_KING_MAP_EARLY = {600, 600, 600, 600, 600, 600, 600, 600,
            600, 600, 600, 600, 600, 600, 600, 600,
            600, 600, 600, 600, 600, 600, 600, 600,
            400, 400, 400, 400, 400, 400, 400, 400,
            200, 200, 200, 200, 200, 200, 200, 200,
            100, 100, 100, 100, 100, 100, 100, 100,
            50, 50, 50, 50, 50, 50, 50, 50,
            0, -20, -10, 5, 0, 5, -15, 0};

    private static final PieceMovement pieceMovement = new PieceMovement();


    // Given a queen, and all piece bitboard, evaluate the power of the queen
    public int evaluateQueen(Piece queen, long allPieceBitBoard) {
        int totalValue = QUEEN_INITIAL_POWER + evaluateActivity(queen, QUEEN_MOVEMENT_MULTIPLIER, allPieceBitBoard);
        totalValue += BISHOP_MAP[positionOnMapOfPiece(queen)];
        return totalValue;
    }

    // Given a rook, and all piece bitboard, evaluate the power of the rook
    public int evaluateRook(Piece rook, long allPiecesBitBoard) {
        int totalValue = ROOK_INITIAL_POWER + evaluateActivity(rook, ROOK_MOVEMENT_MULTIPLIER, allPiecesBitBoard);
        totalValue += ROOK_MAP[positionOnMapOfPiece(rook)];
        return totalValue;
    }

    // Given a bishop, and all piece bitboard, evaluate the power of the bishop
    public int evaluateBishop(Piece bishop, long allPiecesBitBoard) {
        int totalValue = BISHOP_INITIAL_POWER + evaluateActivity(bishop, BISHOP_MOVEMENT_MULTIPLIER, allPiecesBitBoard);
        totalValue += BISHOP_MAP[bishop.getSquare()];
        return totalValue;
    }

    // Given a bishop, and all piece bitboard, evaluate the power of the bishop
    public int evaluateKnight(Piece knight) {
        return KNIGHT_INITIAL_POWER + KNIGHT_MAP[knight.getSquare()];
    }

    // Given a pawn, and all piece bitboard, evaluate the power of the pawn
    public int evaluatePawn(Piece pawn) {
        return PAWN_INITIAL_POWER + PAWN_MAP[positionOnMapOfPiece(pawn)];
    }

    // Given a king, and all piece bitboard, evaluate the power of the king
    // TODO: add late game map
    public int evaluateKingPosition(Piece king) {
        return -NEGATIVE_KING_MAP_EARLY[positionOnMapOfPiece(king)];
    }

    // If white return piece position, if black return piece 63-position
    private int positionOnMapOfPiece(Piece piece) {
        return piece.getColor() ? piece.getSquare() : 63 - piece.getSquare();
    }


    // Given a piece, bitboard of all the piece and the piece movement multiplier
    // calculate how much does the piece is active
    private int evaluateActivity(Piece piece, int movementMultiplier, long allPiecesBitBoard) {
        byte piecePosition = piece.getSquare();
        int numberOfMoves = 0;
        if (piece instanceof Rook)
            numberOfMoves = pieceMovement.getNumberOfRookMovement(piecePosition, allPiecesBitBoard);
        else if (piece instanceof Bishop)
            numberOfMoves = pieceMovement.getNumberOfBishopMovement(piecePosition, allPiecesBitBoard);
        else if (piece instanceof Queen)
            numberOfMoves = pieceMovement.getNumberOfRookMovement(piecePosition, allPiecesBitBoard) +
                    pieceMovement.getNumberOfBishopMovement(piecePosition, allPiecesBitBoard);

        return numberOfMoves * movementMultiplier;
    }


}
