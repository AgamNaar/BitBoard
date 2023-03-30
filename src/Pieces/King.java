package Pieces;

// Class that represent a king
public class King extends Piece {
    public King(byte square, boolean color) {
        super(square, color);
    }

    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard) {
        return pieceMovement.getKingMovement(getSquare(), sameColorPiecesBitBoard);
    }
}
