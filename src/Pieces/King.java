package Pieces;

// Class that represent a king
public class King extends Piece {
    public King(byte square, boolean color) {
        super(square, color);
    }

    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long allSameColorPiecesBitBoard) {
        return pieceMovement.getKingMovement(getSquare(), allSameColorPiecesBitBoard);
    }


}
