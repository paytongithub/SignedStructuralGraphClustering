import java.util.ArrayList;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class MainUnoptimized {

    public static File dataset = new File(".data/slashdotcut.txt");

    public static ArrayList<int[]> fullFile = new ArrayList<int[]>();
    public static int edges;
    public static ArrayList<Integer> edgeWeights = new ArrayList<>();
    public static class neighborhood { //Neighborhood of a node
        public ArrayList<Integer> neighbors = new ArrayList<>(); //id of each neighboring vertex
        public ArrayList<Integer> neighborWeights = new ArrayList<>(); // edge weight between this vertex and neighbor

        public ArrayList<Integer> instanceIDs = new ArrayList<>(); // line numbers of each set

    }

    public static ArrayList<Integer> uniqueVertices = new ArrayList<>(); // avoids tons of recomputation

    public static HashMap<Integer, neighborhood> neighborhoodCache = new HashMap<>(); // avoids redundant recomputation

    public static void main(String[] args) {
        processFullFile();
        extractUniqueVertices();
        edges = fullFile.size();
        countBalancedTriangles();
        for (int i = 0; i < edgeWeights.size(); i++) {
            System.out.println(fullFile.get(i)[0] + " " + fullFile.get(i)[1] + " " + fullFile.get(i)[2] + " " + edgeWeights.get(i));
        }
    }

    public static void processFullFile() {
        try {
            Scanner reader = new Scanner(dataset);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] data = line.split("\\s+");
                fullFile.add(new int[] {parseInt(data[0]), parseInt(data[1]), parseInt(data[2])});
            }
        } catch (FileNotFoundException e) {
            System.out.println("file not found error");
            e.printStackTrace();
        }
    }

    public static void extractUniqueVertices() {
        for (int[] edge : fullFile) {
            uniqueVertices.add(edge[0]);
            uniqueVertices.add(edge[1]);
        }
    }
    public static void initializeEdgeWeights() {
        for (int i = 0; i < edges; i++) {
            edgeWeights.add(2);
        }
    }

    public static void countBalancedTriangles() {
        initializeEdgeWeights(); // lines 1-2 of pseudo

        for (int i : uniqueVertices) { // line 3
            neighborhood u = findNeighborhood(i); // construct neighborhood

            for (int j = 0; j < u.neighbors.size(); j++) { // line 4
                neighborhood v = findNeighborhood(u.neighbors.get(j));

                if (i < u.neighbors.get(j) && u.neighborWeights.get(j) == 1) { // line 5
                    if (u.neighbors.size() < v.neighbors.size()) { // line 6
                        neighborhood temp = u;
                        u = v;
                        v = temp;
                    }

                    for (int k = 0; k < v.neighbors.size(); k++) { // line 8
                        if (v.neighbors.get(k) != i) { // only do this if this v neighbor is not u
                            if (u.neighbors.contains(v.neighbors.get(k))) { // line 9
                                if (u.neighborWeights.get(u.neighbors.indexOf(v.neighbors.get(k))) == 1 && v.neighborWeights.get(k) == 1) { // line 10
                                    if (v.neighbors.get(k) > u.neighbors.get(j)) { // line 11
                                        addToEdgeWeights(i, u.neighbors.get(j), v.neighbors.get(k)); // indexes for u, v, w
                                    }
                                }

                                if (u.neighborWeights.get(u.neighbors.indexOf(v.neighbors.get(k))) == -1 && v.neighborWeights.get(k) == -1) { // line 10
                                    addToEdgeWeights(i, u.neighbors.get(j), v.neighbors.get(k)); // indexes for u, v, w
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /*
        Find the neighborhood of a given vertex
        Input: index value of node
        Output: neighborhood of node

        Note: Must check both sides of the graph, as it is unipartite
     */

    public static neighborhood findNeighborhood(int index) {

        if (neighborhoodCache.containsKey(index)) {
            return neighborhoodCache.get(index);
        }

        neighborhood u = new neighborhood();

        for (int i = 0; i < fullFile.size(); i++) {
            int[] edge = fullFile.get(i);
            if (edge[0] == index) {
                u.instanceIDs.add(i);
                u.neighbors.add(edge[1]);
                u.neighborWeights.add(edge[2]);
            } else if (edge[1] == index) {
                u.instanceIDs.add(i);
                u.neighbors.add(edge[0]);
                u.neighborWeights.add(edge[2]);
            }
        }
        neighborhoodCache.put(index, u);
        return u;
    }

    public static void addToEdgeWeights(int i, int j, int k) { // handles line 12-14 / lines 16-18
        for (int index = 0; index < fullFile.size(); index++) {
            int[] edge = fullFile.get(index);
            int u = edge[0];
            int v = edge[1];

            if ((u == i && v == j) || (u == i && v == k) || (u == j && v == i) || (u == j && v == k) || (u == k && v == i) || (u == k && v == j)) {
                edgeWeights.set(index, edgeWeights.get(index) + 1);
            }

        }
    }
}