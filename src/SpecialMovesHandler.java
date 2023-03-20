import Pieces.King;
import Pieces.Pawn;
import Pieces.Piece;
import Pieces.Rook;
import Utils.BoardUtils;

import java.util.LinkedList;

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
    private boolean whiteShortCastle;
    private boolean whiteLongCastle;
    private boolean blackShortCastle;
    private boolean blackLongCastle;
    private byte enPassantTargetSquare;

    private static final long SHORT_CASTLE_SHOULD_BE_NOT_ATTACKED_SQUARE_BITBOARD = 0b1111;
    private static final long SHORT_CASTLE_SHOULD_BE_EMPTY_SQUARE_BITBOARD = 0b0110;
    private static final long SHORT_CASTLING_SQUARE_BITBOARD = 0b10;
    private static final long LONG_CASTLE_SHOULD_BE_NOT_ATTACKED_SQUARE_BITBOARD = 0b11111000;
    private static final long LONG_CASTLE_SHOULD_BE_EMPTY_SQUARE_BITBOARD = 0b01110000;
    private static final long LONG_CASTLING_SQUARE_BITBOARD = 0b100000;
    private static final int BLACK_CASTLING_SQUARE_OFFSET = 56;

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

    // Given a piece that has been played,  the square it has moved to, update the special moves accordingly
    public void updateSpecialMoves(byte targetSquare, Piece pieceToMove) {
        byte currentSquare = pieceToMove.getSquare();
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
            executeCastling(currentSquare, targetSquare, pieceBoard);
        else
            executeEnPassant(currentSquare, targetSquare, pieceBoard, pieceList);
    }

    // Return whatever or not if target square is a special move square, meaning moving there is castling or en passant move
    public boolean isSpecialMove(byte targetSquare) {
        return targetSquare == WHITE_LONG_CASTLE_SQUARE || targetSquare == WHITE_SHORT_CASTLE_SQUARE
                || targetSquare == BLACK_LONG_CASTLE_SQUARE || targetSquare == BLACK_SHORT_CASTLE_SQUARE
                || targetSquare == enPassantTargetSquare;
    }

    // Update the en passant target square
    private void updateEnPassant(byte currentSquare, byte targetSquare, Piece pieceToMove) {
        // If a pawn has moved, check if it moved 2 squares, meaning enemy pawn can take it using en passant
        if (pieceToMove instanceof Pawn) {
            if (Math.abs(targetSquare - currentSquare) == 16)
                // The target square is a square backwards from where it moved, if its white its -8 from its position if black +8
                enPassantTargetSquare = (byte) ((targetSquare - currentSquare > 0) ? currentSquare + 8 : currentSquare - 8);
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
                case BoardUtils.INITIAL_WHITE_ROOK_SQUARE_SHORT -> whiteShortCastle = false;
                case BoardUtils.INITIAL_WHITE_ROOK_SQUARE_LONG -> whiteLongCastle = false;
                case BoardUtils.INITIAL_BLACK_ROOK_SQUARE_SHORT -> blackShortCastle = false;
                case BoardUtils.INITIAL_BLACK_ROOK_SQUARE_LONG -> blackLongCastle = false;
            }
        }

        // If the king move, disable all of its castling right
        if (pieceToMove instanceof King) {
            if (currentSquare == BoardUtils.INITIAL_WHITE_KING_SQUARE) {
                whiteShortCastle = false;
                whiteLongCastle = false;
            }

            if (currentSquare == BoardUtils.INITIAL_BLACK_KING_SQUARE) {
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
    private void executeCastling(byte currentSquare, byte targetSquare, Piece[] pieceBoard) {
        Piece king = pieceBoard[currentSquare];
        // If target square is larger than current square, it's a long castling, according to castling get the right rook position
        byte rookPosition = targetSquare > currentSquare ? BoardUtils.INITIAL_WHITE_ROOK_SQUARE_LONG : BoardUtils.INITIAL_WHITE_ROOK_SQUARE_SHORT;
        // According to the castling type, get the offset for the target square
        int rookTargetSquareOffset = targetSquare > currentSquare ? -1 : 1;
        // Check if to a need to add offset
        int offset = king.getColor() ? 0 : BLACK_CASTLING_SQUARE_OFFSET;
        Piece rookToMove = pieceBoard[rookPosition + offset];

        // Update the position of the king
        king.setSquare(targetSquare);
        pieceBoard[targetSquare] = king;
        //TODO: better way to clean previous king cell in the board array
        pieceBoard[currentSquare] = pieceBoard[currentSquare - rookTargetSquareOffset];

        // Update the position of the rook,
        rookToMove.setSquare((byte) (targetSquare + rookTargetSquareOffset));
        pieceBoard[targetSquare + rookTargetSquareOffset] = rookToMove;
        //TODO: better way to clean previous rook cell in the board array
        pieceBoard[rookPosition + offset] = pieceBoard[currentSquare];
    }

    private void executeEnPassant(byte currentSquare, byte targetSquare, Piece[] pieceBoard, LinkedList<Piece> pieceList) {
        //TODO: finish
    }
}