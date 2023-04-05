import Pieces.*;
import Utils.BoardUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class tempGui extends JFrame {

    private static final ChessGame game = new ChessGame();
    private final JButton[][] board;
    private static final BoardUtils utils = new BoardUtils();

    int preRow = -1, preCol = -1;
    long preMoves = 0;


    public tempGui() {
        super("Grid of Buttons");
        // create a 2D array of 64 buttons
        board = new JButton[8][8];

        // create a panel with a grid layout
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));

        // create the buttons, add them to the array and to the panel
        for (int row = 7; row >= 0; row--) {
            for (int col = 7; col >= 0; col--) {
                board[row][col] = new JButton();
                if ((row + col) % 2 == 0) {
                    board[row][col].setBackground(Color.WHITE);
                } else {
                    board[row][col].setBackground(Color.GRAY);
                }
                board[row][col].addActionListener(new ButtonListener());
                boardPanel.add(board[row][col]);
            }
        }

        // create a "Reset" button and add it to a separate panel
        JPanel resetPanel = new JPanel();
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ResetButtonListener());
        resetButton.setPreferredSize(new Dimension(80, 30)); // set preferred size
        resetPanel.add(resetButton);

        // create a container panel and add the board panel and reset panel to it
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

        updateBoard();
    }

    // ActionListener for the "Reset" button
    private class ResetButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            game.reset();
            updateBoard();
        }
    }


    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (e.getSource() == board[row][col]) {
                        function(row, col);
                        return;
                    }
                }
            }
        }
    }

    private void updateBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    board[row][col].setBackground(Color.WHITE);
                } else {
                    board[row][col].setBackground(Color.GRAY);
                }
                board[row][col].setText(" ");
            }
        }
        addPieces();
    }

    private boolean[] convertLongMovementToArr(long movement) {
        boolean[] map = new boolean[64];
        long mask = 1;
        for (int i = 0; i < 64; i++) {
            map[i] = ((mask << i) & movement) != 0;
        }
        return map;
    }


    private void function(int currRow, int curCol) {
        if (preCol == -1 && preRow == -1) {
            byte square = (byte) ((currRow * 8) + curCol);
            long movement = game.getMovesAsBitBoard(square);
            if (movement != 0) {
                boolean[] possibleMoves = convertLongMovementToArr(movement);
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        if (possibleMoves[(row * 8) + col]) {
                            board[row][col].setBackground(Color.YELLOW);
                        }
                    }
                }
            }
            preRow = currRow;
            preCol = curCol;
            preMoves = movement;
        } else {
            if (preMoves != 0) {
                byte targetSquare = (byte) ((currRow * 8) + curCol), currentSquare = (byte) ((preRow * 8) + preCol);
                boolean[] possibleMoves = convertLongMovementToArr(preMoves);
                if (possibleMoves[targetSquare]) {
                    int gameStatus = game.executeMove(currentSquare, targetSquare);
                    // System.out.println(gameStatus);
                }
            }
            preCol = -1;
            preRow = -1;
            updateBoard();
        }
    }

    private void addPieces() {
        LinkedList<Piece> pieceList = game.getPieceList();
        for (Piece piece : pieceList) {
            String pieceSymbol = "";

            if (piece.getColor() == BoardUtils.WHITE) {
                if (piece instanceof King)
                    pieceSymbol = "K";
                if (piece instanceof Queen)
                    pieceSymbol = "Q";
                if (piece instanceof Rook)
                    pieceSymbol = "R";
                if (piece instanceof Bishop)
                    pieceSymbol = "B";
                if (piece instanceof Knight)
                    pieceSymbol = "N";
                if (piece instanceof Pawn)
                    pieceSymbol = "P";
            }

            if (piece.getColor() == BoardUtils.BLACK) {
                if (piece instanceof King)
                    pieceSymbol = "k";
                if (piece instanceof Queen)
                    pieceSymbol = "q";
                if (piece instanceof Rook)
                    pieceSymbol = "r";
                if (piece instanceof Bishop)
                    pieceSymbol = "b";
                if (piece instanceof Knight)
                    pieceSymbol = "n";
                if (piece instanceof Pawn)
                    pieceSymbol = "p";
            }

            int row = utils.getRowOfSquare(piece.getSquare()), col = utils.getColOfSquare(piece.getSquare());
            board[row][col].setText(pieceSymbol);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(tempGui::new);
    }
}
