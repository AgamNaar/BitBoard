package Pieces;

// Class that represent a queen
public class Queen extends LinePiece {
    public Queen(byte square, boolean color) {
        super(square, color);
    }

    @Override
    public long getTreatLines(Piece enemyKing, Long boardBitBoard) {
        return getTreatLinesBishop(enemyKing, boardBitBoard) & getTreatLinesRook(enemyKing, boardBitBoard);
    }

    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long allSameColorPiecesBitBoard) {
        return pieceMovement.getQueenMovement(getSquare(), allPiecesBitBoard, allSameColorPiecesBitBoard);
    }
}
