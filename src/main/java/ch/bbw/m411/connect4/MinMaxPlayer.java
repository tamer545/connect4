package ch.bbw.m411.connect4;

import static ch.bbw.m411.connect4.Connect4ArenaMain.*;

/**
 * A Player using the minmax algorithm
 */
public class MinMaxPlayer extends DefaultPlayer{
        int bestMove = NOMOVE;

        int maxDepth;

        int minimalDepth;

    /**
     * Constructor for the MinMaxPlayer
     * @param depth the depth for minmax algorithm
     */
    public MinMaxPlayer(int depth) {
            super();
            maxDepth = depth;
        }

    /**
     * The play method calling all the necessary functions
     * @return the best possible move
     */
    @Override
    int play() {
        int movesAvailable = countAvailableMoves(board);
        minimalDepth = Math.min(movesAvailable, maxDepth);
        playUsingMinMax(myColor, minimalDepth);
        return bestMove;
    }

    /**
     * MinMax Algorithm
     * @param myColor the min max Player
     * @param depth the min max algorithm depth
     * @return min or max
     */
    private int playUsingMinMax(Connect4ArenaMain.Stone myColor, int depth) {
        if (isWinning(board, myColor.opponent())) {
            return Integer.MIN_VALUE + 1;
        }

        if (depth == 0) {
            return evaluate(myColor, board);
        }

        if (myColor == this.myColor) {
            int max = Integer.MIN_VALUE;
            for (int move : getPossibleMoves(board)) {
                board[move] = myColor; // play to a possible move
                int currentValue = playUsingMinMax(myColor.opponent(), depth - 1);
                board[move] = null; // undo playing
                if (depth == minimalDepth) {
                    System.out.println("Index: " + move + " Value: " + currentValue + "\n");
                }
                if (currentValue > max) {
                    max = currentValue;
                    if (depth == minimalDepth) {
                        bestMove = move;
                    }
                }
            }
            return max;
        } else {
            int min = Integer.MAX_VALUE;
            for (int move : getPossibleMoves(board)) {
                board[move] = myColor; // play to a possible move
                int currentValue = playUsingMinMax(myColor.opponent(), depth - 1);
                board[move] = null; // undo playing
                if (depth == minimalDepth) {
                    System.out.println("Index: " + move + " Value: " + currentValue + "\n");
                }
                if (currentValue < min) {
                    min = currentValue;
                }
            }
            return min;
        }
    }
}
