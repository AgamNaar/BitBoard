package gamelogic;

import gamelogic.pieces.King;
import gamelogic.pieces.Pawn;
import gamelogic.pieces.Piece;

import java.util.LinkedList;

import static gamelogic.GameLogicUtilities.WHITE_PAWN_MOVE_OFFSET;

// Class that handle all the aspects of making only legal moves
// king not walking into a check, not letting piece move and expose their own king to check
public class LegalMoveHandler {

    private final LinkedList<Long> threatLineList = new LinkedList<>();

    // Empty constructor, need to update threat line to initialize
    public LegalMoveHandler() {
    }

    // Given the current square of the piece and the target square, check if it's a valid move or not
    public boolean isValidMove(byte currentSquare, byte targetSquare, Piece[] pieceBoard, boolean colorOfPlayersTurn,
                               long bitBoardLegalMoves) {
        Piece pieceToMove = pieceBoard[currentSquare];

        // Check if the piece is the same color of the player whom turn it is
        if (pieceToMove.getColor() == !colorOfPlayersTurn)
            return false;

        // Check if the target square is a legal move the piece can do
        return (bitBoardLegalMoves & GameLogicUtilities.squareAsBitBoard(targetSquare)) != 0;
    }

    // Given a piece and the bitboard of moves it can do, remove all moves that are illegal
    //  moves are king walking into a check or piece move that will case a check
    public long removeIllegalMoves(long bitBoardMoves, Piece pieceToMove, LinkedList<Piece> pieceList,
                                   boolean colorOfPlayersTurn, long allPiecesBitBoard, long playerTurnPiecesBitBoard,
                                   boolean isKPlayerTurnKingChecked, byte enPassantSquare, Piece king) {

        long piecePositionAsBitBoard = pieceToMove.getSquareAsBitBoard(),
                kingPositionBitBoard = king.getSquareAsBitBoard();

        long enPassantSquareBitBoardPosition = GameLogicUtilities.squareAsBitBoard(enPassantSquare);
        // If king, remove all squares that enemy piece threat
        if (pieceToMove instanceof King) {
            long threatenedSquare = threatenedSquareForKing(pieceList, allPiecesBitBoard, colorOfPlayersTurn, king);
            return bitBoardMoves & ~threatenedSquare;
        }

        if (isKPlayerTurnKingChecked) {
            // flag to check if en-passant need to be added to one of the threat lines
            boolean flag = pieceToMove instanceof Pawn && enPassantSquare != -1
                    && (enPassantSquareBitBoardPosition & bitBoardMoves) != 0;
            // While king check, only if u can stop all checks
            for (Long threatLine : threatLineList) {
                // Find the matching threat line to add it the en-passant square
                if (flag && doesNeedToAddEnPassantToThreatLine(threatLine, enPassantSquareBitBoardPosition,
                        colorOfPlayersTurn))
                    threatLine |= enPassantSquareBitBoardPosition;

                // Check if some piece already block this threat line, if not, piece most block it
                if ((threatLine & (playerTurnPiecesBitBoard & ~kingPositionBitBoard)) == 0)
                    bitBoardMoves &= threatLine;

                // If piece is on a threat line, that piece most stay on that threat line
                if ((threatLine & piecePositionAsBitBoard) != 0)
                    bitBoardMoves &= threatLine;
            }
        } else {
            // Check if moving a piece won't expose the king to a check
            for (Long threatLine : threatLineList) {
                if ((piecePositionAsBitBoard & threatLine) != 0)
                    return bitBoardMoves & threatLine;
            }
        }
        return bitBoardMoves;
    }

    // Update the treating lines
    public void updateTreatingLines(LinkedList<Piece> pieceList, long allPiecesBitBoard, long enemyPiecesBitBoard,
                                    boolean colorOfPlayersTurn, Piece myKing) {

        threatLineList.clear();
        for (Piece piece : pieceList) {
            long treatKingLine = 0;
            // If piece is enemy piece and line piece, get its treating line
            if (piece.getColor() != colorOfPlayersTurn)
                treatKingLine = piece.getThreatLines(myKing.getSquare(), allPiecesBitBoard);

            //  add treating line is not 0, and remove threats line that in the path of them there same color piece
            if (treatKingLine != 0 && (treatKingLine & ~piece.getSquareAsBitBoard() & enemyPiecesBitBoard) == 0)
                threatLineList.add(treatKingLine);
        }
    }

    // The threat line of the pawn who played en-passant, is 8 squares backwards from its threat square
    // it should be the only square of the threat square, and it's the only threat line en-passant should be added to
    private boolean doesNeedToAddEnPassantToThreatLine(Long threatLine, Long enPassantSquareBitBoardPosition,
                                                       boolean colorOfPlayersTurn) {

        if (colorOfPlayersTurn)
            return (threatLine & ~(enPassantSquareBitBoardPosition >> WHITE_PAWN_MOVE_OFFSET)) == 0;

        return (threatLine & ~(enPassantSquareBitBoardPosition << WHITE_PAWN_MOVE_OFFSET)) == 0;
    }

    // Return as bitboard all the squares that are threatened by enemy player
    private long threatenedSquareForKing(LinkedList<Piece> pieceList, long allPiecesBitBoard,
                                         boolean colorOfPlayersTurn, Piece king) {

        // By removing the king, squares that are threatened beyond him will also be marked
        long kingBitBoardSquare = king.getSquareAsBitBoard();
        long bitBoardWithoutKing = allPiecesBitBoard & ~kingBitBoardSquare;
        long movementBitBoard = 0;
        // For each piece in piece list, added the movement of pieces with same color as player
        for (Piece piece : pieceList)
            if (piece.getColor() == !colorOfPlayersTurn)
                if (piece instanceof Pawn)
                    movementBitBoard |= ((Pawn) piece).getPawnAttackSquare();
                else
                    // By setting friendly pieces to 0, pieces that are protected are also marked
                    movementBitBoard |= piece.getMovesAsBitBoard(bitBoardWithoutKing, GameLogicUtilities.EMPTY_BOARD);

        return movementBitBoard;
    }
}
