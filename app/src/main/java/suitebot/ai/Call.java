package suitebot.ai;

import suitebot.game.Direction;
import suitebot.game.GameState;
import suitebot.game.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Call {

    static int phase = 1;
    static int count = 1;
    static int printed = 0;
    static Direction lastDirection = Direction.UP;

    static List<Direction> getDirection(int botId, GameState gameState) {
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


        lastDirection = direction;
        List<Direction> avd =checkAvailableDirections(botId, gameState);
        if (!avd.contains(direction)){
            return List.of();
        }
        return List.of(direction);
    }

    private static List<Direction> checkAvailableDirections(int botId, GameState gameState) {
        Point botLocation = gameState.getBotLocation(botId);
        Set<Point> obstacles = gameState.getObstacleLocations();
        int width = gameState.getPlanWidth();
        int height = gameState.getPlanHeight();

        List<Direction> availableDirections = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            Point next = wrapAround(dir.from(botLocation), width, height);
            if (!obstacles.contains(next)) {
                availableDirections.add(dir);
            }
        }

        if (availableDirections.isEmpty()) {
            phase = 1;
            count = 1;
            printed = 0;
        }

        return availableDirections;
    }




    private static Point wrapAround(Point point, int width, int height) {
        return new Point((point.x + width) % width, (point.y + height) % height);
    }
}