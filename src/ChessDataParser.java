import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChessDataParser {

    // For testing
    public static void main(String[] args) {
        List<int[]> test1 = getPairings("https://chess-results.com/tnr507448.aspx?lan=0&art=2&rd=9&turdet=YES&flag=30&prt=7");
        List<int[]> test2 = getPairings("https://chess-results.com/tnr507448.aspx?lan=0&art=2&rd=3&turdet=YES&flag=30&prt=7", 9);
        List<int[]> test3 = getPairings(507448, 9);
        assert test1.equals(test2);
        assert test1.equals(test3);
    }

    private enum ChessDataType { PAIRING, RANKING}

    // inputLink should contain a valid link to a tournament on chess-results.com,
    // from a page which shows the desired round.
    public static List<int[]> getPairings(String inputLink) {
        return getPairings(buildLinkFromString(inputLink, ChessDataType.PAIRING));
    }

    // This method allows using any link from the tournament, the round in the link is ignored.
    // The desired round is set via the method's argument.
    public static List<int[]> getPairings(String inputLink, int round) {
        return getPairings(buildLinkFromValues(getTournamentNumber(inputLink), round, ChessDataType.PAIRING));
    }

    public static List<int[]> getPairings(int tournamentNumber, int round) {
        return getPairings(buildLinkFromValues(tournamentNumber, round, ChessDataType.PAIRING));
    }

    public static List<int[]> getPairings(URL link) {
        Scanner scanner = getScanner(link);
        List<int[]> pairings = new ArrayList<>();
        boolean paringsStarted = false;
        while (scanner.hasNextLine()) {
            // Remove HTML tags and numerical character code points.
            String line = scanner.nextLine()
                    .replaceAll("<[^>]*>", "")
                    .replaceAll("&#\\d*;", "");
            if (!paringsStarted) {
                // Find the start of the table.
                if (line.startsWith("Br.;Nr.;Name;")) {
                    // Parse pairings starting from the next line.
                    paringsStarted = true;
                }
            } else {
                // Stop parsing pairings on the first line which does not start with a digit.
                if (!line.matches("^\\d.*")) {
                    break;
                }
                int[] pairing = parsePairingLine(line);
                // "nicht ausgelost" pairings return null and should not be added to the list.
                if (pairing != null) {
                    pairings.add(pairing);
                }
            }
        }
        return pairings;
    }

    private static URL buildLinkFromString(String inputLink, ChessDataType type) {
        int tournamentNumber = getTournamentNumber(inputLink);
        int round = getRound(inputLink);
        if (type.equals(ChessDataType.PAIRING)) {
            return buildLinkFromValues(tournamentNumber, round, type);
        } else {
            // TODO refactor ranking parsing to call it from here
            return null;
        }
    }

    private static int getTournamentNumber(String inputLink) {
        try {
            return Integer.parseInt(inputLink.replaceFirst(".*tnr(\\d+).*", "$1"));
        } catch (NumberFormatException e) {
            System.out.println("Could not get tournament number from link: " + inputLink + System.lineSeparator() +
                    "Make sure that the \"tnr\" key in the link is set to a valid integer.");
            System.exit(1);
            return -1; // Unreachable, but required for compiling.
        }
    }

    private static int getRound(String inputLink) throws NumberFormatException {
        try {
            return Integer.parseInt(inputLink.replaceFirst(".*rd=(\\d+).*", "$1"));
        } catch (NumberFormatException e) {
            System.out.println("Could not get the round from link: " + inputLink + System.lineSeparator() +
                    "Make sure that the \"rd\" key in the link is set to a valid integer.");
            System.exit(1);
            return -1; // Unreachable, but required for compiling.
        }
    }

    private static URL buildLinkFromValues(int tournamentNumber, int roundNumber, ChessDataType type) {
        int art = type.equals(ChessDataType.PAIRING) ? 2 : 1;
        try {
            return new URL("https://chess-results.com/tnr" + tournamentNumber +
                    ".aspx?lan=0&art=" + art + "&rd=" + roundNumber + "&turdet=NO&flag=NO&prt=7");
        } catch (MalformedURLException e) {
            // This block should never be reached.
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    private static Scanner getScanner(URL link) {
        try {
            return new Scanner(link.openStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not get valid data from link: " + link);
            System.exit(1);
            return null; // Unreachable but necessary for compilation
        }
    }

    private static int[] parsePairingLine(String line) {
        String[] lineEntries = line.split("[;]");
        // White's starting rank and title are not separated.
        // Only using the digits in the string hopefully solves this issue.
        int whiteStartingRank = Integer.parseInt(lineEntries[1].replaceAll("[^0-9]+", ""));
        String lastEntry = lineEntries[lineEntries.length - 1];
        int blackStartingRank;
        if (lastEntry.equals("spielfrei")) {
            blackStartingRank = 0;
        } else if (lastEntry.equals("nicht ausgelost")) {
            // Pairing will not be returned.
            return null;
        } else {
            // In some cases separators are missing.
            // Only using the digits in the string hopefully solves this issue.
            String lastEntryNumbersOnly = lastEntry.replaceAll("[^0-9]+", "");
            String blackStartingRankString;
            // The input data has no separator between black elo and black starting rank,
            // so this is necessary.
            if (lastEntryNumbersOnly.startsWith("0")) {
                // Elo is zero.
                blackStartingRankString = lastEntryNumbersOnly.substring(1);
            } else if (lastEntryNumbersOnly.matches("^[12]\\d+")) {
                // Elo has four digits.
                blackStartingRankString = lastEntryNumbersOnly.substring(4);
            } else {
                // Elo has three digits.
                blackStartingRankString = lastEntryNumbersOnly.substring(3);
            }
            blackStartingRank = Integer.parseInt(blackStartingRankString);
        }
        return new int[]{whiteStartingRank, blackStartingRank};
    }
}
