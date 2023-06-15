package gamelogic.pieces;

// class containing all the constant for piece evaluation
public class PieceEvaluationConstants {
    protected static final int BISHOP_MOVEMENT_MULTIPLIER = 3;

    protected static final int ROOK_MOVEMENT_MULTIPLIER_EARLY = 2;
    protected static final int ROOK_MOVEMENT_MULTIPLIER_END = 10;

    protected static final int QUEEN_MOVEMENT_MULTIPLIER_EARLY = 1;

    protected static final int QUEEN_MOVEMENT_MULTIPLIER_END = 8;

    protected static final int PAWN_INITIAL_POWER = 100;
    protected static final int KNIGHT_INITIAL_POWER = 200;
    protected static final int BISHOP_INITIAL_POWER = 200;
    protected static final int ROOK_INITIAL_POWER = 350;
    protected static final int QUEEN_INITIAL_POWER = 600;


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

    protected static final int[] KNIGHT_MAP = {0, 0, 0, 0, 0, 0, 0, 0,
            0, 5, 15, 25, 25, 15, 5, 0,
            10, 60, 80, 80, 80, 80, 80, 10,
            20, 80, 120, 80, 80, 120, 80, 20,
            20, 80, 80, 80, 80, 80, 80, 10,
            10, 60, 120, 80, 80, 120, 60, 20,
            0, 5, 15, 25, 25, 15, 5, 0,
            0, 0, 0, 0, 0, 0, 0, 0};

    protected static final int[] PAWN_MAP_EARLY = {0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            -20, 0, 60, 80, 80, -60, -80, -20,
            -10, 0, 20, 40, 40, -40, -40, -10,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0};

    protected static final int[] PAWN_MAP_END = {0, 0, 0, 0, 0, 0, 0, 0,
            350, 300, 250, 250, 250, 250, 300, 350,
            250, 200, 150, 150, 150, 150, 200, 250,
            150, 100, 50, 50, 50, 50, 100, 150,
            100, 50, 60, 50, 50, 50, 50, 100,
            100, 50, 60, 50, 50, 50, 50, 100,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0};

    protected static final int[] NEGATIVE_KING_MAP_EARLY = {600, 600, 600, 600, 600, 600, 600, 600,
            600, 600, 600, 600, 600, 600, 600, 600,
            600, 600, 600, 600, 600, 600, 600, 600,
            400, 400, 400, 400, 400, 400, 400, 400,
            200, 200, 200, 200, 200, 200, 200, 200,
            100, 100, 100, 100, 100, 100, 100, 100,
            50, 50, 50, 50, 50, 50, 50, 50,
            0, -20, -80, 5, 0, 5, -100, 0};

    protected static final int[] NEGATIVE_KING_MAP_END = {150, 100, 100, 100, 100, 100, 100, 150,
            60, 60, 60, 60, 60, 60, 60, 60,
            20, 20, 20, 20, 20, 20, 20, 20,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            20, 20, 20, 20, 20, 20, 20, 20,
            60, 60, 60, 60, 60, 60, 60, 60,
            150, 100, 100, 100, 100, 100, 100, 150};
}
