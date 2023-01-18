package ch.bbw.m411.connect4;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Plays a game of Connect Four on a 4x7 board (a variation of the original 6x7 board).
 * The pieces fall straight down, occupying the lowest available space within the column.
 */
public class Connect4ArenaMain {

    static final int WIDTH = 7;

    static final int HEIGHT = 4;

    static final int NOMOVE = -1;

    static final int ALL_POSITIONS = WIDTH * HEIGHT;

    public static void main(String[] args) {
        new Connect4ArenaMain().play(new AlphaBetaPlayer(8), new HumanPlayer());
    }

    static String toDebugString(Stone[] board) {
        var sb = new StringBuilder();
        for (int r = 0; r < HEIGHT; r++) {
            for (int c = 0; c < WIDTH; c++) {
                var value = board[r * WIDTH + c];
                sb.append(value == null ? "." : (value == Stone.RED ? "X" : "O"));
            }
            sb.append("-");
        }
        return sb.toString();
    }

    Connect4Player play(Connect4Player red, Connect4Player blue) {
        if (red == blue) {
            throw new IllegalStateException("must be different players (simply create two instances)");
        }
        var board = new Stone[WIDTH * HEIGHT];
        red.initialize(Arrays.copyOf(board, board.length), Stone.RED);
        blue.initialize(Arrays.copyOf(board, board.length), Stone.BLUE);
        var lastMove = NOMOVE;
        var currentPlayer = red;
        for (int round = 0; round < board.length; round++) {
            var currentColor = currentPlayer == red ? Stone.RED : Stone.BLUE;
            System.out.println(HumanPlayer.toPrettyString(board) + currentColor + " to play next...");
            lastMove = currentPlayer.play(lastMove);
            if ((lastMove < 0 || lastMove >= WIDTH * HEIGHT) ||
                    board[lastMove] != null || (lastMove >= WIDTH && board[lastMove - WIDTH] == null)) {
                throw new IllegalStateException("cannot play to position " + lastMove + " @ " + toDebugString(board));
            }
            board[lastMove] = currentColor;
            if (isWinning(board, currentColor)) {
                System.out.println(
                        HumanPlayer.toPrettyString(board) + "...and the winner is: " + currentColor + " @ " + toDebugString(board));
                return currentPlayer;
            }
            currentPlayer = currentPlayer == red ? blue : red;
        }
        System.out.println(HumanPlayer.toPrettyString(board) + "...it's a DRAW @ " + toDebugString(board));
        return null; // null implies a draw
    }

    public static boolean isWinning(Stone[] board, Stone forColor) {

        //a loop to check for a diagonal win
        for (int i = 0; i < WIDTH; i++) {
            if (board[i] == forColor && board[i] == board[i + 7] && board[i + 7] == board[i + 14] && board[i + 14] == board[i + 21]) {
                return true;
            }
        }

        //a loop to check for a diagonal win
        for (int i = 3; i < WIDTH; i++) {
            if (board[i] == forColor && board[i] == board[i + 6] && board[i + 6] == board[i + 12] && board[i + 12] == board[i + 18])
                return true;
        }

        //a loop to check for a vertical win
        for (int i = 0; i < HEIGHT; i++) {
            if (board[i] == forColor && board[i] == board[i + 8] && board[i + 8] == board[i + 16] && board[i + 16] == board[i + 24]) {
                return true;
            }
        }

        //a loop to check for any horizontal win
        for (int i = 0; i < ALL_POSITIONS - 3; i++) {
            if ((i == 0 || i == 1 || i == 2 || i == 3 || i == 7 || i == 8 || i == 9 || i == 10 || i == 14 || i == 15 || i == 16 || i == 17 || i == 21 || i == 22 || i == 23 || i == 24) && board[i] == forColor && board[i] == board[i + 1] && board[i + 1] == board[i + 2] && board[i + 2] == board[i + 3]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Looks at all the possible moves (loops through all the blocks and adds them to an array if they are still empty)
     *
     * @param board the play board
     * @return an array of all the possible moves
     */
    public static ArrayList<Integer> getPossibleMoves(Stone[] board) {
        ArrayList<Integer> possibleMoves = new ArrayList<>();

        //makes the player start in the middle
        int rowCount = 3;

        //a loop through all rows
        for (int i = 0; i < 7; i++) {
            //move ordering
            if (i % 2 == 0) {
                rowCount += i;
            } else {
                rowCount -= i;
            }
            for (int j = rowCount; j < ALL_POSITIONS; j += 7) {
                if (board[j] == null) {
                    possibleMoves.add(j);
                    break;
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Evaluates the score of the player
     *
     * @param myColor the player
     * @param board   the board for the game
     * @return totalpoints of the player
     */
    public static int evaluate(Stone myColor, Stone[] board) {

        //the points for each position
        int[] points = {3, 4, 6, 7, 6, 4, 3
                , 2, 4, 6, 7, 6, 4, 2
                , 2, 4, 6, 7, 6, 4, 2
                , 3, 4, 6, 7, 6, 4, 3
        };
        int totalPoints = 0;
        for (int i = 0; i < ALL_POSITIONS; i++) {
            if (board[i] == myColor) {
                totalPoints += points[i]; //if the stone belongs to the player, add the points
            } else if (board[i] == myColor.opponent()) {
                totalPoints -= points[i]; //if the stone belongs to the opponent, subtract the points
            }
        }
        return totalPoints;
    }

    public enum Stone {
        RED, BLUE;

        public Stone opponent() {
            return this == RED ? BLUE : RED;
        }
    }

    public interface Connect4Player {

        /**
         * Called before the game starts and guaranteed to only be called once per livetime of the player.
         *
         * @param board       the starting board, usually an empty board.
         * @param colorToPlay the color of this player
         */
        void initialize(Stone[] board, Stone colorToPlay);

        /**
         * Perform a next move, will only be called if the Game is not over yet.
         * Each player has to keep an internal state of the 4x7 board, wher the 0-index is on the bottom row.
         * The index-layout looks as:
         * <pre>
         * 30 31 32 33 34 35 36
         * 14 15 16 17 18 19 29
         *  7  8  9 10 11 12 13
         *  0  1  2  3  4  5  6
         * </pre>
         *
         * @param opponendPlayed the last index where the opponent played to (in range 0 - width*height exclusive)
         *                       or -1 if this is the first move.
         * @return an index to play to (in range 0 - width*height exclusive)
         */
        int play(int opponendPlayed);
    }

    /**
     * An abstract helper class to keep track of a board (and whatever we or the opponent played).
     */
    public abstract static class DefaultPlayer implements Connect4Player {

        Stone[] board;

        Stone myColor;

        @Override
        public void initialize(Stone[] board, Stone colorToPlay) {
            this.board = board;
            myColor = colorToPlay;
        }

        @Override
        public int play(int opponendPlayed) {
            if (opponendPlayed != NOMOVE) {
                board[opponendPlayed] = myColor.opponent();
            }
            var playTo = play();
            board[playTo] = myColor;
            return playTo;
        }

        /**
         * Givent the current {@link #board}, find a suitable position-index to play to.
         *
         * @return the position to play to as defined by {@link Connect4Player#play(int)}.
         */
        abstract int play();

    }

    public static class HumanPlayer extends DefaultPlayer {

        static String toPrettyString(Stone[] board) {
            var sb = new StringBuilder();
            for (int r = HEIGHT - 1; r >= 0; r--) {
                for (int c = 0; c < WIDTH; c++) {
                    var index = r * WIDTH + c;
                    if (board[index] == null) {
                        if (index < WIDTH || board[index - WIDTH] != null) {
                            sb.append("\033[37m").append(index).append("\033[0m ");
                            if (index < 10) {
                                sb.append(" ");
                            }
                        } else {
                            sb.append("\033[37m.\033[0m  ");
                        }
                    } else if (board[index] == Stone.RED) {
                        sb.append("\033[1;31mX\033[0m  ");
                    } else {
                        sb.append("\033[1;34mO\033[0m  ");
                    }
                }
                sb.append("\n");
            }
            return sb.toString();
        }

        @Override
        int play() {
            System.out.println("where to to put the next " + myColor + "?");
            var scanner = new Scanner(System.in, StandardCharsets.UTF_8);
            return Integer.parseInt(scanner.nextLine());
        }

    }

    public static class GreedyPlayer extends DefaultPlayer {

        @Override
        int play() {
            for (int c = 0; c < WIDTH; c++) {
                for (int r = 0; r < HEIGHT; r++) {
                    var index = r * WIDTH + c;
                    if (board[index] == null) {
                        return index;
                    }
                }
            }
            throw new IllegalStateException("cannot play at all");
        }
    }
}
