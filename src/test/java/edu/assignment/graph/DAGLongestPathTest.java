package edu.assignment.graph;
import edu.assignment.graph.dagsp.DAGLongestPath;
import edu.assignment.graph.topo.TopologicalSort;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
/**
 * Unit tests for DAGLongestPath.
 */
public class DAGLongestPathTest {

    @Test
    public void testSingleVertex() {
        Graph g = new Graph(1);
        List<Integer> topo = TopologicalSort.getTopoOrder(g, new BasicMetrics());
        DAGLongestPath lp = new DAGLongestPath(g, topo, new BasicMetrics());
        assertEquals(0, lp.getCriticalLength());
    }

    @Test
    public void testSimpleChain() {
        Graph g = new Graph(4);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 2);
        g.addEdge(2, 3, 3);
        List<Integer> topo = TopologicalSort.getTopoOrder(g, new BasicMetrics());
        DAGLongestPath lp = new DAGLongestPath(g, topo, new BasicMetrics());
        assertEquals(6, lp.getCriticalLength());
        assertEquals("[0, 1, 2, 3]", lp.getCriticalPath().toString());
    }

    @Test
    public void testNoEdges() {
        Graph g = new Graph(2);
        List<Integer> topo = TopologicalSort.getTopoOrder(g, new BasicMetrics());
        DAGLongestPath lp = new DAGLongestPath(g, topo, new BasicMetrics());
        assertEquals(0, lp.getCriticalLength());
    }
}