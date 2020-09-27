import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PairingParser {
    // For testing
    public static void main(String[] args) {
        getPairings("https://chess-results.com/tnr535000.aspx?lan=0&art=2&rd=6&turdet=NO&prt=7");
    }

    public static List<int[]> getPairings(int tournamentNumber, int round) {
        return getPairings(
                "https://chess-results.com/tnr" + tournamentNumber + ".aspx" +
                        "?lan=0&art=2&rd=" + round + "&flag=NO&turdet=NO");
    }

    public static List<int[]> getPairings(String link) {
        Scanner scanner = getScanner(improveLink(link));
        List<int[]> pairings = new ArrayList<>();
        boolean paringsStarted = false;
        while (scanner.hasNextLine()) {
            // Remove HTML tags and numerical character code points.
            String line = scanner.nextLine()
                    .replaceAll("<[^>]*>", "")
                    .replaceAll("&#\\d*;", "");
            if (!paringsStarted) {
                if (line.startsWith("Br.;Nr.;Name;")) {
                    paringsStarted = true;
                }
            } else {
                if (line.length() == 0) {
                    break;
                }
                pairings.add(parseLine(line));
            }
        }
        return pairings;
    }

    private static String improveLink(String link) {
        return link
                .replaceFirst("turdet=[^&]*", "turdet=NO")
                .replaceFirst("flag=[^&]*", "flag=NO")
                .replaceFirst("lan=[^&]*", "lan=0")
                .replaceFirst("art=[^&]*", "art=2")
                .replaceFirst("&zeilen=[^&]*", "")
                .replaceFirst("&prt=[^&]*", "")
                .concat("&prt=7");
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
        String[] lineEntries = line.split("[; ]");
        int whiteStartingRank = Integer.parseInt(lineEntries[1]);
        String lastEntry = lineEntries[lineEntries.length - 1];
        int blackStartingRank;
        if (lastEntry.equals("spielfrei")) {
            blackStartingRank = 0;
        } else {
            String blackStartingRankString;
            // The input data has no separator between black elo and black starting rank,
            // so this is necessary.
            if (lastEntry.startsWith("0")) {
                blackStartingRankString = lastEntry.substring(1);
            } else if (lastEntry.matches("^[12]\\d+")) {
                blackStartingRankString = lastEntry.substring(4);
            } else {
                blackStartingRankString = lastEntry.substring(3);
            }
            blackStartingRank = Integer.parseInt(blackStartingRankString);
        }
        return new int[]{whiteStartingRank, blackStartingRank};
    }
}
