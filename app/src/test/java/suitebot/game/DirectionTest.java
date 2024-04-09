package suitebot.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DirectionTest
{
	private static final Point FROM = new Point(3, 7);

	@Test
	public void testLeftFrom()
	{
		assertThat(Direction.LEFT.from(FROM)).isEqualTo(new Point(2, 7));
	}

	@Test
	public void testRightFrom()
	{
		assertThat(Direction.RIGHT.from(FROM)).isEqualTo(new Point(4, 7));
	}

	@Test
	public void testUpFrom() throws Exception
	{
		assertThat(Direction.UP.from(FROM)).isEqualTo(new Point(3, 6));
	}

	@Test
	public void testDownFrom() throws Exception
	{
		assertThat(Direction.DOWN.from(FROM)).isEqualTo(new Point(3, 8));
	}
}