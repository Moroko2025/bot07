package suitebot.strategies;

import suitebot.game.Direction;
import suitebot.game.GameState;
import suitebot.game.Point;

import java.util.*;

public class FloodFillHeuristic {
    public static Map<Direction, Integer> evaluateMoves(int botId, GameState gameState, int maxDepth) {
        Map<Direction, Integer> moveScores = new EnumMap<>(Direction.class);
        Point botLocation = gameState.getBotLocation(botId);
        Set<Point> obstacles = gameState.getObstacleLocations();
        int width = gameState.getPlanWidth();
        int height = gameState.getPlanHeight();

        for (Direction direction : Direction.values()) {
            Point nextPosition = wrapAround(direction.from(botLocation), width, height);
            if (!obstacles.contains(nextPosition)) {
                moveScores.put(direction, floodFillForMaxDepth(nextPosition, obstacles, width, height, maxDepth));
            } else {
                moveScores.put(direction, 0);
            }
        }
        return moveScores;
    }

    private static Point wrapAround(Point point, int width, int height) {
        return new Point((point.x + width) % width, (point.y + height) % height);
    }

    private static int floodFillForMaxDepth(Point start, Set<Point> obstacles, int width, int height, int maxDepth) {
        Set<Point> visited = new HashSet<>();
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        int depth = 0;
        while (!queue.isEmpty() && depth < maxDepth) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Point p = queue.poll();
                for (Direction direction : Direction.values()) {
                    Point next = wrapAround(direction.from(p), width, height);
                    if (!visited.contains(next) && !obstacles.contains(next)) {
                        queue.add(next);
                        visited.add(next);
                    }
                }
            }
            depth++; // Increment depth after exploring all points at this level
        }
        return depth; // Return the maximum depth reached
    }
}
