package suitebot.game;

import java.util.*;

public class GameStateFactory
{
	public static final char OBSTACLE = '*';
	public static final char TREASURE = '!';
	public static final char BATTERY = '+';
	public static final char EMPTY = ' ';

	public static final int DEFAULT_BOT_ENERGY = 10;

	public static GameState createFromString(String gameStateAsString)
	{
		return createFromString(gameStateAsString, DEFAULT_BOT_ENERGY);
	}

	public static GameState createFromString(String gameStateAsString, int botEnergy)
	{
		List<Integer> botIds = new ArrayList<>();
		Map<Integer, Point> botLocationMap = new HashMap<>();
		Map<Integer, Integer> botEnergyMap = new HashMap<>();
		Set<Point> obstacles = new HashSet<>();
		Set<Point> treasures = new HashSet<>();
		Set<Point> batteries = new HashSet<>();

		String[] lines = gameStateAsString.replaceAll("\n$", "").split("\n");
		assertRectangularPlan(lines);

		int width = lines[0].length();
		int height = lines.length;

		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				char ch = lines[y].charAt(x);
				Point location = new Point(x, y);

				if (ch == OBSTACLE)
					obstacles.add(location);
				else if (ch == TREASURE)
					treasures.add(location);
				else if (ch == BATTERY)
					batteries.add(location);
				else if (Character.isDigit(ch))
				{
					int botId = Character.getNumericValue(ch);
					botIds.add(botId);
					botLocationMap.put(botId, location);
					botEnergyMap.put(botId, botEnergy);
				}
				else if (ch != EMPTY)
					throw new GameStateCreationException("unrecognized character: " + ch);
			}
		}

		return ImmutableGameState.builder()
				.setPlanWidth(width)
				.setPlanHeight(height)
				.setBotIds(botIds)
				.setBotLocationMap(botLocationMap)
				.setObstacles(obstacles)
				.build();
	}

	private static void assertRectangularPlan(String[] lines)
	{
		int width = lines[0].length();

		for (int i = 1; i < lines.length; i++)
		{
			if (lines[i].length() != width)
			{
				throw new GameStateCreationException(
						String.format("non-rectangular plan: line %d width (%d) is different from the line 1 width (%d)",
						              (i + 1), lines[i].length(), width));
			}
		}
	}

	public static class GameStateCreationException extends RuntimeException
	{
		public GameStateCreationException(String message)
		{
			super(message);
		}
	}
}
