package aoc21;

public class Day6 {

  public static void main(String[] args) {
    String input = Utils.readLines("aoc21/input6.txt")[0];
    String[] split = input.split(",");


    long[] nbPerGen = new long[9];
    for (String s : split) {
      int gen = Integer.parseInt(s);
      nbPerGen[gen] = nbPerGen[gen] + 1;
    }

    for (int day = 0; day < 256; day++) {

      long[] newNbPerGen = new long[9];
      for (int i = 0; i < nbPerGen.length; i++) {
        if (i == 0) {
          newNbPerGen[8] = nbPerGen[0];
          newNbPerGen[6] = nbPerGen[0];
        } else {
          newNbPerGen[i - 1] = nbPerGen[i] + newNbPerGen[i - 1];
        }
      }
      nbPerGen = newNbPerGen;
    }

    long tot = 0;
    for (long l : nbPerGen) {
      tot += l;
    }
    System.out.println(tot);
  }
}

