package ch.bbw.m411.connect4;

import static ch.bbw.m411.connect4.Connect4ArenaMain.*;

public class AlphaBetaPlayer extends Connect4ArenaMain.DefaultPlayer {

    //the best move
    int bestMove = NOMOVE;

    //the depth defined by the user
    int maxDepth;

    //number of cutoffs
    int cutOffNodeCount = 0;

    /**
     * The constructor for the smart player (Alpha Beta Player)
     *
     * @param depth The depth for the alpha beta algorithm
     */
    public AlphaBetaPlayer(int depth) {
        super();
        maxDepth = depth;
    }

    /**
     * The play method for this class, calling all the necessary functions
     *
     * @return the best possible move
     */
    @Override
    int play() {
        //start a timer to check for the duration of one play
        long startTime = System.currentTimeMillis();
        cutOffNodeCount = 0;

        //start the alpha-beta loop
        playUsingAlphaBeta(myColor, maxDepth, -10000, 10000);

        //stop the timer
        long endTime = System.currentTimeMillis();

        //print the execution time and the number of cutoffs
        System.out.println("\u001B[33m" + "Execution time: " + (endTime - startTime) + " milliseconds" + "\u001B[0m");
        System.out.println("\u001B[32m" + "Cutoffs: " + cutOffNodeCount + "\u001B[0m");

        return bestMove;
    }

    /**
     * The alpha beta algorithm method
     *
     * @param myColor the alpha beta player aka. smart player
     * @param depth   the depth of the alpha beta algorithm
     * @param alpha   the alpha value
     * @param beta    the beta value
     * @return max
     */
    private int playUsingAlphaBeta(Connect4ArenaMain.Stone myColor, int depth, int alpha, int beta) {
        //the opponent won
        if (isWinning(board, myColor.opponent())) {
            return -1000;
        }

        //evaluate the score if the depth reaches 0
        if (depth == 0) {
            return evaluate(myColor, board);
        }

        int max = alpha;

        //loop through all possible moves
        for (int move : getPossibleMoves(board)) {
            board[move] = myColor; // play to a possible move

            int currentValue = -playUsingAlphaBeta(myColor.opponent(), depth - 1, -beta, -max);

            board[move] = null; // undo playing
            if (depth == maxDepth) {
                System.out.println("Index: " + move + " Value: " + currentValue + "\n");
            }

            if (currentValue > max) {
                max = currentValue;
                if (depth == maxDepth) {
                    bestMove = move;
                }

            }
            if (max >= beta) {
                cutOffNodeCount++;
                break; //Alpha-Beta pruning
            }
        }
        return max;
    }
}

