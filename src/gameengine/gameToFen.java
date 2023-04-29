package gameengine;

import gamelogic.pieces.*;

// Class that will translate a ChessGame object into a fen
public class gameToFen {

    public static String getFEN(Piece[] board) {
        StringBuilder fen = new StringBuilder();

        // Iterate through each row of the board from the top down
        for (int row = 7; row >= 0; row--) {
            int emptySquares = 0;

            // Iterate through each square in the row from left to right
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row * 8 + col];

                if (piece == null) {
                    // If the square is empty, increment the empty square count
                    emptySquares++;
                } else {
                    if (emptySquares > 0) {
                        // If there were empty squares before this piece, add their count to the FEN string
                        fen.append(emptySquares);
                        emptySquares = 0;
                    }

                    // Add the piece character to the FEN string
                    fen.append(getPieceChar(piece));
                }
            }

            if (emptySquares > 0) {
                // If there were empty squares at the end of the row, add their count to the FEN string
                fen.append(emptySquares);
            }

            if (row > 0) {
                // If this is not the last row, add a separator character to the FEN string
                fen.append("/");
            }
        }

        // Add the side to move, castling availability, en passant target square, and halfmove and fullmove counters to the FEN string
        fen.append(" w - - 0 1");

        return fen.toString();
    }

    public static char getPieceChar(Piece piece) {
        if (piece == null) {
            return ' ';
        }

        boolean isWhite = piece.getColor();

        // Check the piece type and return the corresponding FEN character
        if (piece instanceof Pawn) {
            return isWhite ? 'P' : 'p';
        } else if (piece instanceof Knight) {
            return isWhite ? 'N' : 'n';
        } else if (piece instanceof Bishop) {
            return isWhite ? 'B' : 'b';
        } else if (piece instanceof Rook) {
            return isWhite ? 'R' : 'r';
        } else if (piece instanceof Queen) {
            return isWhite ? 'Q' : 'q';
        } else if (piece instanceof King) {
            return isWhite ? 'K' : 'k';
        }

        // This should never happen, but just in case
        return ' ';
    }

}
