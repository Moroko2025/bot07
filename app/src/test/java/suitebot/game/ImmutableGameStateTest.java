package suitebot.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ImmutableGameStateTest
{
	private static final int PLAN_WIDTH = 12;
	private static final int PLAN_HEIGHT = 12;
	private static final List<Integer> BOT_IDS = ImmutableList.of(1, 2, 3);
	private static final List<Integer> LIVE_BOT_IDS = ImmutableList.of(1, 2);
	private static final Map<Integer, Point> BOT_LOCATIONS = ImmutableMap.of(
			1, new Point(0, 0),
			2, new Point(11, 11));
	private static final List<Point> OBSTACLES = ImmutableList.of(new Point(1, 1), new Point(2, 2));

	private GameState gameState;

	@BeforeEach
	void setUp()
	{
		gameState = ImmutableGameState.builder()
				.setPlanWidth(PLAN_WIDTH)
				.setPlanHeight(PLAN_HEIGHT)
				.setBotIds(BOT_IDS)
				.setLiveBotIds(LIVE_BOT_IDS)
				.setBotLocationMap(BOT_LOCATIONS)
				.setObstacles(OBSTACLES)
				.build();
	}

	@Test
	void getBotLocation_onDeadBotId_shouldReturnNull()
	{
		assertThat(gameState.getBotLocation(3)).isNull();
	}

	@Test
	void getBotLocation_onUnknownBotId_shouldThrowException()
	{
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					gameState.getBotLocation(4);
				});
	}
}
