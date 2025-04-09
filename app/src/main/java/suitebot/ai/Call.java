package suitebot.ai;

import suitebot.game.Direction;
import suitebot.game.GameState;
import suitebot.strategies.MonteCarloTreeSearch;

import java.util.*;

public class Call {
    // Original spiral movement variables (kept for fallback)
    static int phase = 1; // Keeps track of U/R or D/L phases
    static int count = 1; // Number of actions to return in each segment
    static int printed = 0; // How many actions have been returned in current segment

    // MCTS configuration
    private static final int SIMULATION_DEPTH = 15; // How deep to simulate
    private static final int ITERATIONS = 100; // How many iterations to run
    private static final int BOT_ID = 1; // Assuming this is our bot's ID

    // Game state cache
    private static GameState lastGameState = null;
    private static Direction lastDirection = null;

    /**
     * Main method to get the next direction for the snake
     * Uses Monte Carlo Tree Search to evaluate the best move
     */
    public static Direction getDirection(GameState gameState) {
        // If we have a valid game state, use MCTS
        if (gameState != null) {
            lastGameState = gameState;
            Map<Direction, Integer> moveScores = MonteCarloTreeSearch.evaluateMoves(BOT_ID, gameState, SIMULATION_DEPTH, ITERATIONS);

            // Find the direction with the highest score
            Direction bestDirection = null;
            int bestScore = -1;

            for (Map.Entry<Direction, Integer> entry : moveScores.entrySet()) {
                if (entry.getValue() > bestScore) {
                    bestScore = entry.getValue();
                    bestDirection = entry.getKey();
                }
            }

            // If we found a valid direction, use it
            if (bestDirection != null && bestScore > 0) {
                lastDirection = bestDirection;
                System.out.println(bestDirection);
                return bestDirection;
            }
        }

        // Fallback to spiral pattern if MCTS doesn't find a good move
        // or if we don't have a game state
        return getSpiralDirection();
    }

    /**
     * Original spiral movement method (kept as fallback)
     */
    private static Direction getSpiralDirection() {
        Direction direction;

        if (phase % 2 == 1) { // UR phase
            if (printed < count) {
                direction = Direction.UP;
            } else {
                direction = Direction.RIGHT;
            }
        } else { // DL phase
            if (printed < count) {
                direction = Direction.DOWN;
            } else {
                direction = Direction.LEFT;
            }
        }

        printed++;

        if (printed == count * 2) {
            printed = 0;
            count++;
            phase++;
        }

        return direction;
    }

    /**
     * Legacy method for compatibility with original code
     */
    public static Direction getDirection() {
        if (lastGameState != null && lastDirection != null) {
            return lastDirection;
        }
        return getSpiralDirection();
    }
}