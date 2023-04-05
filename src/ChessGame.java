import Pieces.King;
import Pieces.LinePiece;
import Pieces.Piece;
import Utils.BoardUtils;

import java.util.LinkedList;

// TODO: pawn promotion
// A class that represent a game of chess
public class ChessGame {
    private boolean colorOfPlayersTurn;
    private LinkedList<Piece> pieceList;
    private final Piece[] pieceBoard = new Piece[BoardUtils.BOARD_SIZE];
    private long playerTurnPiecesBitBoard;
    private long allPiecesBitBoard;
    private final LinkedList<Long> treatingKingLines = new LinkedList<>();
    private boolean isKPlayerTurnKingChecked;

    private static final BoardUtils boardUtils = new BoardUtils();
    private SpecialMovesHandler specialMovesHandler;

    private static final int NORMAL = 0;
    private static final int CHECK = 1;
    private static final int DRAW = 2;
    private static final int CHECKMATE = 3;

    @SuppressWarnings("unused")
    // Initialize a game of chess using a fen
    public ChessGame(String fen) {
        FenTranslator translator = new FenTranslator(fen);
        getGameSetUp(translator);
    }

    // Initialize a game of at the default startup
    public ChessGame() {
        FenTranslator translator = new FenTranslator();
        getGameSetUp(translator);
    }

    // Retrieve all game setups (castling right, piece position, en passant target square) from fen
    // set up the board by converting the list into a board, and update the bitboards according to it
    private void getGameSetUp(FenTranslator translator) {
        colorOfPlayersTurn = translator.isWhiteTurnToPlay();
        specialMovesHandler = new SpecialMovesHandler(translator.canWhiteShortCastle(),
                translator.canWhiteLongCastle(),
                translator.canBlackShortCastle(),
                translator.canBlackLongCastle(),
                translator.getEnPassantSquareToCapture());

        pieceList = translator.getPieceList();
        convertPieceListToBoard();
        updateGameAttributes();
    }

    // Insert all the pieces in their correct position on the board from the list ofp pieces
    private void convertPieceListToBoard() {
        for (Piece piece : pieceList)
            pieceBoard[piece.getSquare()] = piece;
    }

    // Update the game attributes such as bitboards, treating line, and if the king is checked
    private void updateGameAttributes() {
        updateBitBoards();
        isKPlayerTurnKingChecked = isPlayerChecked(colorOfPlayersTurn);
        updateTreatingLines();
    }

    // update the value of allPiecesBitBoard and playerTurnPiecesBitBoard according to the piece list
    private void updateBitBoards() {
        allPiecesBitBoard = 0;
        playerTurnPiecesBitBoard = 0;
        // For each piece, if its same color as player turn add to bitboard of player turn, either way add to all pieces bitboard
        for (Piece piece : pieceList) {
            long pieceBitBoardPosition = piece.getSquareAsBitBoard();
            if (piece.getColor() == colorOfPlayersTurn)
                playerTurnPiecesBitBoard |= pieceBitBoardPosition;

            allPiecesBitBoard |= pieceBitBoardPosition;
        }
    }

    // Execute a move of a piece that its in the initial square, to the target square. return status after the move
    public int executeMove(byte currentSquare, byte targetSquare) {
        Piece pieceToMove = pieceBoard[currentSquare];
        if (specialMovesHandler.isSpecialMove(targetSquare, pieceToMove)) {
            specialMovesHandler.executeSpecialMove(currentSquare, targetSquare, pieceList, pieceBoard);
        } else
            boardUtils.updatePiecePosition(targetSquare, currentSquare, pieceBoard, pieceList);

        // Change the turn of the player, update bitboards and the special moves
        colorOfPlayersTurn = !colorOfPlayersTurn;
        specialMovesHandler.updateSpecialMoves(currentSquare, currentSquare, pieceToMove);
        updateGameAttributes();
        return getGameStatus();
    }

    // get the status of the game - normal, check, draw or checkmate
    private int getGameStatus() {
        // Check if the enemy player is checked, checkmated or if it's a draw
        if (isKPlayerTurnKingChecked) {
            if (doesPlayerHasLegalMovesToPlay(colorOfPlayersTurn))
                return CHECK;
            else
                return CHECKMATE;
        } else {
            if (!doesPlayerHasLegalMovesToPlay(colorOfPlayersTurn))
                return DRAW;
        }
        return NORMAL;
    }

    // Check if a player has a legal moves to do, if it has at least 1 return true
    private boolean doesPlayerHasLegalMovesToPlay(boolean playerColor) {
        for (Piece piece : pieceList) {
            if (piece.getColor() == playerColor) {
                long pieceMovement = getLegalMovesAsBitBoard(piece);
                if (pieceMovement != 0)
                    return true;
            }
        }
        return false;
    }

    // Given a color of a player, check if their king is checked, if yes return true
    private boolean isPlayerChecked(boolean playerColor) {
        // Find the players king, and all enemy players movement
        Piece playerKing = getKing(playerColor);
        long enemyMovement = getPlayerMoves(!playerColor), kingBitPosition;

        assert playerKing != null;
        kingBitPosition = boardUtils.getSquarePositionAsBitboardPosition(playerKing.getSquare());
        // Check if king is on one of the movement squares of enemy piece
        return (kingBitPosition & enemyMovement) != 0;
    }

    // Return the king with same color as player color
    private Piece getKing(boolean playerColor) {
        for (Piece piece : pieceList)
            if (piece.getColor() == playerColor && piece instanceof King)
                return piece;

        // there is always a king, but for compiler
        return null;
    }

    // Given a color of a player, return as bitboard all the moves it pieces can do
    private long getPlayerMoves(boolean playerColor) {
        long movementBitBoard = 0;
        // if the player color to get his movement is not the same as the play turn, remove from all allPiecesBitBoard all the same color pieces
        long sameColorPieceBitBoard = colorOfPlayersTurn == playerColor ? playerTurnPiecesBitBoard : allPiecesBitBoard & ~playerTurnPiecesBitBoard;
        // For each piece in piece list, added the movement of pieces with same color as player
        for (Piece piece : pieceList)
            if (piece.getColor() == playerColor)
                movementBitBoard |= piece.getMovesAsBitBoard(allPiecesBitBoard, sameColorPieceBitBoard);

        return movementBitBoard;
    }

    // Return as bitboard all the squares that are threatened by enemy player
    private long threatenedSquare() {
        // By removing the king, squares that are threatened beyond him will also be marked
        long bitBoardWithoutKing = allPiecesBitBoard & ~getKing(colorOfPlayersTurn).getSquareAsBitBoard();
        long movementBitBoard = 0;
        // For each piece in piece list, added the movement of pieces with same color as player
        for (Piece piece : pieceList)
            if (piece.getColor() == !colorOfPlayersTurn)
                // By setting friendly pieces to 0, pieces that are protected are also marked
                movementBitBoard |= piece.getMovesAsBitBoard(bitBoardWithoutKing, 0);

        return movementBitBoard;
    }

    // Given a square, get all the legal moves that piece can do as bitboard
    public long getMovesAsBitBoard(byte square) {
        Piece piece = pieceBoard[square];
        return getLegalMovesAsBitBoard(piece);
    }

    private long getLegalMovesAsBitBoard(Piece piece) {
        if (piece != null && colorOfPlayersTurn == piece.getColor()) {
            long pieceMoves = piece.getMovesAsBitBoard(allPiecesBitBoard, playerTurnPiecesBitBoard);
            long specialMoves = specialMovesHandler.getSpecialMoves(piece, getPlayerMoves(!colorOfPlayersTurn), allPiecesBitBoard);
            return removeIllegalMoves(pieceMoves | specialMoves, piece);
        }
        return 0;
    }

    // Given a piece and the bitboard of moves it can do, remove all moves that are illegal
    //  moves are king walking into a check or piece move that will case a check
    private long removeIllegalMoves(long bitBoardMoves, Piece pieceToMove) {
        long piecePositionAsBitBoard = pieceToMove.getSquareAsBitBoard();
        long enemyKingBitPosition = getKing(!colorOfPlayersTurn).getSquareAsBitBoard();
        // If king, remove all squares that enemy piece can go to
        if (pieceToMove instanceof King) {
            long threatenedSquare = threatenedSquare();
            return bitBoardMoves & ~threatenedSquare;
        }


        if (isKPlayerTurnKingChecked) {
            // Check if moving a piece won't expose the king to a check
            for (Long treatLine : treatingKingLines)
                if ((treatLine & (playerTurnPiecesBitBoard & ~enemyKingBitPosition)) == 0)
                    bitBoardMoves &= treatLine;
        } else {
            // Check if moving a piece won't expose the king to a check
            for (Long treatLine : treatingKingLines) {
                if ((piecePositionAsBitBoard & treatLine) != 0)
                    return bitBoardMoves & treatLine;
            }
        }
        return bitBoardMoves;
    }

    // Update the treating lines
    private void updateTreatingLines() {
        treatingKingLines.clear();
        Piece enemyKing = getKing(colorOfPlayersTurn);
        for (Piece piece : pieceList) {
            long treatKingLine = 0;
            // If piece is enemy piece and line piece, get its treating line
            if (piece.getColor() != colorOfPlayersTurn && piece instanceof LinePiece)
                treatKingLine = ((LinePiece) piece).getTreatLines(enemyKing.getSquare(), allPiecesBitBoard);

            // Only add if treating line is not 0
            if (treatKingLine != 0)
                treatingKingLines.add(treatKingLine);
        }
    }

    // Reset the game to default start up
    public void reset() {
        FenTranslator translator = new FenTranslator();
        getGameSetUp(translator);
    }

    // Return a copy of the list of all the pieces
    public LinkedList<Piece> getPieceList() {
        return new LinkedList<>(pieceList);
    }

}
