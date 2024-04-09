package suitebot.game;

public enum Direction
{
	UP(0, -1, "U"),
	DOWN(0, 1, "D"),
	LEFT(-1, 0, "L"),
	RIGHT(1, 0, "R");

	public final int dx;
	public final int dy;
	public final String asString;

	Direction(int dx, int dy, String asString)
	{
		this.dx = dx;
		this.dy = dy;
		this.asString = asString;
	}

	public Point from(Point source)
	{
		return new Point(source.x + dx, source.y + dy);
	}

	@Override
	public String toString()
	{
		return asString;
	}
}
