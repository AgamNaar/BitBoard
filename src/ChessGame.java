import Pieces.King;
import Pieces.Pawn;
import Pieces.Piece;
import Utils.BoardUtils;

import java.util.LinkedList;

// A class that represent a game of chess
public class ChessGame {
    private boolean colorOfPlayersTurn;
    private LinkedList<Piece> pieceList;
    private final Piece[] pieceBoard = new Piece[BoardUtils.BOARD_SIZE];
    private long whitePiecesBitBoard;
    private long blackPiecesBitBoard;

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

    // Set up the value of the white/black pieces bitboards from the piece list
    private void updateBitBoards() {
        whitePiecesBitBoard = 0;
        blackPiecesBitBoard = 0;
        // For each piece, add its position to its appropriate board according to its color
        for (Piece piece : pieceList) {
            long currentBitBoard = boardUtils.getSquarePositionAsBitboardPosition(piece.getSquare());
            if (piece.getColor() == BoardUtils.WHITE)
                whitePiecesBitBoard |= currentBitBoard;
            else
                blackPiecesBitBoard |= currentBitBoard;
        }
    }

    // Execute a move of a piece that its in the initial square, to the target square
    public void executeMove(byte currentSquare, byte targetSquare) {
        Piece pieceToRemove = pieceBoard[targetSquare];
        Piece pieceToMove = pieceBoard[currentSquare];

        // Update the position of the piece
        pieceToMove.setSquare(targetSquare);
        pieceBoard[targetSquare] = pieceToMove;

        // Remove the piece on the target square and remove from the board the piece from the previous position
        pieceBoard[currentSquare] = pieceToRemove;
        pieceList.remove(pieceToRemove);

        // Change the turn of the player, update bitboards and the special moves
        updateBitBoards();
        colorOfPlayersTurn = !colorOfPlayersTurn;
        specialMovesHandler.updateSpecialMoves(currentSquare, targetSquare, pieceToMove);

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

    // If player has no moves to do and checked, he is checkmated
    private boolean isPlayerCheckMated(boolean playerColor) {
        return getPlayerMoves(playerColor) == 0;
    }

    // Given a color of a player, return as bitboard all the moves it pieces can do
    private long getPlayerMoves(boolean playerColor) {
        long movementBitBoard = 0, allPiecesBitBoard = whitePiecesBitBoard | blackPiecesBitBoard;
        long playerPiecesBitBoard = getBitBoardByColor(playerColor);

        // For each piece in piece list, added the movement of pieces with same color as player
        for (Piece piece : pieceList)
            if (piece.getColor() == playerColor)
                movementBitBoard = movementBitBoard | piece.getMovesAsBitBoard(allPiecesBitBoard, playerPiecesBitBoard);

        return removeIllegalMoves(movementBitBoard, playerColor);
    }

    // Given a square, get all the legal moves that piece can do as bitboard
    public long getMovesAsBitBoards(byte square) {
        long sameColorPieceBitBoard = getBitBoardByColor(colorOfPlayersTurn),basicMoves,specialMoves;
        Piece piece = pieceBoard[square];

        // Check if there is a piece on that square, and is the same color as the player who's playing
        if (piece != null && colorOfPlayersTurn == piece.getColor()) {
            basicMoves = piece.getMovesAsBitBoard(whitePiecesBitBoard | blackPiecesBitBoard, sameColorPieceBitBoard);
            // if it's a pawn or a king check if it can do a special move, either way remove illegal moves
            if (piece instanceof King || piece instanceof Pawn) {
                specialMoves = specialMovesHandler.getSpecialMoves(square,piece);
                return removeIllegalMoves(specialMoves | basicMoves,colorOfPlayersTurn);
            }
            else
                return removeIllegalMoves(basicMoves, colorOfPlayersTurn);
        }
        return 0;
    }

    // Receive a bitboard of moves, return a bitboard with only the legal moves
    // Moving a piece won't case a check, and if checked will stop the check
    private long removeIllegalMoves(long moves, boolean playerColor) {
        return 0;
    }

    // Given player color, return his co-responding bitboard that represent his pieces positions
    private long getBitBoardByColor(boolean playerColor) {
        return playerColor ? whitePiecesBitBoard : blackPiecesBitBoard;
    }

    // Return the king with same color as player color
    private Piece getKing(boolean playerColor) {
        for (Piece piece : pieceList)
            if (piece.getColor() == playerColor && piece instanceof King)
                return piece;

        // there is always a king, but for compiler
        return null;
    }

    public LinkedList<Piece> getPieceList() {
        return pieceList;
    }
}
