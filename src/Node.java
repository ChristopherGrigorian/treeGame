import java.util.Objects;

public class Node implements Comparable<Node> {
    Point cords;
    int gscore;
    int fscore;
    Node previous;

    public Node(Point cords, int gscore, int fscore, Node previous) {
        this.cords = cords;
        this.gscore = gscore;
        this.fscore = fscore;
        this.previous = previous;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cords.x, cords.y);
    }

    @Override
    public boolean equals(Object other) {
        Node node = (Node) other;
        return other != null
                && getClass() == other.getClass()
                && cords.x == node.cords.x
                && cords.y == node.cords.y;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.fscore, other.fscore);
    }


}

