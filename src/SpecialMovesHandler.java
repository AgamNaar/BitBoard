import Pieces.*;
import Utils.BoardUtils;

import java.util.LinkedList;

import static Utils.BoardUtils.BLACK_PAWN_MOVE_OFFSET;
import static Utils.BoardUtils.WHITE_PAWN_MOVE_OFFSET;

// TODO: add pawn promotion
/*
Class that handle all the special moves in a game, Special moves are castling and en passant
has 4 public functions:
1. updateSpecialMoves: Given a piece and a square that the piece has moved to, update the castling rights and en passant square
2. getSpecialMoves: Given a piece, the possible enemy movement and bitboard of all pieces position
return as bitboard if the piece can can do en passant or castling
3. executeSpecialMove: Given the current square of the piece and its target square,
execute the special move and update the list and board of pieces - assume that the target square was given from getSpecialMoves
4.  isSpecialMove: Given a target square, check if it's a square of castling or en passant move - if yes return true
 */
public class SpecialMovesHandler {

    private static final byte LAST_ROW_WHITE = 55;
    private static final byte LAST_ROW_BLACK = 8;
    private boolean whiteShortCastle;
    private boolean whiteLongCastle;
    private boolean blackShortCastle;
    private boolean blackLongCastle;
    private byte enPassantTargetSquare;

    private static final int PAWN_DOUBLE_MOVE_OFFSET = 16;

    public static final byte INITIAL_WHITE_KING_SQUARE = 3;
    public static final byte INITIAL_BLACK_KING_SQUARE = 59;
    public static final byte INITIAL_WHITE_ROOK_SQUARE_SHORT = 0;
    public static final byte INITIAL_WHITE_ROOK_SQUARE_LONG = 7;
    public static final byte INITIAL_BLACK_ROOK_SQUARE_SHORT = 56;
    public static final byte INITIAL_BLACK_ROOK_SQUARE_LONG = 63;

    private static final long SHORT_CASTLE_SHOULD_BE_NOT_ATTACKED_SQUARE_BITBOARD = 0b1111;
    private static final long SHORT_CASTLE_SHOULD_BE_EMPTY_SQUARE_BITBOARD = 0b0110;
    private static final long SHORT_CASTLING_SQUARE_BITBOARD = 0b10;
    private static final long LONG_CASTLE_SHOULD_BE_NOT_ATTACKED_SQUARE_BITBOARD = 0b11111000;
    private static final long LONG_CASTLE_SHOULD_BE_EMPTY_SQUARE_BITBOARD = 0b01110000;
    private static final long LONG_CASTLING_SQUARE_BITBOARD = 0b100000;
    private static final int BLACK_CASTLING_SQUARE_OFFSET = 56;
    private static final byte SHORT_CASTLE_ROOK_OFFSET_FROM_TARGET_SQUARE = 1;
    private static final byte LONG_CASTLE_ROOK_OFFSET_FROM_TARGET_SQUARE = -1;

    private static final long WHITE_SHORT_CASTLE_SQUARE = 1;
    private static final long WHITE_LONG_CASTLE_SQUARE = 5;
    private static final long BLACK_SHORT_CASTLE_SQUARE = 57;
    private static final long BLACK_LONG_CASTLE_SQUARE = 61;

    private static final int NO_EN_PASSANT_TARGET_SQUARE = -1;

    private static final BoardUtils utils = new BoardUtils();

    // Builder
    public SpecialMovesHandler(boolean whiteShortCastle, boolean whiteLongCastle, boolean blackShortCastle, boolean blackLongCastle, byte enPassantTargetSquare) {
        this.whiteShortCastle = whiteShortCastle;
        this.whiteLongCastle = whiteLongCastle;
        this.blackShortCastle = blackShortCastle;
        this.blackLongCastle = blackLongCastle;
        this.enPassantTargetSquare = enPassantTargetSquare;
    }

    // Given a piece that has been played, the square it has moved to, update the special moves accordingly
    public void updateSpecialMoves(byte currentSquare, byte targetSquare, Piece pieceToMove) {
        updateCastling(currentSquare, pieceToMove);
        updateEnPassant(currentSquare, targetSquare, pieceToMove);
    }

    // given a piece, check what special moves it can do. A pawn can do en passant and a king can castle
    public long getSpecialMoves(Piece piece, long enemyMovement, long piecesBitBoard) {
        if (piece instanceof King)
            return getKingSpecialMoves(piece, enemyMovement, piecesBitBoard);

        if (piece instanceof Pawn)
            return getPawnSpecialMoves(piece);

        return 0;
    }

    // Execute the special to target square according to the piece and the square, update the board and list accordingly
    public void executeSpecialMove(byte currentSquare, byte targetSquare, LinkedList<Piece> pieceList, Piece[] pieceBoard) {
        if (pieceBoard[currentSquare] instanceof King)
            executeCastling(currentSquare, targetSquare, pieceBoard, pieceList);
        else {
            if (targetSquare == enPassantTargetSquare)
                executeEnPassant(currentSquare, targetSquare, pieceBoard, pieceList);
            else
                executePromotion(currentSquare, targetSquare, pieceBoard, pieceList);
        }
    }

    // Return whatever or not if target square is a special move square, meaning moving there is castling or en passant move
    public boolean isSpecialMove(byte targetSquare, Piece pieceToMove) {
        if (pieceToMove instanceof Pawn) {
            return targetSquare == enPassantTargetSquare || isPromotionSquare(targetSquare);
        }

        if (pieceToMove instanceof King)
            return (targetSquare == WHITE_SHORT_CASTLE_SQUARE && whiteShortCastle) ||
                    (targetSquare == WHITE_LONG_CASTLE_SQUARE && whiteLongCastle) ||
                    (targetSquare == BLACK_SHORT_CASTLE_SQUARE && blackShortCastle) ||
                    (targetSquare == BLACK_LONG_CASTLE_SQUARE && blackLongCastle);

        return false;
    }

    // Check if the pawn is on a promotion square, either or on the last row or the first row
    private boolean isPromotionSquare(byte targetSquare) {
        return targetSquare < LAST_ROW_BLACK || targetSquare > LAST_ROW_WHITE;
    }

    // Update the en passant target square
    private void updateEnPassant(byte currentSquare, byte targetSquare, Piece pieceToMove) {
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

    // Update all the castling right according to the piece that has been moved
    private void updateCastling(byte currentSquare, Piece pieceToMove) {
        // If a rook moved from its initial position, disable that rook side castling
        if (pieceToMove instanceof Rook) {
            switch (currentSquare) {
                case INITIAL_WHITE_ROOK_SQUARE_SHORT -> whiteShortCastle = false;
                case INITIAL_WHITE_ROOK_SQUARE_LONG -> whiteLongCastle = false;
                case INITIAL_BLACK_ROOK_SQUARE_SHORT -> blackShortCastle = false;
                case INITIAL_BLACK_ROOK_SQUARE_LONG -> blackLongCastle = false;
            }
        }

        // If the king move, disable all of its castling right
        if (pieceToMove instanceof King) {
            if (currentSquare == INITIAL_WHITE_KING_SQUARE) {
                whiteShortCastle = false;
                whiteLongCastle = false;
            }
            if (currentSquare == INITIAL_BLACK_KING_SQUARE) {
                blackShortCastle = false;
                blackLongCastle = false;
            }
        }
    }

    // Get pawn special moves, meaning if it can do an en passant
    // Can do en passant if target square of en passant is one of its possible attack squares (regardless if there is an enemy piece there)
    private long getPawnSpecialMoves(Piece piece) {
        long enPassantTargetSquareBitBoard = utils.getSquarePositionAsBitboardPosition(enPassantTargetSquare);
        return piece.getPieceMovement().getPawnCaptureSquare(piece.getColor(), piece.getSquare()) & enPassantTargetSquareBitBoard;
    }

    // Get king special moves square, for the king is castling short/long
    private long getKingSpecialMoves(Piece piece, long enemyMovement, long piecesBitBoard) {
        long specialMoves = 0;
        if (piece.getColor() == BoardUtils.WHITE) {
            if (whiteShortCastle && checkShortCastling(piecesBitBoard, enemyMovement, 0))
                specialMoves |= SHORT_CASTLING_SQUARE_BITBOARD;

            if (whiteLongCastle && checkLongCastling(piecesBitBoard, enemyMovement, 0))
                specialMoves |= LONG_CASTLING_SQUARE_BITBOARD;
        } else {
            if (blackShortCastle && checkShortCastling(piecesBitBoard, enemyMovement, BLACK_CASTLING_SQUARE_OFFSET))
                specialMoves |= SHORT_CASTLING_SQUARE_BITBOARD << BLACK_CASTLING_SQUARE_OFFSET;

            if (blackLongCastle && checkLongCastling(piecesBitBoard, enemyMovement, BLACK_CASTLING_SQUARE_OFFSET))
                specialMoves |= LONG_CASTLING_SQUARE_BITBOARD << BLACK_CASTLING_SQUARE_OFFSET;
        }
        return specialMoves;
    }

    // Check that the square that should be empty for short castling are empty and the square that need to be no threatened are not threaded
    // Offset the square if it's black to the last rank
    private boolean checkShortCastling(long piecesBitBoard, long enemyMovementBitBoard, int offset) {
        return (piecesBitBoard & SHORT_CASTLE_SHOULD_BE_EMPTY_SQUARE_BITBOARD << offset) == 0 && (SHORT_CASTLE_SHOULD_BE_NOT_ATTACKED_SQUARE_BITBOARD << offset & enemyMovementBitBoard) == 0;
    }

    // Check that the square that should be empty for long castling are empty and the square that need to be no threatened are not threaded
    // Offset the square if it's black to the last rank
    private boolean checkLongCastling(long piecesBitBoard, long enemyMovementBitBoard, int offset) {
        return (piecesBitBoard & LONG_CASTLE_SHOULD_BE_EMPTY_SQUARE_BITBOARD << offset) == 0 && (LONG_CASTLE_SHOULD_BE_NOT_ATTACKED_SQUARE_BITBOARD << offset & enemyMovementBitBoard) == 0;

    }

    // Given a current square, target square, update pieceBoard and piece square position according to the castling that need to be done
    // Short/long castling, and of which color
    private void executeCastling(byte currentSquare, byte targetSquare, Piece[] pieceBoard, LinkedList<Piece> pieceList) {
        Piece king = pieceBoard[currentSquare];
        int rookPosition, rookTargetPosition;
        // If current square (the king position) is larger than his target square, its short castling
        if (currentSquare > targetSquare) {
            rookPosition = INITIAL_WHITE_ROOK_SQUARE_SHORT;
            rookTargetPosition = targetSquare + SHORT_CASTLE_ROOK_OFFSET_FROM_TARGET_SQUARE;
        } else {
            rookPosition = INITIAL_WHITE_ROOK_SQUARE_LONG;
            rookTargetPosition = targetSquare + LONG_CASTLE_ROOK_OFFSET_FROM_TARGET_SQUARE;
        }
        // Check if you need to add an offset, rook position is white rook position so if black need to add offset
        int offset = king.getColor() ? 0 : BLACK_CASTLING_SQUARE_OFFSET;

        utils.updatePiecePosition(targetSquare, currentSquare, pieceBoard, pieceList);
        utils.updatePiecePosition((byte) rookTargetPosition, (byte) (rookPosition + offset), pieceBoard, pieceList);
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
}