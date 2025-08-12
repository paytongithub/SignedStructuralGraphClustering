import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/*
    This code is just to extract the oddly stored data from the bitcoin OTC dataset
    When downloading it, it came as a series of files, empty and named as the datapoint
    This code turns it into one dataset.
    This file is complete, likely doesn't need to be run again
*/
public class ExtractBitcoinSet {

    public static File directory = new File("soc-sign-bitcoinotc.csv");

    public static void main(String[] args) {
        String newfile = ".data/otc.txt";
        File[] files = directory.listFiles();
        try (FileWriter writer = new FileWriter(newfile)) {
            if (files != null) {
                System.out.println("passed file check");
                for (File file : files) {
                    if (file.isFile()) {
                        String filename = file.getName();
                        String editedFilename = separateAtCommas(filename);
                        writer.write(editedFilename);
                    }
                }
            }
        } catch (IOException e){
            System.err.print("error");
        }

    }

    public static String separateAtCommas(String filename) {
        String[] parts = filename.split(",");
        return parts[0] + "\t" + parts[1] + "\t" + parts[2] + "\n";
    }
}
