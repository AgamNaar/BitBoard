package gameengine;

import Gui.ChessBoardGui;

// Monitor class for GameEngine thread, to let it know to stop searching when the time is over
public class GameEngineMonitor extends Thread {

    private static final long SECOND_TO_MILLISECOND_MULTIPLIER = 1000;

    private final GameEngine gameEngine;

    private final int timeToKIllInSecond;
    private final ChessBoardGui gui;

    // Builder, get gameEngine to interrupt it, and the gui that when the search is over to play that move
    public GameEngineMonitor(GameEngine gameEngine, ChessBoardGui gui, int searchTimeSecond) {
        timeToKIllInSecond = searchTimeSecond;
        this.gameEngine = gameEngine;
        this.gui = gui;
    }

    // Run of monitor thread, sleep the time given to the engine to find a move, and interrupt it when its over
    @Override
    public void run() {
        try {
            sleep(timeToKIllInSecond * SECOND_TO_MILLISECOND_MULTIPLIER);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // stop the search and notify the gui
            gameEngine.interrupt();
            gui.playBotTurn();
        }
    }
}
