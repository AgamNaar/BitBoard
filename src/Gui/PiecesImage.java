package Gui;

import gamelogic.GameLogicUtilities;
import gamelogic.pieces.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Objects;

import static gamelogic.GameLogicUtilities.WHITE;

// A class that represent an object that gets image of the pieces from the folders
public class PiecesImage {

    private static final Image[] whitePiecesImages = new Image[6];
    private static final Image[] blackPiecesImages = new Image[6];
    private static final String KING = "King";
    private static final String QUEEN = "Queen";
    private static final String ROOK = "Rook";
    private static final String BISHOP = "Bishop";
    private static final String KNIGHT = "Knight";
    private static final String PAWN = "Pawn";

    private static final int KING_PIECE_TYPE = 0;
    private static final int QUEEN_PIECE_TYPE = 1;

    private static final int ROOK_PIECE_TYPE = 2;
    private static final int BISHOP_PIECE_TYPE = 3;
    private static final int KNIGHT_PIECE_TYPE = 4;
    private static final int PAWN_PIECE_TYPE = 5;

    // File representing the folder that you select using a FileChooser
    private static final File dir = new File("imageFile");

    // return the collection of images from the file
    public PiecesImage(int size) {
        // make sure it's a directory
        if (dir.isDirectory()) {
            for (final File f : Objects.requireNonNull(dir.listFiles(IMAGE_FILTER))) {
                try {
                    // insert the image to the right place on the collection
                    insertImageToCollection(f, size);
                } catch (final IOException e) {
                    System.out.println("error test");
                }
            }
        }
    }

    private static final String[] EXTENSIONS = new String[]{"gif", "png", "bmp"};

    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = (dir, name) -> {
        for (final String ext : EXTENSIONS) {
            if (name.endsWith("." + ext)) {
                return (true);
            }
        }
        return (false);
    };

    private void insertImageToCollection(File f, int size) throws IOException {
        int type = -1, indexOfPoint;
        boolean color;

        // From file name get the name of the piece
        String fileName = f.getName();
        indexOfPoint = fileName.indexOf(".");
        String pieceTypeString = fileName.substring(1, indexOfPoint);

        switch (pieceTypeString) {
            case KING -> type = KING_PIECE_TYPE;
            case QUEEN -> type = QUEEN_PIECE_TYPE;
            case ROOK -> type = ROOK_PIECE_TYPE;
            case BISHOP -> type = BISHOP_PIECE_TYPE;
            case KNIGHT -> type = KNIGHT_PIECE_TYPE;
            case PAWN -> type = PAWN_PIECE_TYPE;
        }

        // from file name get the color of the piece
        color = fileName.charAt(0) == 'W' ? WHITE : GameLogicUtilities.BLACK;

        if (color == WHITE)
            whitePiecesImages[type] = ImageIO.read(f).getScaledInstance(size, size, Image.SCALE_SMOOTH);
        else
            blackPiecesImages[type] = ImageIO.read(f).getScaledInstance(size, size, Image.SCALE_SMOOTH);
    }

    // return the collection of images from the file
    public Image getImageOfPiece(Piece piece) {
        int pieceType = KING_PIECE_TYPE;

        if (piece instanceof Queen)
            pieceType = QUEEN_PIECE_TYPE;

        if (piece instanceof Rook)
            pieceType = ROOK_PIECE_TYPE;

        if (piece instanceof Bishop)
            pieceType = BISHOP_PIECE_TYPE;

        if (piece instanceof Knight)
            pieceType = KNIGHT_PIECE_TYPE;

        if (piece instanceof Pawn)
            pieceType = PAWN_PIECE_TYPE;

        if (piece.getColor() == WHITE)
            return whitePiecesImages[pieceType];
        else
            return blackPiecesImages[pieceType];

    }

}


