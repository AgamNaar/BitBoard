package gameLogic.Pieces;

// Abstract class that extend piece and represent a pieces that move in a line (bishop,rook,queen)
public abstract class LinePiece extends Piece {

    protected static final PieceThreateningLine threateningLine = new PieceThreateningLine();

    public LinePiece(byte square, boolean color) {
        super(square, color);
    }

    /*
     Given the enemy king and the board as bit board, return the treat line of this piece on a king as bitboard
     treat line - if the piece threat the king, or if an enemy piece block the piece from threading the king
     the treat line will be all the square from the piece to the king as bitboards
     if more than 1 piece block a piece from treating, or the king is not even on the attack line, return 0
     */
    public abstract long getTreatLines(byte enemyKingSquare, Long boardBitBoard);
}
