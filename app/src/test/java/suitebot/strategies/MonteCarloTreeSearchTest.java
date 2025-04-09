package suitebot.strategies;

import org.junit.jupiter.api.Test;
import suitebot.game.Direction;
import suitebot.game.GameState;
import suitebot.game.GameStateFactory;

import java.util.Map;

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
}
