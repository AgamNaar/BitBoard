import gameengine.PieceMove;
import gamelogic.ChessGame;
import gamelogic.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class tempThread extends Thread {
    private final PiecesImage pieceImage;
    private final JButton[][] buttonsBoard;
    private final ChessGame theGame;


    public tempThread(JButton[][] buttonsBoard, ChessGame game) {
        this.pieceImage = new PiecesImage(buttonsBoard[0][0].getHeight());
        this.buttonsBoard = buttonsBoard;
        theGame = game;
    }

    @Override
    public void run() {
        super.run();
        numberOfPossiblePositions(2, theGame);
    }

    /////////////////////////////
    public int numberOfPossiblePositions(int depth, ChessGame game) {
        return numberOfPossiblePositions(depth, game, 0);
    }

    private int numberOfPossiblePositions(int depth, ChessGame game, int numberOfMoves) {
        if (depth == 0)
            return ++numberOfMoves;

        LinkedList<PieceMove> pieceMovesList = getAllPossibleMoves(game);
        depth--;
        for (PieceMove pieceMove : pieceMovesList) {
            ChessGame newGame = new ChessGame(game);
            newGame.executeMove(pieceMove.getPiecePosition(), pieceMove.getTargetSquare());
            updateBoard(newGame.getPieceBoard());
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(numberOfMoves);

            numberOfMoves = numberOfPossiblePositions(depth, newGame, numberOfMoves);
        }
        return numberOfMoves;
    }

    // Given a ChessGame, return a list of all the possible moves in that position
    private LinkedList<PieceMove> getAllPossibleMoves(ChessGame game) {
        LinkedList<PieceMove> pieceMoveList = new LinkedList<>();
        LinkedList<Piece> pieceList = game.getPieceList();
        for (Piece piece : pieceList) {
            // Check if the piece color is the game as the player turn color
            if (piece.getColor() == game.getPlayerToPlay()) {
                byte piecePosition = piece.getSquare();
                pieceMoveList.addAll(transferFromBitBoardMovesToMoves(game.getMovesAsBitBoard(piecePosition), piecePosition));
            }
        }
        return pieceMoveList;
    }

    // Given a bitboard and the piece position, return as a list all the piece moves based on the pieceMovesBitBoards
    private LinkedList<PieceMove> transferFromBitBoardMovesToMoves(long pieceMovesBitBoards, byte piecePosition) {
        LinkedList<PieceMove> pieceMoveList = new LinkedList<>();
        for (byte i = 0; i < 64; i++) {
            if (((1L << i) & pieceMovesBitBoards) != 0)
                pieceMoveList.add(new PieceMove(piecePosition, i));
        }
        return pieceMoveList;
    }

    // Update the colors and piece images of the chess board according to the piece board of the game
    private void updateBoard(Piece[] board) {
        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                if ((row + col) % 2 == 0)
                    buttonsBoard[row][col].setBackground(Color.WHITE);
                else
                    buttonsBoard[row][col].setBackground(Color.GRAY);

                // if piece at that position not null add its image on the right place
                if (board[row + col * 8] != null) {
                    Image pieceImage = this.pieceImage.getImageOfPiece(board[row + col * 8]);
                    buttonsBoard[col][row].setIcon(new ImageIcon(pieceImage));
                } else
                    buttonsBoard[col][row].setIcon(null);
            }
        }
    }
    //////////////////////////////////////////////
}
