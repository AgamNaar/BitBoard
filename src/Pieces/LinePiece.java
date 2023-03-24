package Pieces;

// Abstract class that extend piece and represent pieces that move in a line (bishop,rook,queen)
public abstract class LinePiece extends Piece {
    public LinePiece(byte square, boolean color) {
        super(square, color);
    }

    // Given the enemy king and the set-up of the board, return the treat line of this piece on a king as bitboard
    // treat line - if an enemy piece block the piece from threading the king, the treat line will be all the square from the piece to the king as bitboards
    // if more than 1 piece block a piece from treating, or the king is not even on the attack line, return 0
    public abstract long getTreatLines(Piece enemyKing, Long boardBitBoard);

    // Return the treating line of a bishop
    protected long getTreatLinesBishop(Piece enemyKing, Long boardBitBoard) {
        //TODO: finish
        return 0;
    }

    // Return the treating line a rook
    protected long getTreatLinesRook(Piece enemyKing, Long boardBitBoard) {
        // TODO: finish
        return 0;
    }

}
