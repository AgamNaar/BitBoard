public class TestingMain {

    public static void main(String[] args) {
        long allPieces = 0b0000_0000_0000_0000_0000_0000_0100_1101_0000_0000_0000_1000_0000_0000_0000_1000L;

        BitBoards bitBoard = new BitBoards();
        PieceMovement pieceMovement = new PieceMovement(bitBoard);

        bitBoard.setBlackPawns(allPieces);
        bitBoard.setWhiteRooks(35);

        /*
        long possible = pieceMovement.getRookMovement((byte)35);
        System.out.println("moves ");
        print(possible);

         possible = pieceMovement.getBishopMovement((byte)35);
        System.out.println("moves ");
        print(possible);

         */







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