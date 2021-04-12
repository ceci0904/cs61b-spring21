package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author CECILIA WANG
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */

    public int findNextOccupiedRow (int c, int r) {
        for (int i = r - 1; i >= 0; i = i - 1) {
            if (board.tile(c, i) != null) {

                return i;
            }
        }
        return -1;
    }

//    public Tile mergeTwoTiles (Tile t) {
//        if (findNextOccupiedRow(t.col(), t.row()) == -1) {
//            System.out.println("empty col");
//            return t;
//        } else { //if there is a tile below
//            int nR = findNextOccupiedRow(t.col(), t.row());
//            if (t.value() == tile(t.col(),nR).value()) { //if the tile below t has the same value
//
//                board.move(t.col(), t.row(), nT);
//                Tile mT = board.tile(t.col(), t.row());
//                score = score + mT.value();
//
//                System.out.println("score updated");
//                return mT;
//                 //move nT to t's position
//
//            } else {
//                System.out.println("not a merge");
//                return t;
//            }
//        }
//
//    }
    //return the row of the nearest empty space, return -1 when there is no empty space above6
    public int findEmptyRowAbove (int c, int r) {
        int x = r;
//        if (r == size() - 1) {
//            return -1;
//        }


        while (x < size() - 1 && board.tile(c, x + 1) == null) {
            x = x + 1;
        }

        if (x != r) {
            return x;
        } else {
            return -1;
        }
    }


    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.

        board.setViewingPerspective(side);

        boolean hasMerged = false;


        for (int c = 0; c < size(); c = c + 1) {

            for (int r = size() - 2; r >= 0; r = r - 1) {
                if (board.tile(c, r) != null) { //find occupied tile below last row
                    Tile t = board.tile(c, r);
                    if (findEmptyRowAbove(c, r) == size() - 1) { //if the empty row is the last row, move the tile to the last row
                        board.move(c, size() - 1, t);
                        changed = true;
                        System.out.println("changed to true at 1 " + "col is " + c + " row is " + r +" findabove is " + findEmptyRowAbove(c, r));
                        hasMerged = false;
                    } else if (findEmptyRowAbove(c, r) != -1) {//check if the tile above is a merge
                        if (!hasMerged && tile(c, findEmptyRowAbove(c, r) + 1).value() == t.value()) { //if same value, move t to the tile above it
                            board.move(c, findEmptyRowAbove(c, r) + 1, t);
                            score = score + tile(c, findEmptyRowAbove(c, r) + 1).value();
                            changed = true;
                            System.out.println("changed to true at 2 " + "col is " + c + " row is " + r +" findabove is " + findEmptyRowAbove(c, r));
                            hasMerged = true;

                        } else {
                            board.move(c, findEmptyRowAbove(c, r), t); //if not same value, move t to the tile below
                            changed = true;
                            System.out.println("changed to true at 3 " + "col is " + c + " row is " + r +" findabove is " + findEmptyRowAbove(c, r));
                            hasMerged = false;

                        }
                    } else if (findEmptyRowAbove(c, r) == -1) {
                        if (t.value() == tile(c, r + 1).value()) {
                            System.out.println("c is " + c + " r is " + r);
                            board.move(c, r + 1, t);
                            score = score + tile(c, r + 1).value();
                            changed = true;
                            System.out.println("changed to true at 4 " + "col is " + c + " row is " + r +" findabove is " + findEmptyRowAbove(c, r));
                            hasMerged = true;
                        } else {

                            System.out.println("changed to false" + " tile value above is " + tile(c, r + 1).value() + "col is " + c + " row is " + r);
                        }


                    }
                }


            }

        }




        checkGameOver();
        if (changed) {
            setChanged();
        }
        System.out.println("final changed status is" + changed);
        board.setViewingPerspective(Side.NORTH);
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        for (int i = 0; i < b.size(); i = i + 1) {
            for (int j = 0; j < b.size(); j = j + 1) {
                if (b.tile (i, j) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {

        for (int i = 0; i < b.size(); i = i + 1) {

            for (int j = 0; j < b.size(); j = j + 1) {
                if (b.tile(i, j) == null) {
                    continue;
                }
                if (b.tile (i, j).value() == MAX_PIECE) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {

        if (emptySpaceExists(b)) {
            return true;
        }


        for (int i = 0; i < b.size() - 1; i = i + 1) {

            for (int j = 0; j < b.size() - 1; j = j + 1) {
                if (b.tile(i, j).value() == b.tile(i + 1, j).value() || b.tile(i, j).value() == b.tile(i, j + 1).value()) {
                    return true;
                }
            }

            if (b.tile(i, b.size() - 1).value() == b.tile(i + 1, b.size() - 1).value()) {
                return true;
            }
        }

        for (int i = 0; i < b.size() - 1; i = i + 1) {
            if (b.tile(b.size() - 1, i).value() == b.tile(b.size() - 1, i + 1).value()) {
                return true;
            }
        }



        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
