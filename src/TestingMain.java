import java.util.HashMap;

public class TestingMain {


    private static final HashMap<Long,Long>[]ROOK_MOVES = (HashMap<Long, Long>[]) new HashMap<?,?>[64];
    private static final HashMap<Long,Long>[]BISHOP_MOVES =  (HashMap<Long, Long>[]) new HashMap<?,?>[64];

    public static void main(String[] args) {

        long allPieces = 0b0000_0000_0000_0000_0000_0000_0100_1101_0000_0000_0000_1000_0000_0000_0000_1000L;

        for (int i = 0 ; i < 64 ; i++) {
            ROOK_MOVES[i] = new HashMap<>();
            BISHOP_MOVES[i] = new HashMap<>();
        }
        /*
        BitBoards bitBoards = new BitBoards();
        PieceMovement pieceMovement = new PieceMovement(bitBoards);
        for(int i = 0 ; i < 64 ; i++) {
            print(pieceMovement.ROOK_MASK[i]);
            print(-1);
        }

         */
        BitBoardMoveGenerator bitBoardMoveGenerator = new BitBoardMoveGenerator();
        long [] arr1 = new long[64];
        long [] arr2 = new long[64];

        bitBoardMoveGenerator.generateLinePieceMoves(ROOK_MOVES,BISHOP_MOVES);
bitBoardMoveGenerator.generatePawnMoves(arr1,arr2,false);
        System.out.println("work");
        long val = 0;
        for (int i = 0 ; i < 64 ; i++) {
            System.out.println(i);
            print(arr1[i]);
        }



    }

    // for testing, print long as 8x8 of it bits value
    public static void print ( long toPrint){
        String binaryString = String.format("%64s", Long.toBinaryString(toPrint)).replace(' ', '0');
        for (int i = 0; i < 64; i += 8) {
            System.out.println(binaryString.substring(i, i + 8));
        }
        System.out.println();
    }

}