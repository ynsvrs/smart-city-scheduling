package edu.assignment.graph;
import edu.assignment.graph.dagsp.DAGShortestPath;
import edu.assignment.graph.topo.TopologicalSort;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
/**
 * Unit tests for DAGShortestPath.
 */
public class DAGShortestPathTest {

    @Test
    public void testSingleVertex() {
        Graph g = new Graph(1);
        List<Integer> topo = TopologicalSort.getTopoOrder(g, new BasicMetrics());
        DAGShortestPath sp = new DAGShortestPath(g, 0, topo, new BasicMetrics());
        assertEquals(0, sp.getDist(0));
    }

    @Test
    public void testSimpleChain() {
        Graph g = new Graph(4);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 2);
        g.addEdge(2, 3, 3);
        List<Integer> topo = TopologicalSort.getTopoOrder(g, new BasicMetrics());
        DAGShortestPath sp = new DAGShortestPath(g, 0, topo, new BasicMetrics());
        assertEquals(6, sp.getDist(3));
        assertEquals("[0, 1, 2, 3]", sp.reconstructPath(3).toString());
    }

    @Test
    public void testUnreachable() {
        Graph g = new Graph(2);
        List<Integer> topo = TopologicalSort.getTopoOrder(g, new BasicMetrics());
        DAGShortestPath sp = new DAGShortestPath(g, 0, topo, new BasicMetrics());
        assertEquals(Long.MAX_VALUE / 2, sp.getDist(1));
    }
}