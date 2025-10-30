package edu.assignment.graph;
/**
 * Operation counters and timing using System.nanoTime().
 * Tracks performance metrics for graph algorithms.
 */
public class BasicMetrics {
    private long start, end;
    private int dfsVisits, dfsEdges, kahnPushes, kahnPops, relaxations;
    /**
     * Start timing measurement.
     */
    public void start() {
        start = System.nanoTime();
    }
    /**
     * Stop timing measurement.
     */
    public void stop() {
        end = System.nanoTime();
    }
    /**
     * Get elapsed time in nanoseconds.
     * @return elapsed time in nanoseconds
     */
    public long elapsedNs() {
        return end - start;
    }
    /** Increment DFS visit counter. */
    public void incDfsVisits() { dfsVisits++; }

    /** Increment DFS edge counter. */
    public void incDfsEdges() { dfsEdges++; }

    /** Increment Kahn push counter. */
    public void incKahnPushes() { kahnPushes++; }

    /** Increment Kahn pop counter. */
    public void incKahnPops() { kahnPops++; }

    /** Increment relaxation counter. */
    public void incRelaxations() {
        relaxations++;
    }
    public int getDfsVisits() {
        return dfsVisits;
    }
    public int getDfsEdges() {
        return dfsEdges;
    }
    public int getKahnPushes() {
        return kahnPushes;
    }
    public int getKahnPops() {
        return kahnPops;
    }
    public int getRelaxations() {
        return relaxations;
    }
    @Override
    public String toString() {
        return String.format("DFS Visits: %d | Edges: %d | Time: %.3f ms",
                dfsVisits, dfsEdges, elapsedNs() / 1e6);
    }
}