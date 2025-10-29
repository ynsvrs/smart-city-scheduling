package edu.assignment.graph;
/**
 * Operation counters and timing using System.nanoTime().
 */
public class BasicMetrics {
    private long start, end;
    private int dfsVisits, dfsEdges, kahnPushes, kahnPops, relaxations;
    /**
     * Start timing.
     */
    public void start() { start = System.nanoTime(); }
    /**
     * Stop timing.
     */
    public void stop() { end = System.nanoTime(); }
    public long elapsedNs() { return end - start; }
    public void incDfsVisits() { dfsVisits++; }
    public void incDfsEdges() { dfsEdges++; }
    public void incKahnPushes() { kahnPushes++; }
    public void incKahnPops() { kahnPops++; }
    public void incRelaxations() { relaxations++; }
    public int getDfsVisits() { return dfsVisits; }
    public int getDfsEdges() { return dfsEdges; }
    public int getKahnPushes() { return kahnPushes; }
    public int getKahnPops() { return kahnPops; }
    public int getRelaxations() { return relaxations; }
    @Override
    public String toString() {
        return String.format("DFS Visits: %d\nEdges Processed: %d\nTime (ms): %.2f", dfsVisits, dfsEdges, elapsedNs() / 1e6);
    }
}