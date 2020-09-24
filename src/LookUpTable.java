import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LookUpTable {
    private static boolean ready = false;
    private static final ArrayList<LookUpTableEntry> lookUpTable = Main.lookUpTable;

    private LookUpTable() {
    }

    public static ArrayList<LookUpTableEntry> createLookUpTable() {
        ArrayList<LookUpTableEntry> lookUpTableEntryArrayList = new ArrayList<>(4840000);
        try (FileInputStream inputStream = new FileInputStream("C:\\Users\\Laurin\\Desktop\\lookuptable1.txt"); Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] splitLine = line.split(" ");
                lookUpTableEntryArrayList.add(new LookUpTableEntry(splitLine));
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ready = true;
        return lookUpTableEntryArrayList;
    }

    public static List<Float> getProbabilities(int EloWhite, int EloBlack) {
        while (!ready) {
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return lookUpTable.get((EloWhite - 800) * 2000 + (EloBlack - 800)).getProbabilities();
    }
}