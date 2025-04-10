package suitebot.ai;

import suitebot.game.Direction;
import suitebot.game.GameState;
import suitebot.game.Point;
import suitebot.strategies.MonteCarloTreeSearch;

import java.util.*;

/**
 * Call class that integrates the Monte Carlo Tree Search algorithm
 * for determining the best direction for the bot to move in a multi-snake game.
 */
public class Call {

    // Configuration for the MCTS algorithm
    private static final int SIMULATION_DEPTH = 15;
    private static final int SIMULATION_ITERATIONS = 120;

    // Additional parameters for multi-snake strategy
    private static final double ENEMY_AVOIDANCE_WEIGHT = 1.5; // Higher values prioritize avoiding other snakes

    /**
     * Uses Monte Carlo Tree Search to determine the best direction for the bot to move
     * in a game with multiple snakes.
     *
     * @param gameState The current state of the game
     * @return The best direction to move based on MCTS evaluation
     */
    public static Direction getDirection(GameState gameState) {
        // Get our bot ID from the calling context
        int botId = getBotId(gameState);

        // Evaluate all possible moves using MCTS
        Map<Direction, Integer> moveScores = MonteCarloTreeSearch.evaluateMoves(
                botId,
                gameState,
                SIMULATION_DEPTH,
                SIMULATION_ITERATIONS
        );

        // Apply additional strategic considerations for multi-snake games
        moveScores = applyMultiSnakeStrategy(moveScores, botId, gameState);

        // Log the scores for debugging
        System.out.println("Bot " + botId + " MCTS Move Scores:");
        for (Direction dir : Direction.values()) {
            System.out.println(" - " + dir + ": " + moveScores.getOrDefault(dir, 0));
        }

        // Find the direction with the highest score
        Optional<Direction> bestDirection = moveScores.entrySet().stream()
                .filter(entry -> entry.getValue() > 0) // Only consider valid moves
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);

        // Return the best direction or a random valid direction as fallback
        if (bestDirection.isPresent()) {
            return bestDirection.get();
        } else {
            // Try to find any valid move if all scored 0
            List<Direction> validMoves = getValidMoves(botId, gameState);
            if (!validMoves.isEmpty()) {
                return validMoves.get(new Random().nextInt(validMoves.size()));
            }
            return Direction.DOWN; // Last resort
        }
    }

    /**
     * Apply additional strategic considerations for multi-snake games.
     * This adjusts the MCTS scores based on proximity to other snakes and available space.
     */
    private static Map<Direction, Integer> applyMultiSnakeStrategy(
            Map<Direction, Integer> baseScores, int botId, GameState gameState) {

        Map<Direction, Integer> adjustedScores = new HashMap<>(baseScores);
        Point currentPos = gameState.getBotLocation(botId);

        // For each direction, evaluate proximity to other snakes
        for (Direction dir : Direction.values()) {
            if (adjustedScores.getOrDefault(dir, 0) <= 0) continue;

            Point nextPos = dir.from(currentPos);
            nextPos = wrapAround(nextPos, gameState.getPlanWidth(), gameState.getPlanHeight());

            // Calculate distance to nearest enemy snake
            int minDistanceToEnemy = Integer.MAX_VALUE;
            // Updated to use getLiveBotIds() instead of getBotIds()
            for (int enemyId : gameState.getLiveBotIds()) {
                if (enemyId == botId) continue;

                Point enemyPos = gameState.getBotLocation(enemyId);
                if (enemyPos != null) {
                    int distance = manhattanDistance(nextPos, enemyPos, gameState.getPlanWidth(), gameState.getPlanHeight());
                    minDistanceToEnemy = Math.min(minDistanceToEnemy, distance);
                }
            }

            if (minDistanceToEnemy < Integer.MAX_VALUE) {
                // Adjust score based on distance to enemies
                // Closer enemies result in lower scores
                int avoidanceBonus = (int)(minDistanceToEnemy * ENEMY_AVOIDANCE_WEIGHT);
                adjustedScores.put(dir, adjustedScores.get(dir) + avoidanceBonus);
            }

            // Also consider open space in each direction (using a simple flood fill)
            int openSpace = calculateOpenSpace(nextPos, gameState, 8); // Check up to 8 steps ahead
            adjustedScores.put(dir, adjustedScores.get(dir) + openSpace);
        }

        return adjustedScores;
    }

    /**
     * Calculate open space available from a given position using a limited-depth flood fill
     */
    private static int calculateOpenSpace(Point start, GameState gameState, int maxDepth) {
        Set<Point> visited = new HashSet<>();
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Integer> distances = new HashMap<>();

        queue.add(start);
        visited.add(start);
        distances.put(start, 0);

        Set<Point> obstacles = gameState.getObstacleLocations();
        int width = gameState.getPlanWidth();
        int height = gameState.getPlanHeight();

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            int currentDepth = distances.get(current);

            if (currentDepth >= maxDepth) continue;

            for (Direction dir : Direction.values()) {
                Point next = dir.from(current);
                next = wrapAround(next, width, height);

                if (!visited.contains(next) && !obstacles.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                    distances.put(next, currentDepth + 1);
                }
            }
        }

        return visited.size();
    }

    /**
     * Get all valid moves from the current position
     */
    private static List<Direction> getValidMoves(int botId, GameState gameState) {
        List<Direction> validMoves = new ArrayList<>();
        Point currentPos = gameState.getBotLocation(botId);
        Set<Point> obstacles = gameState.getObstacleLocations();
        int width = gameState.getPlanWidth();
        int height = gameState.getPlanHeight();

        for (Direction dir : Direction.values()) {
            Point nextPos = dir.from(currentPos);
            nextPos = wrapAround(nextPos, width, height);

            if (!obstacles.contains(nextPos) && !gameState.getBotLocations().contains(nextPos)) {
                validMoves.add(dir);
            }
        }

        return validMoves;
    }

    /**
     * Calculate Manhattan distance between two points, accounting for wraparound
     */
    private static int manhattanDistance(Point p1, Point p2, int width, int height) {
        // Calculate X distance considering wraparound
        int xDist = Math.abs(p1.x - p2.x);
        xDist = Math.min(xDist, width - xDist);

        // Calculate Y distance considering wraparound
        int yDist = Math.abs(p1.y - p2.y);
        yDist = Math.min(yDist, height - yDist);

        return xDist + yDist;
    }

    /**
     * Handle wrapping around the game board
     */
    private static Point wrapAround(Point point, int width, int height) {
        int x = (point.x + width) % width;
        int y = (point.y + height) % height;
        return new Point(x, y);
    }

    /**
     * Get the bot ID from the game state
     */
    private static int getBotId(GameState gameState) {

        // Default approach: return the first bot ID from the game state
        // Updated to use getLiveBotIds instead of getBotIds
        return gameState.getLiveBotIds().iterator().next();
    }
}
