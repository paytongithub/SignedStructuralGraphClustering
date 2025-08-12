import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CountEdgeWeights {
    public static File dataset = new File(".data/otc.txt");

    public static void main(String[] args) throws FileNotFoundException {
        int positive = 0;
        int negative = 0;
        try (Scanner reader = new Scanner(dataset)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] data = line.trim().split("\\s+");
                int weight = Integer.parseInt(data[2]);
                if (weight > 0) {
                    positive++;
                } else {
                    negative++;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.print("file not found");
        }
        System.out.println("Total edges: " + (positive + negative));
        System.out.println("Total positive edges: " + positive);
        System.out.println("Total negative edges: " + negative);
    }
}
