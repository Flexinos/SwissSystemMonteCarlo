import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public final class ChessDataParser {

    private static final Pattern TOURNAMENT_NUMBER_PATTERN = Pattern.compile(".*tnr(\\d+).*");
    private static final Pattern ROUND_PATTERN = Pattern.compile(".*rd=(\\d+).*");
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern CHARACTER_CODE_PATTERN = Pattern.compile("&[^;]*;");

    // Prevent instantiation
    private ChessDataParser() {
    }

    // For testing
    public static void main(final String[] args) {
        //final List<int[]> test1 = getPairings("https://chess-results.com/tnr507448.aspx?lan=0&art=2&rd=9&turdet=YES&flag=30&prt=7");
        //final List<int[]> test2 = getPairings("https://chess-results.com/tnr507448.aspx?lan=0&art=2&rd=3&turdet=YES&flag=30&prt=7", 9);
        //final List<int[]> test3 = getPairings(507448, 9);
        //assert test1.equals(test2);
        //assert test1.equals(test3);
        //final List<Participant> participantTest1 = getParticipants("https://chess-results.com/tnr507448.aspx?lan=0&art=0&turdet=NO&flag=NO&prt=7");
        getTournamentData(507448);
    }

    public static List<Participant> getTournamentData(final CharSequence inputLink) {
        return getTournamentData(getTournamentNumber(inputLink));
    }

    public static List<Participant> getTournamentData(final int tournamentNumber) {
        final List<PlayerData> participantsData = ParticipantUtilities.getParticipantsData(buildStartingRankLink(tournamentNumber));
        final List<PlayerHistory> playerHistories = GamesUtilities.parsePlayedGames(buildGamesLink(tournamentNumber));
        return createParticipants(participantsData, playerHistories);
    }

    private static List<Participant> createParticipants(final List<PlayerData> playersData, final List<PlayerHistory> playerHistories) {
        if (playersData.size() != playerHistories.size()) {
            System.out.println("Number of entries from art 0 and art 5 does not match");
            System.exit(1);
        }
        final List<Participant> participants = new ArrayList<>();
        for (int playerIndex = 0; playerIndex < playersData.size(); ++playerIndex) {
            participants.add(createParticipant(playersData.get(playerIndex), playerHistories.get(playerIndex)));
        }
        return participants;
    }

    /**
     * Combines the data from instances of the data classes PlayerData and PlayerHistory
     * to create a new Participant. The two objects must describe the same player,
     * otherwise the resulting Participant object will be useless.
     * @param playerData A PlayerData object as returned by ParticipantUtilities.parseParticipantLine
     *                   (called via ParticipantUtilities.getParticipantsData)
     * @param playerHistory A playerHistory object as returned by GamesUtilities.parseGamesOfPlayer
     *                      (called via GamesUtilities.parsePlayedGames)
     * @return A Participant object containing the data of the two arguments.
     */
    private static Participant createParticipant(final PlayerData playerData, final PlayerHistory playerHistory) {
        final Map<Integer, Float> pastResults = createPastResults(playerHistory.normalGames);
        return new Participant(playerData.startingRank, playerData.title, playerData.name, playerData.country,
                playerData.elo, playerData.type, playerData.isFemale, pastResults, playerHistory.pointsByForfeit,
                playerHistory.nextOpponentStartingRank, playerHistory.isWhiteNextGame, playerHistory.hasReceivedBye);
    }

    private static Map<Integer, Float> createPastResults(final Iterable<Game> games) {
        final Map<Integer, Float> pastResults = new HashMap<>();
        for (final Game game : games) {
            pastResults.put(game.opponentStartingRank, gameResultToFloat(game.result));
        }
        return pastResults;
    }

    private static Float gameResultToFloat(final GameResult gameResult) {
        return switch (gameResult) {
            case WON -> 1.0f;
            case DRAW -> 0.5f;
            default -> 0.0f;
        };
    }

    private static URL buildStartingRankLink(final int tournamentNumber) {
        return buildLinkFromValues(tournamentNumber, 0, ChessDataType.STARTING_RANK);
    }

    private static URL buildGamesLink(final int tournamentNumber) {
        return buildLinkFromValues(tournamentNumber, 0, ChessDataType.GAMES);
    }

    private static URL buildLinkFromValues(final int tournamentNumber, final int roundNumber, final ChessDataType type) {
        final int art;
        final boolean shouldContainRound;
        switch (type) {
            case STARTING_RANK -> {
                art = 0;
                shouldContainRound = false;
            }
            case GAMES -> {
                art = 5;
                shouldContainRound = false;
            }
            default -> throw new IllegalArgumentException("Provided ChessDataType is not supported");
        }
        final StringBuilder stringBuilder = new StringBuilder("https://chess-results.com/tnr")
                .append(tournamentNumber).append(".aspx?lan=0&art=").append(art);
        if (shouldContainRound) {
            stringBuilder.append("&rd=").append(roundNumber);
        }
        stringBuilder.append("&turdet=NO&flag=NO&prt=7&zeilen=99999");
        try {
            return new URL(stringBuilder.toString());
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

    private static Scanner prepareScanner(final URL link, final Pattern tableHeaderPattern) {
        final Scanner scanner = getScanner(link);
        advanceScannerToTableStart(scanner, tableHeaderPattern);
        return scanner;
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

    private static String advanceScannerToTableStart(final Scanner scanner, final Pattern tableHeaderPattern) {
        while (scanner.hasNextLine()) {
            final String line = cleanUpLine(scanner.nextLine());
            if (tableHeaderPattern.matcher(line).matches()) {
                return line;
            }
        }
        return null;
    }

    private static String cleanUpLine(final CharSequence line) {
        // Remove HTML tags and numerical character code points.
        return CHARACTER_CODE_PATTERN.matcher(HTML_TAG_PATTERN.matcher(line).replaceAll("")).replaceAll("");
    }

    private static boolean matches(final CharSequence line, final Pattern pattern) {
        return pattern.matcher(line).matches();
    }

    private enum ChessDataType {STARTING_RANK, GAMES}

    private static class ParticipantUtilities {
        private static final Pattern STARTING_RANK_TABLE_HEADER_PATTERN = Pattern.compile("^Nr\\.;Name;.*");
        private static final Pattern NAME_NOT_SEPARATE_FROM_FIDE_ID_PATTERN = Pattern.compile("(\\d+[A-Z]*;[^\\d;]+)(\\d)");

        private static PlayerData parseParticipantLine(final CharSequence line, final Map<ParticipantEntryType, Integer> fieldIndices) {
            final String fixedLine = addMissingSeparatorAfterName(line);
            final String[] lineEntries = fixedLine.split(";");
            final int startingRank = parseStartingRank(lineEntries, fieldIndices);
            final String title = parseTitle(lineEntries, fieldIndices);
            final String name = parseName(lineEntries, fieldIndices);
            final long fideId = parseFideId(lineEntries, fieldIndices);
            final String country = parseCountry(lineEntries, fieldIndices);
            final int elo = parseElo(lineEntries, fieldIndices);
            final boolean isFemale = parseIsFemale(lineEntries, fieldIndices);
            final String type = parseType(lineEntries, fieldIndices);
            return new PlayerData(startingRank, title, name, fideId, country, elo, isFemale, type);
        }

        private static final Pattern STARTING_RANK_BEFORE_LETTERS_PATTERN = Pattern.compile("Nr\\.");
        private static final Pattern TITLE_AFTER_DIGITS_PATTERN = Pattern.compile("Nr\\.");
        private static final Pattern NAME_PATTERN = Pattern.compile("Name");
        private static final Pattern FIDE_ID_BEFORE_LETTERS_PATTERN = Pattern.compile("FideIDLand");
        private static final Pattern COUNTRY_AFTER_DIGITS_PATTERN = Pattern.compile("FideIDLand");
        private static final Pattern ELO_AT_START_PATTERN = Pattern.compile("Elo|EloIEloNsex");
        private static final Pattern SEX_AFTER_DIGITS_PATTERN = Pattern.compile("EloIEloNsex");
        private static final Pattern TYPE_PATTERN = Pattern.compile("Typ");

        private static class EntryTypeUtilities {
            private static final Pattern NO_DIGITS_PATTERN = Pattern.compile("\\D+");
            private static final Pattern DIGITS_PATTERN = Pattern.compile("\\d+");

            private static int parseStartingRankBeforeLetters(final CharSequence entry) {
                return Integer.parseInt(NO_DIGITS_PATTERN.matcher(entry).replaceFirst(""));
            }

            private static String parseTitleAfterDigits(final CharSequence entry) {
                return DIGITS_PATTERN.matcher(entry).replaceFirst("");
            }

            private static String parseName(final String entry) {
                return entry;
            }

            private static long parseFideIdBeforeLetters(final CharSequence entry) {
                return Long.parseLong(NO_DIGITS_PATTERN.matcher(entry).replaceFirst(""));
            }

            private static String parseCountryAfterDigits(final CharSequence entry) {
                return DIGITS_PATTERN.matcher(entry).replaceFirst("");
            }

            private static int parseEloAtStart(final String entry) {
                return switch (entry.charAt(0)) {
                    case '0' -> 0;
                    case '1', '2' -> Integer.parseInt(entry.substring(0, 4));
                    default -> Integer.parseInt(entry.substring(0, 3));
                };
            }

            private static boolean parseSexAfterDigits(final CharSequence entry) {
                return (int) entry.charAt(entry.length() - 1) == (int) 'w';
            }

            private static String parseType(final String entry) {
                return entry;
            }
        }

        private static List<PlayerData> getParticipantsData(final URL link) {
            final Scanner scanner = getScanner(link);//prepareScanner(link, STARTING_RANK_TABLE_HEADER_PATTERN);
            final String tableHeader = advanceScannerToTableStart(scanner, STARTING_RANK_TABLE_HEADER_PATTERN);
            if (tableHeader == null) {
                System.out.println("Could not find table header.");
                System.exit(1);
            }
            final Map<ParticipantEntryType, Integer> fieldIndices = parseTableStructure(tableHeader);
            final List<PlayerData> participantsData = new ArrayList<>();
            while (scanner.hasNextLine()) {
                final String line = cleanUpLine(scanner.nextLine());
                if (line.isEmpty()) {
                    break;
                }
                participantsData.add(parseParticipantLine(line, fieldIndices));
            }
            return participantsData;
        }

        private static Map<ParticipantEntryType, Integer> parseTableStructure(final String tableHeader) {
            final String[] headerEntries = tableHeader.split(";");
            final Map<ParticipantEntryType, Integer> fieldIndices = new EnumMap<>(ParticipantEntryType.class);
            for (int headerEntryIndex = 0; headerEntryIndex < headerEntries.length; headerEntryIndex++) {
                final List<ParticipantEntryType> participantEntryTypes =
                        parseHeaderEntryTypes(headerEntries[headerEntryIndex]);
                for (final ParticipantEntryType participantEntryType : participantEntryTypes) {
                    fieldIndices.put(participantEntryType, headerEntryIndex);
                }
            }
            return fieldIndices;
        }

        private static List<ParticipantEntryType> parseHeaderEntryTypes(final CharSequence headerEntry) {
            final List<ParticipantEntryType> fieldTypes = new ArrayList<>();
            if (matches(headerEntry, STARTING_RANK_BEFORE_LETTERS_PATTERN)) {
                fieldTypes.add(ParticipantEntryType.STARTING_RANK_BEFORE_LETTERS);
            }
            if (matches(headerEntry, TITLE_AFTER_DIGITS_PATTERN)) {
                fieldTypes.add(ParticipantEntryType.TITLE_AFTER_DIGITS);
            }
            if (matches(headerEntry, NAME_PATTERN)) {
                fieldTypes.add(ParticipantEntryType.NAME);
            }
            if (matches(headerEntry, FIDE_ID_BEFORE_LETTERS_PATTERN)) {
                fieldTypes.add(ParticipantEntryType.FIDE_ID_BEFORE_LETTERS);
            }
            if (matches(headerEntry, COUNTRY_AFTER_DIGITS_PATTERN)) {
                fieldTypes.add(ParticipantEntryType.COUNTRY_AFTER_DIGITS);
            }
            if (matches(headerEntry, ELO_AT_START_PATTERN)) {
                fieldTypes.add(ParticipantEntryType.ELO_AT_START);
            }
            if (matches(headerEntry, SEX_AFTER_DIGITS_PATTERN)) {
                fieldTypes.add(ParticipantEntryType.SEX_AFTER_DIGITS);
            }
            if (matches(headerEntry, TYPE_PATTERN)) {
                fieldTypes.add(ParticipantEntryType.TYPE);
            }
            return fieldTypes;
        }

        private enum ParticipantEntryType {STARTING_RANK_BEFORE_LETTERS, TITLE_AFTER_DIGITS, NAME, FIDE_ID_BEFORE_LETTERS, COUNTRY_AFTER_DIGITS, ELO_AT_START, SEX_AFTER_DIGITS, TYPE}

        private static String addMissingSeparatorAfterName(final CharSequence line) {
            return NAME_NOT_SEPARATE_FROM_FIDE_ID_PATTERN.matcher(line).replaceFirst("$1;$2");
        }

        private static int parseStartingRank(final String[] lineEntries, final Map<ParticipantEntryType, Integer> fieldIndices) {
            try {
                if (fieldIndices.containsKey(ParticipantEntryType.STARTING_RANK_BEFORE_LETTERS)) {
                    return EntryTypeUtilities.parseStartingRankBeforeLetters(
                            lineEntries[fieldIndices.get(ParticipantEntryType.STARTING_RANK_BEFORE_LETTERS)]);
                }
                throw new IOException("No entry for rank found.");
            } catch (final IOException e) {
                e.printStackTrace();
                System.exit(1);
                return 0; // unreachable
            }
        }

        private static String parseTitle(final String[] lineEntries, final Map<ParticipantEntryType, Integer> fieldIndices) {
            if (fieldIndices.containsKey(ParticipantEntryType.TITLE_AFTER_DIGITS)) {
                return EntryTypeUtilities.parseTitleAfterDigits(lineEntries[fieldIndices.get(ParticipantEntryType.TITLE_AFTER_DIGITS)]);
            }
            return "";
        }

        private static String parseName(final String[] lineEntries, final Map<ParticipantEntryType, Integer> fieldIndices) {
            try {
                if (fieldIndices.containsKey(ParticipantEntryType.NAME)) {
                    return EntryTypeUtilities.parseName(lineEntries[fieldIndices.get(ParticipantEntryType.NAME)]);
                }
                throw new IOException("No entry for name found.");
            } catch (final IOException e) {
                e.printStackTrace();
                System.exit(1);
                return null; // unreachable
            }
        }

        private static long parseFideId(final String[] lineEntries, final Map<ParticipantEntryType, Integer> fieldIndices) {
            if (fieldIndices.containsKey(ParticipantEntryType.FIDE_ID_BEFORE_LETTERS)) {
                return EntryTypeUtilities.parseFideIdBeforeLetters(lineEntries[fieldIndices.get(ParticipantEntryType.FIDE_ID_BEFORE_LETTERS)]);
            }
            return 0L;
        }

        private static String parseCountry(final String[] lineEntries, final Map<ParticipantEntryType, Integer> fieldIndices) {
            if (fieldIndices.containsKey(ParticipantEntryType.COUNTRY_AFTER_DIGITS)) {
                return EntryTypeUtilities.parseCountryAfterDigits(lineEntries[fieldIndices.get(ParticipantEntryType.COUNTRY_AFTER_DIGITS)]);
            }
            return "";
        }

        private static int parseElo(final String[] lineEntries, final Map<ParticipantEntryType, Integer> fieldIndices) {
            if (fieldIndices.containsKey(ParticipantEntryType.ELO_AT_START)) {
                return EntryTypeUtilities.parseEloAtStart(lineEntries[fieldIndices.get(ParticipantEntryType.ELO_AT_START)]);
            }
            return 0;
        }

        private static boolean parseIsFemale(final String[] lineEntries, final Map<ParticipantEntryType, Integer> fieldIndices) {
            if (fieldIndices.containsKey(ParticipantEntryType.SEX_AFTER_DIGITS)) {
                return EntryTypeUtilities.parseSexAfterDigits(lineEntries[fieldIndices.get(ParticipantEntryType.SEX_AFTER_DIGITS)]);
            }
            return false;
        }

        private static String parseType(final String[] lineEntries, final Map<ParticipantEntryType, Integer> fieldIndices) {
            if (fieldIndices.containsKey(ParticipantEntryType.TYPE)) {
                return EntryTypeUtilities.parseType(lineEntries[fieldIndices.get(ParticipantEntryType.TYPE)]);
            }
            return "";
        }
    }

    private static class PairingUtilities {
        private static final Pattern PAIRING_LINE_PATTERN = Pattern.compile("^\\d.*");
        private static final Pattern NON_DIGIT_PATTERN = Pattern.compile("\\D");
        private static final Pattern FOUR_DIGIT_ELO_START_PATTERN = Pattern.compile("^[12]\\d*");
        private static final Pattern PAIRING_TABLE_HEADER_PATTERN = Pattern.compile("^Br\\.;Nr\\.;Name;.*");

        private static List<int[]> getPairings(final URL link) {
            final Scanner scanner = prepareScanner(link, PAIRING_TABLE_HEADER_PATTERN);
            final List<int[]> pairings = new ArrayList<>();
            while (scanner.hasNextLine()) {
                final String line = cleanUpLine(scanner.nextLine());
                if (!matches(line, PAIRING_LINE_PATTERN)) {
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

    private static class GamesUtilities {
        private static final Pattern GAMES_TABLE_HEADER_PATTERN = Pattern.compile("^Nr\\.;?Name;.*");
        private static final Pattern GAMES_ENTRY_HEADER_PATTERN = Pattern.compile("\\d+\\.Rd");
        private static final Pattern GAMES_ENTRY_LINE_PATTERN = Pattern.compile("\\d+[A-Z]*;.*");

        private static final Pattern NORMAL_GAME_PATTERN = Pattern.compile("\\d+[ws][01\u00BD]");
        private static final Pattern FORFEIT_GAME_PATTERN = Pattern.compile("\\d+[ws]\\+");
        private static final Pattern BYE_GAME_PATTERN = Pattern.compile("-1");
        private static final Pattern FUTURE_GAME_PATTERN = Pattern.compile("\\d+[ws]");

        private static final Pattern REMOVE_FOR_OPPONENT_PARSING_PATTERN = Pattern.compile("[ws][01\u00BD]?");
        private static final Pattern REMOVE_FOR_RESULT_PARSING_PATTERN = Pattern.compile("\\d+[ws]");

        // participants need to be sorted by ascending starting rank
        private static List<PlayerHistory> parsePlayedGames(final URL link) {
            final Scanner scanner = getScanner(link);
            final String tableHeader = advanceScannerToTableStart(scanner, GAMES_TABLE_HEADER_PATTERN);
            if (tableHeader == null) {
                System.out.println("Could not find game table header. Check link " + link + " and GAMES_TABLE_HEADER_PATTERN.");
                System.exit(1);
            }
            final List<Integer> gameEntryIndices = findGameEntryIndices(tableHeader);
            final List<PlayerHistory> playerHistories = new ArrayList<>();
            while (scanner.hasNextLine()) {
                final String line = cleanUpLine(scanner.nextLine());
                if (!GAMES_ENTRY_LINE_PATTERN.matcher(line).matches()) {
                    break;
                }
                playerHistories.add(parseGamesOfPlayer(line, gameEntryIndices));
            }
            return playerHistories;
        }

        private static List<Integer> findGameEntryIndices(final String tableHeader) {
            final String[] headerEntries = tableHeader.split(";");
            final List<Integer> gameEntryIndices = new ArrayList<>();
            for (int i = 0, headerEntriesLength = headerEntries.length; i < headerEntriesLength; i++) {
                if (GAMES_ENTRY_HEADER_PATTERN.matcher(headerEntries[i]).matches()) {
                    gameEntryIndices.add(i);
                }
            }
            return gameEntryIndices;
        }

        private static PlayerHistory parseGamesOfPlayer(final String line, final Iterable<Integer> gameEntryIndices) {
            final String[] lineEntries = line.split(";");
            final List<Game> games = new ArrayList<>();
            boolean hasReceivedBye = false;
            int pointsByForfeit = 0;
            int nextOpponentStartingRank = 0;
            boolean isWhiteNextGame = false;
            for (final Integer gameEntryIndex : gameEntryIndices) {
                final String entry = lineEntries[gameEntryIndex];
                if (NORMAL_GAME_PATTERN.matcher(entry).matches()) {
                    final int opponentStartingRank = parseOpponentStartingRank(entry);
                    final boolean isWhite = isWhite(entry);
                    final GameResult result = parseResult(entry);
                    games.add(new Game(opponentStartingRank, isWhite, result));
                } else if (FORFEIT_GAME_PATTERN.matcher(entry).matches()) {
                    ++pointsByForfeit;
                } else if (BYE_GAME_PATTERN.matcher(entry).matches()) {
                    hasReceivedBye = true;
                } else if (FUTURE_GAME_PATTERN.matcher(entry).matches()) {
                    nextOpponentStartingRank = parseOpponentStartingRank(entry);
                    isWhiteNextGame = isWhite(entry);
                }
            }
            return new PlayerHistory(games, hasReceivedBye, pointsByForfeit, nextOpponentStartingRank, isWhiteNextGame);
        }
        
        private static int parseOpponentStartingRank(final CharSequence entry) {
            return Integer.parseInt(REMOVE_FOR_OPPONENT_PARSING_PATTERN.matcher(entry).replaceFirst(""));
        }

        private static boolean isWhite(final String entry) {
            return entry.contains("w");
        }

        private static GameResult parseResult(final CharSequence entry) {
            return switch (REMOVE_FOR_RESULT_PARSING_PATTERN.matcher(entry).replaceFirst("")) {
                case "1" -> GameResult.WON;
                case "0" -> GameResult.LOST;
                default -> GameResult.DRAW;
            };
        }
    }

    private static final class PlayerData {
        private final int startingRank;
        private final String title;
        private final String name;
        private final long fideId;
        private final String country;
        private final int elo;
        private final boolean isFemale;
        private final String type;

        private PlayerData(final int startingRank, final String title, final String name, final long fideId,
                           final String country, final int elo, final boolean isFemale, final String type) {
            this.startingRank = startingRank;
            this.title = title;
            this.name = name;
            this.fideId = fideId;
            this.country = country;
            this.elo = elo;
            this.isFemale = isFemale;
            this.type = type;
        }
    }

    private static final class PlayerHistory {
        private final List<Game> normalGames;
        private final boolean hasReceivedBye;
        private final int pointsByForfeit;
        private final int nextOpponentStartingRank;
        private final boolean isWhiteNextGame;

        private PlayerHistory(final List<Game> normalGames, final boolean hasReceivedBye, final int pointsByForfeit,
                              final int nextOpponentStartingRank, final boolean isWhiteNextGame) {
            this.normalGames = normalGames;
            this.hasReceivedBye = hasReceivedBye;
            this.pointsByForfeit = pointsByForfeit;
            this.nextOpponentStartingRank = nextOpponentStartingRank;
            this.isWhiteNextGame = isWhiteNextGame;
        }
    }

    private enum GameResult {WON, DRAW, LOST}

    private static final class Game {
        private final int opponentStartingRank;
        private final boolean playerIsWhite;
        private final GameResult result;

        private Game(final int opponentStartingRank, final boolean playerIsWhite, final GameResult result) {
            this.opponentStartingRank = opponentStartingRank;
            this.playerIsWhite = playerIsWhite;
            this.result = result;
        }
    }
}
