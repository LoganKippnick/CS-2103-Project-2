import java.io.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

/**
 * Implements the GraphSearchEngine interface.
 */
public class GraphSearchEngineImpl implements GraphSearchEngine {
	public GraphSearchEngineImpl () {
	}

	public List<Node> findShortestPath (Node s, Node t) {
		Queue<Node> queue = new LinkedList<Node>();
		ArrayList<Node> visited = new ArrayList<Node>();
		HashMap<Node, Node> predecessor = new HashMap<Node, Node>();

		queue.add(s);
		visited.add(s);
		predecessor.put(s, null);

		while (!queue.isEmpty()) {
			Node current = queue.remove();

			if (current.equals(t)) {
				ArrayList<Node> path = new ArrayList<Node>();
				path.add(current);

				Node nextInPath = predecessor.get(current);

				while (nextInPath != null) {
					path.addFirst(nextInPath);
					nextInPath = predecessor.get(nextInPath);
				}

				return path;
			}
			else {
				for (Node neighbor : current.getNeighbors()) {
					if (!visited.contains(neighbor)) {
						queue.add(neighbor);
						visited.add(neighbor);
						predecessor.put(neighbor, current);
					}
				}
			}
		}

		return null;
	}
}
