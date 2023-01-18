package ch.bbw.m411.connect4;

import static ch.bbw.m411.connect4.Connect4ArenaMain.*;

/**
 * A Player using the minmax algorithm
 */
public class MinMaxPlayer extends DefaultPlayer{

    //the best move
    int bestMove = NOMOVE;

    //the depth defined by the user
    int maxDepth;

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
        //start a timer to check for the duration of one play
        long startTime = System.currentTimeMillis();

        //start the minmax loop
        playUsingMinMax(myColor, maxDepth); // min max Player

        //stop the timer
        long endTime = System.currentTimeMillis();

        //print the execution time and the number of cutoffs
        System.out.println("\u001B[33m" + "Execution time: " + (endTime - startTime) + " milliseconds" + "\u001B[0m");

        return bestMove;
    }

    /**
     * MinMax Algorithm
     * @param myColor the min max Player
     * @param depth the min max algorithm depth
     * @return min or max
     */
    private int playUsingMinMax(Connect4ArenaMain.Stone myColor, int depth) {
        //the opponent won
        if (isWinning(board, myColor.opponent())) {
            return Integer.MIN_VALUE + 1;
        }
        //evaluate the score if the depth reaches 0
        if (depth == 0) {
            return evaluate(myColor, board);
        }

        int best = Integer.MIN_VALUE;

        //loop through all possible moves
        for (int move : getPossibleMoves(board)) {
                board[move] = myColor; // play to a possible move

                int currentValue = -playUsingMinMax(myColor.opponent(), depth - 1);

                board[move] = null; // undo playing

                if (depth == maxDepth) {
                    System.out.println("Index: " + move + " Value: " + currentValue + "\n");
                }
                if (currentValue > best) {
                    best = currentValue;
                    if (depth == maxDepth) {
                        bestMove = move;
                    }
                }
            }
            return best;
        }
}
