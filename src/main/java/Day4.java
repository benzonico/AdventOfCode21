import java.util.*;
import java.util.function.Predicate;

public class Day4 {
    public static void main(String[] args) {
        String[] input = Utils.readLines("input4.txt");

        List<int[][]> boards = getboards(input);
        List<Integer> boardWon = new ArrayList<>();
        List<Integer> winningDraw = new ArrayList<>();
        int[] numberDrawn = Arrays.stream(input[0].split(",")).mapToInt(Integer::parseInt).toArray();
        for (int draw : numberDrawn) {
            System.out.println("Drawing "+draw);
            for (int boardIndex = 0; boardIndex < boards.size(); boardIndex++) {
                if (boardWon.contains(boardIndex)) {
                    continue;
                }
                int[][] board = boards.get(boardIndex);
                Pos pos = findInBoard(draw, board);
                if (pos != Pos.NOT_FOUND) {
                    board[pos.line][pos.col] = -1;
//                    System.out.println(boardToString(board));
                    if (isWinning(board)) {
                        boardWon.add(boardIndex);
                        winningDraw.add(draw);
                    }
                }
            }
        }
        System.out.println(computeScore(boards.get(boardWon.get(boardWon.size()-1)), winningDraw.get(winningDraw.size()-1)));

    }

    private static String boardToString(int[][] board) {
        StringBuilder res = new StringBuilder();
        for (int[] line : board) {
            res.append(Arrays.toString(line)).append("\n");
        }
        return res.toString();
    }

    private static int computeScore(int[][] board, int draw) {
        int res = 0;
        for (int[] currentLine : board) {
            for (int nb : currentLine) {
                if(nb != -1) res+=nb;
            }
        }
        return res*draw;
    }

    private static boolean isWinning(int[][] board) {
        int[] columnSums = new int[5];
        for (int[] currentLine : board) {
            int lineSum = 0;
            for (int j = 0; j < currentLine.length; j++) {
                columnSums[j] = columnSums[j] + currentLine[j];
                lineSum += currentLine[j];
            }
            if (lineSum == -5) {
                return true;
            }
        }
        for (int columnSum : columnSums) {
            if (columnSum == -5) {
                return true;
            }
        }
        return false;
    }

    private static Pos findInBoard(int num, int[][] board) {
        for (int i = 0; i < board.length; i++) {
            int[] currentLine = board[i];
            for (int j = 0; j < currentLine.length; j++) {
                if (num == currentLine[j]) {
                    System.out.println("Find "+ num + " at "+i+","+j+"\n"+boardToString(board) );
                    return new Pos(i, j);
                }
            }
        }
        return Pos.NOT_FOUND;
    }

    private static int[][] newBoard() {
        int[][] board = new int[5][];
        for (int i = 0; i < board.length; i++) {
            board[i] = new int[5];
        }
        return board;
    }

    private static List<int[][]> getboards(String[] input) {
        List<int[][]> res = new ArrayList<>();
        int[][] board = new int[5][];
        int boardLine = 0;
        for (int i = 2; i < input.length; i++) {
            String line = input[i];
            if (line.isBlank()) {
                res.add(board);
                board = new int[5][];
                boardLine = 0;
            } else {
                String[] split = line.split("[\\s]+");
                board[boardLine] = Arrays.stream(split).filter(Predicate.not(String::isBlank)).mapToInt(Integer::parseInt).toArray();
                boardLine++;
                if (boardLine > 5) {
                    throw new IllegalStateException("woopsie");
                }
            }
        }
        return res;
    }

    static class Pos {
        static final Pos NOT_FOUND = new Pos(-1, -1);

        public final int line;
        public final int col;

        public Pos(int i, int j) {
            line = i;
            col = j;
        }

        @Override
        public String toString() {
            return "(" + line+","+col+")";
        }
    }
}
