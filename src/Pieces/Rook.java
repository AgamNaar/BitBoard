package Pieces;

// Class that represent a rook
public class Rook extends Piece {
    public Rook(byte square, boolean color) {
        super(square, color);
    }

    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long allSameColorPiecesBitBoard) {
        return pieceMovement.getRookMovement(getSquare(), allPiecesBitBoard, allSameColorPiecesBitBoard);
    }
}
