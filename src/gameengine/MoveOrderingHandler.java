package gameengine;

import gamelogic.ChessGame;
import gamelogic.GameLogicUtilities;
import gamelogic.GameStatusHandler;
import gamelogic.pieces.King;
import gamelogic.pieces.Pawn;
import gamelogic.pieces.Piece;
import gamelogic.pieces.Rook;

import java.util.Comparator;
import java.util.LinkedList;

// Class responsible for finding the most likely good moves to play
public class MoveOrderingHandler {

    private static final long ROOK_FILE_MASK = 0x8080808080808080L;
    private static final MoreChessGameData gameData = new MoreChessGameData();
    private static final int BACK_MOVE_BONUS = 200;
    private static final int NO_VALUE_MOVE_BONUS = -500;
    private static final int CHECK_BONUS = 200;
    private static final int MOVE_KING_END_GAME_BONUS = 50;
    private static final int CASTLE_KING_EARLY_GAME = 50;
    private static final int MOVING_KING_FOR_NO_REASON = -2000;
    private static final int SEMI_OPEN_FILE_NO_ENEMY_PAWN_BONUS = 15;
    private static final int SEMI_OPEN_FILE_NO_FRIENDLY_PAWN_BONUS = 35;
    private static final int OPEN_FILE_BONUS = 50;
    private static final int PROMOTION_TO_QUEEN = 800;
    private static final int PROMOTION_NOT_TO_A_QUEEN_BONUS = 800;

    // Receive a list of moves and current game
    // Return a list where likely good moves are at the top of the list and bad at the end
    public static void sortMoveByOrderValue(ChessGame game, LinkedList<PieceMove> pieceMoveList) {
        gameData.updateParameters(game);

        for (PieceMove pieceMove : pieceMoveList)
            pieceMove.setMoveAssumedValue(-assumeMoveValue(pieceMove, game));

        pieceMoveList.sort(Comparator.comparingInt(PieceMove::getMoveAssumedValue));
    }

    // try to guess how good a move will be, and set pieceMove moveOrderValue to that value
    private static int assumeMoveValue(PieceMove pieceMove, ChessGame game) {
        int assumedValue = 0;
        boolean isPawn = pieceMove.getPieceToMove() instanceof Pawn;
        long targetSquareBitBoard = GameLogicUtilities.squareAsBitBoard(pieceMove.getTargetSquare());
        long currentSquareBitBoard = GameLogicUtilities.squareAsBitBoard(pieceMove.getCurrentPieceSquare());
        long newSquareAttackSquares = getNewSquareAttackSquare(pieceMove.getPieceToMove(), game, pieceMove);

        // Special case if it's a king
        if (pieceMove.getPieceToMove() instanceof King)
            return assumeKingMoveValue(pieceMove, game);

        // Special bonus for pawns
        if (isPawn)
            assumedValue = assumePawnSpecialBonus(pieceMove, targetSquareBitBoard, newSquareAttackSquares);

        // Special bonus for rook
        if (pieceMove.getPieceToMove() instanceof Rook)
            assumedValue = assumeRookSpecialBonus(targetSquareBitBoard, currentSquareBitBoard);

        assumedValue += assumeCaptureAndIfPieceIsThreaten(pieceMove, game, isPawn, targetSquareBitBoard,
                currentSquareBitBoard);

        // Usually going back is not good
        if (isBackwardMove(pieceMove) && game.getGameStage() != GameStatusHandler.END_GAME)
            assumedValue -= BACK_MOVE_BONUS;

        // New square attacking bonuses
        if (!isPawn)
            assumedValue += newSquareAttackingBonus(pieceMove, newSquareAttackSquares);

        // If the assumed value is 0, probably and a move worth checking  
        return assumedValue == 0 ? NO_VALUE_MOVE_BONUS : assumedValue;
    }

    // Assume the move value of the king
    private static int assumeKingMoveValue(PieceMove pieceMove, ChessGame game) {
        boolean isCastlingMove = game.isCastlingMove(pieceMove.getTargetSquare(), pieceMove.getPieceToMove());
        int assumedValue = 0;

        // If check, probably good idea to move with the king
        if (game.getGameStatus() == GameStatusHandler.CHECK)
            assumedValue += CHECK_BONUS;

        // If it's not end game, and not a castling move, probably bad idea to move the king
        if (isCastlingMove && game.getGameStage() == GameStatusHandler.EARLY_GAME)
            assumedValue += CASTLE_KING_EARLY_GAME;

        // If it's end game, might be good idea to move the king
        if (game.getGameStage() == GameStatusHandler.END_GAME)
            assumedValue += MOVE_KING_END_GAME_BONUS;

        // If it's a free capture, probably good idea
        if (freeCapture(pieceMove, game, gameData.getAllEnemyThreatenSquare()))
            assumedValue += game.getPiece(pieceMove.getTargetSquare()).getPieceValue(game.getAllPieceBitBoard()
                    , game.getGameStage());

        // If there is no good reason to move the king, then it's probably an awful idea to move it
        if (assumedValue == 0)
            return MOVING_KING_FOR_NO_REASON;
        else
            return assumedValue;
    }

    // Check if this move makes a free capture
    private static boolean freeCapture(PieceMove pieceMove, ChessGame game, long threatenSquare) {
        long targetSquareBitBoard = GameLogicUtilities.squareAsBitBoard
                (pieceMove.getTargetSquare());
        // Check if the king can take a piece, if yes it's good
        return (((game.getEnemyBitBoard() & targetSquareBitBoard) != 0)
                && ((targetSquareBitBoard & threatenSquare) == 0));
    }

    // Special bonuses for the rook
    private static int assumeRookSpecialBonus(long targetSquareBitBoard, long currentSquareBitBoard) {
        int bonus = 0;

        // Find the file of the target square
        long mask = ROOK_FILE_MASK;
        while ((mask & targetSquareBitBoard) == 0)
            mask = mask >>> 1;

        boolean isRookOnFile = (mask & currentSquareBitBoard) != 0;

        // Check if it's semi open file were enemy have no pawn on file
        if ((mask & gameData.getMapAccordingToType(Piece.PAWN)) == 0 && isRookOnFile)
            bonus += SEMI_OPEN_FILE_NO_ENEMY_PAWN_BONUS;

        // Check if it's semi open file were you have no pawn on file
        if ((mask & gameData.getMyPawnMap()) == 0 && isRookOnFile)
            bonus += SEMI_OPEN_FILE_NO_FRIENDLY_PAWN_BONUS;

        // Check if it's an open file
        if ((((gameData.getMapAccordingToType(Piece.PAWN) | gameData.getMyPawnMap()) & mask) == 0) && isRookOnFile)
            bonus += OPEN_FILE_BONUS;

        return bonus;
    }

    // Special bonuses for the pawn
    private static int assumePawnSpecialBonus(PieceMove pieceMove, long targetSquareBitBoard,
                                              long newSquareAttackSquares) {
        int assumedValue = 0;

        // Promotion to queen is almost always good
        if (pieceMove.isItPromotionMove())
            if (pieceMove.getTypeOfPieceToPromoteTo() == ChessGame.PROMOTE_TO_QUEEN)
                assumedValue = PROMOTION_TO_QUEEN;
            else
                // It's almost never a good idea to promote to something that's not a queen
                assumedValue = -PROMOTION_NOT_TO_A_QUEEN_BONUS;

        long[] mapArray = gameData.getEnemyPieceByPiecesMapArray();
        // If threaten a piece who can't attack piece back, big bonus
        for (int i = 0; i < mapArray.length; i++) {
            int currentBonus = 0;
            if ((mapArray[i] & newSquareAttackSquares) != 0)
                currentBonus += gameData.threatBonusByPieceType(i);

            if ((targetSquareBitBoard & (gameData.getMyPawnThreatenSquare() & targetSquareBitBoard)) != 0)
                currentBonus = (int) (currentBonus * 1.5);

            assumedValue += currentBonus;
        }

        return assumedValue;
    }

    // Check if it's a backwards move
    private static boolean isBackwardMove(PieceMove pieceMove) {
        // Check the difference between the rows of the current square and target square
        int columnDifference = (pieceMove.getCurrentPieceSquare() / 8) - (pieceMove.getTargetSquare() / 8);
        if (pieceMove.getPieceToMove().getColor())
            return columnDifference > 0;
        else
            return columnDifference < 0;
    }

    // Return the assumed value of moving the piece from its position to the target square based on if the piece is
    // threatened and if it can take a piece and if it's likely that it will be taken bck
    private static int assumeCaptureAndIfPieceIsThreaten(PieceMove pieceMove, ChessGame game, Boolean isPawn,
                                                         long targetSquareBitBoard, long currentSquareBitBoard) {
        int assumedValue = 0;
        long enemyBitBoard = game.getEnemyBitBoard();

        // Check if the move capture enemy piece, the higher the piece value the better
        if ((enemyBitBoard & targetSquareBitBoard) != 0)
            assumedValue += game.getPiece(pieceMove.getTargetSquare()).getPieceValue(game.getAllPieceBitBoard(),
                    game.getGameStage());

        // Capture bonus higher for pawn
        if (isPawn && !(game.getPiece(pieceMove.getTargetSquare()) instanceof Pawn))
            assumedValue += assumedValue;

        // Moving to a square threatened by a pawn is very bad
        if ((targetSquareBitBoard & gameData.getEnemyPawnThreatenSquare()) != 0)
            assumedValue -= pieceMove.getPieceToMoveValue() * 1.5;

        // Moving to a square threatened by a piece is very bad
        if ((targetSquareBitBoard & gameData.getEnemyWithoutSamePieceTypeAndWithoutPawn(0)) != 0) {
            if (!isPawn)
                assumedValue -= pieceMove.getPieceToMoveValue();
            else
                assumedValue -= pieceMove.getPieceToMoveValue() * 1.25;


        }

        // If piece is threatened by a pawn, it's likely very good to move it
        if ((currentSquareBitBoard & gameData.getEnemyPawnThreatenSquare()) != 0)
            assumedValue += pieceMove.getPieceToMoveValue() * 1.25;

        // If piece is threatened by a piece, it's likely good to move it
        if ((currentSquareBitBoard & gameData.getEnemyThreatenedSquareWithoutPawnThreatenSquare()) != 0)
            // If piece is not protected it's better to move, if protected less
            if ((currentSquareBitBoard & gameData.getMyProtectedSquares()) == 0)
                assumedValue += pieceMove.getPieceToMoveValue();
            else {
                // If protected and not a pawn
                if (!isPawn)
                    assumedValue += pieceMove.getPieceToMoveValue() / 2;
            }

        return assumedValue;
    }


    // Given the new square the piece can go after it moved, check what it can attack
    private static int newSquareAttackingBonus(PieceMove pieceMove, long newSquareAttackSquares) {
        int bonus = 0;
        Piece pieceToMove = pieceMove.getPieceToMove();

        long[] mapArray = gameData.getEnemyPieceByPiecesMapArray();
        // If threaten a piece who can't attack piece back, big bonus
        for (int i = 0; i < mapArray.length; i++) {
            if (pieceToMove.getPieceType() != i) {
                if ((newSquareAttackSquares & mapArray[i]) != 0)
                    if (((newSquareAttackSquares & mapArray[i]) & gameData.getAllEnemyThreatenSquare()) == 0)
                        bonus += gameData.threatBonusByPieceType(i);
                    else
                        bonus += gameData.threatBonusByPieceType(i) / 2;
            } else if ((newSquareAttackSquares & mapArray[i]) != 0)
                bonus -= pieceMove.getPieceToMoveValue();
        }
        return bonus;
    }

    // TODO: add documents
    private static long getNewSquareAttackSquare(Piece pieceToMove, ChessGame game, PieceMove pieceMove) {
        pieceToMove.setSquare(pieceMove.getTargetSquare());
        long newSquareAttackSquares = pieceToMove.getMovesAsBitBoard(game.getAllPieceBitBoard(), game.getSameColorPieceBitBoard());
        if (pieceToMove instanceof Pawn)
            newSquareAttackSquares = ((Pawn) pieceToMove).getPawnAttackSquare();
        pieceToMove.setSquare(pieceMove.getCurrentPieceSquare());

        return newSquareAttackSquares;
    }
}
