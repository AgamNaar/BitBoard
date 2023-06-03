package gamelogic.pieces;

// Responsible for evaluation pieces given their position and state of the board
// The power of lines pieces depends on their activity - how many squares they attack, and their postion on the board
public class PieceEvaluation {

    private static final int BISHOP_MOVEMENT_MULTIPLIER = 5;
    private static final int ROOK_MOVEMENT_MULTIPLIER = 10;
    private static final int QUEEN_MOVEMENT_MULTIPLIER = 12;
    private static final int BISHOP_INITIAL_POWER = 200;
    private static final int ROOK_INITIAL_POWER = 350;
    private static final int QUEEN_INITIAL_POWER = 350;

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

    private static final PieceMovement pieceMovement = new PieceMovement();

    // Given a queen, and all piece bitboard, evaluate the power of the queen
    public int evaluateQueen(Piece queen, long allPieceBitBoard) {
        int offset = queen.getColor() ? 0 : 63;
        int totalValue = QUEEN_INITIAL_POWER + evaluateActivity(queen, QUEEN_MOVEMENT_MULTIPLIER, allPieceBitBoard);
        totalValue += BISHOP_MAP[offset - queen.getSquare()];
        return totalValue;
    }

    // Given a rook, and all piece bitboard, evaluate the power of the rook
    public int evaluateRook(Piece rook, long allPiecesBitBoard) {
        int offset = rook.getColor() ? 0 : 63;
        int totalValue = ROOK_INITIAL_POWER + evaluateActivity(rook, ROOK_MOVEMENT_MULTIPLIER, allPiecesBitBoard);
        totalValue += ROOK_MAP[offset - rook.getSquare()];
        return totalValue;
    }

    // Given a bishop, and all piece bitboard, evaluate the power of the bishop
    public int evaluateBishop(Piece bishop, long allPiecesBitBoard) {
        int totalValue = BISHOP_INITIAL_POWER + evaluateActivity(bishop, BISHOP_MOVEMENT_MULTIPLIER, allPiecesBitBoard);
        totalValue += BISHOP_MAP[bishop.getSquare()];
        return totalValue;
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
