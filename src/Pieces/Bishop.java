package Pieces;

// Class that represent a bishop
public class Bishop extends Piece {
    public Bishop(byte square, boolean color) {
        super(square, color);
    }

    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long allSameColorPiecesBitBoard) {
        return pieceMovement.getBishopMovement(getSquare(), allPiecesBitBoard, allSameColorPiecesBitBoard);
    }
}
