import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PairingParser {
    // For testing
    public static void main(String[] args) {
        //getPairings("https://chess-results.com/tnr535000.aspx?lan=0&art=2&rd=6&turdet=NO&prt=7");
        getPairings("https://chess-results.com/tnr507448.aspx?lan=0&art=2&rd=9&turdet=YES&flag=30&prt=7");
    }

    public static List<int[]> getPairings(int tournamentNumber, int round) {
        return getPairings(
                "https://chess-results.com/tnr" + tournamentNumber + ".aspx" +
                        "?lan=0&art=2&rd=" + round + "&flag=NO&turdet=NO");
    }

    public static List<int[]> getPairings(String link) {
        Scanner scanner = getScanner(buildValidLink(link));
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
                int[] pairing = parseLine(line);
                // "nicht ausgelost" pairings return null and should not be added to the list.
                if (pairing != null) {
                    pairings.add(pairing);
                }
            }
        }
        return pairings;
    }

    private static String buildValidLink(String inputLink) {
        int tournamentNumber = getTournamentNumber(inputLink);
        int round = getRound(inputLink);
        return getPairingDataLink(tournamentNumber, round);
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

    private static String getPairingDataLink(int tournamentNumber, int roundNumber) {
        return "https://chess-results.com/tnr" + tournamentNumber +
                ".aspx?lan=0&art=2&rd=" + roundNumber + "&turdet=NO&flag=NO&prt=7";
    }

    private static Scanner getScanner(String link) {
        try {
            return new Scanner(new URL(link).openStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not get valid data from link: " + link);
            System.exit(1);
            return null; // Unreachable but necessary for compilation
        }
    }

    private static int[] parseLine(String line) {
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
