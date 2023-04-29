import gameengine.GameEngine;
import gamelogic.ChessGame;
import gamelogic.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class tempGui extends JFrame {

    private static final String fen = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -  ";

    private static final ChessGame game = new ChessGame(fen);
    private final JButton[][] buttonsBoard;

    private final PiecesImage pieceImage;

    byte preSquare = -1;

    public tempGui() {
        super("Grid of Buttons");
        // create a 2D array of 64 buttons
        buttonsBoard = new JButton[8][8];

        // create a panel with a grid layout
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));

        // create the buttons, add them to the array and to the panel
        for (int row = 7; row >= 0; row--) {
            for (int col = 7; col >= 0; col--) {
                buttonsBoard[row][col] = new JButton();
                if ((row + col) % 2 == 0) {
                    buttonsBoard[row][col].setBackground(Color.WHITE);
                } else {
                    buttonsBoard[row][col].setBackground(Color.GRAY);
                }
                buttonsBoard[row][col].addActionListener(new ButtonListener());
                boardPanel.add(buttonsBoard[row][col]);
            }
        }

        // create a "Reset" button and add it to a separate panel
        JPanel resetPanel = new JPanel();
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ResetButtonListener());
        resetPanel.add(resetButton);

        // create a container panel and add the buttonsBoard panel and reset panel to it
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.add(boardPanel);
        containerPanel.add(resetPanel);

        // add the container panel to the frame
        getContentPane().add(containerPanel);

        // set the frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 450); // increased height to accommodate the "Reset" button
        setLocationRelativeTo(null);
        setVisible(true);

        pieceImage = new PiecesImage(buttonsBoard[0][0].getHeight());

        updateBoard();


        int DEAPTH = 4;
        GameEngine engine = new GameEngine();
        for (int i = 1; i <= DEAPTH; i++)
            System.out.println(engine.numberOfPossiblePositions(i, game));

        //Thread thread = new tempThread(buttonsBoard,game);
        //thread.start();
    }

    // ActionListener for the "Reset" button
    private class ResetButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            game.reset(fen);
            updateBoard();
        }
    }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            for (int row = 0; row < 8; row++)
                for (int col = 0; col < 8; col++)
                    if (e.getSource() == buttonsBoard[row][col])
                        boardButtonClicked(row, col);
        }
    }

    // Update the colors and piece images of the chess board according to the piece board of the game
    private void updateBoard() {
        Piece[] board = game.getPieceBoard();
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

    // convert a long to a 64 boolean array, where the bit is on the long will be true on the array
    private boolean[] convertLongMovementToArr(long movement) {
        boolean[] map = new boolean[64];
        long mask = 1;
        for (int i = 0; i < 64; i++) {
            map[i] = ((mask << i) & movement) != 0;
        }
        return map;
    }

    // function to handle a press of a button that is a part of the piece bitboard
    // if it's the first click, show moves of the piece (according to color)
    // if second click, execute the move
    private void boardButtonClicked(int currRow, int curCol) {
        if (preSquare == -1) {
            byte square = (byte) ((currRow * 8) + curCol);
            long movement = game.getMovesAsBitBoard(square);
            if (movement != 0) {
                paintPieceMoveSquares(convertLongMovementToArr(movement));
                preSquare = square;
            }
        } else {
            byte targetSquare = (byte) ((currRow * 8) + curCol), currentSquare = preSquare;
            int gameStatus = game.executeMove(currentSquare, targetSquare);
            updateBoard();
            afterMoveHandle(gameStatus, targetSquare, currentSquare);
            preSquare = -1;
        }
    }

    private void afterMoveHandle(int gameStatus, byte targetSquare, byte currentSquare) {
        if (gameStatus == ChessGame.MOVE_NOT_EXECUTED)
            return;

        if (gameStatus == ChessGame.CHECK || gameStatus == ChessGame.CHECKMATE)
            paintSquare(Color.decode("#A44040"), game.getPlayerTurnKingSquare());

        if (gameStatus == ChessGame.CHECK)
            makeSound(gameStatus);

        paintSquare(Color.YELLOW, targetSquare);
        paintSquare(Color.YELLOW, currentSquare);

    }

    //TODO: for making sound
    private void makeSound(int gameStatus) {
    }

    private void paintSquare(Color color, byte square) {
        int row = square / 8, col = square % 8;
        buttonsBoard[row][col].setBackground(color);
    }

    private void paintPieceMoveSquares(boolean[] possibleMoves) {
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++)
                if (possibleMoves[(row * 8) + col])
                    buttonsBoard[row][col].setBackground(Color.decode("#DBD6D6"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(tempGui::new);
    }
}
