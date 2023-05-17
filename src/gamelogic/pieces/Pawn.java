package gamelogic.pieces;

import gamelogic.GameLogicUtilities;

// Class that represent a pawn
public class Pawn extends Piece {
    public Pawn(byte square, boolean color) {
        super(square, color);
    }

    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard) {
        long enemyPiecesBitBoards = allPiecesBitBoard & ~sameColorPiecesBitBoard;
        return pieceMovement.getPawnMovement(getSquare(), getColor(), allPiecesBitBoard, enemyPiecesBitBoards);
    }

    // The threat line of a pawn is its square, if he threatens the king
    @Override
    public long getThreatLines(byte enemyKingSquare, Long boardBitBoard) {
        long pawnAttackSquares = getPawnAttackSquare();
        long enemyKingBitBoardPosition = GameLogicUtilities.getSquarePositionAsBitboardPosition(enemyKingSquare);
        if ((pawnAttackSquares & enemyKingBitBoardPosition) != 0)
            return getSquareAsBitBoard();

        return 0;
    }

    // Return the attack square of the pawn as bitboards
    public long getPawnAttackSquare() {
        return pieceMovement.getPawnCaptureSquare(getColor(), getSquare());
    }
}
