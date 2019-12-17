package main.entity;

public class LCS {

    public static final double MATCHING_PERCENT = .7;

    public static boolean computeMatching(String word, String sample) {
        StringBuilder findMatch = new StringBuilder();
        int M = word.length();
        int N = sample.length();

        int[][] opt = new int[M + 1][N + 1];

        for (int i = M - 1; i >= 0; i--) {
            for (int j = N - 1; j >= 0; j--) {
                if (word.charAt(i) == sample.charAt(j))
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                else
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
            }
        }

        int i = 0, j = 0;
        while (i < M && j < N) {
            if (word.charAt(i) == sample.charAt(j)) {
                findMatch.append(word.charAt(i));
                i++;
                j++;
            } else if (opt[i + 1][j] >= opt[i][j + 1]) i++;
            else j++;
        }
        System.out.println("Znaleziony :" + findMatch + " dla :" + sample.length() * MATCHING_PERCENT + " wychodzi :" + (findMatch.length() >= sample.length() * MATCHING_PERCENT));
        return findMatch.length() >= sample.length() * MATCHING_PERCENT;
    }

}
