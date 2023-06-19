package gameengine;

import gamelogic.ChessGame;
import gamelogic.pieces.King;
import gamelogic.pieces.Pawn;
import gamelogic.pieces.Piece;

import java.util.Arrays;

// Class that has even more information about the chess game
public class MoreChessGameData {

    private long enemyThreatenedSquare;
    private long myThreatenSquares;
    private long myPawnThreatenSquare;
    private long myPawnMap;

    private final long[] enemyPieceByPiecesMapArray = new long[6];
    private final long[] enemyThreatenSquareByPieceArray = new long[6];

    // Update all the parameters according to the game
    public void updateParameters(ChessGame game) {
        resetElements();

        // For each piece, add the data about it
        for (Piece piece : game.getPieceList()) {
            long currentPieceAttackSquare;
            if (piece.getColor() == game.getPlayerToPlay()) {
                if (piece instanceof Pawn) {
                    myPawnMap |= piece.getSquareAsBitBoard();
                    myPawnThreatenSquare |= ((Pawn) piece).getPawnAttackSquare();
                } else
                    myThreatenSquares |= piece.getMovesAsBitBoard(game.getAllPieceBitBoard(), 0);

            } else {
                if (piece instanceof Pawn)
                    currentPieceAttackSquare = ((Pawn) piece).getPawnAttackSquare();
                else
                    currentPieceAttackSquare = piece.getMovesAsBitBoard(game.getAllPieceBitBoard(), 0);

                enemyPieceByPiecesMapArray[piece.getPieceType()] |= piece.getSquareAsBitBoard();
                enemyThreatenSquareByPieceArray[piece.getPieceType()] |= currentPieceAttackSquare;
                enemyThreatenedSquare |= piece.getMovesAsBitBoard(game.getAllPieceBitBoard(), 0);
            }
        }
    }

    // Reset all the data to 0
    private void resetElements() {
        // Clear array
        Arrays.fill(enemyThreatenSquareByPieceArray, 0);
        Arrays.fill(enemyPieceByPiecesMapArray, 0);
        enemyThreatenedSquare = 0;
        myThreatenSquares = 0;
        myPawnThreatenSquare = 0;
        myPawnMap = 0;
    }

    // Get all enemy threaten square
    public long getAllEnemyThreatenSquare() {
        return enemyThreatenedSquare;
    }

    // Get all my protected squares square
    public long getMyProtectedSquares() {
        return myPawnThreatenSquare | myThreatenSquares;
    }

    // Get enemy piece of only type according to type given
    public long getMapAccordingToType(int pieceType) {
        return enemyPieceByPiecesMapArray[pieceType];
    }

    // Getter methods
    public long getEnemyThreatenedSquareWithoutPawnThreatenSquare() {
        return enemyThreatenedSquare & ~enemyThreatenSquareByPieceArray[Piece.PAWN];
    }

    public long getEnemyPawnThreatenSquare() {
        return enemyThreatenSquareByPieceArray[Piece.PAWN];
    }

    public long getMyPawnThreatenSquare() {
        return myPawnThreatenSquare;
    }

    public long getMyPawnMap() {
        return myPawnMap;
    }

    public long[] getEnemyPieceByPiecesMapArray() {
        return enemyPieceByPiecesMapArray;
    }

    public long[] getEnemyThreatenSquareByPieceArray() {
        return enemyThreatenSquareByPieceArray;
    }

    public long getEnemyWithoutSamePieceTypeAndWithoutPawn(int pieceType) {
        long result = 0;
        for (int i = 0; i < enemyThreatenSquareByPieceArray.length; i++) {
            if (i != Piece.PAWN && i != pieceType)
                result |= enemyThreatenSquareByPieceArray[i];
        }
        return result;
    }

    int[] threateningPieceBonus = {150, 10, 50, 50, 100, 200};
    int[] threateningPieceSamePieceBonus = {400, 100, 280, 300, 500, 850};

    public int threatBonusByPieceType(int pieceType) {
        return threateningPieceBonus[pieceType];
    }
}
