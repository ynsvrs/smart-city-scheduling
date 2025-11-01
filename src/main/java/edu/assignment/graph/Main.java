package edu.assignment.graph;
import com.google.gson.*;
import edu.assignment.graph.scc.*;
import edu.assignment.graph.topo.*;
import edu.assignment.graph.dagsp.*;
import java.io.*;
import java.util.*;
/**
 * Main entry point for Smart City Scheduling system
 * Processes task dependency graphs and computes optimal schedules
 */
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: mvn exec:java -Dexec.args=\"data/file.json\"");
            return;
        }

        String path = args[0];
        String csvPath = "results.csv";
        boolean csvExists = new File(csvPath).exists();

        System.out.println("==============================");
        System.out.println("Processing dataset: " + path);
        System.out.println("==============================");
        // Load and build graph
        JsonGraph jg = loadJson(path);
        Graph g = new Graph(jg.n);
        for (JsonEdge e : jg.edges) {
            g.addEdge(e.u, e.v, e.w);
        }
        System.out.println(" Dataset Information");
        System.out.println("File: " + path);
        System.out.println("Vertices: " + jg.n);
        System.out.println("Edges: " + jg.edges.length);
        System.out.println("Directed: " + jg.directed);
        System.out.println("Weight model: " + jg.weight_model);
        System.out.println("Source vertex: " + jg.source);
        System.out.println("--------------------------------");
        // Find SCCs
        BasicMetrics sccM = new BasicMetrics();
        SCCFinder scc = new SCCFinder(g, sccM);

        System.out.println(" Strongly Connected Components (SCC)");
        System.out.println("--------------------------------");
        System.out.println("Total SCCs found: " + scc.getSCCs().size());
        for (int i = 0; i < scc.getSCCs().size(); i++) {
            List<Integer> component = scc.getSCCs().get(i);
            System.out.println("SCC #" + (i+1) + " (size " + component.size() + "): " + component);
        }
        System.out.println("\nSCC Metrics:");
        System.out.println(" DFS Visits: " + sccM.getDfsVisits());
        System.out.println(" Edges Processed: " + sccM.getDfsEdges());
        System.out.println(" Time: " + String.format("%.3f ms", sccM.elapsedNs() / 1e6));
        System.out.println("--------------------------------");
        // Build Condensation graph
        Graph dag = scc.getCondensation();
        System.out.println(" Condensation Graph (DAG)");
        System.out.println("--------------------------------");
        System.out.println("DAG Vertices (SCCs): " + dag.getN());
        int dagEdgeCount = 0;
        for (int u = 0; u < dag.getN(); u++) {
            dagEdgeCount += dag.getAdj().get(u).size();
        }
        System.out.println("DAG Edges: " + dagEdgeCount);
        System.out.println("Condensation edges:");
        for (int u = 0; u < dag.getN(); u++) {
            for (Graph.Edge e : dag.getAdj().get(u)) {
                System.out.println(" SCC" + (u+1) + " → SCC" + (e.to+1) + " (weight: " + e.weight + ")");
            }
        }
        System.out.println("--------------------------------");
        // Topological Sort
        BasicMetrics topoM = new BasicMetrics();
        List<Integer> topo = TopologicalSort.getTopoOrder(dag, topoM);

        System.out.println(" Topological Order");
        System.out.println("--------------------------------");
        System.out.println("Component order: " + topo);

        // Derive original task order
        List<Integer> derivedOrder = new ArrayList<>();
        for (int comp : topo) {
            List<Integer> nodes = new ArrayList<>(scc.getSCCs().get(comp));
            Collections.sort(nodes);
            derivedOrder.addAll(nodes);
        }
        System.out.println("Derived task execution order: " + derivedOrder);
        System.out.println("\nTopological Sort Metrics:");
        System.out.println(" Queue Pushes: " + topoM.getKahnPushes());
        System.out.println(" Queue Pops: " + topoM.getKahnPops());
        System.out.println(" Time: " + String.format("%.3f ms", topoM.elapsedNs() / 1e6));
        System.out.println("--------------------------------");
        // Shortest Paths
        int srcSCC = scc.getComponent(jg.source);
        BasicMetrics spM = new BasicMetrics();
        DAGShortestPath sp = new DAGShortestPath(dag, srcSCC, topo, spM);

        System.out.println(" Shortest Paths from Source");
        System.out.println("--------------------------------");
        System.out.println("Source: vertex " + jg.source + " (in SCC #" + (srcSCC+1) + ")");
        System.out.println("Distances to all SCCs:");
        for (int i = 0; i < dag.getN(); i++) {
            long d = sp.getDist(i);
            if (d != Long.MAX_VALUE / 2) {
                System.out.println(" To SCC #" + (i+1) + ": " + d);
            } else {
                System.out.println(" To SCC #" + (i+1) + ": UNREACHABLE");
            }
        }

        int target = dag.getN() - 1;
        List<Integer> shortestPath = sp.reconstructPath(target);
        System.out.println("\nShortest path to SCC #" + (target+1) + ": " + shortestPath);
        System.out.println("Shortest distance: " + sp.getDist(target));
        System.out.println("\nShortest Path Metrics:");
        System.out.println(" Relaxations: " + spM.getRelaxations());
        System.out.println(" Time: " + String.format("%.3f ms", spM.elapsedNs() / 1e6));
        System.out.println("--------------------------------");
        // Longest Path (Critical Path)
        BasicMetrics lpM = new BasicMetrics();
        DAGLongestPath lp = new DAGLongestPath(dag, topo, lpM);

        System.out.println(" Critical Path (Longest Path)");
        System.out.println("--------------------------------");
        List<Integer> criticalPath = lp.getCriticalPath();
        long criticalLength = lp.getCriticalLength();
        System.out.println("Critical path: " + criticalPath);
        System.out.println("Critical path length: " + criticalLength);
        System.out.println("\nLongest Path Metrics:");
        System.out.println(" Relaxations: " + lpM.getRelaxations());
        System.out.println(" Time: " + String.format("%.3f ms", lpM.elapsedNs() / 1e6));
        System.out.println("--------------------------------");
        // Summary
        System.out.println(" Analysis Summary");
        System.out.println("--------------------------------");
        System.out.println("Original graph: " + jg.n + " vertices, " + jg.edges.length + " edges");
        System.out.println("SCCs detected: " + scc.getSCCs().size());
        System.out.println("Condensation DAG: " + dag.getN() + " vertices, " + dagEdgeCount + " edges");
        System.out.println("Topological sort: SUCCESS");
        System.out.println("Shortest path computed: YES");
        System.out.println("Critical path length: " + criticalLength);
        System.out.println("--------------------------------");

        // Write to CSV
        writeCSV(csvPath, csvExists, path, jg, scc, dag, sccM, topoM, spM, lpM, sp, lp, target);
        System.out.println(" Results appended to: " + csvPath);
        System.out.println("==============================\n");
    }
    /**
     * Writes metrics to CSV file.
     */
    private static void writeCSV(String csvPath, boolean exists, String dataset,
                                 JsonGraph jg, SCCFinder scc, Graph dag,
                                 BasicMetrics sccM, BasicMetrics topoM,
                                 BasicMetrics spM, BasicMetrics lpM,
                                 DAGShortestPath sp, DAGLongestPath lp, int target)
            throws IOException {
        try (FileWriter fw = new FileWriter(csvPath, true);
             PrintWriter pw = new PrintWriter(fw)) {

            // Writes header if file doesn't exist
            if (!exists) {
                pw.println("Dataset,Vertices,Edges,SCCs,DAG_Vertices,DAG_Edges," +
                        "SCC_DFS_Visits,SCC_DFS_Edges,SCC_Time_ms," +
                        "Topo_Pushes,Topo_Pops,Topo_Time_ms," +
                        "SP_Relaxations,SP_Time_ms,SP_Distance," +
                        "LP_Relaxations,LP_Time_ms,Critical_Path_Length");
            }

            // Counts DAG edges
            int dagEdges = 0;
            for (int u = 0; u < dag.getN(); u++) {
                dagEdges += dag.getAdj().get(u).size();
            }

            // Writes data row
            String datasetName = new File(dataset).getName();
            long spDist = sp.getDist(target);
            String spDistStr = (spDist == Long.MAX_VALUE / 2) ? "INF" : String.valueOf(spDist);

            pw.printf("%s,%d,%d,%d,%d,%d,%d,%d,%.3f,%d,%d,%.3f,%d,%.3f,%s,%d,%.3f,%d%n",
                    datasetName,
                    jg.n,
                    jg.edges.length,
                    scc.getSCCs().size(),
                    dag.getN(),
                    dagEdges,
                    sccM.getDfsVisits(),
                    sccM.getDfsEdges(),
                    sccM.elapsedNs() / 1e6,
                    topoM.getKahnPushes(),
                    topoM.getKahnPops(),
                    topoM.elapsedNs() / 1e6,
                    spM.getRelaxations(),
                    spM.elapsedNs() / 1e6,
                    spDistStr,
                    lpM.getRelaxations(),
                    lpM.elapsedNs() / 1e6,
                    lp.getCriticalLength()
            );
        }
    }
    /**
     * Load graph from JSON file
     */
    private static JsonGraph loadJson(String path) throws IOException {
        Gson gson = new Gson();
        try (FileReader r = new FileReader(path)) {
            return gson.fromJson(r, JsonGraph.class);
        }
    }
    /** JSON graph structure */
    static class JsonGraph {
        boolean directed;
        int n;
        JsonEdge[] edges;
        int source;
        String weight_model;
    }
    /** JSON edge structure */
    static class JsonEdge {
        int u, v, w;
    }
}