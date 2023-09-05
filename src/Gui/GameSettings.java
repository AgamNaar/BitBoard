package Gui;

// Class that represent game settings of the game
// 1. The color that the engine is playing (white/black)
// 2. The time giving the engine to think
// 3. Starting depth of the search
// 4. String that represent the FEN of the starting position
public class GameSettings {
    boolean engineColorToPlay;
    int engineTimeToThink;
    int startingDepthForSearch;
    String fenStartingPosition;

    // Empty builder
    public GameSettings() {
    }
}
