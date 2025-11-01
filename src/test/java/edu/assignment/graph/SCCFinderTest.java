package edu.assignment.graph;
import edu.assignment.graph.scc.SCCFinder;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Unit tests for SCCFinder.
 */
public class SCCFinderTest {

    @Test
    public void testEmptyGraph() {
        Graph g = new Graph(0);
        BasicMetrics m = new BasicMetrics();
        SCCFinder s = new SCCFinder(g, m);
        assertEquals(0, s.getSCCs().size());
    }

    @Test
    public void testSingleVertex() {
        Graph g = new Graph(1);
        BasicMetrics m = new BasicMetrics();
        SCCFinder s = new SCCFinder(g, m);
        assertEquals(1, s.getSCCs().size());
        assertEquals(1, s.getSCCs().get(0).size());
    }

    @Test
    public void testSimpleCycle() {
        Graph g = new Graph(3);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);
        BasicMetrics m = new BasicMetrics();
        SCCFinder s = new SCCFinder(g, m);
        assertEquals(1, s.getSCCs().size());
        assertEquals(3, s.getSCCs().get(0).size());
    }

    @Test
    public void testDAG() {
        Graph g = new Graph(3);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        BasicMetrics m = new BasicMetrics();
        SCCFinder s = new SCCFinder(g, m);
        assertEquals(3, s.getSCCs().size());
    }
}
