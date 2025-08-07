import java.io.*;
import java.util.*;

public class Main {
    public static File dataset = new File(".data/slashdot.txt");
    public static ArrayList<int[]> fullFile = new ArrayList<>(); // computes whole file's data to remove rescanning
    public static ArrayList<Integer> edgeWeights = new ArrayList<>(); // map of all edge weights, initialized to 2 each (lines 1-2)
    public static Set<Integer> uniqueVertices = new TreeSet<>(); // stores all unique vertices, removes redundant computations

    public static class Neighborhood { // neighbors
        public ArrayList<Integer> neighbors = new ArrayList<>(); // indexes of vertex u's neighbors
        public ArrayList<Integer> neighborWeights = new ArrayList<>(); // edge weights between u and neighboring v's
        public HashSet<Integer> neighborSet = new HashSet<>(); // this instead of repeated arraylist.contains calls
    }

    public static HashMap<Integer, Neighborhood> neighborhoodCache = new HashMap<>(); // used in neighborhood precomputation
    public static HashMap<String, Integer> edgeToIndex = new HashMap<>(); // edge index map

    public static void main(String[] args) {
        processFullFile(); // precompute file, maps vertices so we can retrieve unique u's sequentially
        buildEdgeIndexMap(); // maps edges to line number in fullFile list, originally needed full recomputation
        // ^ also does lines 1-2 in paper pseudocode
        countBalancedTriangles(); // the rest of Algorithm 1 pseudocode

        // below: count of total triangles and print
        int counter = 0;
        for (int i = 0; i < edgeWeights.size(); i++) {
            counter += edgeWeights.get(i);
        }

        System.out.println("balanced triangle count: " + counter);
    }

    public static void processFullFile() {
        try (Scanner reader = new Scanner(dataset)) { // reads at the beginning to remove recomputing
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] data = line.trim().split("\\s+");
                if (data.length >= 3)  { // filters out invalid arguments

                    int u = Integer.parseInt(data[0]);
                    int v = Integer.parseInt(data[1]);
                    int sign = Integer.parseInt(data[2]);

                    fullFile.add(new int[]{u, v, sign}); // add to fullFile
                    uniqueVertices.add(u); // adds to unique map, will be skipped if not unique
                    uniqueVertices.add(v);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found error");
            e.printStackTrace();
        }
    }

    /*
        Pre-maps edge weights, otherwise computes per triangle computation
     */
    public static void buildEdgeIndexMap() {
        for (int i = 0; i < fullFile.size(); i++) { // for every edge in the dataset
            int u = fullFile.get(i)[0];
            int v = fullFile.get(i)[1];
            String key1 = u + "," + v;
            String key2 = v + "," + u; // inverses counted as potential keys
            edgeToIndex.put(key1, i); // edge indexing stored as u, v
            edgeToIndex.put(key2, i); // edge reverse indexing v, u
            edgeWeights.add(2); // adds 2 to the edge weight (lines 1-2)
        }
    }

    public static void countBalancedTriangles() {
        for (int i : uniqueVertices) { // line 3 of pseudocode
            Neighborhood u = findNeighborhood(i); // retrieve neighborhood from precomputation

            for (int j = 0; j < u.neighbors.size(); j++) { // line 4
                int vNode = u.neighbors.get(j); // v index
                int uvSign = u.neighborWeights.get(j); // u v edge
                if (i < vNode && uvSign == 1) { // line 5
                    Neighborhood v = findNeighborhood(vNode); // find neighborhood of node in u's neighborhood

                    if (u.neighbors.size() < v.neighbors.size()) { // lines 6-7
                        Neighborhood temp = u;
                        u = v;
                        v = temp;
                    }

                    for (int k = 0; k < v.neighbors.size(); k++) { // line 8
                        int wNode = v.neighbors.get(k); // w index
                        int vwSign = v.neighborWeights.get(k); // v w edge
                        if (wNode == i || !u.neighborSet.contains(wNode)) continue;

                        int uwSign = u.neighborWeights.get(u.neighbors.indexOf(wNode)); // u w edge

                        if (uwSign == 1 && vwSign == 1) { // line 10
                            if (wNode > vNode) { // line 11
                                addToEdgeWeights(i, vNode, wNode); // lines 12-14
                            }
                        } else if (uwSign == -1 && vwSign == -1) { // line 15
                            addToEdgeWeights(i, vNode, wNode); // lines 16-18
                        }
                    }
                }
            }
        }
    }

    public static Neighborhood findNeighborhood(int index) {
        if (neighborhoodCache.containsKey(index)) { // return if node index has already been computed
            return neighborhoodCache.get(index);
        }

        Neighborhood u = new Neighborhood();

        for (int i = 0; i < fullFile.size(); i++) { // across whole graph
            int[] edge = fullFile.get(i);
            if (edge[0] == index) { // if first node in dataset is the desired one,
                u.neighbors.add(edge[1]);
                u.neighborWeights.add(edge[2]);
                u.neighborSet.add(edge[1]);
            } else if (edge[1] == index) { // unsure if I need to do the inverse
                u.neighbors.add(edge[0]);
                u.neighborWeights.add(edge[2]);
                u.neighborSet.add(edge[0]);
            }
        }

        neighborhoodCache.put(index, u); // add to cache to remove recomputations
        return u;
    }

    public static void addToEdgeWeights(int i, int j, int k) {
        incrementWeight(i, j); // increment according to lines 12-14 / 16-18
        incrementWeight(i, k);
        incrementWeight(j, k);
    }

    public static void incrementWeight(int u, int v) {
        Integer index = edgeToIndex.get(u + "," + v); // retrieve index of u, v
        if (index != null) { // if it exists, increment
            edgeWeights.set(index, edgeWeights.get(index) + 1);
        }
    }
}
