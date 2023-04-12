package gameLogic.SpecialMoves;

import gameLogic.Pieces.King;
import gameLogic.Pieces.Piece;
import gameLogic.Pieces.Rook;
import gameLogic.BoardUtils;

import java.util.LinkedList;

// Class that is responsible for updating, executing and giving the special moves a king can do - castling short/long
public class CastlingSpecialMove {
    private boolean whiteShortCastle;
    private boolean whiteLongCastle;
    private boolean blackShortCastle;
    private boolean blackLongCastle;

    public static final BoardUtils utils = new BoardUtils();
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

    public CastlingSpecialMove(boolean whiteShortCastle, boolean whiteLongCastle, boolean blackShortCastle, boolean blackLongCastle) {
        this.whiteShortCastle = whiteShortCastle;
        this.whiteLongCastle = whiteLongCastle;
        this.blackShortCastle = blackShortCastle;
        this.blackLongCastle = blackLongCastle;
    }

    // Update all the castling right according to the piece that has been moved
    public void updateCastlingRights(byte currentSquare, Piece pieceToMove) {
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

    // Given a current square, target square, updateCastlingRights pieceBoard and piece square position according to the castling that need to be done
    // Short/long castling, and of which color
    public void execute(byte currentSquare, byte targetSquare, Piece[] pieceBoard, LinkedList<Piece> pieceList) {
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

    // Get king special moves square, for the king is castling short/long
    public long getMoves(Piece piece, long enemyMovement, long piecesBitBoard) {
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

    // Return if the target square is a square of castling
    public boolean isCastlingMove(byte targetSquare) {
        return (targetSquare == WHITE_SHORT_CASTLE_SQUARE && whiteShortCastle) ||
                (targetSquare == WHITE_LONG_CASTLE_SQUARE && whiteLongCastle) ||
                (targetSquare == BLACK_SHORT_CASTLE_SQUARE && blackShortCastle) ||
                (targetSquare == BLACK_LONG_CASTLE_SQUARE && blackLongCastle);
    }
}
