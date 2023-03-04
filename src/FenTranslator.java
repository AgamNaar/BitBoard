//A class to translate FEN into game settings
//TODO:  now just make from fen bitmaps

public class FenTranslator {

    private static final String CLASSIC_START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

    // FenType is the letter the piece represent - A/a depends on it color, lower case black
    private static final int KING = 'k';
    private static final int QUEEN = 'q';
    private static final int ROOK = 'r';
    private static final int KNIGHT = 'n';
    private static final int BISHOP = 'b';
    private static final int PAWN = 'p';
    private static final boolean WHITE = true;
    private static final int UPPER_CASE_OFFSET = 32;


    // Given a string that represent a FEN, set bitboards according to that string
    public void translateFen(BitBoards bitBoards, String fen) {
        getFenPieces(bitBoards, fen);
        //TODO: add other setting from fen
    }

    // set bitboards according to the classical chess opening
    public void translateFen(BitBoards bitBoards) {
        getFenPieces(bitBoards, CLASSIC_START_FEN);
        //TODO: add other setting from fen
    }

    // extract from the fen all the position of all the pieces, insert into bitBoards
    private void getFenPieces(BitBoards bitBoards, String fen) {
        int square = 0;

        // Run on the entire fen string
        for (int i = 0; i < fen.length(); i++) {
            char curr = fen.charAt(i);

            // If it's a digit skip that amount of squares
            if (Character.isDigit(curr))
                square = square + Character.getNumericValue(curr);
            else if (curr == '/') {// End of a row, don't advance square
                continue;
            } else { // It's a piece, get its fen type, and if its upper case its white, lower is black
                if (Character.isUpperCase(curr))
                    createPieceFen(curr, square, WHITE, bitBoards);
                else
                    createPieceFen(curr, square, !WHITE, bitBoards);

                square++;
            }
        }
    }

    // Add the current piece to its co-responding bitboard, by type and color, according to its square
    private void createPieceFen(int fenType, int square, boolean color, BitBoards bitBoards) {
        long bitPosition = squareToBitboard(square);

        if (color == WHITE) {
            fenType = fenType + UPPER_CASE_OFFSET;
            switch (fenType) {
                case KING -> bitBoards.setWhiteKing(bitBoards.getWhiteKing() | bitPosition);
                case QUEEN -> bitBoards.setWhiteQueens(bitBoards.getWhiteQueens() | bitPosition);
                case ROOK -> bitBoards.setWhiteRooks(bitBoards.getWhiteRooks() | bitPosition);
                case BISHOP -> bitBoards.setWhiteBishops(bitBoards.getWhiteBishops() | bitPosition);
                case KNIGHT -> bitBoards.setWhiteKnights(bitBoards.getWhiteKnights() | bitPosition);
                case PAWN -> bitBoards.setWhitePawns(bitBoards.getWhitePawns() | bitPosition);
            }
        } else {
            switch (fenType) {
                case KING -> bitBoards.setBlackKing(bitBoards.getBlackKing() | bitPosition);
                case QUEEN -> bitBoards.setBlackQueens(bitBoards.getBlackQueens() | bitPosition);
                case ROOK -> bitBoards.setBlackRooks(bitBoards.getBlackRooks() | bitPosition);
                case BISHOP -> bitBoards.setBlackBishops(bitBoards.getBlackBishops() | bitPosition);
                case KNIGHT -> bitBoards.setBlackKnights(bitBoards.getBlackKnights() | bitPosition);
                case PAWN -> bitBoards.setBlackPawns(bitBoards.getBlackPawns() | bitPosition);
            }
        }


    }

    // Convert position into bit position
    private long squareToBitboard(long square) {
        return 1L << square;
    }
}

