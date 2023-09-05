package Gui;

import gameengine.GameEngine;
import gameengine.PieceMove;
import gamelogic.ChessGame;
import gamelogic.GameStatusHandler;
import gamelogic.pieces.Piece;

import javax.swing.*;
import java.awt.*;

public class ChessBoardGui extends JFrame {
    private final ChessGame game = new ChessGame("");
    private final JButton[][] buttonsBoard;
    private GameEngine gameEngine = new GameEngine();
    private final GameSettings gameSettings = new GameSettings();
    boolean botShowDown = false;

    private final PiecesImage pieceImage;

    byte preSquare = -1;

    public ChessBoardGui() {
        super("Luna 1.0V");

        // Create a 2D array of 64 buttons
        buttonsBoard = new JButton[8][8];

        // Create a panel with a grid layout
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));

        // Create the buttons, add them to the array and to the panel
        for (int row = 7; row >= 0; row--) {
            for (int col = 7; col >= 0; col--) {
                buttonsBoard[row][col] = new JButton();
                buttonsBoard[row][col].setName("" + (row * 8 + col));
                if ((row + col) % 2 == 0) {
                    buttonsBoard[row][col].setBackground(Color.WHITE);
                } else {
                    buttonsBoard[row][col].setBackground(Color.GRAY);
                }
                // When button is pressed, send its name as byte to boardButtonClicked (name is the square position)
                buttonsBoard[row][col].addActionListener(e -> boardButtonClicked(
                        Byte.parseByte(((JButton) e.getSource()).getName())));
                boardPanel.add(buttonsBoard[row][col]);
            }
        }

        // Create a new game button
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> GameSettingsGui.createPopupWindow(gameSettings, this));

        // Create another button
        JButton botBattleButton = new JButton("BOT SHOW DOWN!!!!");
        botBattleButton.addActionListener(e -> botShowDown());


        // Organize components using BorderLayout
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(boardPanel, BorderLayout.CENTER);

        // Create a panel for the buttons at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(newGameButton);
        buttonPanel.add(botBattleButton);

        // Make both buttons take up all horizontal space
        newGameButton.setPreferredSize(new Dimension(200, 40)); // Set a preferred size
        botBattleButton.setPreferredSize(new Dimension(200, 40));

        containerPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the container panel to the frame
        getContentPane().add(containerPanel);

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        // Set up images
        pieceImage = new PiecesImage(buttonsBoard[0][0].getHeight());

        // Launch new game setting GUI
        GameSettingsGui.createPopupWindow(gameSettings, this);
    }

    // Will take over the game, and the engine will play vs itself with the current game settings
    private void botShowDown() {
        gameEngine.interrupt();
        botShowDown = true;

        newGameEngineSearch(true);
    }


    // Will start a new game engine search for best move, either with or without time limit
    public void newGameEngineSearch(boolean withTimeLimit) {
        // End previous search if exist
        if (gameEngine != null)
            gameEngine.interrupt();

        // Create new search with the game settings, and with or without time limit
        gameEngine = new GameEngine(game, this, gameSettings.startingDepthForSearch,
                gameSettings.engineTimeToThink, withTimeLimit);
        gameEngine.start();
    }

    // bot will play its turn now, and when finish will start a search while player is playing
    public void playBotTurn() {
        // First, end bot thinking phase
        gameEngine.interrupt();
        // Get best move found and execute it
        PieceMove move = gameEngine.getBestMove();
        game.executeMove(move.getCurrentPieceSquare(), move.getTargetSquare(), move.getTypeOfPieceToPromoteTo());
        afterMoveHandle(game.getGameStatus(), move.getTargetSquare(), move.getCurrentPieceSquare());
        // Check if the game is not over, and start a thinking search while the player is playing
        if (!game.isGameOver()) {
            newGameEngineSearch(false);
        }
    }

    // Update the colors and piece images of the chess board according to the piece board of the game
    private void updateBoard() {
        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                if ((row + col) % 2 == 0)
                    buttonsBoard[row][col].setBackground(Color.WHITE);
                else
                    buttonsBoard[row][col].setBackground(Color.GRAY);

                Piece currentPiece = game.getPiece(row + col * 8);
                // if piece at that position not null add its image on the right place
                if (currentPiece != null) {
                    Image pieceImage = this.pieceImage.getImageOfPiece(currentPiece);
                    buttonsBoard[col][row].setIcon(new ImageIcon(pieceImage));
                } else
                    buttonsBoard[col][row].setIcon(null);
            }
        }
    }

    // function to handle a press of a button that is a part of the piece bitboard
    // if it's the first click, show moves of the piece (according to color)
    // if second click, execute the move
    private void boardButtonClicked(byte square) {
        // Check if player chose a square already and if it's a piece he can play, and not bot show down
        if (preSquare == -1 && (game.getPlayerToPlay() ^ gameSettings.engineColorToPlay) && !botShowDown) {
            // If you get a possible move, show it on the board
            long movement = game.getLegalMovesAsBitBoard(square);
            if (movement != 0) {
                paintPieceMoveSquares(movement);
                preSquare = square;
            }
        } else {
            // This is the case a piece was already chosen, check if it's a possible move the piece can do
            int gameStatus = game.executeMove(preSquare, square, ChessGame.PROMOTE_TO_QUEEN);
            afterMoveHandle(gameStatus, square, preSquare);
            preSquare = -1;
            System.out.println(gameStatus);
        }
    }

    // Handle the gui after a move was played
    private void afterMoveHandle(int gameStatus, byte targetSquare, byte currentSquare) {
        updateBoard();

        if (gameStatus == ChessGame.MOVE_NOT_EXECUTED)
            return;

        if (gameStatus == GameStatusHandler.CHECK || gameStatus == GameStatusHandler.CHECKMATE)
            paintSquare(Color.decode("#A44040"), game.getPlayerTurnKingSquare());

        paintSquare(Color.YELLOW, targetSquare);
        paintSquare(Color.YELLOW, currentSquare);

        // If it's the Engine turn now, run a search with time limit
        if (game.getPlayerToPlay() == gameSettings.engineColorToPlay || botShowDown) {
            newGameEngineSearch(true);
        }
    }

    // Given a long that represent a movement of a piece as bit board, show on the board the possible moves
    private void paintPieceMoveSquares(long pieceBitMovement) {
        for (byte i = 0; i < 64; i++)
            if (((1L << i & pieceBitMovement) != 0))
                paintSquare(Color.decode("#DBD6D6"), i);
    }

    // Given a square and color, paint that square with the color given
    private void paintSquare(Color color, byte square) {
        int row = square / 8, col = square % 8;
        buttonsBoard[row][col].setBackground(color);
    }

    // Start a new game of chess
    public void startGame() {
        botShowDown = false;
        game.reset(gameSettings.fenStartingPosition);
        gameEngine.resetEngine();
        updateBoard();
        // If the bot is white, on the first search it needs to be with time limit, if black without
        newGameEngineSearch(gameSettings.engineColorToPlay);
    }
}
