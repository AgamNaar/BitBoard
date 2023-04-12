package gameLogic.Pieces;

// Class that represent a pawn
public class Pawn extends Piece {
    public Pawn(byte square, boolean color) {
        super(square, color);
    }

    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard) {
        long enemyPiecesBitBoards = allPiecesBitBoard & ~sameColorPiecesBitBoard;
        return pieceMovement.getPawnMovement(getSquare(), getColor(), allPiecesBitBoard, enemyPiecesBitBoards);
    }

    @Override
    public long getThreatLines(byte enemyKingSquare, Long boardBitBoard) {
        long pawnAttackSquares = pieceMovement.getPawnCaptureSquare(getColor(),getSquare());
        long enemyKingBitBoardPosition =utils.getSquarePositionAsBitboardPosition(enemyKingSquare);
        if ((pawnAttackSquares & enemyKingBitBoardPosition) != 0)
            return getSquareAsBitBoard();

        return 0;
    }
}
