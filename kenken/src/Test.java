import java.util.Arrays;

public class Test {



    public static void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }

    public static void permutazioni(int[] v, int n) {
        int[] indexes = new int[n];

        System.out.println(Arrays.toString(v));

        int i = 0;
        while (i < n) {
            if (indexes[i] < i) {
                swap(v, i % 2 == 0 ?  0: indexes[i], i);
                System.out.println(Arrays.toString(v));
                indexes[i]++;
                i = 0;
            }
            else {
                indexes[i] = 0;
                i++;
            }
        }
    }
    public static void main(String[] args) {
        int[][] m = new int[0][0];
    }
}
