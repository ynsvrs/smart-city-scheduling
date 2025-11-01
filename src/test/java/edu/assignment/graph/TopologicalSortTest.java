package edu.assignment.graph;
import edu.assignment.graph.topo.TopologicalSort;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Unit tests for TopologicalSort.
 */
public class TopologicalSortTest {

    @Test
    public void testEmptyGraph() {
        Graph g = new Graph(0);
        BasicMetrics m = new BasicMetrics();
        assertEquals(0, TopologicalSort.getTopoOrder(g, m).size());
    }

    @Test
    public void testSingleVertex() {
        Graph g = new Graph(1);
        BasicMetrics m = new BasicMetrics();
        assertEquals(1, TopologicalSort.getTopoOrder(g, m).size());
    }

    @Test
    public void testSimpleDAG() {
        Graph g = new Graph(3);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        BasicMetrics m = new BasicMetrics();
        assertEquals(3, TopologicalSort.getTopoOrder(g, m).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCycleDetection() {
        Graph g = new Graph(2);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);
        BasicMetrics m = new BasicMetrics();
        TopologicalSort.getTopoOrder(g, m);
    }
}
