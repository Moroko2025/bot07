package suitebot.ai;
import suitebot.game.Direction;
import suitebot.game.GameState;
import suitebot.game.Point;



import java.util.function.Predicate;


/**
 * Sample AI. The AI has some serious flaws, which is intentional.
 */
public class SampleBotAi implements BotAi
{
	/**
	 * Your bot id
	 */
	private int botId;
	/**
	 * The data of the game on the basis of which you choose your move
	 */
	private GameState gameState;
	static int i = 0;

	private final Predicate<Direction> isSafeDirection = direction -> (
			!gameState.getObstacleLocations().contains(destination(direction)) &&
					!gameState.getBotLocations().contains(destination(direction))
	);

	/**
	 * If a random safe move can be made (one that avoids any obstacles), do it;
	 * otherwise, go down.
	 */
	@Override
	public Direction makeMove(int botId, GameState gameState) {
		this.botId = botId;
		this.gameState = gameState;


		//Available directions - based on game plan orientation, not the bot actual direction

		//selects first available free direction (random - because the direction list is in different order every time)
		//TODO: this is just example app,
		// you should implement exception handling, timing verification, and proper algorithm ;o) ...
		//return Stream.of(
		//    safeDirectionSupplier
		//)
		//    .map(Supplier::get)
		//    .map(Stream::findFirst)
		//    .filter(Optional::isPresent)
		//    .map(Optional::get)
		//    .findFirst()
		//    .orElse(Direction.DOWN);
		long startTime = System.nanoTime();

		long duration = System.nanoTime() - startTime;
		double durationMs = duration / 1_000_000.0;
		System.out.printf("Bot %d [%s] decision time: %.2f ms\n", botId, getName(), durationMs);
//		Direction d= Call2.getDirection(botId,gameState);
//
//		if (d!=null){
//			System.out.println("!Call.getDirection(botId,gameState).isEmpty() : "+ d);
//			return d;
//		}
//		else
//			return Call.getDirection(botId,gameState);
		System.out.println("Number of calls" + i++);

		return Call.getDirection(botId, gameState);
	}

	private Point destination(Direction direction)
	{
		int maxWidth = gameState.getPlanWidth() - 1;
		int maxHeight = gameState.getPlanHeight() - 1;

		Point botLocation = gameState.getBotLocation(botId);

		System.out.println("Bot location" + botLocation);
		System.out.println("Bot Id: " + botId);
		System.out.println("Board Height: " + gameState.getPlanHeight());
		System.out.println("Board Width: " + gameState.getPlanWidth());
		System.out.println("Direction: " + direction);

		Point stepDestination = direction.from(botLocation);
		int new_x = stepDestination.x;
		int new_y = stepDestination.y;

		if(stepDestination.x == maxWidth){
			new_x = 0;
		} else if (stepDestination.x < 0) {
			new_x = maxWidth;
		}

		if (stepDestination.y == maxHeight){
			new_y = 0;
		} else if (stepDestination.y < 0) {
			new_y = maxHeight;
		}

		Point final_point = new Point(new_x, new_y);

		System.out.println("Step destination: " + stepDestination);

		return final_point;
	}

	@Override
	public String getName()
	{
		return "Straw hats";
	}
}