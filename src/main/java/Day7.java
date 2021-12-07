import java.util.Arrays;

public class Day7 {
    public static void main(String[] args) {
        String[] input = Utils.readLines("input7.txt");
        int[] positions = Arrays.stream(input[0].split(",")).mapToInt(Integer::parseInt).toArray();

        int max = -2;
        for (int position : positions) {
            max = Math.max(position, max);
        }
        int[] nbByPos = new int[max+1];
        for (int position : positions) {
            nbByPos[position] += 1;
        }

        int cost = Integer.MAX_VALUE;
        for (int tryPos = 0; tryPos < max + 1; tryPos++) {
          cost = Math.min(cost, costToAlign(tryPos, nbByPos, cost));
        }

        System.out.println(cost);
    }

    static int costToAlign(int posToAlign, int[] nbByPos, int currentCost) {
        int res  = 0;
        for (int i = 0; i < nbByPos.length; i++) {
            int nbByPo = nbByPos[i];
            int n = Math.abs(posToAlign - i);
            res += nbByPo * (n*(n+1)/2);
            if(res>currentCost) {
                res= Integer.MAX_VALUE;
                break;
            }
        }
        return res;
    }
}
