package gamelogic;

import gamelogic.pieces.*;

import java.util.LinkedList;

/*
Forsyth–Edwards Notation (FEN) is a standard notation for describing a particular board position of a chess game.
The purpose of FEN is to provide all the necessary information to restart a game from a particular position
 Class that given a fen, represented as a string, translate it and extract all the info from it, the info:
 1. board set up - save as a list
 2. player turn - save as boolean, true meaning white turn
 3. castling rights - save as a boolean for each castling
 4. en peasant target square
 5. half move clock - not extracting yet
 6. full move clock - not extracting yet
 */
public class FenTranslator {

    private static final String CLASSIC_START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -";

    // FenType is the letter the piece represent in the fen- A/a depends on it color, lower case black
    private static final int KING = 'k';
    private static final int QUEEN = 'q';
    private static final int ROOK = 'r';
    private static final int KNIGHT = 'n';
    private static final int BISHOP = 'b';
    private static final int PAWN = 'p';
    private static final int UPPER_CASE_OFFSET = 32;

    private static final char EMPTY = '-';
    private static final char WHITE_TURN = 'w';
    private static final String SHORT_CASTLE_WHITE = "K";
    private static final String LONG_CASTLE_WHITE = "Q";
    private static final String SHORT_CASTLE_BLACK = "q";
    private static final String LONG_CASTLE_BLACK = "k";

    private final String fenStringValue;

    private boolean whiteTurnToPlay;
    private boolean whiteShortCastle;
    private boolean whiteLongCastle;
    private boolean blackShortCastle;
    private boolean blackLongCastle;
    private byte enPassantTargetSquare;
    private final LinkedList<Piece> pieceList = new LinkedList<>();

    // Builder that use the classical chess start up as its fen
    public FenTranslator() {
        fenStringValue = CLASSIC_START_FEN;
        translateFen();
    }

    // Builder that use a string (assume that its valid fen)
    public FenTranslator(String fenStringValue) {
        this.fenStringValue = fenStringValue;
        translateFen();
    }

    // Translate the fen and extract all the info from it
    private void translateFen() {
        int indexPosition = 0;
        indexPosition = extractPiecePlacement(indexPosition);
        whiteTurnToPlay = fenStringValue.charAt(indexPosition) == WHITE_TURN;
        indexPosition = indexPosition + 2;
        indexPosition = extractCastling(indexPosition);
        extractEnPassant(indexPosition);
    }

    // Extract the value of target en passant square
    private void extractEnPassant(int indexPosition) {
        if (fenStringValue.charAt(indexPosition) != EMPTY) {
            // Convert chess square (i.e c3,a4...), to number square
            int column = fenStringValue.charAt(indexPosition++) - 'h';
            int row = Character.getNumericValue(fenStringValue.charAt(indexPosition));
            enPassantTargetSquare = (byte) (column + (row * BoardUtils.BOARD_EDGE_SIZE));
        }
    }

    // Extract all the casting right for each player
    private int extractCastling(int indexPosition) {
        StringBuilder subFenString = new StringBuilder();
        if (fenStringValue.charAt(indexPosition) != EMPTY) {
            // Get substring of values of castling rights
            while (fenStringValue.charAt(indexPosition) != ' ') {
                subFenString.append(fenStringValue.charAt(indexPosition));
                indexPosition++;
            }

            // Each letter, K,Q,k,q represent a castling right, check what letters are in the substring
            whiteShortCastle = subFenString.toString().contains(SHORT_CASTLE_WHITE);
            whiteLongCastle = subFenString.toString().contains(LONG_CASTLE_WHITE);
            blackShortCastle = subFenString.toString().contains(SHORT_CASTLE_BLACK);
            blackLongCastle = subFenString.toString().contains(LONG_CASTLE_BLACK);
        }
        return ++indexPosition;
    }

    // extract from the fen all the pieces, their type, color and position, and save them as a list
    private int extractPiecePlacement(int indexPosition) {
        int square = BoardUtils.BOARD_SIZE - 1;

        // Run on the entire section of the fen that is tell the pieces position
        // its start with square 64 (top most left square), and each char tell what piece it is, number means empty square and '/' mean end of row
        // after counter ran from 63 to 0 we get all the pieces
        while (square > -1) {
            char currChar = fenStringValue.charAt(indexPosition);
            // If it's a digit skip that amount of squares
            if (Character.isDigit(currChar))
                square = square - Character.getNumericValue(currChar);
            else if (currChar != '/') { // It's a piece, get its fen type, and if its upper case its white, lower is black
                if (Character.isUpperCase(currChar))
                    insertFenCharIntoBitBoards(currChar, (byte) square, BoardUtils.WHITE);
                else
                    insertFenCharIntoBitBoards(currChar, (byte) square, BoardUtils.BLACK);
                square--;
            }
            indexPosition++;
        }
        return ++indexPosition;
    }

    // Add the current piece to the list, with the co-responding type to the Genotype, square and color
    private void insertFenCharIntoBitBoards(int fenType, byte square, boolean color) {
        Piece currentPiece = null;
        // Add offset if its white, making caps letter into small letter (i.e K to k)
        fenType = color ? fenType + UPPER_CASE_OFFSET : fenType;
        switch (fenType) {
            case KING -> currentPiece = new King(square, color);
            case QUEEN -> currentPiece = new Queen(square, color);
            case ROOK -> currentPiece = new Rook(square, color);
            case BISHOP -> currentPiece = new Bishop(square, color);
            case KNIGHT -> currentPiece = new Knight(square, color);
            case PAWN -> currentPiece = new Pawn(square, color);
        }
        pieceList.add(currentPiece);
    }

    public boolean isWhiteTurnToPlay() {
        return whiteTurnToPlay;
    }

    public boolean canWhiteShortCastle() {
        return whiteShortCastle;
    }

    public boolean canWhiteLongCastle() {
        return whiteLongCastle;
    }

    public boolean canBlackShortCastle() {
        return blackShortCastle;
    }

    public boolean canBlackLongCastle() {
        return blackLongCastle;
    }

    public byte getEnPassantSquareToCapture() {
        return enPassantTargetSquare;
    }

    public LinkedList<Piece> getPieceList() {
        return pieceList;
    }


}

