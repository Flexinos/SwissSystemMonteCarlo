import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class ChessDataParser {

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
        final List<Participant> participants = getParticipantsFromRanking(507448, 1);
    }

    // inputLink should contain a valid link to a tournament on chess-results.com,
    // from a page which shows the desired round.
    public static List<int[]> getPairings(final String inputLink) {
        return PairingUtilities.getPairings(buildLinkFromString(inputLink, ChessDataType.PAIRING));
    }

    // This method allows using any link from the tournament, the round in the link is ignored.
    // The desired round is set via the method's argument.
    public static List<int[]> getPairings(final String inputLink, final int round) {
        return PairingUtilities.getPairings(buildLinkFromValues(getTournamentNumber(inputLink), round, ChessDataType.PAIRING));
    }

    public static List<int[]> getPairings(final int tournamentNumber, final int round) {
        return PairingUtilities.getPairings(buildLinkFromValues(tournamentNumber, round, ChessDataType.PAIRING));
    }

    public static List<Participant> getParticipantsFromRanking(final String inputLink) {
        return RankingUtilities.getParticipantsFromRank(buildLinkFromString(inputLink, ChessDataType.RANKING));
    }

    public static List<Participant> getParticipantsFromRanking(final String inputLink, final int round) {
        return RankingUtilities.getParticipantsFromRank(buildLinkFromValues(getTournamentNumber(inputLink), round, ChessDataType.RANKING));
    }

    public static List<Participant> getParticipantsFromRanking(final int tournamentNumber, final int round) {
        return RankingUtilities.getParticipantsFromRank(buildLinkFromValues(tournamentNumber, round, ChessDataType.RANKING));
    }

    private static URL buildLinkFromString(final String inputLink, final ChessDataType type) {
        final int tournamentNumber = getTournamentNumber(inputLink);
        final int round = getRound(inputLink);
        return buildLinkFromValues(tournamentNumber, round, type);
    }

    private static URL buildLinkFromValues(final int tournamentNumber, final int roundNumber, final ChessDataType type) {
        final int art = type == ChessDataType.PAIRING ? 2 : 1;
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

    private static int getTournamentNumber(final String inputLink) {
        try {
            return Integer.parseInt(inputLink.replaceFirst(".*tnr(\\d+).*", "$1"));
        } catch (final NumberFormatException e) {
            System.out.println("Could not get tournament number from link: " + inputLink + System.lineSeparator() +
                    "Make sure that the \"tnr\" key in the link is set to a valid integer.");
            System.exit(1);
            return -1; // Unreachable, but required for compiling.
        }
    }

    private static int getRound(final String inputLink) {
        try {
            return Integer.parseInt(inputLink.replaceFirst(".*rd=(\\d+).*", "$1"));
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

    private static void advanceScannerToTableStart(final Scanner scanner, final String tableHeaderPattern) {
        while (scanner.hasNextLine()) {
            if (cleanUpLine(scanner.nextLine()).matches(tableHeaderPattern)) {
                return;
            }
        }
    }

    private static String cleanUpLine(final String line) {
        // Remove HTML tags and numerical character code points.
        return line
                .replaceAll("<[^>]*>", "")
                .replaceAll("&#\\d*;", "");
    }

    private enum ChessDataType {PAIRING, RANKING}

    private static class PairingUtilities {
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
            advanceScannerToTableStart(scanner, "^Br.;Nr.;Name;.*");
        }

        private static boolean isPairingLine(final String line) {
            // Pairing lines start with a digit.
            return line.matches("^\\d.*");
        }

        private static int[] parsePairingLine(final String line) {
            final String[] lineEntries = line.split("[;]");
            // White's starting rank and title are not separated.
            // Only using the digits in the string hopefully solves this issue.
            final int whiteStartingRank = Integer.parseInt(lineEntries[1].replaceAll("[^0-9]+", ""));
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
                final String lastEntryNumbersOnly = lastEntry.replaceAll("[^0-9]+", "");
                final String blackStartingRankString;
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

    private static class RankingUtilities {
        private static List<Participant> getParticipantsFromRank(final URL link) {
            final Scanner scanner = getScanner(link);
            advanceScannerToRankingTableStart(scanner);
            final List<Participant> participants = new ArrayList<>();
            int currentRank = 0;
            while (scanner.hasNextLine()) {
                final String line = cleanUpLine(scanner.nextLine());
                if (!isRankingLine(line)) {
                    break;
                }
                participants.add(parseParticipantLine(line, ++currentRank));
            }
            return participants;
        }

        private static void advanceScannerToRankingTableStart(final Scanner scanner) {
            advanceScannerToTableStart(scanner, "(Rg\\.|rg\\.|Nr\\.);(snr|Snr|Name);.*");
        }

        private static boolean isRankingLine(final String line) {
            // Ranking lines start with a digit.
            return line.matches("^\\d.*");
        }

        private static Participant parseParticipantLine(final String line, final int currentRank) {
            System.out.println(line);
            return null;
        }
    }
}
