// Class that represent a game of chess, using bitboards for each type and color
public class BitBoards {

    private static final BoardUtils utils = new BoardUtils();

    private long whiteKing;
    private long whiteQueens;
    private long whiteRooks;
    private long whiteBishops;
    private long whiteKnights;
    private long whitePawns;
    private long blackKing;
    private long blackQueens;
    private long blackRooks;
    private long blackBishops;
    private long blackKnights;
    private long blackPawns;

    // Given a square, remove the piece on that square
    public void removePieceOnSquare(byte square) {
        movePieceFromCurrentToTargetSquare(square, (byte) -1);
    }

    // Given a current square of a piece and its target square, execute the move and update bit boards accordingly
    // If target square is -1, remove that piece
    public void movePieceFromCurrentToTargetSquare(byte currentSquare, byte targetSquare) {
        long currentBitSquare = utils.getSquarePositionAsBitboardPosition(currentSquare);
        if ((currentBitSquare & whiteKing) != 0)
            whiteKing = (movePiece(whiteKing, currentBitSquare, targetSquare));

        if ((currentBitSquare & whiteQueens) != 0)
            whiteQueens = (movePiece(whiteQueens, currentBitSquare, targetSquare));

        if ((currentBitSquare & whiteRooks) != 0)
            whiteRooks = (movePiece(whiteRooks, currentBitSquare, targetSquare));

        if ((currentBitSquare & whiteBishops) != 0)
            whiteBishops = (movePiece(whiteBishops, currentBitSquare, targetSquare));

        if ((currentBitSquare & whiteKnights) != 0)
            whiteKnights = (movePiece(whiteKnights, currentBitSquare, targetSquare));

        if ((currentBitSquare & whitePawns) != 0)
            whitePawns = (movePiece(whitePawns, currentBitSquare, targetSquare));

        if ((currentBitSquare & blackKing) != 0)
            blackKing = (movePiece(blackKing, currentBitSquare, targetSquare));

        if ((currentBitSquare & blackQueens) != 0)
            blackQueens = (movePiece(blackQueens, currentBitSquare, targetSquare));

        if ((currentBitSquare & blackRooks) != 0)
            blackRooks = (movePiece(blackRooks, currentBitSquare, targetSquare));

        if ((currentBitSquare & blackBishops) != 0)
            blackBishops = (movePiece(blackBishops, currentBitSquare, targetSquare));

        if ((currentBitSquare & blackKnights) != 0)
            blackKnights = (movePiece(blackKnights, currentBitSquare, targetSquare));

        if ((currentBitSquare & blackPawns) != 0)
            blackPawns = (movePiece(blackPawns, currentBitSquare, targetSquare));
    }

    // Given a bit board of all the pieces of the same color, the bitboard of the current piece, and the target square position
    // Move that piece to target square, if target square is -1 remove that piece
    private long movePiece(long bitBoardOfSameTypeAndColorPieces, long bitSquare, byte targetSquare) {
        // Remove current piece position from its bit board
        long bitBoardWithoutCurrent = bitBoardOfSameTypeAndColorPieces & ~bitSquare;
        if (targetSquare == -1)
            return bitBoardWithoutCurrent;

        // Turn the bit on target square as 1
        return bitBoardWithoutCurrent | (1L << targetSquare);
    }

    // Given a color of a player (white = true, black = false), return as bitboards all the pieces of that color
    public long allPiecesWithSameColor(boolean color) {
        return color == BoardUtils.WHITE ? getWhitePieces() : getBlackPieces();
    }

    // Return a long that represent a bitboard with all the white pieces
    public long getWhitePieces() {
        return (whiteKing | whiteQueens | whiteRooks | whiteKnights | whiteBishops | whitePawns);
    }

    // Return a long that represent a bitboard with all the black pieces
    public long getBlackPieces() {
        return (blackKing | blackQueens | blackRooks | blackKnights | blackBishops | blackPawns);
    }

    // Return a long that represent a bitboard of all the pieces
    public long getAllPieces() {
        return (getBlackPieces() | getWhitePieces());
    }


    // get and set method for all the bits boards
    public void setWhiteKing(long whiteKing) {
        this.whiteKing = whiteKing;
    }

    public void setWhiteQueens(long whiteQueens) {
        this.whiteQueens = whiteQueens;
    }

    public void setWhiteRooks(long whiteRooks) {
        this.whiteRooks = whiteRooks;
    }

    public void setWhiteKnights(long whiteKnights) {
        this.whiteKnights = whiteKnights;
    }

    public void setWhiteBishops(long whiteBishops) {
        this.whiteBishops = whiteBishops;
    }

    public void setWhitePawns(long whitePawns) {
        this.whitePawns = whitePawns;
    }

    public void setBlackKing(long blackKing) {
        this.blackKing = blackKing;
    }

    public void setBlackQueens(long blackQueens) {
        this.blackQueens = blackQueens;
    }

    public void setBlackRooks(long blackRooks) {
        this.blackRooks = blackRooks;
    }

    public void setBlackKnights(long blackKnights) {
        this.blackKnights = blackKnights;
    }

    public void setBlackBishops(long blackBishops) {
        this.blackBishops = blackBishops;
    }

    public void setBlackPawns(long blackPawns) {
        this.blackPawns = blackPawns;
    }

    public long getWhiteKing() {
        return whiteKing;
    }

    public long getWhiteQueens() {
        return whiteQueens;
    }

    public long getWhiteRooks() {
        return whiteRooks;
    }

    public long getWhiteKnights() {
        return whiteKnights;
    }

    public long getWhiteBishops() {
        return whiteBishops;
    }

    public long getWhitePawns() {
        return whitePawns;
    }

    public long getBlackKing() {
        return blackKing;
    }

    public long getBlackQueens() {
        return blackQueens;
    }

    public long getBlackRooks() {
        return blackRooks;
    }

    public long getBlackKnights() {
        return blackKnights;
    }

    public long getBlackBishops() {
        return blackBishops;
    }

    public long getBlackPawns() {
        return blackPawns;
    }
}


