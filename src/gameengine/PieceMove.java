package gameengine;

public class PieceMove {
    private final byte piecePosition;
    private final byte targetSquare;

    public byte getPiecePosition() {
        return piecePosition;
    }

    public byte getTargetSquare() {
        return targetSquare;
    }

    public PieceMove(byte piecePosition, byte targetSquare) {
        this.piecePosition = piecePosition;
        this.targetSquare = targetSquare;
    }
}
