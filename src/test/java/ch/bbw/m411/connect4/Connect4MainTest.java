package ch.bbw.m411.connect4;

import java.util.List;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class Connect4MainTest implements WithAssertions {

	protected Connect4ArenaMain newInstance() {
		return new Connect4ArenaMain();
	}

	Connect4ArenaMain.Stone[] fromString(String boardStr) {
		var board = boardStr.codePoints()
				.map(Character::toLowerCase)
				.filter(x -> List.of('x', 'o', '.')
						.contains((char) x))
				.mapToObj(x -> x == 'x' ? Connect4ArenaMain.Stone.RED : (x == 'o' ? Connect4ArenaMain.Stone.BLUE : null))
				.toArray(Connect4ArenaMain.Stone[]::new);
		assertThat(board).hasSize(Connect4ArenaMain.WIDTH * Connect4ArenaMain.HEIGHT);
		return board;
	}

	AbstractBooleanAssert<?> assertThatXWin(String boardStr) {
		var board = fromString(boardStr);
		return assertThat(newInstance().isWinning(board, Connect4ArenaMain.Stone.RED)).as(Connect4ArenaMain.toDebugString(board));
	}

	@Test
	void isWin() {
		assertThatXWin("xxxx... ....... ....... .......").isTrue();
		assertThatXWin(".xxxx.. ....... ....... .......").isTrue();
		assertThatXWin("..xxxx. ....... ....... .......").isTrue();
		assertThatXWin("...xxxx ....... ....... .......").isTrue();
		assertThatXWin("...x... ...x... ...x... ...x...").isTrue();
		assertThatXWin("......x ......x ......x ......x").isTrue();
		assertThatXWin("xooo... .xoo... ..xo... ...x...").isTrue();
		assertThatXWin(".ooxo.. .oxoo.. .xxxx.. .......").isTrue();
		assertThatXWin(".ooxo.x .oxoo.. .ooxx.. .xxxx..").isTrue();
		assertThatXWin("oooo... xxxx... ....... .......").isTrue();
	}

	@Test
	void noWin() {
		assertThatXWin("....... ....... ....... .......").isFalse();
		assertThatXWin("xxx.xx. ....... ....... .......").isFalse();
		assertThatXWin("xxx.xxx xxx.xxx xxx.xxx .......").isFalse();
		assertThatXWin("xx.x.xx xx.x.xx xx.x.xx .......").isFalse();
		assertThatXWin("ooo.ooo xxx.xxx xxx.xxx xxx.xxx").isFalse();
		assertThatXWin("oo.o.oo xx.x.xx xx.x.xx xx.x.xx").isFalse();
		assertThatXWin("oooo... ....... ....... .......").isFalse();
		assertThatXWin("xxx.xx. xxx.xx. xxx.... o......").isFalse();
		assertThatXWin("xxxo... x.x.... x.o.... o.x....").isFalse();
	}

	@Test
	void inAGreedyBattleTheFirstPlayerWillWin() {
		var red = new Connect4ArenaMain.GreedyPlayer();
		var blue = new Connect4ArenaMain.GreedyPlayer();
		assertThat(newInstance().play(red, blue)).isSameAs(red);
	}

	@Test
	void greedyVsAlphaBeta() {
		var redPlayer = new AlphaBetaPlayer(8);
		var bluePlayer = new Connect4ArenaMain.GreedyPlayer();
		assertThat(newInstance().play(redPlayer, bluePlayer)).isSameAs(redPlayer);
	}

	@Test
	void greedyVsMinMax() {
		var redPlayer = new MinMaxPlayer(8);
		var bluePlayer = new Connect4ArenaMain.GreedyPlayer();
		assertThat(newInstance().play(redPlayer, bluePlayer)).isSameAs(redPlayer);
	}

	@Test
	void minMaxVsAlphaBeta() {
		var redPlayer = new AlphaBetaPlayer(5);
		var bluePlayer = new MinMaxPlayer(7);
		assertThat(newInstance().play(redPlayer, bluePlayer)).isSameAs(bluePlayer);
	}

	@Test
	void alphaBetaVsAlphaBeta() {
		var redPlayer = new AlphaBetaPlayer(5);
		var bluePlayer = new AlphaBetaPlayer(10);
		assertThat(newInstance().play(redPlayer, bluePlayer)).isSameAs(bluePlayer);
	}

	@Test
	void minMaxVsMinMax() {
		var redPlayer = new MinMaxPlayer(9);
		var bluePlayer = new MinMaxPlayer(3);
		assertThat(newInstance().play(redPlayer, bluePlayer)).isSameAs(redPlayer);
	}

}
