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

	/**
	 * Finds a shortest path between the given two Nodes in the Graph using a breadth-first search algorithm.
	 *
	 * @param s the start node.
	 * @param t the target node.
	 * @return a list of nodes in a shortest path.
	 */
	public List<Node> findShortestPath (Node s, Node t) {
		// if either of the parameters are null, no need to try to run the algorithm
		if (s == null || t == null) {
			return null;
		}

		Queue<Node> queue = new LinkedList<Node>();
		LinkedList<Node> visited = new LinkedList<Node>();
		HashMap<Node, Node> predecessor = new HashMap<Node, Node>();

		// initialize BFS by adding starting node
		queue.add(s);
		visited.add(s);
		predecessor.put(s, null);

		while (!queue.isEmpty()) {
			Node current = queue.remove();

			if (current.equals(t)) {
				ArrayList<Node> path = new ArrayList<Node>();
				path.add(current);

				Node nextInPath = predecessor.get(current);

				// add predecessors to path until traced back to starting node
				while (nextInPath != null) {
					path.addFirst(nextInPath);
					nextInPath = predecessor.get(nextInPath);
				}

				return path;
			}
			else {
				// add neighbors of current node to end of queue
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
