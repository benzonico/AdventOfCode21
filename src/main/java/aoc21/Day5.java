package aoc21;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Day5 {
    public static void main(String[] args) {
        String[] input = Utils.readLines("aoc21/input5.txt");
        List<Vent> vents = parseLines(input);
        Multiset<Point> points = HashMultiset.create();
        for (Vent v : vents) {
            points.addAll(v.coveredPoints());
        }
        System.out.println(points.entrySet().stream().filter(e -> e.getCount() >= 2).count());
    }

    private static List<Vent> parseLines(String[] input) {
        List<Vent> vents = new ArrayList<>();
        for (String line : input) {
            String arrowString = " -> ";
            int arrow = line.indexOf(arrowString);
            vents.add(new Vent(line.substring(0, arrow), line.substring(arrow + arrowString.length())));
        }
        return vents;
    }

    private static class Point {
        private final int x;
        private final int y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private Point(int[] point) {
            this.x = point[0];
            this.y = point[1];
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ')';
        }
    }

    private static class Vent {

        private final Point start;
        private final Point end;

        private boolean isVertical() {
            return start.y == end.y;
        }

        boolean isHorizontal() {
            return start.x == end.x;
        }

        public Vent(String start, String end) {
            this.start = parse(start);
            this.end = parse(end);

        }

        private Point parse(String pair) {
            return new Point(Arrays.stream(pair.split(",")).mapToInt(Integer::parseInt).toArray());
        }

        public List<Point> coveredPoints() {
            List<Point> points = new ArrayList<>();
            Point vector = new Point(vectorDir(start.x, end.x), vectorDir(start.y, end.y));
            Point currentPoint = this.start;
            points.add(currentPoint);
            while (!currentPoint.equals(end)) {
                currentPoint = new Point(currentPoint.x + vector.x, currentPoint.y + vector.y);
                points.add(currentPoint);
            }
            return points;
        }

        private int vectorDir(int start, int end) {
            return -1*Integer.compare(start, end);
        }
    }
}
