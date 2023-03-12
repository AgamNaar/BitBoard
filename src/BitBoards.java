// Class that represent a game of chess, using bitboards for each type and color
public class BitBoards {

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


