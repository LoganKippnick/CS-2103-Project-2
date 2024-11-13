import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.*;
import java.io.*;

/**
 * Code to test an <tt>GraphSearchEngine</tt> implementation.
 */

public class GraphSearchEngineTester {

	/**
	 * Makes a graph from testActors.tsv and testMovies.tsv databases to be used in tests.
	 *
	 * @return The test graph made from the test databases
	 */
	IMDBGraph makeGraph() {
		IMDBGraph graph;
		try {
			graph = new IMDBGraphImpl(
					IMDBGraphImpl.IMDB_DIRECTORY + "/testActors.tsv",
					IMDBGraphImpl.IMDB_DIRECTORY + "/testMovies.tsv");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Could not make graph.");
			return null;
		}
		return graph;
	}

	/**
	 * Tests to make sure that the search engine will return the shortest path.
	 */
	@Test
	@Timeout(5)
	void testShortestPath() {
		final IMDBGraph graph = makeGraph();
		final GraphSearchEngine searchEngine = new GraphSearchEngineImpl();

		final Node actor1 = graph.getActor("Kris");
		final Node actor2 = graph.getActor("Logan");

		final List<Node> shortestPath = searchEngine.findShortestPath(actor1, actor2);

		// shortest path should be of length 5
		assertEquals(5, shortestPath.size());

		// check that nodes in shortestPath are what we expect
		final String[] correctNames = { "Kris", "Blah2", "Sara", "Blah4", "Logan" };
		int idx = 0;
		for (Node node : shortestPath) {
			assertEquals(correctNames[idx++], node.getName());
		}
	}

	/**
	 * Tests to make sure that two nodes on separate islands will not have path.
	 */
	@Test
	@Timeout(5)
	void testIsland() {
		final IMDBGraph graph = makeGraph();
		final GraphSearchEngine searchEngine = new GraphSearchEngineImpl();

		final Node actor1 = graph.getActor("Logan");
		final Node actor2 = graph.getActor("Finn");

		final List<Node> path = searchEngine.findShortestPath(actor1, actor2);

		// since Logan and Finn are on separate items, they should have no valid path and we expect null
		assertNull(path);
	}

	/**
	 * Tests to make sure that there are no nodes are repeated in a path.
	 */
	@Test
	@Timeout(5)
	void testDuplicates() {
		final IMDBGraph graph = makeGraph();
		final GraphSearchEngine searchEngine = new GraphSearchEngineImpl();

		final Node actor1 = graph.getActor("Ryan");
		final Node actor2 = graph.getActor("Sara");

		final List<Node> path = searchEngine.findShortestPath(actor1, actor2);

		// fail test if search engine can't find path
		assertNotNull(path);

		// iterate through the list and keep track of visited nodes
		final List<Node> visited = new ArrayList<Node>();
		for (Node node : path) {
			if (visited.contains(node)) {
				// if we encounter a node we have already visited, there are duplicates in the path and test fails
				fail("Path contains duplicate nodes.");
				return;
			}
			else {
				// add unique node to visited nodes
				visited.add(node);
			}
		}
	}

	/**
	 * Tests to make sure that any node in the path is a neighbor of the previous node in the path.
	 */
	@Test
	@Timeout(5)
	void testNeighbors() {
		final IMDBGraph graph = makeGraph();
		final GraphSearchEngine searchEngine = new GraphSearchEngineImpl();

		final Node actor1 = graph.getActor("Sandy");
		final Node actor2 = graph.getActor("Kris");

		final List<Node> path = searchEngine.findShortestPath(actor1, actor2);

		// fail test if search engine can't find path
		assertNotNull(path);

		// checks that the next node is a neighbor of each node (except the last)
		for (int i = 0; i < path.size() - 1; i++) {
			assertTrue(path.get(i).getNeighbors().contains(path.get(i + 1)));
		}
	}

	/**
	 * Tests to make sure that nodes that don't have neighbors in the database don't have any neighbors in the graph.
	 */
	@Test
	@Timeout(5)
	void testNoNeighbors() {
		final IMDBGraph graph = makeGraph();

		final Node actor1 = graph.getActor("Martin");

		// Martin has no "known for" titles that are testMovies.tsv
		assertTrue(actor1.getNeighbors().isEmpty());

		final Node movie1 = graph.getMovie("Blah9");

		// t9 is never referenced in testActors.tsv
		assertTrue(movie1.getNeighbors().isEmpty());
	}

	/**
	 * Tests to make sure that only people with an "actor" or "actress" profession from the database are in the graph.
	 */
	@Test
	@Timeout(5)
	void testOnlyActors() {
		final IMDBGraph graph = makeGraph();
		final GraphSearchEngine searchEngine = new GraphSearchEngineImpl();

		final Node notActor = graph.getActor("Jessie");

		// Jessie has only a director role, so should not be in graph
		assertNull(notActor);

		final Node actor = graph.getActor("Finn");

		searchEngine.findShortestPath(actor, notActor);

		// path should return null if either actor is null
		assertNull(searchEngine.findShortestPath(actor, notActor));
	}

	/**
	 * Tests to make sure that only titles with a "movie" type from the database are in the graph.
	 */
	@Test
	@Timeout(5)
	void testOnlyMovies() {
		final IMDBGraph graph = makeGraph();

		final Node notMovie = graph.getMovie("NotBlah");

		// NotBlah is of documentary title type, so should not be in graph
		assertNull(notMovie);

		final Node actor = graph.getActor("Logan");

		// NotBlah should also not be a neighbor of Logan in the graph, although Logan is known for NotBlah
		assertFalse(actor.getNeighbors().contains(notMovie));
	}

	/**
	 * Tests to make sure that all actors have a unique name.
	 */
	@Test
	@Timeout(5)
	void testUniqueNames() {
		final IMDBGraph graph = makeGraph();

		// a9 will be named "Nick" since it's scanned in before a10
		final Node actor1 = graph.getActor("Nick");

		// "Nick" should be an actor in the graph
		assertNotNull(actor1);

		// a10 will be named "Nick 2" since a9 was scanned in first and "Nick" has already been named
		final Node actor2 = graph.getActor("Nick 2");

		// "Nick 2" should be an actor in the graph
		assertNotNull(actor2);
	}
}
