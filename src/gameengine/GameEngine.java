package gameengine;

import gamelogic.ChessGame;
import gamelogic.pieces.Piece;

import java.util.LinkedList;

public class GameEngine {

    // Given a depth and a game, return the number of possible positions for depth moves
    public int numberOfPossiblePositions(int depth, ChessGame game) {
        LinkedList<PieceMove> pieceMovesList = getAllPossibleMoves(game);
        /*
        for (PieceMove move : pieceMovesList) {
            System.out.println("starting square: "+move.getPiecePosition());
            System.out.println("target square: "+move.getTargetSquare());
        }

         */
        if (depth == 1)
            return pieceMovesList.size();



        int numberOfMoves = 0;
        for (PieceMove pieceMove : pieceMovesList) {
            ChessGame newGame = new ChessGame(game);
            newGame.executeMove(pieceMove.getPiecePosition(), pieceMove.getTargetSquare());
            numberOfMoves = numberOfMoves + numberOfPossiblePositions(depth - 1, newGame);
        }
        return numberOfMoves;
    }

    // Given a ChessGame, return a list of all the possible moves in that position
    private LinkedList<PieceMove> getAllPossibleMoves(ChessGame game) {
        LinkedList<PieceMove> pieceMoveList = new LinkedList<>();
        LinkedList<Piece> pieceList = game.getPieceList();
        for (Piece piece : pieceList) {
            // Check if the piece color is the game as the player turn color
            if (piece.getColor() == game.getPlayerToPlay()) {
                byte piecePosition = piece.getSquare();
                pieceMoveList.addAll(transferFromBitBoardMovesToMoves(game.getMovesAsBitBoard(piecePosition), piecePosition));
            }
        }
        return pieceMoveList;
    }

    // Given a bitboard and the piece position, return as a list all the piece moves based on the pieceMovesBitBoards
    private LinkedList<PieceMove> transferFromBitBoardMovesToMoves(long pieceMovesBitBoards, byte piecePosition) {
        LinkedList<PieceMove> pieceMoveList = new LinkedList<>();
        for (byte i = 0; i < 64; i++) {
            if (((1L << i) & pieceMovesBitBoards) != 0)
                pieceMoveList.add(new PieceMove(piecePosition, i));
        }
        return pieceMoveList;
    }

}
