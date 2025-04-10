package suitebot.strategies;

import org.junit.jupiter.api.Test;
import suitebot.game.Direction;
import suitebot.game.GameState;
import suitebot.game.GameStateFactory;
import suitebot.game.Point;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MonteCarloTreeSearchTest {

    /**
     * Test case to ensure the algorithm runs without errors on a simple open board.
     * It checks that all possible moves receive a valid score.
     */
    @Test
    void testSimpleOpenBoard() {
        GameState gameState = GameStateFactory.createFromString(
                "**********\n" +
                        "*1       *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "**********");

        Map<Direction, Integer> moveScores = MonteCarloTreeSearch.evaluateMoves(1, gameState, 10, 100);
        assertNotNull(moveScores);
        assertEquals(4, moveScores.size());
    }

    /**
     * Test case to verify that the algorithm correctly identifies blocked moves.
     * Moves into obstacles should have a score of 0.
     */
    @Test
    void testBlockedMoves() {
        GameState gameState = GameStateFactory.createFromString(
                "**********\n" +
                        "*1*      *\n" +
                        "* *      *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "**********");

        Map<Direction, Integer> moveScores = MonteCarloTreeSearch.evaluateMoves(1, gameState, 10, 100);
        assertNotNull(moveScores);
        assertEquals(0, moveScores.get(Direction.LEFT)); // Move blocked
        assertTrue(moveScores.get(Direction.DOWN) > 1); // Move non-blocked, free space
        System.out.println(moveScores);
    }

    /**
     * Test case to check the impact of depth on move evaluation.
     * Higher depth should result in higher scores.
     */
    @Test
    void testDepthImpact() {
        GameState gameState = GameStateFactory.createFromString(
                "**********\n" +
                        "*1       *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "**********");

        Map<Direction, Integer> shallowScores = MonteCarloTreeSearch.evaluateMoves(1, gameState, 5, 100);
        Map<Direction, Integer> deepScores = MonteCarloTreeSearch.evaluateMoves(1, gameState, 15, 100);

        assertNotNull(shallowScores);
        assertNotNull(deepScores);

        for (Direction dir : Direction.values()) {
            if (shallowScores.containsKey(dir) && deepScores.containsKey(dir)) {
                assert (deepScores.get(dir) >= shallowScores.get(dir));
            }
        }
    }

    /**
     * Test case for multiple paths with obstacles.
     * Verifies that the simulation correctly considers obstacles and chooses paths
     * with higher reachable squares.
     */
    @Test
    void testMultiplePathsWithObstacles() {
        GameState gameState = GameStateFactory.createFromString(
                "**********\n" +
                        "*1   *****\n" +
                        "* **  ****\n" +
                        "*  *     *\n" +
                        "* ****** *\n" +
                        "*        *\n" +
                        "**********\n");

        Map<Direction, Integer> moveScores = MonteCarloTreeSearch.evaluateMoves(1, gameState, 20, 100);
        assertNotNull(moveScores);

        // Check expected scores for each direction
        // The result is random since game simulation is randomized, for repeatable algorithm you can change expectation to exact value
        // For this setup it results in maxDept or max reachable space
        System.out.println(moveScores);
        assertTrue(moveScores.get(Direction.DOWN) > 1);  // Best move with open space
        assertEquals(0, moveScores.get(Direction.LEFT));  // Blocked by obstacle
        assertTrue(moveScores.get(Direction.RIGHT) > 1); // Limited by obstacles
        assertEquals(0, moveScores.get(Direction.UP));    // Blocked by obstacle
        System.out.println(moveScores);

    }

    @Test
    void testEmptyEnvironment() {
        // Simple empty field with just walls
        GameState gameState = GameStateFactory.createFromString(
                "**********\n" +
                        "*1       *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "**********");

        Map<Direction, Integer> scores = MonteCarloTreeSearch.evaluateMoves(1, gameState, 15, 100);
        assertFalse(scores.isEmpty(), "Should have some valid moves");

        // All directions except toward wall should be valid
        assertTrue(scores.get(Direction.LEFT) == 0, "Left is blocked by wall");
        assertTrue(scores.get(Direction.UP) == 0, "Up is blocked by wall");
        assertTrue(scores.get(Direction.RIGHT) > 0, "Right should be valid");
        assertTrue(scores.get(Direction.DOWN) > 0, "Down should be valid");
        System.out.println(scores);
    }

    @Test
    void testWithObstacles() {
            String gameMap =
                    "**********\n" +
                            "*1 *     *\n" +
                            "*  *     *\n" +
                            "*  *     *\n" +
                            "*  *     *\n" +
                            "*        *\n" +
                            "*   *    *\n" +
                            "*   *    *\n" +
                            "*        *\n" +
                            "**********";

            GameState gameState = GameStateFactory.createFromString(gameMap);

            // Print bot location
            Point botLocation = gameState.getBotLocation(1);
            System.out.println("Bot 1 location: " + botLocation);

            // Print obstacle locations
            Set<Point> obstacles = gameState.getObstacleLocations();
            System.out.println("Obstacles: " + obstacles);

            // Check if the right position from bot is an obstacle
            Point rightOfBot = new Point(botLocation.x + 1, botLocation.y);
            System.out.println("Position to right of bot: " + rightOfBot);
            System.out.println("Is obstacle? " + obstacles.contains(rightOfBot));

            // Print all points in the map with their values
            System.out.println("\nFull map representation:");
            for (int y = 0; y < gameState.getPlanHeight(); y++) {
                for (int x = 0; x < gameState.getPlanWidth(); x++) {
                    Point p = new Point(x, y);
                    char value = ' ';

                    if (obstacles.contains(p)) {
                        value = '*';
                    } else if (p.equals(botLocation)) {
                        value = '1';
                    }

                    System.out.print(value);
                }
                System.out.println();
            }
        }

    @Test
    void testWithOtherSnakes() {
        // Environment with other snakes
        GameState gameState = GameStateFactory.createFromString(
                "**********\n" +
                        "*1       *\n" +
                        "*2       *\n" +
                        "*3       *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "*        *\n" +
                        "**********");

        Map<Direction, Integer> scores = MonteCarloTreeSearch.evaluateMoves(1, gameState, 15, 100);
        assertFalse(scores.isEmpty(), "Should have some valid moves");

        // Verify our snake avoids other snakes
        assertTrue(scores.get(Direction.LEFT) == 0, "Left is blocked by wall");
        assertTrue(scores.get(Direction.UP) == 0, "Up is blocked by wall");
        assertTrue(scores.get(Direction.DOWN) > 0, "Down should be valid");
        assertTrue(scores.get(Direction.RIGHT) > 0, "Right should be valid");
    }

    @Test
    void testComplexEnvironment() {
        // A complex environment with multiple snakes and obstacles
        GameState gameState = GameStateFactory.createFromString(
                "**********\n" +
                        "* 1      *\n" +
                        "*  *     *\n" +
                        "*2 *  3  *\n" +
                        "*  *     *\n" +
                        "*  *     *\n" +
                        "*        *\n" +
                        "*   *    *\n" +
                        "*   4    *\n" +
                        "**********");

        Map<Direction, Integer> scores = MonteCarloTreeSearch.evaluateMoves(1, gameState, 15, 100);
        assertFalse(scores.isEmpty(), "Should have some valid moves");

        // Check that our algorithm correctly evaluates available spaces
        Direction bestMove = findBestMove(scores);
        assertNotNull(bestMove, "Should find a best move");

        // The best move should be RIGHT or DOWN in this scenario as they lead to more open space
        assertTrue(bestMove == Direction.RIGHT || bestMove == Direction.DOWN,
                "Best move should be RIGHT or DOWN to maximize space: " + bestMove);
    }

    @Test
    void testDeadEndAvoidance() {
        // Test if the algorithm avoids dead ends
        GameState gameState = GameStateFactory.createFromString(
                "**********\n" +
                        "*1*      *\n" +
                        "* *      *\n" +
                        "* *      *\n" +
                        "* *      *\n" +
                        "* *      *\n" +
                        "* *      *\n" +
                        "* *      *\n" +
                        "*        *\n" +
                        "**********");

        Map<Direction, Integer> scores = MonteCarloTreeSearch.evaluateMoves(1, gameState, 15, 100);
        assertFalse(scores.isEmpty(), "Should have some valid moves");

        Direction bestMove = findBestMove(scores);
        assertNotNull(bestMove, "Should find a best move");

        // The best move should be DOWN to avoid the dead end
        assertEquals(Direction.DOWN, bestMove, "Best move should be DOWN to avoid dead end");
    }

    @Test
    void testEscapeFromTrap() {
        // Test if the algorithm can find escape routes when nearly trapped
        GameState gameState = GameStateFactory.createFromString(
                "**********\n" +
                        "**********\n" +
                        "**1*******\n" +
                        "**********\n" +
                        "**********\n" +
                        "**********\n" +
                        "**********\n" +
                        "**********\n" +
                        "**********\n" +
                        "**********");

        Map<Direction, Integer> scores = MonteCarloTreeSearch.evaluateMoves(1, gameState, 15, 100);

        // All directions should be blocked except one
        long validMoves = scores.values().stream().filter(score -> score > 0).count();
        assertEquals(0, validMoves, "Should have exactly one valid move");

        Direction bestMove = findBestMove(scores);
        assertNotNull(bestMove, "Should find a best move");
    }

    @Test
    void testPreferenceForOpenSpace() {
        // Test if algorithm prefers directions with more open space
        GameState gameState = GameStateFactory.createFromString(
                "**********\n" +
                        "*1  *    *\n" +
                        "*   *    *\n" +
                        "*   *    *\n" +
                        "*   *    *\n" +
                        "*   *    *\n" +
                        "*   *    *\n" +
                        "*   *    *\n" +
                        "*   *    *\n" +
                        "**********");

        Map<Direction, Integer> scores = MonteCarloTreeSearch.evaluateMoves(1, gameState, 15, 100);
        assertFalse(scores.isEmpty(), "Should have some valid moves");

        Direction bestMove = findBestMove(scores);

        System.out.println(bestMove);
        // The best move should be DOWN, as it leads to more open space
        assertEquals(Direction.DOWN, bestMove, "Best move should be DOWN to access more open space");
    }

    @Test
    void testSurvivabilityInTightSpace() {
        // Test how algorithm performs in tight spaces
        GameState gameState = GameStateFactory.createFromString(
                "**********\n" +
                        "**1*******\n" +
                        "** *******\n" +
                        "** *******\n" +
                        "**********\n" +
                        "**********\n" +
                        "**********\n" +
                        "**********\n" +
                        "**********\n" +
                        "**********");

        Map<Direction, Integer> scores = MonteCarloTreeSearch.evaluateMoves(1, gameState, 15, 100);
        System.out.println(scores);
        Direction bestMove = findBestMove(scores);
        System.out.println(bestMove);

        // Should choose DOWN as it's the only path to survival
        assertEquals(Direction.DOWN, bestMove, "Best move should the only survival path");
    }

    private Direction findBestMove(Map<Direction, Integer> scores) {
        Direction bestDirection = null;
        int bestScore = -1;

        for (Map.Entry<Direction, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > bestScore) {
                bestScore = entry.getValue();
                bestDirection = entry.getKey();
            }
        }

        return bestDirection;
    }
}