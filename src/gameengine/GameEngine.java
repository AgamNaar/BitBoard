package gameengine;

import gamelogic.ChessGame;
import gamelogic.pieces.Pawn;
import gamelogic.pieces.Piece;

import java.util.LinkedList;

public class GameEngine {

    public void perft(int depth, ChessGame game) {
        System.out.println("depth " + depth);
        long current, total = 0;
        LinkedList<PieceMove> pieceMovesList = getAllPossibleMoves(game);
        for (PieceMove pieceMove : pieceMovesList) {
            ChessGame newGame = new ChessGame(game);
            newGame.executeMove(pieceMove.getPiecePosition(), pieceMove.getTargetSquare(), pieceMove.getTypeOfPieceToPromoteTo());
            if (depth == 1)
                current = 1;
            else
                current = numberOfPossiblePositions(depth - 1, newGame);
            System.out.println(pieceMove + ": " + current);
            total = total + current;
        }
        System.out.println("total: " + total);
    }

    // Given a depth and a game, return the number of possible positions for depth moves
    public long numberOfPossiblePositions(int depth, ChessGame game) {
        LinkedList<PieceMove> pieceMovesList = getAllPossibleMoves(game);
        if (depth == 1)
            return pieceMovesList.size();

        long numberOfMoves = 0;
        for (PieceMove pieceMove : pieceMovesList) {
            ChessGame newGame = new ChessGame(game);
            newGame.executeMove(pieceMove.getPiecePosition(), pieceMove.getTargetSquare(), pieceMove.getTypeOfPieceToPromoteTo());
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
                pieceMoveList.addAll(transferFromBitBoardMovesToMoves(piece, game.getMovesAsBitBoard(piecePosition), piecePosition));
            }
        }
        return pieceMoveList;
    }

    // Given a bitboard and the piece position, return as a list all the piece moves based on the pieceMovesBitBoards
    private LinkedList<PieceMove> transferFromBitBoardMovesToMoves(Piece piece, long pieceMovesBitBoards, byte piecePosition) {
        LinkedList<PieceMove> pieceMoveList = new LinkedList<>();
        for (byte i = 0; i < 64; i++) {
            if (((1L << i) & pieceMovesBitBoards) != 0) {
                boolean isAPawn = piece instanceof Pawn;
                PieceMove pieceMove = new PieceMove(piecePosition, i, ChessGame.PROMOTE_TO_QUEEN, isAPawn);
                pieceMoveList.add(pieceMove);
                if (pieceMove.isItPromotionMove() && isAPawn) {
                    pieceMoveList.add(new PieceMove(piecePosition, i, ChessGame.PROMOTE_TO_ROOK, true));
                    pieceMoveList.add(new PieceMove(piecePosition, i, ChessGame.PROMOTE_TO_KNIGHT, true));
                    pieceMoveList.add(new PieceMove(piecePosition, i, ChessGame.PROMOTE_TO_BISHOP, true));
                }
            }
        }
        return pieceMoveList;
    }
}
