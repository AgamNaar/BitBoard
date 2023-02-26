// This class is responsible, to generate for each piece, for each square on the board (0-63)
// All the possible moves it can do - regardless of the position of the board
// For each piece it returns an array of 64, each position has a bitboard of the move co-responding for that position
public class BitBoardMoveGenerator {

    private static final byte[] KING_OFFSETS = {-9, -8, -7, -1, 1, 7, 8, 9};
    private static final byte[] ROOK_OFFSETS = {1, -1, 8, -8};
    private static final byte[] BISHOP_OFFSETS = {7, -7, 9, -9};
    private static final byte[] KNIGHT_OFFSETS = {-17, -15, -10, -6, 6, 10, 15, 17};
    private static final byte[] WHITE_PAWN_OFFSETS_ATK = {7, 9};
    private static final byte[] BLACK_PAWN_OFFSETS_ATK = {-7, -9};

    public static final boolean WHITE = true;

    // Change moves array that in each position of the array, it has a bitboard that represent the moves a king can do in the position
    public void generateKingMoves(long[] movesArray) {
        generateMoves(movesArray, KING_OFFSETS);
    }

    // Change moves array that in each position of the array, it has a bitboard that represent the moves a knight can do in the position
    public void generateKnightMoves(long[] movesArray) {
        generateMoves(movesArray, KNIGHT_OFFSETS);
    }

    // Change moves array and atk array, that in each position of the array, it has a bitboard that represent the moves/capture a pawn can do in the position
    public void generatePawnMoves(long[] movesArray, long[] atkArray, boolean color) {
        if (color == WHITE) {
            generatePawnMoves(movesArray, color);
            generateMoves(atkArray, WHITE_PAWN_OFFSETS_ATK);
        } else {
            generatePawnMoves(movesArray, color);
            generateMoves(atkArray, BLACK_PAWN_OFFSETS_ATK);
        }
    }

    // Generate only the moves a pawn can do (not including capture)
    private void generatePawnMoves(long[] moveArray, boolean color) {
        byte offset = (color) ? (byte) 8 : -8;
        for (byte square = 7; square < 56; square++) {
            long currSquareMoves = 0L;
            currSquareMoves |= squareToBitboard(offset + square);

            // if on 2nd raw as white, it can move twice
            if (square <= 15 && offset == 8)
                currSquareMoves |= squareToBitboard((offset * 2) + square);

            // if on the 6th row as black, it can move twice
            if (square >= 47 && offset == -8)
                currSquareMoves |= squareToBitboard((offset * -2) + square);

            moveArray[square] = currSquareMoves;
        }
    }


    // Generate moves for each square of the board for none line pieces (King, Knight, Pawn)
    private void generateMoves(long[] moveArray, byte[] offsetArray) {
        for (byte square = 0; square < 64; square++) {
            long currSquareMoves = 0L;
            for (byte offset : offsetArray) {
                // check if legal offset, if yes added the possible moves
                if (isLegalOffset(square, offset))
                    currSquareMoves |= squareToBitboard(offset + square);
            }
            moveArray[square] = currSquareMoves;
        }
    }

    /*
    // Generate moves for each square of the board for line pieces (Rook, Bishop)
    private void getMovementLinePiece(long[][][] moveArray, byte[]offsetArray) {
        // For each square, for each possible values of rows and columns, get the bitboard value
        for (byte square = 0; square < 64; square++) {
            for (int rowValue = 0; rowValue < 256; rowValue++) {
                for (int columnValue = 0; columnValue < 256; columnValue++) {
                    long currSquareMoves = getMoves(offsetArray,square,rowValue,columnValue);
                    moveArray[square][rowValue][columnValue] = currSquareMoves;
                }
            }
        }

    }


    private long getMoves(byte square, int rowValue, int columnValue) {


        byte[] edgeDistances = getBitTillEdgeOfBoard(position);
        long positionBit = 1L << position, result = 0, allPieces = 0;

        for (byte i = 0; i < offsets.length; i++) {
            // Run until the edge of the board or found a piece
            for (byte j = 1; j <= edgeDistances[i]; j++) {
                long temp;
                // Check if offset is negative or positive to offset right or left
                if (offsets[i] > 0)
                    temp = positionBit << j * offsets[i];
                else
                    temp = positionBit >> j * -offsets[i];

                // if not 0, piece on that position, 0 mean position empty
                if ((temp & allPieces) != 0) {
                    result |= temp;
                    break;
                } else
                    result |= temp;
            }
        }
        return result;
    }

     */


    // Given an offset and a square, check if that offset from that square is legal
    // If on the last/first rank, is not outside of board
    // If on the right/left most file, is not outside of board
    private boolean isLegalOffset(byte position, byte offset) {
        // Calculate the row and column of the given position
        int row = position / 8;
        int col = position % 8;

        // Calculate the row and column of the target square
        int targetRow = row + offset / 8;
        int targetCol = col + offset % 8;

        // Check if the target square is within the board limits
        return targetRow >= 0 && targetRow < 8 && targetCol >= 0 && targetCol < 8;
    }


    // Convert position into bit position
    private long squareToBitboard(long square) {
        return 1L << square;
    }


    // Given a position, return how many bits till the end of the board from the position to right, left, up, down
    private byte[] getBitTillEdgeOfBoard(byte position) {
        byte[] distances = new byte[4];
        // Right
        distances[0] = (byte) (position % 8);
        // Left
        distances[1] = (byte) (7 - (position % 8));
        // Up
        distances[2] = (byte) (7 - (position / 8));
        // Down
        distances[3] = (byte) (position / 8);

        return distances;
    }
}
