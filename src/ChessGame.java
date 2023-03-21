import Pieces.King;
import Pieces.Piece;
import Utils.BoardUtils;

import java.util.LinkedList;

// A class that represent a game of chess
public class ChessGame {
    private boolean colorOfPlayersTurn;
    private LinkedList<Piece> pieceList;
    private final Piece[] pieceBoard = new Piece[BoardUtils.BOARD_SIZE];
    private long playerTurnPiecesBitBoard;
    private long allPiecesBitBoard;

    private static final BoardUtils boardUtils = new BoardUtils();
    private SpecialMovesHandler specialMovesHandler;

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
        updateBitBoards();
    }

    // Insert all the pieces in their correct position on the board from the list ofp pieces
    private void convertPieceListToBoard() {
        for (Piece piece : pieceList)
            pieceBoard[piece.getSquare()] = piece;
    }

    // update the value of allPiecesBitBoard and playerTurnPiecesBitBoard according to the piece list
    private void updateBitBoards() {
        allPiecesBitBoard = 0;
        playerTurnPiecesBitBoard = 0;
        // For each piece, add its position to its appropriate board
        for (Piece piece : pieceList) {
            long pieceBitBoardPosition = boardUtils.getSquarePositionAsBitboardPosition(piece.getSquare());
            if (piece.getColor() == colorOfPlayersTurn)
                playerTurnPiecesBitBoard |= pieceBitBoardPosition;

            allPiecesBitBoard |= pieceBitBoardPosition;
        }
    }

    // Execute a move of a piece that its in the initial square, to the target square
    public void executeMove(byte currentSquare, byte targetSquare) {
        // TODO: still under work
        Piece pieceToMove = pieceBoard[currentSquare];
        if (specialMovesHandler.isSpecialMove(targetSquare)) {
            specialMovesHandler.executeSpecialMove(currentSquare, targetSquare, pieceList, pieceBoard);
        } else
            boardUtils.updatePiecePosition(targetSquare, currentSquare, pieceBoard, pieceList);

        // Change the turn of the player, update bitboards and the special moves
        colorOfPlayersTurn = !colorOfPlayersTurn;
        specialMovesHandler.updateSpecialMoves(targetSquare, currentSquare,pieceToMove);
        updateBitBoards();
        afterTurnCheck();
    }

    // At the end of a turn, check the status of the game (i.e. is it checked, check mated or a draw)
    private void afterTurnCheck() {
        // Check if the enemy player is checked, checkmated or if it's a draw
        if (isPlayerChecked(colorOfPlayersTurn)) {
            if (isPlayerCheckMated(colorOfPlayersTurn))
                System.out.println("check mate");
            else
                System.out.println("check");
        } else {
            if (getPlayerMoves(colorOfPlayersTurn) == 0)
                System.out.println("draw");
        }
    }

    // If player has no moves to do and checked, he is checkmated
    private boolean isPlayerCheckMated(boolean playerColor) {
        return getPlayerMoves(playerColor) == 0;
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

    // Given a square, get all the legal moves that piece can do as bitboard
    public long getMovesAsBitBoards(byte square) {
        Piece piece = pieceBoard[square];
        // Check if there is a piece on that square, and is the same color as the player who's playing
        if (piece != null && colorOfPlayersTurn == piece.getColor()) {
            long pieceMoves = piece.getMovesAsBitBoard(allPiecesBitBoard, playerTurnPiecesBitBoard);
            long specialMoves = specialMovesHandler.getSpecialMoves(piece, getPlayerMoves(!colorOfPlayersTurn), allPiecesBitBoard);
            // TODO: check legal moves (i.e moving a piece wont cuz king to be checked)
            return pieceMoves | specialMoves;
        }
        return 0;
    }

    public LinkedList<Piece> getPieceList() {
        return pieceList;
    }
}
