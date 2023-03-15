import Pieces.King;
import Pieces.Pawn;
import Pieces.Piece;
import Pieces.Rook;
import Utils.BoardUtils;

// Class that handle all the special moves in a game
// Special moves are castling and en passant
public class SpecialMovesHandler {
    private boolean whiteShortCastle;
    private boolean whiteLongCastle;
    private boolean blackShortCastle;
    private boolean blackLongCastle;
    private byte enPassantTargetSquare;

    public SpecialMovesHandler(boolean whiteShortCastle, boolean whiteLongCastle, boolean blackShortCastle, boolean blackLongCastle, byte enPassantTargetSquare) {
        this.whiteShortCastle = whiteShortCastle;
        this.whiteLongCastle = whiteLongCastle;
        this.blackShortCastle = blackShortCastle;
        this.blackLongCastle = blackLongCastle;
        this.enPassantTargetSquare = enPassantTargetSquare;
    }

    // Given a piece that has been played, its current square and the square it will move to, update the special moves accordingly
    public void updateSpecialMoves(byte currentSquare, byte targetSquare, Piece pieceToMove) {
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

        // If a pawn moved, check if it moved 2 squares, to add en passant target square
        if (pieceToMove instanceof Pawn) {
            if (Math.abs(targetSquare - currentSquare) == 16)
                enPassantTargetSquare = (byte) ((targetSquare - currentSquare > 0) ? currentSquare + 8 : currentSquare - 8);
            else
                enPassantTargetSquare = -1;
        } else
            enPassantTargetSquare = -1;
    }

    // Receive a square and a piece, and check what special moves the pieces can do
    // A pawn can do en passant and a king can castle,
    public long getSpecialMoves(byte square, Piece piece) {
        return 0;
    }
}