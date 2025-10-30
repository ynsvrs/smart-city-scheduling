package edu.assignment.graph;
import java.util.ArrayList;
import java.util.List;
/**
 * Weighted directed graph for task dependencies.
 * Represents city-service tasks with dependencies.
 */
public class Graph {
    private final int n;
    private final List<List<Edge>> adj;
    /**
     * Construct graph with n vertices.
     * @param n number of vertices (tasks)
     */
    public Graph(int n) {
        this.n = n;
        adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }
    /**
     * Add directed edge from u to v with weight w.
     * @param u source vertex
     * @param v target vertex
     * @param w edge weight (task duration/cost)
     */
    public void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(v, w));
    }
    /**
     * Get number of vertices.
     * @return number of vertices
     */
    public int getN() {
        return n;
    }
    /**
     * Get adjacency list.
     * @return adjacency list representation
     */
    public List<List<Edge>> getAdj() {
        return adj;
    }
    /**
     * Represents a weighted directed edge.
     */
    public static class Edge {
        public final int to;
        public final int weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }
}