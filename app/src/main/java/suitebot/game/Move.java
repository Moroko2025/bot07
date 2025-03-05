package suitebot.game;

/**
 * Internal communication object for the HTTP request
 *
 * You should use DIRECTION class as a response.
 *
 * DO NOT TOUCH THIS CODE!
 */
public class Move
{
	public static final Move UP = new Move(Direction.UP);
	public static final Move DOWN = new Move(Direction.DOWN);
	public static final Move LEFT = new Move(Direction.LEFT);
	public static final Move RIGHT = new Move(Direction.RIGHT);

	public final Direction step1;
	public final Direction step2 = null;

	public Move(Direction step1)
	{
		this(step1, null);
	}

	private Move(Direction step1, Direction step2)
	{
		if (step1 == null)
			throw new IllegalArgumentException("step1 must not be null");

		this.step1 = step1;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Move move = (Move) o;

		return (step1 == move.step1 && step2 == move.step2);
	}

	@Override
	public int hashCode()
	{
		int result = step1.hashCode();
		result = 31 * result + (step2 != null ? step2.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		if (step2 == null)
			return step1.toString();
		else
			return step1.toString() + step2.toString();
	}
}
