package Pieces;

// Class that represent a knight
public class Knight extends Piece{
    public Knight(byte square, boolean color) {
        super(square, color);
    }

    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long allSameColorPiecesBitBoard) {
        return pieceMovement.getKnightMovement(getSquare(),allSameColorPiecesBitBoard);
    }
}
