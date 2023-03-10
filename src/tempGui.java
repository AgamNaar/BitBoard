import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class tempGui extends JFrame {

    private final BitBoards bitBoards;
    private final PieceMovement pieceMovement;
    private final JButton[][] board;

    int preRow = -1, preCol = -1;
    long preMoves = 0;


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
        for (int row = 7; row >= 0; row--) {
            for (int col = 7; col >= 0; col--) {
                board[row][col] = new JButton();
                if ((row + col) % 2 == 0) {
                    board[row][col].setBackground(Color.WHITE);
                } else {
                    board[row][col].setBackground(Color.GRAY);
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
        SwingUtilities.invokeLater(tempGui::new);
    }


    private void updateBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    board[row][col].setBackground(Color.WHITE);
                } else {
                    board[row][col].setBackground(Color.GRAY);
                }
                long square = (row * 8) + col;
                long squareBit = 1L << square;
                char piece = squareToPiece(squareBit);
                board[row][col].setText(piece + " ");
            }
        }


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
            long square = (currRow * 8L) + curCol;
            long bitSquare = 1L << square;
            long movement = squareToMoves((byte) square, bitSquare);
            System.out.println("row: " + currRow + " col: " + curCol);

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
                int squareOffCurr = (currRow * 8) + curCol, preSquare = (preRow * 8) + preCol;
                boolean[] possibleMoves = convertLongMovementToArr(preMoves);
                if (possibleMoves[squareOffCurr]) {
                    executeMove(squareOffCurr, preSquare);
                }
            }

            preCol = -1;
            preRow = -1;
            updateBoard();
        }


    }

    private void executeMove(int squareOffCurr, int preSquare) {
        long bitSquare = 1L << preSquare;

        if ((bitSquare & bitBoards.getWhiteKing()) != 0)
            bitBoards.setWhiteKing(changeVal(bitBoards.getWhiteKing(), bitSquare, squareOffCurr));

        if ((bitSquare & bitBoards.getWhiteQueens()) != 0)
            bitBoards.setWhiteQueens(changeVal(bitBoards.getWhiteQueens(), bitSquare, squareOffCurr));

        if ((bitSquare & bitBoards.getWhiteRooks()) != 0)
            bitBoards.setWhiteRooks(changeVal(bitBoards.getWhiteRooks(), bitSquare, squareOffCurr));

        if ((bitSquare & bitBoards.getWhiteBishops()) != 0)
            bitBoards.setWhiteBishops(changeVal(bitBoards.getWhiteBishops(), bitSquare, squareOffCurr));

        if ((bitSquare & bitBoards.getWhiteKnights()) != 0)
            bitBoards.setWhiteKnights(changeVal(bitBoards.getWhiteKnights(), bitSquare, squareOffCurr));

        if ((bitSquare & bitBoards.getWhitePawns()) != 0)
            bitBoards.setWhitePawns(changeVal(bitBoards.getWhitePawns(), bitSquare, squareOffCurr));

        if ((bitSquare & bitBoards.getBlackKing()) != 0)
            bitBoards.setBlackKing(changeVal(bitBoards.getBlackKing(), bitSquare, squareOffCurr));

        if ((bitSquare & bitBoards.getBlackQueens()) != 0)
            bitBoards.setBlackQueens(changeVal(bitBoards.getBlackQueens(), bitSquare, squareOffCurr));

        if ((bitSquare & bitBoards.getBlackRooks()) != 0)
            bitBoards.setBlackRooks(changeVal(bitBoards.getBlackRooks(), bitSquare, squareOffCurr));

        if ((bitSquare & bitBoards.getBlackBishops()) != 0)
            bitBoards.setBlackBishops(changeVal(bitBoards.getBlackBishops(), bitSquare, squareOffCurr));

        if ((bitSquare & bitBoards.getBlackKnights()) != 0)
            bitBoards.setBlackKnights(changeVal(bitBoards.getBlackKnights(), bitSquare, squareOffCurr));

        if ((bitSquare & bitBoards.getBlackPawns()) != 0)
            bitBoards.setBlackPawns(changeVal(bitBoards.getBlackPawns(), bitSquare, squareOffCurr));
    }

    private long changeVal(long bitBoardOfPiece, long bitSquare, int squareOffCurr) {
        long currPiece = bitBoardOfPiece & ~bitSquare;
        return currPiece | (1L << squareOffCurr);

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

        return 0;
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

        return 0;
    }


}
