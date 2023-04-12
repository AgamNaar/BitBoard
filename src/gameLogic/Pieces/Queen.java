package gameLogic.Pieces;

// Class that represent a queen
public class Queen extends Piece {
    public Queen(byte square, boolean color) {
        super(square, color);
    }

    @Override
    public long getThreatLines(byte enemyKingSquare, Long boardBitBoard) {
        return threateningLine.getQueenThreateningLine(getSquare(), enemyKingSquare, boardBitBoard);
    }

    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard) {
        return pieceMovement.getQueenMovement(getSquare(), allPiecesBitBoard, sameColorPiecesBitBoard);
    }
}
