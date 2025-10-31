package edu.assignment.graph.dagsp;
import edu.assignment.graph.*;
import java.util.*;
/**
 * Longest path (critical path) in DAG using dynamic programming.
 * Used to identify bottleneck tasks in project scheduling.
 */
public class DAGLongestPath {
    private final long[] dist;
    private final int[] pred;
    private final BasicMetrics m;
    /**
     * Compute the longest paths in DAG.
     * @param dag input DAG
     * @param topo topological order
     * @param m metrics tracker
     */
    public DAGLongestPath(Graph dag, List<Integer> topo, BasicMetrics m) {
        this.m = m;
        int n = dag.getN();
        dist = new long[n];
        pred = new int[n];
        Arrays.fill(dist, Long.MIN_VALUE / 2);
        Arrays.fill(pred, -1);

        // Initialize source vertices (no incoming edges)
        setSources(dag);

        m.start();
        // Relax edges for maximum distance
        for (int u : topo) {
            if (dist[u] == Long.MIN_VALUE / 2) continue;
            for (Graph.Edge e : dag.getAdj().get(u)) {
                m.incRelaxations();
                long cand = dist[u] + e.weight;
                if (cand > dist[e.to]) {
                    dist[e.to] = cand;
                    pred[e.to] = u;
                }
            }
        }
        m.stop();
    }
    /**
     * Set distance 0 for all source vertices.
     */
    private void setSources(Graph dag) {
        boolean[] hasIn = new boolean[dag.getN()];
        for (int u = 0; u < dag.getN(); u++) {
            for (Graph.Edge e : dag.getAdj().get(u)) {
                hasIn[e.to] = true;
            }
        }
        for (int i = 0; i < dag.getN(); i++) {
            if (!hasIn[i]) dist[i] = 0;
        }
    }
    /**
     * Get critical path length (longest path).
     * @return length of critical path
     */
    public long getCriticalLength() {
        long max = Long.MIN_VALUE;
        for (long d : dist) {
            if (d > max) max = d;
        }
        return max == Long.MIN_VALUE / 2 ? 0 : max;
    }
    /**
     * Reconstruct critical path.
     * @return critical path (longest path in DAG)
     */
    public List<Integer> getCriticalPath() {
        int end = -1;
        long max = Long.MIN_VALUE;
        for (int i = 0; i < dist.length; i++) {
            if (dist[i] > max) {
                max = dist[i];
                end = i;
            }
        }
        if (end == -1) return null;

        List<Integer> path = new ArrayList<>();
        for (int at = end; at != -1; at = pred[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}

