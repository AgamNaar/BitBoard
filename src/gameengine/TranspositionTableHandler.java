package gameengine;

import gamelogic.ChessGame;
import gamelogic.pieces.Piece;

import java.util.Hashtable;
import java.util.Random;

/*
    Class responsible for the transposition table. the transposition table store previously evaluated positions, on
    previous searches and on the same search, with the depth that the position was evaluated on, using hashmaps.
    While searching, check if the position was already evaluated in the transposition table, and if the depth that it
    was saved is higher than current depth of the search.
 */
public class TranspositionTableHandler {
    // Represent an entry in the hashmap. for the hash of the position, store the best move of that entry,
    // the score of the move, the depth of the search and
    private static class TranspositionEntry {
        PieceMove move;
        int score;
        int depth;
        TranspositionFlag flag;

        // Builder
        TranspositionEntry(PieceMove move, int score, int depth, TranspositionFlag flag) {
            this.move = move;
            this.score = score;
            this.depth = depth;
            this.flag = flag;
        }
    }

    // Enum for the different type of scores for entries EXACT - An exact score,
    // LOWERBOUND - lower bound score (minimum score), UPPERBOUND - An upper bound score (maximum score)
    private enum TranspositionFlag {
        EXACT,
        LOWERBOUND,
        UPPERBOUND
    }

    // Hash map that represent the transposition table
    private final Hashtable<Long, TranspositionEntry> transpositionTable = new Hashtable<>();

    // Player turn key and pieces key for hash
    private final long PLAYER_KEY_WHITE;
    private final long PLAYER_KEY_BLACK;
    private final long[] pieceKeyArray = new long[6];

    // Builder, generate a key for each color and for each piece
    public TranspositionTableHandler() {
        Random random = new Random();
        PLAYER_KEY_WHITE = random.nextLong();
        PLAYER_KEY_BLACK = random.nextLong();
        for (int i = 0; i < pieceKeyArray.length; i++)
            pieceKeyArray[i] = random.nextLong();
    }

    // Given a chess game, depth, alpha and beta, search if there is a valid evaluation of the position already
    public PieceMove checkIfCalculatedAlready(ChessGame game, int depth, int alpha, int beta) {
        long hash = calculateHash(game);
        TranspositionEntry entry;

        entry = transpositionTable.get(hash);
        // If an entry found, and the depth of the saved entry is higher than current, it's a valid position
        if (entry != null && entry.depth >= depth) {
            // If the entry's score type is EXACT, it means the position has been fully evaluated
            if (entry.flag == TranspositionFlag.EXACT) {
                return entry.move;
                // If lower bound, its minimum score, update the alpha value
            } else if (entry.flag == TranspositionFlag.LOWERBOUND) {
                alpha = Math.max(alpha, entry.score);
                // If upper bound, its maximum score, update the beta value
            } else if (entry.flag == TranspositionFlag.UPPERBOUND) {
                beta = Math.min(beta, entry.score);
            }

            // If the updated alpha is greater than or equal to beta, it's a cutoff, we can return the stored best move.
            if (alpha >= beta) {
                return entry.move;
            }
        }
        return null;
    }

    // After a new position was calculated, add it to the transposition table
    public void updateTranspositionTable(PieceMove bestMove, int depth, int bestScore, int alpha, int beta, ChessGame game) {
        long hash = calculateHash(game);
        // Determine the type of score based on the comparison of bestScore with alpha and beta.
        TranspositionFlag flag = (bestScore <= alpha) ? TranspositionFlag.UPPERBOUND :
                ((bestScore >= beta) ? TranspositionFlag.LOWERBOUND : TranspositionFlag.EXACT);
        // Add the new entry with the position as hash, to the entry table
        transpositionTable.put(hash, new TranspositionEntry(bestMove, bestScore, depth, flag));
    }

    // Given a game of chess, calculate it's hashing
    public long calculateHash(ChessGame game) {
        long hash = 0;

        // For each piece, hash it with the key, and it's position
        for (Piece piece : game.getPieceList()) {
            hash ^= piece.getSquareAsBitBoard();
            hash ^= pieceKeyArray[piece.getPieceType()];
        }

        // Hash the player turn
        if (game.getPlayerToPlay())
            hash ^= PLAYER_KEY_WHITE;
        else
            hash ^= PLAYER_KEY_BLACK;

        return hash;
    }

    // Clear the table
    public void clearTable() {
        transpositionTable.clear();
    }
}

