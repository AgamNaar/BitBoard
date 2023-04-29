package gamelogic;

import gamelogic.pieces.King;
import gamelogic.pieces.Pawn;
import gamelogic.pieces.Piece;

import java.util.LinkedList;

// Class that handle all the aspects of making only legal moves (not walking into a check, using the threat lines and so on)
public class LegalMoveHandler {

    private final LinkedList<Long> treatingKingLines = new LinkedList<>();
    private static final BoardUtils utils = new BoardUtils();

    // Builder set up the initial threat line list
    public LegalMoveHandler(LinkedList<Piece> pieceList, long allPiecesBitBoard, boolean colorOfPlayersTurn) {
        updateTreatingLines(pieceList, allPiecesBitBoard, colorOfPlayersTurn);
    }

    // Given the current square of the piece and the target square, check if it's a valid move or not
    public boolean isValidMove(byte currentSquare, byte targetSquare, Piece[] pieceBoard, boolean colorOfPlayersTurn, long bitBoardLegalMoves) {
        Piece pieceToMove = pieceBoard[currentSquare];

        // Check if the piece is the same color of the player whom turn it is
        if (pieceToMove.getColor() == !colorOfPlayersTurn)
            return false;

        // Check if the target square is a legal move the piece can do
        return (bitBoardLegalMoves & utils.getSquarePositionAsBitboardPosition(targetSquare)) != 0;
    }

    // Given a piece and the bitboard of moves it can do, remove all moves that are illegal
    //  moves are king walking into a check or piece move that will case a check
    public long removeIllegalMoves(long bitBoardMoves, Piece pieceToMove, LinkedList<Piece> pieceList, boolean colorOfPlayersTurn, long allPiecesBitBoard, long playerTurnPiecesBitBoard, boolean isKPlayerTurnKingChecked) {
        long piecePositionAsBitBoard = pieceToMove.getSquareAsBitBoard();
        long enemyKingBitPosition = utils.getKing(!colorOfPlayersTurn, pieceList).getSquareAsBitBoard();
        // If king, remove all squares that enemy piece can go to
        if (pieceToMove instanceof King) {
            long threatenedSquare = threatenedSquare(pieceList, allPiecesBitBoard, colorOfPlayersTurn);
            return bitBoardMoves & ~threatenedSquare;
        }

        if (isKPlayerTurnKingChecked) {
            // Check if moving a piece won't expose the king to a check
            for (Long treatLine : treatingKingLines)
                if ((treatLine & (playerTurnPiecesBitBoard & ~enemyKingBitPosition)) == 0)
                    bitBoardMoves &= treatLine;
        } else {
            // Check if moving a piece won't expose the king to a check
            for (Long treatLine : treatingKingLines) {
                if ((piecePositionAsBitBoard & treatLine) != 0)
                    return bitBoardMoves & treatLine;
            }
        }
        return bitBoardMoves;
    }

    // Update the treating lines
    public void updateTreatingLines(LinkedList<Piece> pieceList, long allPiecesBitBoard, boolean colorOfPlayersTurn) {
        treatingKingLines.clear();
        Piece myKing = utils.getKing(colorOfPlayersTurn, pieceList);
        for (Piece piece : pieceList) {
            long treatKingLine = 0;
            // If piece is enemy piece and line piece, get its treating line
            if (piece.getColor() != colorOfPlayersTurn) {
                treatKingLine = piece.getThreatLines(myKing.getSquare(), allPiecesBitBoard);
            }

            // Only add if treating line is not 0
            if (treatKingLine != 0)
                treatingKingLines.add(treatKingLine);
        }
    }

    // Return as bitboard all the squares that are threatened by enemy player
    private long threatenedSquare(LinkedList<Piece> pieceList, long allPiecesBitBoard, boolean colorOfPlayersTurn) {
        // By removing the king, squares that are threatened beyond him will also be marked
        long bitBoardWithoutKing = allPiecesBitBoard & ~utils.getKing(colorOfPlayersTurn, pieceList).getSquareAsBitBoard();
        long movementBitBoard = 0;
        // For each piece in piece list, added the movement of pieces with same color as player
        for (Piece piece : pieceList)
            if (piece.getColor() == !colorOfPlayersTurn)
                if (piece instanceof Pawn)
                    movementBitBoard |= piece.getMovesAsBitBoard(Long.MAX_VALUE, 0);
                else
                    // By setting friendly pieces to 0, pieces that are protected are also marked
                    movementBitBoard |= piece.getMovesAsBitBoard(bitBoardWithoutKing, 0);

        return movementBitBoard;
    }
}
