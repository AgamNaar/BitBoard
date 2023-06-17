package gamelogic;

import gameengine.PieceMove;
import gamelogic.pieces.Piece;

import java.util.LinkedList;

// Class responsible for handling everything about the status of the game, what stage is it and current state
public class GameStatusHandler {

    public static final int NORMAL = 0;
    public static final int CHECK = 1;
    public static final int DRAW = 2;
    public static final int CHECKMATE = 3;
    public static final int EARLY_GAME = 0;
    public static final int MID_GAME = 1;
    public static final int END_GAME = 2;

    private int gameStatus;
    private int gameStage;

    private boolean repetition = false;
    LinkedList<PieceMove> listOfMoves = new LinkedList<>();


    // Empty builder
    public GameStatusHandler() {
    }

    // Copy builder
    @SuppressWarnings("unchecked")
    public GameStatusHandler(GameStatusHandler gameStatusHandler) {
        this.repetition = gameStatusHandler.repetition;
        this.gameStage = gameStatusHandler.gameStage;
        this.gameStatus = gameStatusHandler.gameStatus;
        this.listOfMoves = (LinkedList<PieceMove>) gameStatusHandler.listOfMoves.clone();
    }

    // Update game status and game stage according to the move played and status of the game, return the game status
    public int afterTurnHandler(PieceMove movePlayed, ChessGame game) {
        listOfMoves.add(movePlayed);
        gameStage = updateGameStage(game.getPieceList());
        boolean doesPlayerHaveMove = doesPlayerHasLegalMovesToPlay(game.getPlayerToPlay(), game);

        // If player is checked, and player has no move to do its checkmate, otherwise check
        // If player is not checked, and can't move, it's a draw
        if (isPlayerChecked(game)) {
            if (doesPlayerHaveMove)
                return (gameStatus = CHECK);
            else
                return (gameStatus = CHECKMATE);
        } else if (!doesPlayerHaveMove)
            return (gameStatus = DRAW);

        // Check if it's a draw by repetition
        if (isRepetitionOfMoves(movePlayed))
            return (gameStatus = DRAW);

        return (gameStatus = NORMAL);
    }

    // Update the current stage of the game
    private int updateGameStage(LinkedList<Piece> piecesList) {
        //TODO: for now its a demo function
        int numberOfPiece = piecesList.size();
        if (numberOfPiece > 28)
            return EARLY_GAME;
        if (numberOfPiece > 12)
            return MID_GAME;

        return END_GAME;
    }

    // Check if a repetition of moves has occurs, if yes return true
    private boolean isRepetitionOfMoves(PieceMove movePlayed) {
        int listSize = listOfMoves.size(), currSquare = movePlayed.getCurrentPieceSquare(),
                targetSquare = movePlayed.getTargetSquare();

        // need to be at least 6 moves in the list
        if (listSize < 9)
            return false;

        // Get the 2 last turn of the player who just played, and check if they're the same as this player
        PieceMove prevMove = listOfMoves.get(listSize - 5);
        PieceMove prevPrevMove = listOfMoves.get(listSize - 9);


        // Check if the player has repeated on 3 move
        if (currSquare == prevMove.getCurrentPieceSquare() && currSquare == prevPrevMove.getCurrentPieceSquare()
                && targetSquare == prevMove.getTargetSquare() && targetSquare == prevPrevMove.getTargetSquare())
            // If the enemy repeated moves on the last turn, it's a draw
            if (repetition)
                return true;
            else { // Not a draw, but now enemy can make a draw if they repeat moves them self
                repetition = true;
                return false;
            }

        return (repetition = false);
    }

    // Check if a player has a legal moves to do, if it has at least 1 return true
    private boolean doesPlayerHasLegalMovesToPlay(boolean playerColor, ChessGame game) {
        // Check if at least 1 of the player pieces has a move to do
        for (Piece piece : game.getCopyOfPieceList()) {
            if (piece.getColor() == playerColor) {
                long pieceMovement = game.getLegalMovesAsBitBoard(piece);
                if (pieceMovement != 0)
                    // Found move, return true
                    return true;
            }
        }
        // Didn't find any move, return false
        return false;
    }

    // Given a color of a player, check if their king is checked, if yes return true
    public boolean isPlayerChecked(ChessGame game) {
        // Check if king is on one of the movement squares of enemy piece
        return (game.currentPlayerKing.getSquareAsBitBoard() & game.getBitBoardOfSquaresThreatenByEnemy()) != 0;
    }

    // Get method of game status
    public int getGameStatus() {
        return gameStatus;
    }

    // Get game stage
    public int getGameStage() {
        return gameStage;
    }

    // Return if the game is over, either a draw or a checkmate
    public boolean isGameOver() {
        return gameStatus == DRAW || gameStatus == CHECKMATE;
    }

    // Reset the move list
    public void resetMoveList() {
        listOfMoves.clear();
    }
}
