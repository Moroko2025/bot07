package suitebot.strategies;

import suitebot.game.Direction;
import suitebot.game.GameState;
import suitebot.game.Point;

import java.util.*;

public class MonteCarloTreeSearch {
    private static final int DEFAULT_SIMULATION_DEPTH = 10;
    private static final int DEFAULT_ITERATIONS = 100;

    public static Map<Direction, Integer> evaluateMoves(int botId, GameState gameState, int maxDepth, int iterations) {
        Map<Direction, Integer> moveScores = new EnumMap<>(Direction.class);
        Point botLocation = gameState.getBotLocation(botId);
        Set<Point> obstacles = gameState.getObstacleLocations();
        int width = gameState.getPlanWidth();
        int height = gameState.getPlanHeight();

        for (Direction direction : Direction.values()) {
            Point nextPosition = direction.from(botLocation);
            nextPosition = wrapAround(nextPosition, width, height);
            if (!obstacles.contains(nextPosition)) {
                moveScores.put(direction, runSimulations(nextPosition, obstacles, width, height, maxDepth, iterations));
            } else {
                // Assign a score of zero for blocked directions
                moveScores.put(direction, 0);
            }
        }

        return moveScores;
    }

    private static int runSimulations(Point start, Set<Point> obstacles, int width, int height, int maxDepth, int iterations) {
        int bestScore = 0;
        for (int i = 0; i < iterations; i++) {
            int simulationScore = simulateGame(start, obstacles, width, height, maxDepth);
            if (simulationScore > bestScore) {
                bestScore = simulationScore;
            }
        }
        return bestScore;
    }


    private static int simulateGame(Point start, Set<Point> obstacles, int width, int height, int maxDepth) {
        Set<Point> visited = new HashSet<>(obstacles);
        Point current = start;
        int score = 0;

        for (int depth = 0; depth < maxDepth; depth++) {
            List<Direction> possibleMoves = new ArrayList<>();
            for (Direction direction : Direction.values()) {
                Point next = direction.from(current);
                next = wrapAround(next, width, height);
                if (!visited.contains(next)) {
                    possibleMoves.add(direction);
                }
            }

            if (possibleMoves.isEmpty()) break;

            Direction chosenMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
            current = chosenMove.from(current);
            current = wrapAround(current, width, height);
            visited.add(current);
            score++;
        }

        return score;
    }

    private static Point wrapAround(Point point, int width, int height) {
        int x = (point.x + width) % width;
        int y = (point.y + height) % height;
        return new Point(x, y);
    }
}
