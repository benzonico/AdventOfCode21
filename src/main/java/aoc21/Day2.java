package aoc21;

public class Day2 {
    public static void main(String[] args) {

        String[] input = Utils.readLines("src/input2.txt");

        int hpos = 0;
        int vpos = 0;
        int aim = 0;
        for (String currentInput : input) {
            int value = Integer.parseInt(currentInput.substring(currentInput.indexOf(" ")+1));
            switch (currentInput.charAt(0)) {
                case 'f':
                    hpos += value;
                    vpos += (aim * value);
                    break;
                case 'u':
                    aim -= value;
                    break;
                case 'd':
                    aim += value;
                    break;
                default:
                    System.out.println(currentInput+"  "+value);
            }
        }
        System.out.println(hpos + "*"+vpos+"="+hpos*vpos);
    }

}
