package suitebot.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImmutableGameState implements GameState
{
	private final int planWidth;
	private final int planHeight;
	private final ImmutableList<Integer> botIds;
	private final ImmutableSet<Integer> liveBotIds;
	private final ImmutableMap<Integer, Point> botLocationMap;
	private final ImmutableSet<Point> obstacles;

	@Override
	public int getPlanWidth()
	{
		return planWidth;
	}

	@Override
	public int getPlanHeight()
	{
		return planHeight;
	}

	@Override
	public List<Integer> getAllBotIds()
	{
		return botIds;
	}

	@Override
	public Set<Integer> getLiveBotIds()
	{
		return liveBotIds;
	}

	@Override
	public Point getBotLocation(int botId)
	{
		Point location = botLocationMap.get(botId);
		if (location == null)
			assertKnownBotId(botId);
		return location;
	}

	@Override
	public Set<Point> getObstacleLocations()
	{
		return obstacles;
	}

	@Override
	public Set<Point> getBotLocations()
	{
		return botLocationMap.values().stream().collect(Collectors.toSet());
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static Builder builder(GameState gameState)
	{
		Map<Integer, Point> botLocationMap = gameState.getAllBotIds().stream()
				.filter(botId -> gameState.getBotLocation(botId) != null)
				.collect(Collectors.toMap(Function.identity(), gameState::getBotLocation));

		return new Builder()
				.setPlanWidth(gameState.getPlanWidth())
				.setPlanHeight(gameState.getPlanHeight())
				.setBotIds(gameState.getAllBotIds())
				.setLiveBotIds(gameState.getLiveBotIds())
				.setBotLocationMap(botLocationMap)
				.setObstacles(gameState.getObstacleLocations());
	}

	private ImmutableGameState(Builder builder)
	{
		preBuildValidation(builder);

		this.planWidth = builder.planWidth;
		this.planHeight = builder.planHeight;
		this.botIds = ImmutableList.copyOf(builder.botIds);
		this.botLocationMap = ImmutableMap.copyOf(builder.botLocationMap);

		this.liveBotIds = Optional.ofNullable(builder.liveBotIds).map(ImmutableSet::copyOf).orElse(ImmutableSet.copyOf(builder.botIds));
		this.obstacles = Optional.ofNullable(builder.obstacles).map(ImmutableSet::copyOf).orElse(ImmutableSet.of());

		postBuildValidation();
	}

	private static void preBuildValidation(Builder builder)
	{
		assertBuildable(builder.botIds != null, "botIds are mandatory");
		assertBuildable(builder.botLocationMap != null, "botLocationMap is mandatory");
	}

	private void postBuildValidation()
	{
		assertBuildable(planWidth > 0, "planWidth must be positive");
		assertBuildable(planHeight > 0, "planHeight must be positive");
		assertBuildable(hasUniqueValues(botIds), "duplicate values in botIds");
		assertBuildable(hasUniqueValues(botLocationMap.values()), "duplicate bot locations");
		assertLocationSetForAllLiveBots();
		assertMultipleObjectsDoNotOccupyTheSameLocation();
	}

	private void assertLocationSetForAllLiveBots()
	{
		for (int botId : liveBotIds)
			if (botLocationMap.get(botId) == null)
				throw new UnableToBuildException("location not set for the bot " + botId);
	}

	private void assertMultipleObjectsDoNotOccupyTheSameLocation()
	{
		List<Point> allLocations = new ArrayList<>();
		allLocations.addAll(botLocationMap.values());
		allLocations.addAll(obstacles);
		assertBuildable(hasUniqueValues(allLocations), "multiple objects may not occupy the same location");
	}

	private static void assertBuildable(boolean assertion, String message) throws UnableToBuildException
	{
		if (!assertion)
			throw new UnableToBuildException(message);
	}

	private static <T> boolean hasUniqueValues(Collection<T> collection)
	{
		return collection.size() == ImmutableSet.copyOf(collection).size();
	}

	private void assertKnownBotId(int botId)
	{
		if (!botIds.contains(botId))
			throw new IllegalArgumentException("unknown bot ID: " + botId);
	}

	public static class Builder
	{
		private int planWidth;
		private int planHeight;
		private Iterable<Integer> botIds;
		private Iterable<Integer> liveBotIds;
		private Map<Integer, Point> botLocationMap;
		private Iterable<Point> obstacles;

		public Builder setPlanWidth(int planWidth)
		{
			this.planWidth = planWidth;
			return this;
		}

		public Builder setPlanHeight(int planHeight)
		{
			this.planHeight = planHeight;
			return this;
		}

		public Builder setBotIds(Iterable<Integer> botIds)
		{
			this.botIds = botIds;
			return this;
		}

		public Builder setLiveBotIds(Iterable<Integer> liveBotIds)
		{
			this.liveBotIds = liveBotIds;
			return this;
		}

		public Builder setBotLocationMap(Map<Integer, Point> botLocationMap)
		{
			this.botLocationMap = botLocationMap;
			return this;
		}

		public Builder setObstacles(Iterable<Point> obstacles)
		{
			this.obstacles = obstacles;
			return this;
		}

		public ImmutableGameState build()
		{
			return new ImmutableGameState(this);
		}
	}

	public static class UnableToBuildException extends RuntimeException
	{
		public UnableToBuildException(String message)
		{
			super(message);
		}
	}
}
