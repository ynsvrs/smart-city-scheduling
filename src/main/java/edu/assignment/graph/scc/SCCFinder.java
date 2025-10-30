package edu.assignment.graph.scc;
import edu.assignment.graph.*;
import java.util.*;
/**
 * Kosaraju's algorithm for finding Strongly Connected Components
 * Detects cyclic dependencies in task graphs
 */
public class SCCFinder {
    private final Graph g;
    private final BasicMetrics m;
    private final List<List<Integer>> sccs = new ArrayList<>();
    private final int[] component;
    private final boolean[] visited;
    /**
     * Find all SCCs in the graph.
     * @param g input graph
     * @param m metrics tracker
     */
    public SCCFinder(Graph g, BasicMetrics m) {
        this.g = g;
        this.m = m;
        component = new int[g.getN()];
        Arrays.fill(component, -1);
        visited = new boolean[g.getN()];
        m.start();
        kosaraju();
        m.stop();
    }
    /**
     * Kosaraju's two-pass algorithm. (My choice)
     */
    private void kosaraju() {
        // First pass: compute finish times
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < g.getN(); i++) {
            if (!visited[i]) dfs1(i, stack);
        }
        // Second pass: find SCCs in transpose graph
        Graph t = transpose();
        Arrays.fill(visited, false);
        int id = 0;
        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (!visited[u]) {
                List<Integer> comp = new ArrayList<>();
                dfs2(t, u, comp);
                sccs.add(comp);
                for (int v : comp) component[v] = id;
                id++;
            }
        }
    }
    /**
     * First DFS: compute finish order
     */
    private void dfs1(int u, Stack<Integer> stack) {
        visited[u] = true;
        m.incDfsVisits();
        for (Graph.Edge e : g.getAdj().get(u)) {
            m.incDfsEdges();
            if (!visited[e.to]) dfs1(e.to, stack);
        }
        stack.push(u);
    }
    /**
     * Transpose the graph (reverse all edges)
     * @return transposed graph
     */
    private Graph transpose() {
        Graph t = new Graph(g.getN());
        for (int u = 0; u < g.getN(); u++) {
            for (Graph.Edge e : g.getAdj().get(u)) {
                t.addEdge(e.to, u, e.weight);
            }
        }
        return t;
    }
    /**
     * Second DFS: find components in transpose
     */
    private void dfs2(Graph t, int u, List<Integer> comp) {
        visited[u] = true;
        m.incDfsVisits();
        comp.add(u);
        for (Graph.Edge e : t.getAdj().get(u)) {
            m.incDfsEdges();
            if (!visited[e.to]) dfs2(t, e.to, comp);
        }
    }
    /**
     * Get list of all SCCs
     * @return list of SCCs (each SCC is a list of vertices)
     */
    public List<List<Integer>> getSCCs() {
        return sccs;
    }
    /**
     * Get component ID of a vertex
     * @param v vertex
     * @return component ID
     */
    public int getComponent(int v) {
        return component[v];
    }
    /**
     * Build condensation graph (DAG of SCCs)
     * @return condensation DAG
     */
    public Graph getCondensation() {
        int num = sccs.size();
        Graph dag = new Graph(num);
        Set<String> added = new HashSet<>();

        for (int u = 0; u < g.getN(); u++) {
            int cu = component[u];
            for (Graph.Edge e : g.getAdj().get(u)) {
                int cv = component[e.to];
                // Add edge between different components (avoid duplicates)
                if (cu != cv) {
                    String key = cu + "-" + cv;
                    if (!added.contains(key)) {
                        dag.addEdge(cu, cv, e.weight);
                        added.add(key);
                    }
                }
            }
        }
        return dag;
    }
}