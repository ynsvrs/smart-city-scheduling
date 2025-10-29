package edu.assignment.graph;
import java.util.ArrayList;
import java.util.List;
/**
 * Weighted directed graph for task dependencies.
 */
public class Graph {
    private final int n;
    private final List<List<Edge>> adj;
    /**
     * Construct graph with n vertices.
     * @param n number of vertices
     */
    public Graph(int n) {
        this.n = n;
        adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }
    /**
     * Add directed edge from u to v with weight w.
     * @param u source
     * @param v target
     * @param w weight
     */
    public void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(v, w));
    }
    public int getN() { return n; }
    public List<List<Edge>> getAdj() { return adj; }
    public static class Edge {
        public final int to;
        public final int weight;
        public Edge(int to, int weight) { this.to = to; this.weight = weight; }
    }
}
