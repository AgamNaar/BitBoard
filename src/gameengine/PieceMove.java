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

    @Override
    public String toString() {
        return positionToNotation(piecePosition)+positionToNotation(targetSquare);
    }

    private String positionToNotation(byte position) {
        String row = String.valueOf((position/8)+1);
        char colum = (char) ((7-(position%8))+'a');
        return colum+row;
    }
}
