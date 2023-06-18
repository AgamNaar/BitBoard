package gamelogic;

import gameengine.PieceMove;
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

    private GameStatusHandler gameStatusHandler = new GameStatusHandler();

    public static final int MOVE_NOT_EXECUTED = -1;
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
        gameStatusHandler.initialize(this);
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
        pieceList = game.getCopyOfPieceList();
        specialMovesHandler = new SpecialMovesHandler(game.specialMovesHandler);
        for (Piece piece : pieceList)
            pieceBoard[piece.getSquare()] = piece;
        updateAttributes();
        this.gameStatusHandler = new GameStatusHandler(game.gameStatusHandler);
    }

    // Given a square, get all the legal moves that piece can do as bitboard
    public long getLegalMovesAsBitBoard(byte square) {
        return getLegalMovesAsBitBoard(pieceBoard[square]);
    }

    // Given a piece, return the legal moves that the piece can do
    public long getLegalMovesAsBitBoard(Piece piece) {
        if (piece != null && colorOfPlayersTurn == piece.getColor()) {
            long pieceMoves = piece.getMovesAsBitBoard(allPiecesBitBoard, playerTurnPiecesBitBoard);
            long specialMoves = specialMovesHandler.getSpecialMoves(piece, getBitBoardOfSquaresThreatenByEnemy(),
                    allPiecesBitBoard, pieceList, colorOfPlayersTurn, currentPlayerKing);

            long allPieceMoves = pieceMoves | specialMoves;
            return legalMoveHandler.removeIllegalMoves(allPieceMoves, piece, pieceList, colorOfPlayersTurn,
                    allPiecesBitBoard, playerTurnPiecesBitBoard, gameStatusHandler.isPlayerChecked(this),
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


        return gameStatusHandler.afterTurnHandler(new PieceMove(currentSquare, targetSquare,
                typeOfPieceToPromoteTo, getPiece(targetSquare), 0), this);
    }

    // Return as bitboard all the square that are currently threatened by enemy player
    public long getBitBoardOfSquaresThreatenByEnemy() {
        long movementBitBoard = 0, enemyBitBoard = getEnemyBitBoard();
        for (Piece piece : pieceList)
            if (piece.getColor() == !colorOfPlayersTurn)
                if (piece instanceof Pawn)
                    movementBitBoard |= ((Pawn) piece).getPawnAttackSquare();
                else
                    movementBitBoard |= piece.getMovesAsBitBoard(allPiecesBitBoard, enemyBitBoard);

        return movementBitBoard;
    }

    // Given a square, return if it's a castling move
    public boolean isCastlingMove(byte targetSquare, Piece pieceToMove) {
        // Is special move check if it's a special move of a king or a pawn, in case of a king it can onl be castling
        if (pieceToMove instanceof King)
            return specialMovesHandler.isSpecialMove(targetSquare, pieceToMove);
        else
            return false;
    }

    // Getter methods

    // Return if the game is over
    public boolean isGameOver() {
        return gameStatusHandler.isGameOver();
    }

    // Return status of the game
    public int getGameStatus() {
        return gameStatusHandler.getGameStatus();
    }

    // Return stage of the game
    public int getGameStage() {
        return gameStatusHandler.getGameStage();
    }

    // Given a square, return the piece on that square
    public Piece getPiece(int position) {
        return pieceBoard[position];
    }

    // Return a copy of the list of all the pieces
    public LinkedList<Piece> getCopyOfPieceList() {
        LinkedList<Piece> newPieceList = new LinkedList<>();
        for (Piece piece : pieceList)
            newPieceList.add(piece.clone());

        return newPieceList;
    }

    // Return the piece list
    public LinkedList<Piece> getPieceList() {
        return pieceList;
    }

    // Return all piece bitboard
    public long getAllPieceBitBoard() {
        return allPiecesBitBoard;
    }

    // Return same color piece bitboard
    public long getSameColorPieceBitBoard() {
        return playerTurnPiecesBitBoard;
    }

    // Return the pieces of the enemy as bitboard
    public long getEnemyBitBoard() {
        return allPiecesBitBoard & ~playerTurnPiecesBitBoard;
    }

    // Return true if its white turn to play or false if it's black
    public boolean getPlayerToPlay() {
        return colorOfPlayersTurn;
    }

    // Return the current player's king square
    public byte getPlayerTurnKingSquare() {
        return currentPlayerKing.getSquare();
    }

    // Update parameters functions

    // Update parameters
    private void updateAttributes() {
        updateCurrentKing();
        updateBitBoards();
        legalMoveHandler.updateTreatingLines(pieceList, allPiecesBitBoard, getEnemyBitBoard(),
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


}
