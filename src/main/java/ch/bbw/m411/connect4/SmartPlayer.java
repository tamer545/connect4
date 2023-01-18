package ch.bbw.m411.connect4;

import static ch.bbw.m411.connect4.Connect4ArenaMain.*;

public class SmartPlayer extends Connect4ArenaMain.DefaultPlayer {
    int bestMove = NOMOVE;

    int maxDepth;

    int minimalDepth;

    /**
     * The constructor for the smart player (Alpha Beta Player)
     * @param depth The depth for the alpha beta algorithm
     */
    public SmartPlayer(int depth) {
        super();
        maxDepth = depth;
    }

    /**
     * The play method for this class, calling all the necessary functions
     * @return the best possible move
     */
    @Override
    int play() {
        int movesAvailable = countAvailableMoves(board);
        minimalDepth = Math.min(movesAvailable, maxDepth);
        playUsingAlphaBeta(myColor, minimalDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return bestMove;
    }

    /**
     * The alpha beta algorithm method
     * @param myColor the alpha beta player aka. smart player
     * @param depth the depth of the alpha beta algorithm
     * @param alpha the alpha value
     * @param beta the beta value
     * @return max
     */
    private int playUsingAlphaBeta(Connect4ArenaMain.Stone myColor, int depth, int alpha, int beta) {
        if (isWinning(board, myColor.opponent())) {
            return Integer.MIN_VALUE + 1;
        }

        if (depth == 0) {
            return evaluate(myColor, board);
        }

        int max = alpha;

        for (int move : getPossibleMoves(board)) {
            board[move] = myColor; // play to a possible move

            int currentValue = -playUsingAlphaBeta(myColor.opponent(), depth - 1, -beta, -max);

            board[move] = null; // undo playing
            if (depth == minimalDepth) {
                System.out.println("Index: " + move + " Value: " + currentValue + "\n");
            }

            if (currentValue > max) {
                max = currentValue;
                if (depth == minimalDepth) {
                    bestMove = move;
                }
                if (max >= beta) {
                    break;
                }
            }
        }
        return max;
    }
}

