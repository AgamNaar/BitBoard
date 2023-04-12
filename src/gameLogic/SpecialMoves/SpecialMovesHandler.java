package gameLogic.SpecialMoves;

import gameLogic.Pieces.*;

import java.util.LinkedList;

/*
Class that handle all the special moves in a game, Special moves are castling and en passant
has 4 public functions:
1. updateSpecialMoves: Given a piece and a square that the piece has moved to, updateCastlingRights the castling rights and en passant square
2. getSpecialMoves: Given a piece, the possible enemy movement and bitboard of all pieces position
return as bitboard if the piece can can do en passant or castling
3. executeSpecialMove: Given the current square of the piece and its target square,
execute the special move and updateCastlingRights the list and board of pieces - assume that the target square was given from getSpecialMoves
4.  isSpecialMove: Given a target square, check if it's a square of castling or en passant move - if yes return true
 */
public class SpecialMovesHandler {

    private final PawnSpecialMoves pawnSpecialMoves;
    private final CastlingSpecialMove castlingSpecialMove;

    // Builder
    public SpecialMovesHandler(boolean whiteShortCastle, boolean whiteLongCastle, boolean blackShortCastle, boolean blackLongCastle, byte enPassantTargetSquare) {
        pawnSpecialMoves = new PawnSpecialMoves(enPassantTargetSquare);
        castlingSpecialMove = new CastlingSpecialMove(whiteShortCastle,whiteLongCastle,blackShortCastle,blackLongCastle);
    }

    // Given a piece that has been played, the square it has moved to, updateCastlingRights the special moves accordingly
    public void updateSpecialMoves(byte currentSquare, byte targetSquare, Piece pieceToMove) {
        castlingSpecialMove.updateCastlingRights(currentSquare, pieceToMove);
        pawnSpecialMoves.updateEnPassantSquare(currentSquare, targetSquare, pieceToMove);
    }

    // given a piece, check what special moves it can do. A pawn can do en passant and a king can castle
    public long getSpecialMoves(Piece piece, long enemyMovement, long piecesBitBoard) {
        if (piece instanceof King)
            return castlingSpecialMove.getMoves(piece, enemyMovement, piecesBitBoard);

        if (piece instanceof Pawn)
            return pawnSpecialMoves.getMoves(piece);

        return 0;
    }

    // Execute the special to target square according to the piece and the square, updateCastlingRights the board and list accordingly
    public void executeSpecialMove(byte currentSquare, byte targetSquare, LinkedList<Piece> pieceList, Piece[] pieceBoard) {
        if (pieceBoard[currentSquare] instanceof King)
            castlingSpecialMove.execute(currentSquare, targetSquare, pieceBoard, pieceList);
        else
            pawnSpecialMoves.execute(currentSquare,targetSquare,pieceBoard,pieceList);
    }

    // Return whatever or not if target square is a special move square, meaning moving there is castling or en passant move
    public boolean isSpecialMove(byte targetSquare, Piece pieceToMove) {
        if (pieceToMove instanceof Pawn)
            return pawnSpecialMoves.isSpecialMove(targetSquare);

        if (pieceToMove instanceof King)
            return castlingSpecialMove.isCastlingMove(targetSquare);

        return false;
    }
}