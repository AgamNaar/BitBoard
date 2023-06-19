package gamelogic.pieces;

// class containing all the constant for piece evaluation
public class PieceEvaluationConstants {
    protected static final int BISHOP_MOVEMENT_MULTIPLIER = 3;

    protected static final int ROOK_MOVEMENT_MULTIPLIER_EARLY = 2;
    protected static final int ROOK_MOVEMENT_MULTIPLIER_END = 3;

    protected static final int QUEEN_MOVEMENT_MULTIPLIER_EARLY = 1;

    protected static final int QUEEN_MOVEMENT_MULTIPLIER_END = 5;

    public static final int PAWN_INITIAL_POWER = 100;
    public static final int KNIGHT_INITIAL_POWER = 280;
    public static final int BISHOP_INITIAL_POWER = 300;
    public static final int ROOK_INITIAL_POWER = 500;
    public static final int QUEEN_INITIAL_POWER = 850;


    protected static final int[] ROOK_MAP = {50, 50, 50, 50, 50, 50, 50, 50,
            85, 85, 85, 85, 85, 85, 85, 85,
            5, -5, -15, -20, -20, -15, -5, 5,
            5, -5, -15, -20, -20, -15, -5, 5,
            5, -5, -15, -20, -20, -15, -5, 5,
            5, -5, -15, -20, -20, -15, -5, 5,
            0, 0, 0, 0, 0, 0, 0, 0,
            20, 20, 20, 20, 20, 20, 20, 20};

    protected static final int[] BISHOP_MAP = {15, 10, 5, 0, 0, 5, 10, 15,
            15, 15, 10, 5, 5, 10, 15, 15,
            0, 5, 10, 10, 10, 10, 5, 0,
            0, 5, 20, 15, 15, 20, 5, 0,
            0, 5, 20, 15, 15, 20, 5, 0,
            0, 5, 10, 10, 10, 10, 5, 0,
            15, 15, 10, 5, 5, 10, 15, 15,
            15, 10, 5, 0, 0, 5, 10, 15};

    protected static final int[] KNIGHT_MAP_EARLY = {-40, -40, -40, -40, -40, -40, -40, -40,
            -40, -40, -40, -40, -40, -40, -40, -40,
            -40, -40, -40, -40, -40, -40, -40, -40,
            -40, -40, -40, -40, -40, -40, -40, -40,
            -40, -20, -20, -20, -20, -20, -20, -40,
            -20, 20, 60, 10, 10, 60, 20, -20,
            -20, 10, 10, 35, 35, 10, 10, -20,
            -20, 0, -20, -20, -20, -20, 0, -20};

    protected static final int[] KNIGHT_MAP_MID = {-40, -40, -40, -40, -40, -40, -40, -40,
            -40, -40, -40, -40, -40, -40, -40, -40,
            -40, 55, 60, 50, 50, 60, 55, -40,
            -40, 55, 60, 50, 50, 60, 55, -40,
            -40, 55, 60, 50, 50, 60, 55, -40,
            -20, 20, 50, 10, 10, 50, 20, -20,
            -20, 10, 10, 35, 35, 10, 10, -20,
            -20, 0, -20, -20, -20, -20, 0, -20};

    protected static final int[] KNIGHT_MAP_END = {-20, 10, 20, 35, 35, 20, 10, -20,
            -40, -30, -20, -20, -20, -20, -30, -40,
            -40, 30, 40, 50, 50, 40, 30, -40,
            -40, 40, 50, 60, 60, 50, 40, -40,
            -40, 40, 50, 60, 60, 50, 40, -40,
            -20, 30, 40, 50, 50, 40, 30, -40,
            -20, 10, 20, 35, 35, 20, 10, -20,
            -40, -30, -20, -20, -20, -20, -30, -40};

    protected static final int[][] KNIGHT_MAPS = {KNIGHT_MAP_EARLY, KNIGHT_MAP_MID, KNIGHT_MAP_END};

    protected static final int[] PAWN_MAP_EARLY_WHITE = {0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            -20, 0, 60, 70, 80, -60, -80, -20,
            10, 0, 20, 40, 40, -40, -40, 10,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0};

    protected static final int[] PAWN_MAP_EARLY_BLACK = {0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            -20, -80, -60, 80, 70, 60, 0, -20,
            10, -40, -40, 40, 40, 20, 0, 10,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0};

    protected static final int[] PAWN_MAP_MID_WHITE = {0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            -20, 0, 60, 70, 80, -60, -80, -20,
            10, 0, 20, 40, 40, -40, -40, 10,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0};

    protected static final int[] PAWN_MAP_MID_BLACK = {0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 80, 80, 80, 80,
            -20, -80, -60, 80, 70, 60, 0, -20,
            10, -40, -40, 40, 40, 20, 0, 10,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0};

    protected static final int[] PAWN_MAP_END = {0, 0, 0, 0, 0, 0, 0, 0,
            350, 300, 250, 250, 250, 250, 300, 350,
            250, 200, 150, 150, 150, 150, 200, 250,
            150, 100, 75, 75, 75, 75, 75, 150,
            85, 50, 65, 65, 65, 65, 65, 85,
            60, 50, 60, 50, 50, 50, 50, 60,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0};

    protected static final int[][] PAWN_MAPS_WHITE = {PAWN_MAP_EARLY_WHITE, PAWN_MAP_MID_WHITE, PAWN_MAP_END};
    protected static final int[][] PAWN_MAPS_BLACK = {PAWN_MAP_EARLY_BLACK, PAWN_MAP_MID_BLACK, PAWN_MAP_END};

    protected static final int[] NEGATIVE_KING_MAP_EARLY_WHITE = {600, 600, 600, 600, 600, 600, 600, 600,
            600, 600, 600, 600, 600, 600, 600, 600,
            600, 600, 600, 600, 600, 600, 600, 600,
            400, 400, 400, 400, 400, 400, 400, 400,
            200, 200, 200, 200, 200, 200, 200, 200,
            100, 100, 100, 100, 100, 100, 100, 100,
            50, 50, 50, 50, 50, 50, 50, 50,
            0, -20, -80, 50, 0, 50, -100, 0};
    protected static final int[] NEGATIVE_KING_MAP_EARLY_BLACK = {600, 600, 600, 600, 600, 600, 600, 600,
            600, 600, 600, 600, 600, 600, 600, 600,
            600, 600, 600, 600, 600, 600, 600, 600,
            400, 400, 400, 400, 400, 400, 400, 400,
            200, 200, 200, 200, 200, 200, 200, 200,
            100, 100, 100, 100, 100, 100, 100, 100,
            50, 50, 50, 50, 50, 50, 50, 50,
            0, -100, 50, 0, 50, -80, -20, 0};

    protected static final int[] NEGATIVE_KING_MAP_MID = {600, 600, 600, 600, 600, 600, 600, 600,
            600, 600, 600, 600, 600, 600, 600, 600,
            600, 600, 600, 600, 600, 600, 600, 600,
            400, 400, 400, 400, 400, 400, 400, 400,
            200, 200, 200, 200, 200, 200, 200, 200,
            100, 100, 100, 100, 100, 100, 100, 100,
            50, 50, 50, 50, 50, 50, 50, 50,
            0, 0, 0, 0, 0, 0, 0, 0};

    protected static final int[] NEGATIVE_KING_MAP_END = {150, 100, 80, 100, 100, 80, 100, 150,
            100, 75, 50, 60, 60, 50, 75, 100,
            50, 30, 20, 10, 10, 20, 30, 50,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            50, 30, 20, 10, 10, 20, 30, 50,
            100, 75, 50, 60, 60, 50, 75, 100,
            150, 100, 80, 100, 100, 80, 100, 150};

    protected static final int[][] KING_MAPS_WHITE = {NEGATIVE_KING_MAP_EARLY_WHITE, NEGATIVE_KING_MAP_MID, NEGATIVE_KING_MAP_END};
    protected static final int[][] KING_MAPS_BLACK = {NEGATIVE_KING_MAP_EARLY_BLACK, NEGATIVE_KING_MAP_MID, NEGATIVE_KING_MAP_END};

}
