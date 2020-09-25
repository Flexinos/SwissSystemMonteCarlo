import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LookupTable {

    private static final int LOWEST_ELO = 800;
    private static final int HIGHEST_ELO = 2999;
    private static final int ELO_RANGE = HIGHEST_ELO - LOWEST_ELO + 1;

    private static final float[][][] lookupTable = new float[ELO_RANGE][ELO_RANGE][3];

    private LookupTable() {
    }

    public static void createLookupTable() {
        assert HIGHEST_ELO >= LOWEST_ELO;
        try (FileInputStream inputStream = new FileInputStream("C:\\Users\\Laurin\\Desktop\\lookuptable1.txt"); Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            for (int row = 0; row < ELO_RANGE; ++row) {
                for (int column = 0; column < ELO_RANGE; ++column) {
                    lookupTable[row][column] = new LookupTableEntry(sc.nextLine().split(" ")).PROBABILITIES;
                }
            }
        } catch (IOException | NoSuchElementException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static float[] getProbabilities(int EloWhite, int EloBlack) {
        return lookupTable[EloBlack - LOWEST_ELO][EloWhite - LOWEST_ELO];
    }
}