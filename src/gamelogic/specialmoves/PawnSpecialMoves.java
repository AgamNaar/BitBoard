package gamelogic.specialmoves;

import gamelogic.ChessGame;
import gamelogic.pieces.*;
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
    // Also can't do en passant if expose to check
    public long getMoves(Piece piece, LinkedList<Piece> pieceList, long allPieceBitboard, boolean colorOfPlayersTurn) {
        // If de move expose to check from a rook it's not valid, return 0
        if (doesExposeToRookCheck(piece.getSquare(), pieceList, allPieceBitboard, colorOfPlayersTurn))
            return 0;
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
    public void execute(byte currentSquare, byte targetSquare, Piece[] pieceBoard, LinkedList<Piece> pieceList, char typeOfPieceToPromoteTo) {
        if (targetSquare == enPassantTargetSquare)
            executeEnPassant(currentSquare, targetSquare, pieceBoard, pieceList);
        else
            executePromotion(currentSquare, targetSquare, pieceBoard, pieceList, typeOfPieceToPromoteTo);
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
    private void executePromotion(byte currentSquare, byte targetSquare, Piece[] pieceBoard, LinkedList<Piece> pieceList, char typeOfPieceToPromoteTo) {
        boolean colorOfPiece = pieceBoard[currentSquare].getColor();
        // Remove piece on target square and replace piece on current square to a queen
        pieceList.remove(pieceBoard[currentSquare]);
        pieceList.remove(pieceBoard[targetSquare]);
        pieceBoard[currentSquare] = null;
        Piece newPiece = createPieceForPromotion(targetSquare, colorOfPiece, typeOfPieceToPromoteTo);
        pieceBoard[targetSquare] = newPiece;
        pieceList.add(newPiece);
    }

    // According to the type of piece to promotion, create a new piece of that type with the given color and square
    private Piece createPieceForPromotion(byte targetSquare, boolean colorOfPiece, char typeOfPieceToPromoteTo) {
        Piece piece;
        // Create a piece according the type of piece
        if (ChessGame.PROMOTE_TO_QUEEN == typeOfPieceToPromoteTo)
            piece = new Queen(targetSquare, colorOfPiece);
        else if (ChessGame.PROMOTE_TO_ROOK == typeOfPieceToPromoteTo)
            piece = new Rook(targetSquare, colorOfPiece);
        else if (ChessGame.PROMOTE_TO_BISHOP == typeOfPieceToPromoteTo)
            piece = new Bishop(targetSquare, colorOfPiece);
        else if (ChessGame.PROMOTE_TO_KNIGHT == typeOfPieceToPromoteTo)
            piece = new Knight(targetSquare, colorOfPiece);
        else //default to queen
            piece = new Queen(targetSquare, colorOfPiece);

        return piece;
    }

    // Check if the target square is either en-passant square or promotion square
    public boolean isSpecialMove(byte targetSquare) {
        return targetSquare == enPassantTargetSquare || isPromotionSquare(targetSquare);
    }

    // Check if the pawn is on a promotion square, either or on the last row or the first row
    private boolean isPromotionSquare(byte targetSquare) {
        return targetSquare < LAST_ROW_BLACK || targetSquare > LAST_ROW_WHITE;
    }

    // While doing en-passant it can cuz a special situating where it will expose the king to a check from a rook, check that case
    private boolean doesExposeToRookCheck(byte currentSquare, LinkedList<Piece> pieceList, long allPieceBitboard, boolean colorOfPlayersTurn) {
        long rowMask = 0xffL << (utils.getRowOfSquare(currentSquare) * 8);
        Piece myKing = utils.getKing(colorOfPlayersTurn, pieceList);
        int offset = 1;
        long currentPosition;

        // If the king isn't in the same row as the pawn, it can't be exposed to check
        if ((myKing.getSquareAsBitBoard() & rowMask) == 0)
            return false;

        // Check if one of the enemy rooks is on the same row as the king as the pawn
        for (Piece piece : pieceList) {
            if (piece instanceof Rook && piece.getColor() != colorOfPlayersTurn && (piece.getSquareAsBitBoard() & rowMask) != 0) {
                int counter = 0;

                if (myKing.getSquare() > piece.getSquare())
                    offset = -1;

                if (offset == 1)
                    currentPosition = myKing.getSquareAsBitBoard() << offset;
                else
                    currentPosition = myKing.getSquareAsBitBoard() >> -offset;

                // Check how many piece there are between the king and the rook
                while (currentPosition != piece.getSquareAsBitBoard()) {
                    if ((currentPosition & allPieceBitboard) != 0)
                        counter++;

                    if (offset == 1)
                        currentPosition = currentPosition << offset;
                    else
                        currentPosition = currentPosition >> -offset;
                }

                // only 2 pieces, return true
                if (counter == 2)
                    return true;
            }
        }
        return false;
    }
}
