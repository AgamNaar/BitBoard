package Pieces;

// Class that represent a queen
public class Queen extends Piece {
    public Queen(byte square, boolean color) {
        super(square, color);
    }

    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long allSameColorPiecesBitBoard) {
        return pieceMovement.getQueenMovement(getSquare(), allPiecesBitBoard, allSameColorPiecesBitBoard);
    }
}
