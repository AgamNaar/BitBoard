package Pieces;

// Class that represent a bishop
public class Bishop extends LinePiece {
    public Bishop(byte square, boolean color) {
        super(square, color);
    }

    @Override
    public long getTreatLines(byte enemyKingSquare, Long boardBitBoard) {
        return threateningLine.getBishopThreateningLine(getSquare(), enemyKingSquare, boardBitBoard);
    }

    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard) {
        return pieceMovement.getBishopMovement(getSquare(), allPiecesBitBoard, sameColorPiecesBitBoard);
    }
}
