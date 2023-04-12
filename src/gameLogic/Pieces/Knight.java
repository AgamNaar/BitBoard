package gameLogic.Pieces;

// Class that represent a knight
public class Knight extends Piece {
    public Knight(byte square, boolean color) {
        super(square, color);
    }

    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard) {
        return pieceMovement.getKnightMovement(getSquare(), sameColorPiecesBitBoard);
    }

    @Override
    public long getThreatLines(byte enemyKingSquare, Long boardBitBoard) {
        long movement = getMovesAsBitBoard(0,0);
        long enemyKingBitBoardPosition =utils.getSquarePositionAsBitboardPosition(enemyKingSquare);
        if ((movement & enemyKingBitBoardPosition) != 0)
            return getSquareAsBitBoard();

        return 0;
    }
}
