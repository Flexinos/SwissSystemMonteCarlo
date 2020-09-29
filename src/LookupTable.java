import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class LookupTable {
    private static final int LOWEST_ELO = 800;
    private static final int HIGHEST_ELO = 2999;
    private static final int ELO_RANGE = (HIGHEST_ELO - LOWEST_ELO) + 1;

    private static final float[][][] lookupTable = new float[ELO_RANGE][ELO_RANGE][3];

    private LookupTable() {
    }

    public static void createLookupTable(final String lookupTableFile) {
        try (final FileInputStream inputStream = new FileInputStream(lookupTableFile); final Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            for (int row = 0; row < ELO_RANGE; ++row) {
                for (int column = 0; column < ELO_RANGE; ++column) {
                    final String[] splitLine = sc.nextLine().split(" ");
                    lookupTable[row][column] = new float[]{Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]), Float.parseFloat(splitLine[4])};
                }
            }
        } catch (final FileNotFoundException | NoSuchElementException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (final IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static float[] getProbabilities(final int EloWhite, final int EloBlack) {
        return lookupTable[EloWhite - LOWEST_ELO][EloBlack - LOWEST_ELO];
    }
}