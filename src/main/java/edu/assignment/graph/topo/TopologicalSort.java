package edu.assignment.graph.topo;
import edu.assignment.graph.*;
import java.util.*;
/**
 * Kahn's algorithm for topological sorting of DAGs.
 * Computes valid execution order for tasks.
 */
public class TopologicalSort {

    /**
     * Compute topological order using Kahn's algorithm.
     * @param dag input DAG
     * @param m metrics tracker
     * @return topological order (list of vertices)
     * @throws IllegalArgumentException if graph contains a cycle
     */
    public static List<Integer> getTopoOrder(Graph dag, BasicMetrics m) {
        m.start();
        int n = dag.getN();

        // Calculate in-degrees
        int[] in = new int[n];
        for (int u = 0; u < n; u++) {
            for (Graph.Edge e : dag.getAdj().get(u)) {
                in[e.to]++;
            }
        }
        // Initialize queue with zero in-degree vertices
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (in[i] == 0) {
                q.add(i);
                m.incKahnPushes();
            }
        }
        // Process vertices in topological order
        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            m.incKahnPops();
            order.add(u);
            // Reduce in-degree of neighbors
            for (Graph.Edge e : dag.getAdj().get(u)) {
                if (--in[e.to] == 0) {
                    q.add(e.to);
                    m.incKahnPushes();
                }
            }
        }

        m.stop();

        // Check for cycles
        if (order.size() != n) {
            throw new IllegalArgumentException("Cycle detected in DAG");
        }

        return order;
    }
}