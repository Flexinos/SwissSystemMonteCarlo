import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public final class ChessDataParser {

    private static final Pattern TOURNAMENT_NUMBER_PATTERN = Pattern.compile(".*tnr(\\d+).*");
    private static final Pattern ROUND_PATTERN = Pattern.compile(".*rd=(\\d+).*");
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern CHARACTER_CODE_PATTERN = Pattern.compile("&#\\d*;");

    // Prevent instantiation
    private ChessDataParser() {
    }

    // For testing
    public static void main(final String[] args) {
        final List<int[]> test1 = getPairings("https://chess-results.com/tnr507448.aspx?lan=0&art=2&rd=9&turdet=YES&flag=30&prt=7");
        final List<int[]> test2 = getPairings("https://chess-results.com/tnr507448.aspx?lan=0&art=2&rd=3&turdet=YES&flag=30&prt=7", 9);
        final List<int[]> test3 = getPairings(507448, 9);
        //assert test1.equals(test2);
        //assert test1.equals(test3);
    }

    // inputLink should contain a valid link to a tournament on chess-results.com,
    // from a page which shows the desired round.
    public static List<int[]> getPairings(final CharSequence inputLink) {
        return PairingUtilities.getPairings(buildLinkFromString(inputLink, ChessDataType.PAIRING));
    }

    // This method allows using any link from the tournament, the round in the link is ignored.
    // The desired round is set via the method's argument.
    public static List<int[]> getPairings(final CharSequence inputLink, final int round) {
        return PairingUtilities.getPairings(buildLinkFromValues(getTournamentNumber(inputLink), round, ChessDataType.PAIRING));
    }

    public static List<int[]> getPairings(final int tournamentNumber, final int round) {
        return PairingUtilities.getPairings(buildLinkFromValues(tournamentNumber, round, ChessDataType.PAIRING));
    }

    private static URL buildLinkFromString(final CharSequence inputLink, final ChessDataType type) {
        final int tournamentNumber = getTournamentNumber(inputLink);
        final int round = getRound(inputLink);
        return buildLinkFromValues(tournamentNumber, round, type);
    }

    private static URL buildLinkFromValues(final int tournamentNumber, final int roundNumber, final ChessDataType type) {
        final int art = (type == ChessDataType.PAIRING) ? 2 : 1;
        try {
            return new URL("https://chess-results.com/tnr" + tournamentNumber +
                    ".aspx?lan=0&art=" + art + "&rd=" + roundNumber + "&turdet=NO&flag=NO&prt=7&zeilen=99999");
        } catch (final MalformedURLException e) {
            // This block should never be reached.
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    private static int getTournamentNumber(final CharSequence inputLink) {
        try {
            return Integer.parseInt(TOURNAMENT_NUMBER_PATTERN.matcher(inputLink).replaceFirst("$1"));
        } catch (final NumberFormatException e) {
            System.out.println("Could not get tournament number from link: " + inputLink + System.lineSeparator() +
                    "Make sure that the \"tnr\" key in the link is set to a valid integer.");
            System.exit(1);
            return -1; // Unreachable, but required for compiling.
        }
    }

    private static int getRound(final CharSequence inputLink) {
        try {
            return Integer.parseInt(ROUND_PATTERN.matcher(inputLink).replaceFirst("$1"));
        } catch (final NumberFormatException e) {
            System.out.println("Could not get the round from link: " + inputLink + System.lineSeparator() +
                    "Make sure that the \"rd\" key in the link is set to a valid integer.");
            System.exit(1);
            return -1; // Unreachable, but required for compiling.
        }
    }

    private static Scanner getScanner(final URL link) {
        try {
            return new Scanner(link.openStream());
        } catch (final IOException e) {
            e.printStackTrace();
            System.out.println("Could not get valid data from link: " + link);
            System.exit(1);
            return null; // Unreachable but necessary for compilation
        }
    }

    private static void advanceScannerToTableStart(final Scanner scanner, final Pattern tableHeaderPattern) {
        while (scanner.hasNextLine()) {
            if (tableHeaderPattern.matcher(cleanUpLine(scanner.nextLine())).matches()) {
                return;
            }
        }
    }

    private static String cleanUpLine(final CharSequence line) {
        // Remove HTML tags and numerical character code points.
        return CHARACTER_CODE_PATTERN.matcher(HTML_TAG_PATTERN.matcher(line).replaceAll("")).replaceAll("");
    }

    private enum ChessDataType {PAIRING}

    private static class PairingUtilities {
        private static final Pattern PAIRING_LINE_PATTERN = Pattern.compile("^\\d.*");
        private static final Pattern NON_DIGIT_PATTERN = Pattern.compile("[^0-9]+");
        private static final Pattern FOUR_DIGIT_ELO_START_PATTERN = Pattern.compile("^[12]\\d+");
        private static final Pattern PAIRING_TABLE_HEADER_PATTERN = Pattern.compile("^Br.;Nr.;Name;.*");

        private static List<int[]> getPairings(final URL link) {
            final Scanner scanner = getScanner(link);
            advanceScannerToPairingTableStart(scanner);
            final List<int[]> pairings = new ArrayList<>();
            while (scanner.hasNextLine()) {
                final String line = cleanUpLine(scanner.nextLine());
                if (!isPairingLine(line)) {
                    break;
                }
                final int[] pairing = parsePairingLine(line);
                // "nicht ausgelost" pairings return null and should not be added to the list.
                if (pairing != null) {
                    pairings.add(pairing);
                }
            }
            return pairings;
        }

        private static void advanceScannerToPairingTableStart(final Scanner scanner) {
            advanceScannerToTableStart(scanner, PAIRING_TABLE_HEADER_PATTERN);
        }

        private static boolean isPairingLine(final CharSequence line) {
            // Pairing lines start with a digit.
            return PAIRING_LINE_PATTERN.matcher(line).matches();
        }

        private static int[] parsePairingLine(final String line) {
            final String[] lineEntries = line.split(";");
            // White's starting rank and title are not separated.
            // Only using the digits in the string hopefully solves this issue.
            final int whiteStartingRank = Integer.parseInt(NON_DIGIT_PATTERN.matcher(lineEntries[1]).replaceAll(""));
            final String lastEntry = lineEntries[lineEntries.length - 1];
            final int blackStartingRank;
            if (lastEntry.equals("spielfrei")) {
                blackStartingRank = 0;
            } else if (lastEntry.equals("nicht ausgelost")) {
                // Pairing will not be returned.
                return null;
            } else {
                // In some cases separators are missing.
                // Only using the digits in the string hopefully solves this issue.
                final String lastEntryNumbersOnly = NON_DIGIT_PATTERN.matcher(lastEntry).replaceAll("");
                final String blackStartingRankString;
                // The input data has no separator between black elo and black starting rank,
                // so this is necessary.
                if (lastEntryNumbersOnly.startsWith("0")) {
                    // Elo is zero.
                    blackStartingRankString = lastEntryNumbersOnly.substring(1);
                } else if (FOUR_DIGIT_ELO_START_PATTERN.matcher(lastEntryNumbersOnly).matches()) {
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
}
