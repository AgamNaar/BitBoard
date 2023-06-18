package gameengine;

import gamelogic.pieces.Pawn;
import gamelogic.pieces.Piece;

/* Class that represent a move of a piece
currentPieceSquare - Current square of the piece
targetSquare - The square that the piece will go to
typeOfPieceToPromoteTo - The type of piece to promote to, default for a queen
pieceToMove - The piece that will do the move
pieceToMoveValue - The value of the piece
moveAssumedValue - The assumed value of how good will the move be
moveValue - The value of the move, meaning in the best case and depth given, what will be the eval of the board
 */
public class PieceMove {
    private static final byte FIRST_SQUARE_ON_SECOND_ROW = 8;
    private static final int LAST_SQUARE_ON_7TH_ROW = 55;

    private final int pieceToMoveValue;
    private final byte currentPieceSquare;
    private final byte targetSquare;

    private final char typeOfPieceToPromoteTo;
    private final Piece pieceToMove;

    private int moveAssumedValue;
    private int moveValue;

    public PieceMove(byte piecePosition, byte targetSquare, char typeOfPieceToPromoteTo, Piece piece,
                     int pieceToMoveValue) {
        this.currentPieceSquare = piecePosition;
        this.targetSquare = targetSquare;
        this.typeOfPieceToPromoteTo = typeOfPieceToPromoteTo;
        this.pieceToMoveValue = pieceToMoveValue;
        this.pieceToMove = piece;
    }

    // Empty constructor
    public PieceMove() {
        this.currentPieceSquare = 0;
        this.targetSquare = 0;
        this.typeOfPieceToPromoteTo = 0;
        this.pieceToMoveValue = 0;
        this.pieceToMove = new Pawn((byte) 1, true);
    }

    // Check if the target square is a promotion square
    public boolean isItPromotionMove() {
        return (targetSquare < FIRST_SQUARE_ON_SECOND_ROW || targetSquare > LAST_SQUARE_ON_7TH_ROW)
                && pieceToMove instanceof Pawn;
    }

    @Override
    public String toString() {
        // If it's a promotion move, add the type of piece to promote
        if (isItPromotionMove() && pieceToMove instanceof Pawn)
            return positionToNotation(currentPieceSquare) + positionToNotation(targetSquare) + typeOfPieceToPromoteTo;
        return positionToNotation(currentPieceSquare) + positionToNotation(targetSquare);
    }

    // Return string of the move with its move value
    public String toStringWithMoveValue() {
        return this + " move value: " + moveValue;
    }

    // Return the notation of the byte square
    private String positionToNotation(byte position) {
        String row = String.valueOf((position / 8) + 1);
        char column = (char) ((7 - (position % 8)) + 'a');
        return column + row;
    }

    // Getter method
    public byte getCurrentPieceSquare() {
        return currentPieceSquare;
    }

    public int getPieceToMoveValue() {
        return pieceToMoveValue;
    }

    public char getTypeOfPieceToPromoteTo() {
        return typeOfPieceToPromoteTo;
    }

    public int getMoveValue() {
        return moveValue;
    }

    public byte getTargetSquare() {
        return targetSquare;
    }

    public int getMoveAssumedValue() {
        return moveAssumedValue;
    }

    public Piece getPieceToMove() {
        return pieceToMove;
    }

    // Setter method
    public void setMoveValue(int moveValue) {
        this.moveValue = moveValue;
    }

    public void setMoveAssumedValue(int moveAssumedValue) {
        this.moveAssumedValue = moveAssumedValue;
    }
}
