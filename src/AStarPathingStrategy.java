import javax.print.attribute.standard.CopiesSupported;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy {

    public static int heuristic(Point start, Point end) {
        return Math.abs(end.x - start.x) + Math.abs(end.y - start.y);
    }

    @Override
    public List<Point> computePath(Point start, Point end, Predicate<Point> canPassThrough, BiPredicate<Point, Point> withinReach, Function<Point, Stream<Point>> potentialNeighbors) {
        Node startNode = new Node(start, 0, heuristic(start, end), null);
        List<Node> openset = new ArrayList<>();
        List<Node> closedset = new ArrayList<>();
        List<Point> path = new ArrayList<>();

        openset.add(startNode);

        while (!openset.isEmpty()) {
            Node current = openset.stream()
                    .min(Node::compareTo)
                    .orElse(null);

            openset.remove(current);
            closedset.add(current);

            if (withinReach.test(current.cords, end)) {
                Node curNode = current;
                while (curNode.previous != null) {
                    path.add(0, curNode.cords);
                    curNode = curNode.previous;
                }
                return path;
            }

            Stream<Point> neighborStream = potentialNeighbors.apply(current.cords);

            List<Node> neighborList = neighborStream
                    .filter(canPassThrough)
                    .map(p -> new Node(p, current.gscore + 1, heuristic(p, end), current))
                    .filter(p -> !closedset.contains(p))
                    .toList();

            for (Node neighbor : neighborList) {
                if (!openset.contains(neighbor)) {
                    openset.add(neighbor);
                } else if (neighbor.gscore < openset.get(openset.indexOf(neighbor)).gscore) {
                    openset.remove(neighbor);
                    openset.add(neighbor);
                }
            }
        }
        return Collections.emptyList(); // No path can be found
    }
}

