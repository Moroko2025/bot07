package suitebot.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ImmutableGameStateBuilderTest {
    private static final int PLAN_WIDTH = 12;
    private static final int PLAN_HEIGHT = 12;
    private static final List<Integer> BOT_IDS = ImmutableList.of(1, 2, 3);
    private static final List<Integer> LIVE_BOT_IDS = ImmutableList.of(1, 2);
    private static final Map<Integer, Point> BOT_LOCATIONS = ImmutableMap.of(
            1, new Point(0, 0),
            2, new Point(11, 11));
    private static final List<Point> OBSTACLES = ImmutableList.of(new Point(1, 1), new Point(2, 2));

    private ImmutableGameState.Builder gameStateBuilder;

    @BeforeEach
    void setUp() {
        gameStateBuilder = ImmutableGameState.builder()
                .setPlanWidth(PLAN_WIDTH)
                .setPlanHeight(PLAN_HEIGHT)
                .setBotIds(BOT_IDS)
                .setLiveBotIds(LIVE_BOT_IDS)
                .setBotLocationMap(BOT_LOCATIONS)
                .setObstacles(OBSTACLES);
    }

    @Test
    void testBuild() {
        GameState gameState = gameStateBuilder.build();

        assertThat(gameState.getPlanWidth()).isEqualTo(PLAN_WIDTH);
        assertThat(gameState.getPlanHeight()).isEqualTo(PLAN_HEIGHT);
        assertThat(gameState.getAllBotIds()).containsExactlyInAnyOrderElementsOf(BOT_IDS);
        assertThat(gameState.getLiveBotIds()).containsExactlyInAnyOrderElementsOf(LIVE_BOT_IDS);
        assertThat(gameState.getObstacleLocations()).containsExactlyInAnyOrderElementsOf(OBSTACLES);
        assertThat(gameState.getBotLocation(1)).isEqualTo(new Point(0, 0));
    }

    @Test
    void testBuild_fromGameState() {
        GameState sourceGameState = gameStateBuilder.build();
        GameState gameState = ImmutableGameState.builder(sourceGameState).build();

        assertThat(gameState.getPlanWidth()).isEqualTo(PLAN_WIDTH);
        assertThat(gameState.getPlanHeight()).isEqualTo(PLAN_HEIGHT);
        assertThat(gameState.getAllBotIds()).containsExactlyInAnyOrderElementsOf(BOT_IDS);
        assertThat(gameState.getLiveBotIds()).containsExactlyInAnyOrderElementsOf(LIVE_BOT_IDS);
        assertThat(gameState.getObstacleLocations()).containsExactlyInAnyOrderElementsOf(OBSTACLES);
        assertThat(gameState.getBotLocation(1)).isEqualTo(new Point(0, 0));
    }

    @Test
    void missingBotIds_shouldThrowException() {
        assertThatExceptionOfType(ImmutableGameState.UnableToBuildException.class)
                .isThrownBy(() -> {
                    gameStateBuilder.setBotIds(null).build();
                });
    }

    @Test
    void missingBotLocations_shouldThrowException() {
        assertThatExceptionOfType(ImmutableGameState.UnableToBuildException.class)
                .isThrownBy(() -> {
                    gameStateBuilder.setBotLocationMap(null).build();
                });
    }

    @Test
    void locationNotSet_forAnyBot_shouldThrowException() {
        assertThatExceptionOfType(ImmutableGameState.UnableToBuildException.class)
                .isThrownBy(() -> {
                    gameStateBuilder.setBotLocationMap(ImmutableMap.of(1, new Point(0, 0))).build();
                });
    }

    @Test
    void nonPositivePlanWidth_shouldThrowException() {
        assertThatExceptionOfType(ImmutableGameState.UnableToBuildException.class)
                .isThrownBy(() -> {
                    gameStateBuilder.setPlanWidth(-1).build();
                });
    }

    @Test
    public void nonPositivePlanHeight_shouldThrowException() {
        assertThatExceptionOfType(ImmutableGameState.UnableToBuildException.class)
                .isThrownBy(() -> {
                    gameStateBuilder.setPlanHeight(0).build();
                });
    }

    @Test
    void liveBots_notSubsetOf_allBots_shouldThrowException() {
        assertThatExceptionOfType(ImmutableGameState.UnableToBuildException.class)
                .isThrownBy(() -> {
                    gameStateBuilder.setLiveBotIds(ImmutableSet.of(1, 2, 3, 4)).build();
                });
    }

    @Test
    void nonUniqueBotIds_shouldThrowException() {
        assertThatExceptionOfType(ImmutableGameState.UnableToBuildException.class)
                .isThrownBy(() -> {
                    gameStateBuilder.setBotIds(ImmutableList.of(2, 2)).build();
                });
    }

    @Test
    void nonUniqueBotLocations_shouldThrowException() {
        Map<Integer, Point> duplicateBotLocations = ImmutableMap.of(
                1, new Point(0, 0),
                2, new Point(0, 0));
        assertThatExceptionOfType(ImmutableGameState.UnableToBuildException.class)
                .isThrownBy(() -> {
                    gameStateBuilder.setBotLocationMap(duplicateBotLocations).build();
                });
    }

    @Test
    void locationOccupiedByMultipleObjects_shouldThrowException_1() {
        // collides with the location of the bot 1
        assertThatExceptionOfType(ImmutableGameState.UnableToBuildException.class)
                .isThrownBy(() -> {
                    gameStateBuilder.setObstacles(ImmutableSet.of(new Point(0, 0))).build();
                });
    }
}
