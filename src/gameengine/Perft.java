package gameengine;

import gamelogic.ChessGame;

import java.util.LinkedList;

// Class responsible for Perft - "performance test, move path enumeration" for debugging purposes, and checking that
// The game only find legal moves
public class Perft {

    private static final String[] perftArray = {"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ",
            "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ",
            "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",
            "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8  ",
            "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10 "};

    private static final int[] depthArray = {7, 6, 8, 6, 5, 6};

    // This function will test the games' logic on 6 known perft result
    public static void generalTest() {
        ChessGame game = new ChessGame("");

        for (int i = 0; i < perftArray.length; i++) {
            System.out.println();
            System.out.println("--------        Starting test perft " + (i + 1) + "--------        ");
            game.reset(perftArray[i]);
            perft(depthArray[i], game);
        }
    }

    // Given a depth and a game, print the perft result of the game
    public static void perft(int depth, ChessGame game) {
        System.out.println("depth " + depth);

        long current, total = 0;
        LinkedList<PieceMove> pieceMovesList = GameEngineUtilities.getAllPossibleMoves(game);
        // For each possible move in that position, play that move
        for (PieceMove pieceMove : pieceMovesList) {
            ChessGame newGame = new ChessGame(game);
            newGame.executeMove(pieceMove.getCurrentPieceSquare(), pieceMove.getTargetSquare(),
                    pieceMove.getTypeOfPieceToPromoteTo());

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
    private static long numberOfPossiblePositions(int depth, ChessGame game) {
        LinkedList<PieceMove> pieceMovesList = GameEngineUtilities.getAllPossibleMoves(game);
        if (depth == 1)
            return pieceMovesList.size();

        // For each possible move in that position, play that move
        long numberOfMoves = 0;
        for (PieceMove pieceMove : pieceMovesList) {
            ChessGame newGame = new ChessGame(game);
            newGame.executeMove(pieceMove.getCurrentPieceSquare(), pieceMove.getTargetSquare(),
                    pieceMove.getTypeOfPieceToPromoteTo());

            // Go to the position after the move has been played and check how many moves it can do
            numberOfMoves = numberOfMoves + numberOfPossiblePositions(depth - 1, newGame);
        }
        return numberOfMoves;
    }
}
