package gamelogic.specialmoves;

import gamelogic.pieces.Pawn;
import gamelogic.pieces.Piece;
import gamelogic.pieces.Queen;
import gamelogic.BoardUtils;

import java.util.LinkedList;

import static gamelogic.BoardUtils.BLACK_PAWN_MOVE_OFFSET;
import static gamelogic.BoardUtils.WHITE_PAWN_MOVE_OFFSET;

// Class that is responsible for updating, executing and giving the special moves a pawn can do (en-passant and promotion)
public class PawnSpecialMoves {

    private byte enPassantTargetSquare;

    private static final int NO_EN_PASSANT_TARGET_SQUARE = -1;
    private static final int PAWN_DOUBLE_MOVE_OFFSET = 16;
    private static final byte LAST_ROW_WHITE = 55;
    private static final byte LAST_ROW_BLACK = 8;
    private static final BoardUtils utils = new BoardUtils();

    public PawnSpecialMoves(byte enPassantTargetSquare) {
        this.enPassantTargetSquare = enPassantTargetSquare;
    }

    public PawnSpecialMoves(PawnSpecialMoves pawnSpecialMoves) {
        this.enPassantTargetSquare = pawnSpecialMoves.enPassantTargetSquare;
    }

    // Get pawn special moves, meaning if it can do an en passant
    // Can do en passant if target square of en passant is one of its possible attack squares (regardless if there is an enemy piece there)
    public long getMoves(Piece piece) {
        long enPassantTargetSquareBitBoard = utils.getSquarePositionAsBitboardPosition(enPassantTargetSquare);
        return piece.getPieceMovement().getPawnCaptureSquare(piece.getColor(), piece.getSquare()) & enPassantTargetSquareBitBoard;
    }

    // Update the en passant target square
    public void updateEnPassantSquare(byte currentSquare, byte targetSquare, Piece pieceToMove) {
        // If a pawn has moved, check if it moved 2 squares, meaning enemy pawn can take it using en passant
        if (pieceToMove instanceof Pawn) {
            // The target square is a square backwards from where it moved, if its white its -8 from its position if black +8
            if (Math.abs(targetSquare - currentSquare) == PAWN_DOUBLE_MOVE_OFFSET)
                enPassantTargetSquare = (byte) ((targetSquare - currentSquare > 0) ? currentSquare + WHITE_PAWN_MOVE_OFFSET : currentSquare + BLACK_PAWN_MOVE_OFFSET);
            else
                enPassantTargetSquare = NO_EN_PASSANT_TARGET_SQUARE;
        } else
            enPassantTargetSquare = NO_EN_PASSANT_TARGET_SQUARE;
    }

    // Given current square and target square of the piece, execute and update the board and list of pieces
    public void execute(byte currentSquare, byte targetSquare, Piece[] pieceBoard, LinkedList<Piece> pieceList) {
        if (targetSquare == enPassantTargetSquare)
            executeEnPassant(currentSquare, targetSquare, pieceBoard, pieceList);
        else
            executePromotion(currentSquare, targetSquare, pieceBoard, pieceList);
    }

    // Given the current square of the pawn who do en passant to target square, execute the move
    private void executeEnPassant(byte currentSquare, byte targetSquare, Piece[] pieceBoard, LinkedList<Piece> pieceList) {
        // The target en passant square + 1 square in the direction the pawn went, is where pawn is now
        byte enPassantPawnToCaptureSquare = (byte) (targetSquare + (pieceBoard[currentSquare].getColor() ? BLACK_PAWN_MOVE_OFFSET : WHITE_PAWN_MOVE_OFFSET));
        utils.updatePiecePosition(targetSquare, currentSquare, pieceBoard, pieceList);
        // Remove the captured pawn from list and board
        pieceList.remove(pieceBoard[enPassantPawnToCaptureSquare]);
        pieceBoard[enPassantPawnToCaptureSquare] = null;
    }

    // Execute promotion move
    private void executePromotion(byte currentSquare, byte targetSquare, Piece[] pieceBoard, LinkedList<Piece> pieceList) {
        boolean colorOfPiece = pieceBoard[currentSquare].getColor();
        // Remove piece on target square and replace piece on current square to a queen
        pieceList.remove(pieceBoard[currentSquare]);
        pieceList.remove(pieceBoard[targetSquare]);
        pieceBoard[currentSquare] = null;
        Piece newQueen = new Queen(targetSquare, colorOfPiece);
        pieceBoard[targetSquare] = newQueen;
        pieceList.add(newQueen);
    }

    // Check if the target square is either en-passant square or promotion square
    public boolean isSpecialMove(byte targetSquare) {
        return targetSquare == enPassantTargetSquare || isPromotionSquare(targetSquare);
    }

    // Check if the pawn is on a promotion square, either or on the last row or the first row
    private boolean isPromotionSquare(byte targetSquare) {
        return targetSquare < LAST_ROW_BLACK || targetSquare > LAST_ROW_WHITE;
    }
}
