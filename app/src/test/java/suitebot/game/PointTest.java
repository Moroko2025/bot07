package suitebot.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PointTest
{
	@Test
	void x_shouldHaveTheValueSetInTheConstructor()
	{
		int x = 13;
		Point point = new Point(x, 17);

		assertThat(point.x).isEqualTo(x);
	}

	@Test
	void y_shouldHaveTheValueSetInTheConstructor()
	{
		int y = 19;
		Point point = new Point(11, y);

		assertThat(point.y).isEqualTo(y);
	}

	@Test
	void samePoints_shouldEqual()
	{
		assertThat(new Point(1, 1)).isEqualTo(new Point(1, 1));
	}

	@Test
	void differentPoints_shouldNotEqual()
	{
		assertThat(new Point(1, 1)).isNotEqualTo(new Point(1, 2));
		assertThat(new Point(1, 1)).isNotEqualTo(new Point(2, 1));
	}
}