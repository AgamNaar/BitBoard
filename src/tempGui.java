import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class tempGui extends JFrame {


    private BitBoards bitBoards;
    private PieceMovement pieceMovement;

    private JPanel panel;


    private JButton[][] board;




    public tempGui() {
        super("Grid of Buttons");

        bitBoards = new BitBoards();
        FenTranslator fenTranslator = new FenTranslator();
        fenTranslator.translateFen(bitBoards);
        pieceMovement = new PieceMovement(bitBoards);


        // create a 2D array of 64 buttons
        board = new JButton[8][8];

        // create a panel with a grid layout
        JPanel panel = new JPanel(new GridLayout(8, 8));

        // create the buttons, add them to the array and to the panel
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = new JButton();
                if ((row + col) % 2 == 0) {
                    board[row][col].setBackground(Color.GRAY);
                } else {
                    board[row][col].setBackground(Color.WHITE);
                }
                board[row][col].addActionListener(new ButtonListener());
                panel.add(board[row][col]);
            }
        }

        // add the panel to the frame
        getContentPane().add(panel);

        // set the frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        updateBoard();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new tempGui();
            }
        });
    }

    private int convRowAndColToSquare(int row, int col) {
        return row * 8 + col;
    }

    private long convRowAndColToSquareBit(int row, int col) {
        return (1L << (row * 8 + col));
    }

    private char squareToPiece(long square) {
        if ((square & bitBoards.getWhiteKing()) != 0)
            return 'K';

        if ((square & bitBoards.getWhiteQueens()) != 0)
            return 'Q';

        if ((square & bitBoards.getWhiteRooks()) != 0)
            return 'R';

        if ((square & bitBoards.getWhiteBishops()) != 0)
            return 'B';

        if ((square & bitBoards.getWhiteKnights()) != 0)
            return 'N';

        if ((square & bitBoards.getWhitePawns()) != 0)
            return 'P';

        if ((square & bitBoards.getBlackKing()) != 0)
            return 'k';

        if ((square & bitBoards.getBlackQueens()) != 0)
            return 'q';

        if ((square & bitBoards.getBlackRooks()) != 0)
            return 'r';

        if ((square & bitBoards.getBlackBishops()) != 0)
            return 'b';

        if ((square & bitBoards.getBlackKnights()) != 0)
            return 'n';

        if ((square & bitBoards.getBlackPawns()) != 0)
            return 'p';

        return ' ';
    }

    private long squareToMoves(byte square, long bitSquare) {
        if ((bitSquare & bitBoards.getWhiteKing()) != 0)
            return pieceMovement.getKingMovement(square, true);

        if ((bitSquare & bitBoards.getWhiteQueens()) != 0)
            return pieceMovement.getQueenMovement(square, true);

        if ((bitSquare & bitBoards.getWhiteRooks()) != 0)
            return pieceMovement.getRookMovement(square, true);

        if ((bitSquare & bitBoards.getWhiteBishops()) != 0)
            return pieceMovement.getBishopMovement(square, true);

        if ((bitSquare & bitBoards.getWhiteKnights()) != 0)
            return pieceMovement.getKnightMovement(square, true);

        if ((bitSquare & bitBoards.getWhitePawns()) != 0)
            return pieceMovement.getPawnMovement(square, true);

        if ((bitSquare & bitBoards.getBlackKing()) != 0)
            return pieceMovement.getKingMovement(square, false);

        if ((bitSquare & bitBoards.getBlackQueens()) != 0)
            return pieceMovement.getQueenMovement(square, false);

        if ((bitSquare & bitBoards.getBlackRooks()) != 0)
            return pieceMovement.getRookMovement(square, false);

        if ((bitSquare & bitBoards.getBlackBishops()) != 0)
            return pieceMovement.getBishopMovement(square, false);

        if ((bitSquare & bitBoards.getBlackKnights()) != 0)
            return pieceMovement.getKnightMovement(square, false);

        if ((bitSquare & bitBoards.getBlackPawns()) != 0)
            return pieceMovement.getPawnMovement(square, false);

        return ' ';
    }


    private void updateBoard() {
        for (int row = 7; row >= 0; row--) {
            for (int col = 7; col >= 0; col--) {
                long square = (row * 8) + col;
                long squarebit = 1L << square;
                char piece = squareToPiece(squarebit);
                board[row][col].setText(piece + " ");
            }
        }


    }

    private boolean[] convertLongMovementToArr(long movement) {
        boolean[] map = new boolean[64];
        long mask = 1;
        for (int i = 0; i < 64; i++) {
            if (((mask << i) & movement) != 0)
                map[i] = true;
            else
                map[i] = false;
        }
        return map;
    }


    private void function(int CurrRow, int CurrCol) {
        bitBoards.getWhitePawns();

        long square = (CurrRow * 8) + CurrCol;
        long bitSquare = 1L << square;
        long movement = squareToMoves((byte)square,bitSquare);
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

    }


}
