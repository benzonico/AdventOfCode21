package aoc21;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day3 {


    public static void main(String[] args) {
        String[] input = Utils.readLines("aoc21/input3.txt");
        int[] setPositions = findSetPositions(input);
//        part1(input, setPositions);
        part2(input, setPositions);


    }

    private static void part2(String[] input, int[] set_occurences) {


        List<String> resO2 = Arrays.asList(input);
        List<String> resCO2 = Arrays.asList(input);

        int index = 0;
        char mostOccur = set_occurences[0] > input.length / 2 ? '1' : '0';
        char leastOccur = set_occurences[0] > input.length / 2 ? '0' : '1';

        while (resO2.size() > 1 || resCO2.size() > 1) {
            System.out.println("-------------"+index+"------------");
            System.out.println(resO2);
            System.out.println(resCO2);
            System.out.println(leastOccur+"--"+mostOccur);
            System.out.println("-----------------------------");
            int finalIndex = index;

            if (resO2.size() > 1) {
                char finalMostOccur = mostOccur;
                resO2 = resO2.stream().filter(s -> s.charAt(finalIndex) == finalMostOccur).collect(Collectors.toList());
            }
            if (resCO2.size() > 1) {
                char finalLeastOccur = leastOccur;
                resCO2 = resCO2.stream().filter(s -> s.charAt(finalIndex) == finalLeastOccur).collect(Collectors.toList());
            }
            index++;
            if (index == set_occurences.length) {
                break;
            }
            int set = 0;
            int unset = 0;

            //o2
            for (String s : resO2) {
                if (s.charAt(index) == '1') set++;
                else unset++;
            }
            if (set >= unset) {
                mostOccur = '1';
            } else {
                mostOccur = '0';
            }

            //Co2
            set = 0;
            unset = 0;
            for (String s : resCO2) {
                if (s.charAt(index) == '1') set++;
                else unset++;
            }
            if (set < unset) {
                leastOccur = '1';
            } else {
                leastOccur = '0';
            }
        }
        String o2String = resO2.get(0);
        String co2String = resCO2.get(0);
        System.out.println(o2String);
        System.out.println(co2String);

        int o2 = Integer.parseInt(o2String, 2);
        int co2 = Integer.parseInt(co2String, 2);
        System.out.println(o2);
        System.out.println(co2);
        System.out.println(o2 * co2);

    }


    private static void part1(String[] input, int[] set_occurences) {
        int linesReport = input.length;
        StringBuilder gammaBits = new StringBuilder();
        StringBuilder epsilonBits = new StringBuilder();

        for (int set_occurence : set_occurences) {
            if (set_occurence > linesReport / 2) {
                gammaBits.append("1");
                epsilonBits.append("0");
            } else {
                epsilonBits.append("1");
                gammaBits.append("0");
            }
        }
        int gamma = Integer.parseInt(gammaBits.toString(), 2);
        int epsilon = Integer.parseInt(epsilonBits.toString(), 2);
        System.out.println(gammaBits + " " + gamma);
        System.out.println(epsilonBits + " " + epsilon);
        System.out.println(gamma * epsilon);
    }

    private static int[] findSetPositions(String[] input) {
        int[] set_occurences = new int[12];
        for (String binary : input) {
            for (int i = 0; i < binary.length(); i++) {
                char c = binary.charAt(i);
                if (c == '1') {
                    set_occurences[i] = set_occurences[i] + 1;
                }
            }
        }
        return set_occurences;
    }
}
