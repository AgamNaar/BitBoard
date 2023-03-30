package Pieces;

import Utils.BoardUtils;

// Abstract class that represent a piece, has 2 attributes
// Color - color of the piece (white/black - true/false)
// Square - square of the piece on the board
public abstract class Piece {

    private byte square;
    private final boolean color;

    protected static final PieceMovement pieceMovement = new PieceMovement();
    private static final BoardUtils utils = new BoardUtils();

    public Piece(byte square, boolean color) {
        this.square = square;
        this.color = color;
    }

    // Abstract method, given bitboard of all pieces on the board and bitboard of only same color piece
    // Return bitboard of all the move the piece can do (without checking if It's legal or not)
    public abstract long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard);

    // Return the position of the piece as bitboard
    public long getSquareAsBitBoard() {
        return utils.getSquarePositionAsBitboardPosition(square);
    }

    // getter and setter methods
    public byte getSquare() {
        return square;
    }

    public void setSquare(byte square) {
        this.square = square;
    }

    public boolean getColor() {
        return color;
    }

    public PieceMovement getPieceMovement() {
        return pieceMovement;
    }
}
