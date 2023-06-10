package gamelogic;

import gamelogic.pieces.King;
import gamelogic.pieces.Pawn;
import gamelogic.pieces.Piece;
import gamelogic.specialmoves.SpecialMovesHandler;

import java.util.LinkedList;

// A class that represent a game of chess
public class ChessGame {
    private boolean colorOfPlayersTurn;
    private LinkedList<Piece> pieceList;
    private Piece[] pieceBoard;
    private long playerTurnPiecesBitBoard;
    private long allPiecesBitBoard;
    Piece currentPlayerKing;

    private SpecialMovesHandler specialMovesHandler;
    private final LegalMoveHandler legalMoveHandler = new LegalMoveHandler();

    public static final int MOVE_NOT_EXECUTED = -1;
    public static final int NORMAL = 0;
    public static final int CHECK = 1;
    public static final int DRAW = 2;
    public static final int CHECKMATE = 3;
    public static final char PROMOTE_TO_QUEEN = 'q';
    public static final char PROMOTE_TO_ROOK = 'r';
    public static final char PROMOTE_TO_KNIGHT = 'n';
    public static final char PROMOTE_TO_BISHOP = 'b';

    // Initialize a game of chess using a fen
    public ChessGame(String fen) {
        reset(fen);
    }

    // Reset the game to default start up
    public void reset(String fen) {
        pieceBoard = new Piece[GameLogicUtilities.BOARD_SIZE];
        FenTranslator translator;
        if (fen == null || fen.trim().isEmpty())
            translator = new FenTranslator();
        else
            translator = new FenTranslator(fen);
        getGameSetUp(translator);
    }

    // Retrieve all game setups (castling right, piece position, en passant target square) from fen
    // Set up the board of pieces, bitboards and handlers
    private void getGameSetUp(FenTranslator translator) {
        colorOfPlayersTurn = translator.isWhiteTurnToPlay();
        specialMovesHandler = new SpecialMovesHandler(translator.canWhiteShortCastle(),
                translator.canWhiteLongCastle(),
                translator.canBlackShortCastle(),
                translator.canBlackLongCastle(),
                translator.getEnPassantSquareToCapture());

        pieceList = translator.getPieceList();
        // Insert pieces from the list into the board
        for (Piece piece : pieceList)
            pieceBoard[piece.getSquare()] = piece;
        updateAttributes();
    }

    // TODO: temp create copy of a game chess until undo move
    public ChessGame(ChessGame game) {
        pieceBoard = new Piece[GameLogicUtilities.BOARD_SIZE];
        colorOfPlayersTurn = game.getPlayerToPlay();
        pieceList = game.getPieceList();
        specialMovesHandler = new SpecialMovesHandler(game.specialMovesHandler);
        for (Piece piece : pieceList)
            pieceBoard[piece.getSquare()] = piece;
        updateAttributes();
    }

    // Given a square, get all the legal moves that piece can do as bitboard
    public long getLegalMovesAsBitBoard(byte square) {
        return getLegalMovesAsBitBoard(pieceBoard[square]);
    }

    // Given a piece, return the legal moves that the piece can do
    private long getLegalMovesAsBitBoard(Piece piece) {
        if (piece != null && colorOfPlayersTurn == piece.getColor()) {
            long pieceMoves = piece.getMovesAsBitBoard(allPiecesBitBoard, playerTurnPiecesBitBoard);
            long specialMoves = specialMovesHandler.getSpecialMoves(piece, getBitBoardOfSquaresThreatenByEnemy(),
                    allPiecesBitBoard, pieceList, colorOfPlayersTurn, currentPlayerKing);

            long allPieceMoves = pieceMoves | specialMoves;
            return legalMoveHandler.removeIllegalMoves(allPieceMoves, piece, pieceList, colorOfPlayersTurn,
                    allPiecesBitBoard, playerTurnPiecesBitBoard, isPlayerChecked(),
                    specialMovesHandler.getEnPassantSquare(), currentPlayerKing);
        }
        return 0;
    }

    // Execute a move of a piece that its in the initial square, to the target square.
    // Check if it's a valid move, if not don't execute the move, return the status of the game after the move
    public int executeMove(byte currentSquare, byte targetSquare, char typeOfPieceToPromoteTo) {
        Piece pieceToMove = pieceBoard[currentSquare];

        if (!legalMoveHandler.isValidMove(currentSquare, targetSquare, pieceBoard, colorOfPlayersTurn,
                getLegalMovesAsBitBoard(pieceToMove)))
            return MOVE_NOT_EXECUTED;

        if (specialMovesHandler.isSpecialMove(targetSquare, pieceToMove))
            specialMovesHandler.executeSpecialMove(currentSquare, targetSquare, pieceList, pieceBoard,
                    typeOfPieceToPromoteTo);
        else
            GameLogicUtilities.updatePiecePosition(targetSquare, currentSquare, pieceBoard, pieceList);

        // Change the turn of the player, and update all other game attributes
        colorOfPlayersTurn = !colorOfPlayersTurn;
        specialMovesHandler.updateSpecialMoves(currentSquare, targetSquare, pieceToMove);
        updateAttributes();

        return getGameStatus();
    }

    // get the status of the game - normal, check, draw or checkmate
    public int getGameStatus() {
        // Check if the enemy player is checked, checkmated or if it's a draw
        if (isPlayerChecked()) {
            if (doesPlayerHasLegalMovesToPlay(colorOfPlayersTurn))
                return CHECK;
            else
                return CHECKMATE;
        } else if (!doesPlayerHasLegalMovesToPlay(colorOfPlayersTurn))
            return DRAW;

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

    // Return as bit board all the square that are currently threatened by enemy player
    private long getBitBoardOfSquaresThreatenByEnemy() {
        long movementBitBoard = 0, enemyBitBoard = enemyBitBoard();
        for (Piece piece : pieceList)
            if (piece.getColor() == !colorOfPlayersTurn)
                if (piece instanceof Pawn)
                    movementBitBoard |= ((Pawn) piece).getPawnAttackSquare();
                else
                    movementBitBoard |= piece.getMovesAsBitBoard(allPiecesBitBoard, enemyBitBoard);

        return movementBitBoard;
    }

    // Given a color of a player, check if their king is checked, if yes return true
    private boolean isPlayerChecked() {
        // Check if king is on one of the movement squares of enemy piece
        return (currentPlayerKing.getSquareAsBitBoard() & getBitBoardOfSquaresThreatenByEnemy()) != 0;
    }

    // Return the pieces of the enemy as bitboard
    private long enemyBitBoard() {
        return allPiecesBitBoard & ~playerTurnPiecesBitBoard;
    }

    // Getter methods

    // Return a copy of the board of all the pieces
    public Piece[] getPieceBoard() {
        return pieceBoard.clone();
    }

    // Return a copy of the list of all the pieces
    public LinkedList<Piece> getPieceList() {
        LinkedList<Piece> newPieceList = new LinkedList<>();
        for (Piece piece : pieceList)
            newPieceList.add(piece.clone());

        return newPieceList;
    }

    // Return all piece bitboard
    public long getAllPieceBitBoard() {
        return allPiecesBitBoard;
    }

    public boolean getPlayerToPlay() {
        return colorOfPlayersTurn;
    }

    public byte getPlayerTurnKingSquare() {
        return currentPlayerKing.getSquare();
    }

    // Update parameters functions

    // Update parameters
    private void updateAttributes() {
        updateCurrentKing();
        updateBitBoards();
        legalMoveHandler.updateTreatingLines(pieceList, allPiecesBitBoard, enemyBitBoard(),
                colorOfPlayersTurn, currentPlayerKing);
    }

    // Update the current king to match the king of the player turn
    private void updateCurrentKing() {
        for (Piece piece : pieceList)
            if (piece instanceof King && piece.getColor() == colorOfPlayersTurn) {
                currentPlayerKing = piece;
                return;
            }
    }

    // update the value of allPiecesBitBoard and playerTurnPiecesBitBoard according to the piece list
    private void updateBitBoards() {
        allPiecesBitBoard = 0;
        playerTurnPiecesBitBoard = 0;
        // For each piece, add its bitboard position to it's matching bitboard
        for (Piece piece : pieceList) {
            long pieceBitBoardPosition = piece.getSquareAsBitBoard();
            if (piece.getColor() == colorOfPlayersTurn)
                playerTurnPiecesBitBoard |= pieceBitBoardPosition;

            allPiecesBitBoard |= pieceBitBoardPosition;
        }
    }


    public boolean isGameOver() {
        int gameStatus = getGameStatus();
        return gameStatus == DRAW || gameStatus == CHECKMATE;
    }
}
