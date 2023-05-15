package gamelogic;

import gamelogic.pieces.Pawn;
import gamelogic.pieces.Piece;
import gamelogic.specialmoves.SpecialMovesHandler;

import java.util.Arrays;
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
    private LegalMoveHandler legalMoveHandler;

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
        FenTranslator translator;
        if (fen == null || fen.trim().isEmpty())
            translator = new FenTranslator();
        else
            translator = new FenTranslator(fen);
        getGameSetUp(translator);
    }

    // TODO: temp create copy of a game chess until undo move
    public ChessGame(ChessGame game) {
        colorOfPlayersTurn = game.getPlayerToPlay();
        pieceList = game.getPieceList();
        specialMovesHandler = new SpecialMovesHandler(game.specialMovesHandler);
        Arrays.fill(pieceBoard, null);
        for (Piece piece : pieceList)
            pieceBoard[piece.getSquare()] = piece;
        legalMoveHandler = new LegalMoveHandler(pieceList, allPiecesBitBoard, enemyBitBoard(), colorOfPlayersTurn);
        updateBitBoards();
    }

    // Reset the game to default start up
    public void reset(String fen) {
        FenTranslator translator = new FenTranslator(fen);
        getGameSetUp(translator);
    }

    // Given a square, get all the legal moves that piece can do as bitboard
    public long getMovesAsBitBoard(byte square) {
        Piece piece = pieceBoard[square];
        if (piece != null && piece.getColor() == colorOfPlayersTurn)
            return getLegalMovesAsBitBoard(pieceBoard[square]);
        else
            return 0;
    }

    // Execute a move of a piece that its in the initial square, to the target square. return status after the move
    public int executeMove(byte currentSquare, byte targetSquare, char typeOfPieceToPromoteTo) {
        Piece pieceToMove = pieceBoard[currentSquare];

        if (!legalMoveHandler.isValidMove(currentSquare, targetSquare, pieceBoard, colorOfPlayersTurn, getLegalMovesAsBitBoard(pieceToMove)))
            return MOVE_NOT_EXECUTED;

        if (specialMovesHandler.isSpecialMove(targetSquare, pieceToMove))
            specialMovesHandler.executeSpecialMove(currentSquare, targetSquare, pieceList, pieceBoard, typeOfPieceToPromoteTo);
        else
            boardUtils.updatePiecePosition(targetSquare, currentSquare, pieceBoard, pieceList);

        // Change the turn of the player, updateCastlingRights bitboards and the special moves
        colorOfPlayersTurn = !colorOfPlayersTurn;
        specialMovesHandler.updateSpecialMoves(currentSquare, targetSquare, pieceToMove);
        updateBitBoards();
        legalMoveHandler.updateTreatingLines(pieceList, allPiecesBitBoard, enemyBitBoard(), colorOfPlayersTurn);

        return getGameStatus();
    }

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

    // Return true if its white turn to play
    public boolean getPlayerToPlay() {
        return colorOfPlayersTurn;
    }

    // Get the square of the king
    public byte getPlayerTurnKingSquare() {
        return boardUtils.getKing(colorOfPlayersTurn, pieceList).getSquare();
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
        Arrays.fill(pieceBoard, null);
        for (Piece piece : pieceList)
            pieceBoard[piece.getSquare()] = piece;
        updateBitBoards();
        legalMoveHandler = new LegalMoveHandler(pieceList, allPiecesBitBoard, enemyBitBoard(), colorOfPlayersTurn);
    }

    // get the status of the game - normal, check, draw or checkmate
    private int getGameStatus() {
        // Check if the enemy player is checked, checkmated or if it's a draw
        if (isPlayerChecked(colorOfPlayersTurn)) {
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

    // Given a color of a player, return as bitboard all the moves it pieces can do
    private long getPlayerThreatenedSquare(boolean playerColor) {
            long movementBitBoard = 0;
        // if the player color to get his movement is not the same as the play turn, remove from all allPiecesBitBoard all the same color pieces
        long sameColorPieceBitBoard = colorOfPlayersTurn == playerColor ? playerTurnPiecesBitBoard : allPiecesBitBoard & ~playerTurnPiecesBitBoard;
        // For each piece in piece list, added the movement of pieces with same color as player
        for (Piece piece : pieceList)
            if (piece.getColor() == playerColor)
                if (piece instanceof Pawn)
                    movementBitBoard |= ((Pawn) piece).getPawnAttackSquare();
                else
                    movementBitBoard |= piece.getMovesAsBitBoard(allPiecesBitBoard, sameColorPieceBitBoard);

        return movementBitBoard;
    }

    // Given a piece, return the legal moves that the piece can do
    private long getLegalMovesAsBitBoard(Piece piece) {
        if (piece != null && colorOfPlayersTurn == piece.getColor()) {
            long pieceMoves = piece.getMovesAsBitBoard(allPiecesBitBoard, playerTurnPiecesBitBoard);
            long specialMoves = specialMovesHandler.getSpecialMoves(piece, getPlayerThreatenedSquare(!colorOfPlayersTurn), allPiecesBitBoard, pieceList, colorOfPlayersTurn);
            long allPieceMoves = pieceMoves | specialMoves;
            return legalMoveHandler.removeIllegalMoves(allPieceMoves, piece, pieceList, colorOfPlayersTurn, allPiecesBitBoard, playerTurnPiecesBitBoard, isPlayerChecked(colorOfPlayersTurn),specialMovesHandler.getEnPassantSquare());
        }
        return 0;
    }

    // Given a color of a player, check if their king is checked, if yes return true
    private boolean isPlayerChecked(boolean playerColor) {
        // Find the players king, and all enemy players movement
        Piece playerKing = boardUtils.getKing(playerColor, pieceList);
        long enemyMovement = getPlayerThreatenedSquare(!playerColor), kingBitPosition;

        assert playerKing != null;
        kingBitPosition = boardUtils.getSquarePositionAsBitboardPosition(playerKing.getSquare());
        // Check if king is on one of the movement squares of enemy piece
        return (kingBitPosition & enemyMovement) != 0;
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

    // Return the pieces of the enemy as bitboard
    private long enemyBitBoard() {
        return allPiecesBitBoard & ~playerTurnPiecesBitBoard;
    }


}
