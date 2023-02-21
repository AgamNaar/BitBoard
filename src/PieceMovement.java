
// Class that provide movement of pieces given their position, according to the state of the board (board represented as bitboard)
public class PieceMovement {


    private final BitBoards bitBoards;

    private static final byte[] SHIFT_DISTANCES_ROOK = {1, -1, 8, -8};
    private static final byte[] SHIFT_DISTANCES_BISHOP = {7, -7, 9, -9};
    private static final byte[] SHIFT_DISTANCES_KNIGHT = {6, -6, 10, -10, 15, -15, 17, -17};

    // builder receive bitboard
    public PieceMovement(BitBoards bitBoards) {
        this.bitBoards = bitBoards;
    }

    // TODO: finish
    // Given a position of a knight, return the moves it can do as bitboard
    public long getKnightMovement(byte position) {
        return 0;
    }

    // Given a position of a rook, return the moves it can do as bitboard
    public long getRookMovement(byte position) {
        return getMovementLinePiece(position, SHIFT_DISTANCES_ROOK);
    }

    // Given a position of a bishop, return the moves it can do as bitboard
    public long getBishopMovement(byte position) {
        return getMovementLinePiece(position, SHIFT_DISTANCES_BISHOP);
    }

    // Given a position of a queen, return the moves it can do as bitboard
    public long getQueenMovement(byte position) {
        return getBishopMovement(position) | getRookMovement(position);
    }

    // Given a position of a line piece, as a number, and its offset movement, return all the moves it can do as bitBoard
    private long getMovementLinePiece(byte position, byte[] offset) {
        byte[] edgeDistances = getBitTillEdgeOfBoard(position);
        long positionBit = 1L << position, result = 0, allPieces = bitBoards.getAllPieces(), temp;

        for (byte i = 0; i < offset.length; i++) {
            // Run until the edge of the board or found a piece
            for (byte j = 1; j <= edgeDistances[i]; j++) {
                // Check if offset is negative or positive to offset right or left
                if (offset[i] > 0)
                    temp = positionBit << j * offset[i];
                else
                    temp = positionBit >> j * -offset[i];

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
