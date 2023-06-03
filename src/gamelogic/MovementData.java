package gamelogic;

// Class that represent the data of piece movement, how many square it can attack and the bitboard of it's movement

public class MovementData {
    public long bitBoardMovement;
    public int numberOfMovesPieceCanDo;

    public MovementData(long bitBoardMovement, int numberOfMovesPieceCanDo) {
        this.bitBoardMovement = bitBoardMovement;
        this.numberOfMovesPieceCanDo = numberOfMovesPieceCanDo;
    }
}
