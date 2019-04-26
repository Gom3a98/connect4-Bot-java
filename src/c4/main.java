package c4;

import java.io.IOException;
import static java.lang.Math.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class main {

//public static int counter=0 ; ;
    public static char AI_PIECE = 'x';
    public static char PLAYER_PIECE = 'o';
    public static int ROW_COUNT = 6;
    public static int COLUMN_COUNT = 7;
    static private Scanner input = new Scanner(System.in);

    public static int getRandom(Vector array) {
        int rnd = new Random().nextInt(array.size());
        return (int) array.elementAt(rnd);
    }



    public static Vector<Integer> getlocation(char[][] board) {
        Vector<Integer> valid_locations = new Vector<>();
        for (int i = 0; i < COLUMN_COUNT; i++) {
            if (board[ROW_COUNT - 2][i] == ' ') {
                valid_locations.add(i);
            }
        }

        return valid_locations;
    }

    public static int evaluate_window(Vector<Character> window2, char p) {
        char[] window = new char[window2.size()];
        for (int i = 0; i < window2.size(); i++) {
            window[i] = window2.elementAt(i);
        }
        int score = 0;
        char opp_piece = PLAYER_PIECE;
        if (p == PLAYER_PIECE) {
            opp_piece = AI_PIECE;
        }
        int counter = 0, counterempty = 0, counteropp = 0;
        for (int i = 0; i < window.length; i++) {
            if (window[i] == p) {
                counter++;
            } else if (window[i] == ' ') {
                counterempty++;
            } else if (window[i] == opp_piece) {
                counteropp++;
            }
        }
        if (counter == 4) {
            score += 100;
        } else if (counter == 3 && counterempty == 1) {
            score += 5;
        } else if (counter == 2 && counterempty == 2) {
            score += 2;
        } else if (counteropp == 3 && counterempty == 1) {
            score -= 4;
        }
        return score;
    }

    public static int score_position(char[][] board, char p) {

        int score = 0;
        // Score center column
        int counter = 0;
        for (int i = 0; i < ROW_COUNT; i++) {
            if (board[i][4] == p) {
                counter++;
            }
        }
        score += counter * 3;

        // Score Horizontal
        for (int r = 0; r < ROW_COUNT; r++) {
            Vector<Character> row_array = new Vector<>();
            for (int i = 0; i < COLUMN_COUNT; i++) {
                row_array.add(board[r][i]);
            }
            for (int c = 0; c < COLUMN_COUNT - 3; c++) {
                Vector<Character> window = new Vector<>();
                for (int i = c; i < c + 4; i++) {
                    window.add(row_array.elementAt(i));
                }
                score += evaluate_window(window, p);

            }
        }

        // Score Vertical
        for (int c = 0; c < COLUMN_COUNT; c++) {
            Vector<Character> col_array = new Vector<>();
            for (int i = 0; i < 6; i++) {
                col_array.add(board[i][c]);

            }
            for (int r = 0; r < ROW_COUNT - 3; r++) {
                Vector<Character> window = new Vector<>();
                for (int i = r; i < r + 4; i++) {
                    window.add(col_array.elementAt(i));
                }

                score += evaluate_window(window, p);

            }
        }
        // Score posiive sloped diagonal
        for (int r = 0; r < ROW_COUNT - 3; r++) {
            Vector<Character> col_array = new Vector<>();

            for (int c = 0; c < COLUMN_COUNT - 3; c++) {
                Vector<Character> window = new Vector<>();
                for (int i = 0; i < 4; i++) {
                    window.add(board[r + i][c + i]);
                }
                score += evaluate_window(window, p);

            }
        }
        for (int r = 0; r < ROW_COUNT - 3; r++) {
            Vector<Character> col_array = new Vector<>();

            for (int c = 0; c < COLUMN_COUNT - 3; c++) {
                Vector<Character> window = new Vector<>();
                for (int i = 0; i < 4; i++) {
                    window.add(board[r + 3 - i][c + i]);
                }
                score += evaluate_window(window, p);

            }
        }

        return score;
    }

    public static int[] minimax(char[][] board, int depth, int alpha, int beta, boolean maximizingPlayer) {

        Vector<Integer> valid_locations = new Vector<>();
        valid_locations = getlocation(board);

        boolean endplay = test(board, AI_PIECE) || test(board, PLAYER_PIECE) || valid_locations.size() == 0;

        if (depth == 0 || endplay) {
            if (endplay) {
                if (test(board, AI_PIECE)) {
                    int[] arr = new int[2];
                    arr[0] = -1;
                    arr[1] = 1000000000;

                    return arr;
                } else if (test(board, PLAYER_PIECE)) {
                    int[] arr = new int[2];
                    arr[0] = -1;
                    arr[1] = -1000000000;

                    return arr;
                } else {
                    int[] arr = new int[2];
                    arr[0] = -1;
                    arr[1] = 0;

                    return arr;
                }
            } else {
                int[] arr = new int[2];
                arr[0] = -1;

                arr[1] = score_position(board, AI_PIECE);   /// da 

                return arr;
            }
        }
        if (maximizingPlayer) {
            int value = (int) Double.NEGATIVE_INFINITY;

            int column = getRandom(valid_locations);  /// da

            for (int col = 0; col < valid_locations.size(); col++) {
                int row = -1;
                //  int row = get_next_open_row(board, valid_locations.elementAt(col)) ;
                for (int i = 0; i < ROW_COUNT; i++) {
                    if (board[i][valid_locations.elementAt(col)] == ' ') {
                        row = i;
                        break;
                    }
                }

                char[][] b_copy = new char[ROW_COUNT][COLUMN_COUNT];
                for (int i = 0; i < ROW_COUNT; i++) {
                    for (int j = 0; j < COLUMN_COUNT; j++) {
                        b_copy[i][j] = board[i][j];
                    }
                };
                b_copy[row][valid_locations.elementAt(col)] = AI_PIECE;
                int new_score = minimax(b_copy, depth - 1, alpha, beta, false)[1];
                if (new_score > value) {
                    value = new_score;
                    column = valid_locations.elementAt(col);
                }
                alpha = Math.max(alpha, value);
                if (alpha >= beta) {
                    break;
                }
            }
            int[] arr = new int[2];
            arr[0] = column;
            arr[1] = value;
            return arr;
        } else {
            int value = (int) Double.POSITIVE_INFINITY;
            int column = getRandom(valid_locations);

            for (int col = 0; col < valid_locations.size(); col++) {
                int row = -1;
                //    int row = get_next_open_row(board, valid_locations.elementAt(col)) ;
                for (int i = 0; i < ROW_COUNT; i++) {
                    if (board[i][valid_locations.elementAt(col)] == ' ') {
                        row = i;
                        break;
                    }
                }
                if (row == -1) {
                    continue;
                }
                char[][] b_copy = new char[ROW_COUNT][COLUMN_COUNT];
                for (int i = 0; i < ROW_COUNT; i++) {
                    for (int j = 0; j < COLUMN_COUNT; j++) {
                        b_copy[i][j] = board[i][j];
                    }
                }
                b_copy[row][valid_locations.elementAt(col)] = PLAYER_PIECE;
                int new_score = minimax(b_copy, depth - 1, alpha, beta, true)[1];
                if (new_score < value) {
                    value = new_score;
                    column = valid_locations.elementAt(col);
                }
                beta = Math.min(alpha, value);
                if (alpha >= beta) {
                    break;
                }
            }
            int[] arr = new int[2];
            arr[0] = column;
            arr[1] = value;
            return arr;

        }

    }

    public static void Insert(char[][] board, int[] s) {
        while (true) {
            int x;

            x = minimax(board, 5, (int) -Double.POSITIVE_INFINITY, (int) Double.POSITIVE_INFINITY, true)[0];
            System.out.println("Player one put on : " + x);
            if (board[ROW_COUNT - 1][x] == ' ') {
                board[s[x]][x] = 'x';
                s[x]++;
            }
            clearScreen();
            display(board);
            if (test(board, 'x') == true) {
                System.out.println("Player one Win");
                return;
            }
            System.out.print("player two drob where: ");
            x = input.nextInt();
            x--;

            if (board[ROW_COUNT - 1][6 - x] == ' ') {

                board[s[6 - x]][6 - x] = 'o';
                s[6 - x]++;
            }
    clearScreen();
            display(board);
            if (test(board, 'y') == true) {
                System.out.println("Player Two Win");
                return;
            }
        }
    }
        public static void clearScreen() {  
    System.out.print("\033[H\033[2J");  
    System.out.flush();  
} 
    public static void display(char[][] board) {

        System.out.print("\n  1      2      3      4      5      6      7\n\n");
        for (int a = 5; a >= 0; a--) {
            for (int b = 6; b >= 0; b--) {
                System.out.print("[ " + board[a][b] + " ]" + "  ");
            }
            System.out.print("\n \n \n");
        }
    }

    public static boolean test(char[][] board, char p) {

        for (int y = 0; y < COLUMN_COUNT - 3; y++) {
            for (int i = 0; i < ROW_COUNT; i++) {
                if (board[i][y] == p && board[i][y + 1] == p && board[i][y + 2] == p && board[i][y + 3] == p) {

                    return true;
                }
            }
        }
        for (int i = 0; i < COLUMN_COUNT; i++) {
            for (int y = 0; y < ROW_COUNT - 3; y++) {
                if (board[y][i] == p && board[y + 1][i] == p && board[y + 2][i] == p && board[y + 3][i] == p) {
                    return true;
                }
            }
        }
        for (int i = 0; i < COLUMN_COUNT - 3; i++) {
            for (int y = 0; y < ROW_COUNT - 3; y++) {
                if (board[y][i] == p && board[y + 1][i + 1] == p && board[y + 2][i + 2] == p && board[y + 3][i + 3] == p) {
                    return true;
                }

            }
        }
        for (int i = 0; i < COLUMN_COUNT - 3; i++) {
            for (int y = 3; y < ROW_COUNT; y++) {
                if (board[y][i] == p && board[y - 1][i + 1] == p && board[y - 2][i + 2] == p && board[y - 3][i + 3] == p) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[] s = {0, 0, 0, 0, 0, 0, 0};
        char[][] board = new char[6][7];
        for (int a = 0; a <= 5; a++) {
            for (int b = 0; b <= 6; b++) {
                board[a][b] = ' ';
            }
        }
        display(board);
        Insert(board, s);
    }
}
