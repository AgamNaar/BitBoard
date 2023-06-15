package gamelogic.pieces;

import gamelogic.ChessGame;


// Responsible for evaluation pieces given their position and state of the board
// The power of lines pieces depends on their activity - how many squares they attack, and their position on the board
public class PieceEvaluation extends PieceEvaluationConstants {
    private static final PieceMovement pieceMovement = new PieceMovement();

    // Given a queen, all piece bitboard and gameStage, evaluate the power of the queen
    public int evaluateQueen(Piece queen, long allPieceBitBoard, int gameStage) {
        int multiplayer = gameStage == ChessGame.END_GAME ? QUEEN_MOVEMENT_MULTIPLIER_END : QUEEN_MOVEMENT_MULTIPLIER_EARLY;
        int totalValue = QUEEN_INITIAL_POWER + evaluateActivity(queen, multiplayer, allPieceBitBoard);
        totalValue += BISHOP_MAP[positionOnMapOfPiece(queen)];
        return totalValue;
    }

    // Given a rook, all piece bitboard and gameStage, evaluate the power of the rook
    public int evaluateRook(Piece rook, long allPiecesBitBoard, int gameStage) {
        int multiplayer = gameStage == ChessGame.END_GAME ? ROOK_MOVEMENT_MULTIPLIER_END : ROOK_MOVEMENT_MULTIPLIER_EARLY;
        int totalValue = ROOK_INITIAL_POWER + evaluateActivity(rook, multiplayer, allPiecesBitBoard);
        totalValue += ROOK_MAP[positionOnMapOfPiece(rook)];
        return totalValue;
    }

    // Given a bishop and all piece bitboard, evaluate the power of the bishop
    public int evaluateBishop(Piece bishop, long allPiecesBitBoard) {
        int totalValue = BISHOP_INITIAL_POWER + evaluateActivity(bishop, BISHOP_MOVEMENT_MULTIPLIER, allPiecesBitBoard);
        totalValue += BISHOP_MAP[bishop.getSquare()];
        return totalValue;
    }

    // Given a knight and stage game, evaluate the power of the bishop
    public int evaluateKnight(Piece knight) {
        return KNIGHT_INITIAL_POWER + KNIGHT_MAP[knight.getSquare()];
    }

    // Given a pawn, game stage and all piece bitboard, evaluate the power of the pawn
    public int evaluatePawn(Piece pawn, int gameStage) {
        if (gameStage == ChessGame.END_GAME)
            return PAWN_INITIAL_POWER + PAWN_MAP_END[positionOnMapOfPiece(pawn)];

        return PAWN_INITIAL_POWER + PAWN_MAP_EARLY[positionOnMapOfPiece(pawn)];
    }

    // Given a king, game stage and all piece bitboard, evaluate the power of the king
    public int evaluateKingPosition(Piece king, int gameStage) {
        if (gameStage == ChessGame.END_GAME)
            return -NEGATIVE_KING_MAP_END[positionOnMapOfPiece(king)];

        return -NEGATIVE_KING_MAP_EARLY[positionOnMapOfPiece(king)];
    }

    // If white return piece position, if black return piece 63-position
    private int positionOnMapOfPiece(Piece piece) {
        return piece.getColor() ? 63 - piece.getSquare() : piece.getSquare();
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
