package gamelogic.pieces;

// Class that represent a king
public class King extends Piece {
    public King(byte square, boolean color) {
        super(square, color);
    }

    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard) {
        return pieceMovement.getKingMovement(getSquare(), sameColorPiecesBitBoard);
    }

    // King can't have a threat line, always return 0
    @Override
    public long getThreatLines(byte enemyKingSquare, Long boardBitBoard) {
        return 0;
    }

    @Override
    public int getPieceValue(long allPieceBitBoard, int gameStage) {
        return pieceEvaluation.evaluateKingPosition(this, gameStage);
    }
}
