import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LookUpTable {
    public static List<LookUpTableEntry> lookUpTable = new ArrayList<>(4840000);

    private LookUpTable() {
    }

    public static void createLookUpTable() {
        try (FileInputStream inputStream = new FileInputStream("C:\\Users\\Laurin\\Desktop\\lookuptable1.txt"); Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] splitLine = line.split(" ");
                lookUpTable.add(new LookUpTableEntry(splitLine));
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static float[] getProbabilities(int EloWhite, int EloBlack) {
        return lookUpTable.get((EloWhite - 800) * 2200 + (EloBlack - 800)).PROBABILITIES;
    }
}