import java.util.ArrayList;
import java.util.List;

public class Day6 {

  public static void main(String[] args) {
    String input = Utils.readLines("input6.txt")[0];
    //26984457539
    //7052877487
    //8301465628
    String[] split = "3,4,3,1,2".split(",");
    List<Integer> fishes = new ArrayList<>();
    for (String s : split) {
      fishes.add(Integer.parseInt(s));
    }



    
    // a,b,c...
    long[] nbFishByDays = new long[258];
    int[] daysLeft = new int[258];
    nbFishByDays[0] = fishes.size();
    for (int day = 0; day <= 256; day++) {
      System.out.println("Day"+day);
      long newFishesCount = 0;

      for (int popDay = 0; popDay < day; popDay++) {
        if (popDay == 0) {
          List<Integer> next = new ArrayList<>();
          for (int fish : fishes) {
            if (fish == 0) {
              next.add(6);
              newFishesCount++;
            } else {
              next.add(fish - 1);
            }
          }
          fishes = next;
          System.out.println(fishes);
        } else {
          int fish = daysLeft[popDay];
          if (fish == 0) {
            daysLeft[popDay] = 6;
            newFishesCount += nbFishByDays[popDay];
          } else {
            daysLeft[popDay] = fish - 1;
          }
        }
      }

      System.out.println(newFishesCount);
      //if(day!=256) {
        nbFishByDays[day+1] = newFishesCount;
        daysLeft[day+1] = 8;
      //}
    }

    long tot = 0;
    for (long nbFishByDay : nbFishByDays) {
      System.out.println(nbFishByDay);
      tot+=nbFishByDay;
    }
    //1 102 910 859
    System.out.println(tot);
    //357 289 594 too low
    //455 623 822 970 low
  }
}

