package gamelogic.pieces;

import gamelogic.GameLogicUtilities;

import static gamelogic.GameLogicUtilities.EMPTY_BOARD;

// Class that represent a knight
public class Knight extends Piece {
    public Knight(byte square, boolean color) {
        super(square, color);
    }

    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard) {
        return pieceMovement.getKnightMovement(getSquare(), sameColorPiecesBitBoard);
    }

    // The threat line of a knight is its square, if he threatens the knight
    @Override
    public long getThreatLines(byte enemyKingSquare, Long boardBitBoard) {
        long movement = getMovesAsBitBoard(EMPTY_BOARD, EMPTY_BOARD);
        long enemyKingBitBoardPosition = GameLogicUtilities.getSquarePositionAsBitboardPosition(enemyKingSquare);
        if ((movement & enemyKingBitBoardPosition) != 0)
            return getSquareAsBitBoard();

        return 0;
    }

    //TODO: finish
    public int getPieceValue(long allPieceBitBoard) {
        return 0;
    }
}
