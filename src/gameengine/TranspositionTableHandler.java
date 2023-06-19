package gameengine;

import gamelogic.ChessGame;
import gamelogic.pieces.Piece;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class TranspositionTableHandler {

    private static final int TABLE_ARRAY_SIZE = 5;
    private int indexOfCurrentTable;
    private final ArrayList<Hashtable<Long, TranspositionEntry>> transpositionTableArray = new ArrayList<>();


    private static class TranspositionEntry {
        PieceMove move;
        int score;
        int depth;
        TranspositionFlag flag;

        TranspositionEntry(PieceMove move, int score, int depth, TranspositionFlag flag) {
            this.move = move;
            this.score = score;
            this.depth = depth;
            this.flag = flag;
        }
    }

    private enum TranspositionFlag {
        EXACT,
        LOWERBOUND,
        UPPERBOUND
    }


    // Player turn key
    private final long PLAYER_KEY_WHITE;
    private final long PLAYER_KEY_BLACK;
    private final long[] pieceKeyArray = new long[6];

    public TranspositionTableHandler() {
        Random random = new Random();
        PLAYER_KEY_WHITE = random.nextLong();
        PLAYER_KEY_BLACK = random.nextLong();
        for (int i = 0; i < pieceKeyArray.length; i++)
            pieceKeyArray[i] = random.nextLong();

        for (int i = 0; i < TABLE_ARRAY_SIZE; i++)
            transpositionTableArray.add(new Hashtable<>());
        indexOfCurrentTable = 0;
    }

    public PieceMove checkIfCalculatedAlready(ChessGame game, int depth, int alpha, int beta) {
        long hash = calculateHash(game);
        TranspositionEntry entry;

        for (Hashtable<Long, TranspositionEntry> table : transpositionTableArray) {
            entry = table.get(hash);
            if (entry != null && entry.depth >= depth) {
                if (entry.flag == TranspositionFlag.EXACT) {
                    return entry.move;
                } else if (entry.flag == TranspositionFlag.LOWERBOUND) {
                    alpha = Math.max(alpha, entry.score);
                } else if (entry.flag == TranspositionFlag.UPPERBOUND) {
                    beta = Math.min(beta, entry.score);
                }
                if (alpha >= beta) {
                    return entry.move;
                }
            }
        }

        return null;
    }


    public void updateTranspositionTable(PieceMove bestMove, int depth, int bestScore, int alpha, int beta, ChessGame game) {
        long hash = calculateHash(game);
        TranspositionFlag flag = (bestScore <= alpha) ? TranspositionFlag.UPPERBOUND :
                ((bestScore >= beta) ? TranspositionFlag.LOWERBOUND : TranspositionFlag.EXACT);
        transpositionTableArray.get(indexOfCurrentTable).put(hash, new TranspositionEntry(bestMove, bestScore, depth, flag));
    }

    // Given a game of chess, calculate it's hashing
    public long calculateHash(ChessGame game) {
        long hash = 0;

        for (Piece piece : game.getPieceList()) {
            hash ^= piece.getSquareAsBitBoard();
            hash ^= pieceKeyArray[piece.getPieceType()];
        }

        if (game.getPlayerToPlay())
            hash ^= PLAYER_KEY_WHITE;
        else
            hash ^= PLAYER_KEY_BLACK;

        return hash;
    }

    //
    public void deleteOldestTable() {
        int oldestTable = indexOfCurrentTable;
        indexOfCurrentTable++;
        if (indexOfCurrentTable == TABLE_ARRAY_SIZE)
            indexOfCurrentTable = 0;

        transpositionTableArray.get(oldestTable).clear();
    }
}

