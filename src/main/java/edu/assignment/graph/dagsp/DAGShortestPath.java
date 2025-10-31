package edu.assignment.graph.dagsp;
import edu.assignment.graph.*;
import java.util.*;
/**
 * Single-source the shortest paths in DAG using dynamic programming
 * Processes vertices in topological order for optimal efficiency
 */
public class DAGShortestPath {
    private final long[] dist;
    private final int[] pred;
    private final BasicMetrics m;
    /**
     * Compute the shortest paths from source in DAG
     * @param dag input DAG
     * @param source source vertex
     * @param topo topological order
     * @param m metrics tracker
     */
    public DAGShortestPath(Graph dag, int source, List<Integer> topo, BasicMetrics m) {
        this.m = m;
        int n = dag.getN();
        dist = new long[n];
        pred = new int[n];
        Arrays.fill(dist, Long.MAX_VALUE / 2);
        Arrays.fill(pred, -1);
        dist[source] = 0;

        m.start();
        // Relax edges in topological order
        for (int u : topo) {
            if (dist[u] == Long.MAX_VALUE / 2) continue;
            for (Graph.Edge e : dag.getAdj().get(u)) {
                m.incRelaxations();
                long cand = dist[u] + e.weight;
                if (cand < dist[e.to]) {
                    dist[e.to] = cand;
                    pred[e.to] = u;
                }
            }
        }
        m.stop();
    }
    /**
     * Get the shortest distance to vertex v
     * @param v target vertex
     * @return shortest distance (or infinity if unreachable)
     */
    public long getDist(int v) {
        return dist[v];
    }
    /**
     * Reconstruct the shortest path to vertex v
     * @param v target vertex
     * @return path from source to v
     */
    public List<Integer> reconstructPath(int v) {
        List<Integer> path = new ArrayList<>();
        for (int at = v; at != -1; at = pred[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}