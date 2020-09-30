import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class XLSXParser {
    public static void main(final String[] args) throws IOException {
        final List<Participant> participants = getParticipantsFromRanking("https://chess-results.com/tnr507448.aspx?lan=0&zeilen=0&art=1&rd=8&turdet=YES&flag=30&prt=4&excel=2010");
        for (final Participant participant : participants) {
            System.out.println(participant);
        }
    }

    public static List<Participant> getParticipantsFromRanking(final String link) throws IOException {
        final List<Participant> participants = new ArrayList<>();
        final XSSFSheet worksheet = getWorksheet(improveLink(link));
        boolean tableStarted = false;
        List<RankingColumnType> columnStructure = new ArrayList<>(); // The only purpose of the initialization is suppressing uninitialized warnings.
        for (final Iterator<Row> rowIterator = worksheet.rowIterator(); rowIterator.hasNext(); ) {
            final Row row = rowIterator.next();
            // Before the participant entries start check for the table header.
            if (!tableStarted) {
                if (isTableHeader(row)) {
                    // Indicate that the relevant data has been reached.
                    tableStarted = true;
                    columnStructure = getRankingColumnStructure(row);
                }
                continue;
            }
            // Check for end of participant entries.
            if (!isParticipantEntry(row)) {
                break;
            }
            // Runs for each row containing participant data.
            participants.add(createParticipantFromRow(row, columnStructure));
        }
        return participants;
    }

    private static XSSFSheet getWorksheet(final String link) throws IOException {
        return new XSSFWorkbook(new URL(link).openStream()).getSheetAt(0);
    }

    private static String improveLink(final String link) {
        return link.replace("flag=30", "flag=NO").replaceFirst("turdet=[^&]*", "turdet=NO");
    }

    private static boolean isTableHeader(final Row row) {
        final Iterator<Cell> cellIterator = row.cellIterator();
        final Cell cell = cellIterator.next();
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().matches("rg\\.|Rg\\.|Nr\\.");
        }
        return false;
    }

    private static boolean isParticipantEntry(final Row row) {
        final Iterator<Cell> cellIterator = row.cellIterator();
        final Cell cell = cellIterator.next();
        return cell.getCellType() == CellType.NUMERIC;
    }

    private static List<RankingColumnType> getRankingColumnStructure(final Row tableHeader) {
        final List<RankingColumnType> columnStructure = new ArrayList<>();
        for (final Iterator<Cell> cellIterator = tableHeader.cellIterator(); cellIterator.hasNext(); ) {
            final Cell cell = cellIterator.next();
            final String cellContent = cell.getStringCellValue();
            final RankingColumnType columnType = getColumnType(cellContent);
            columnStructure.add(columnType);
        }
        return columnStructure;
    }

    private static Participant createParticipantFromRow(final Row row, final List<RankingColumnType> columnStructure) {
        int startingRank = 0;
        String title = "";
        String name = "";
        String country = "";
        int elo = 0;
        String bundesland = "";
        double score = 0;
        double tieBreak1 = 0;
        double tieBreak2 = 0;
        double tieBreak3 = 0;
        String type = "";
        String sex = "";
        final Iterator<RankingColumnType> columnTypeIterator = columnStructure.iterator();
        final Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            final Cell cell = cellIterator.next();
            switch (columnTypeIterator.next()) {
                case STARTING_RANK -> startingRank = (int) cell.getNumericCellValue();
                case TITLE -> title = cell.getStringCellValue();
                case NAME -> name = cell.getStringCellValue();
                case COUNTRY -> country = cell.getStringCellValue();
                case BUNDESLAND -> bundesland = cell.getStringCellValue();
                case ELO -> elo = (int) cell.getNumericCellValue();
                case SCORE -> score = cell.getNumericCellValue();
                case TIE_BREAK_1 -> tieBreak1 = cell.getNumericCellValue();
                case TIE_BREAK_2 -> tieBreak2 = cell.getNumericCellValue();
                case TIE_BREAK_3 -> tieBreak3 = cell.getNumericCellValue();
                case TYPE -> type = cell.getStringCellValue();
                case SEX -> sex = cell.getStringCellValue();
            }
        }
        final boolean isFemale = sex.matches("w");
        return new Participant(startingRank, title, name, country, bundesland, elo, type, isFemale, new HashMap<>());
    }

    private static RankingColumnType getColumnType(final String columnName) {
        if (isStartingRank(columnName)) {
            return RankingColumnType.STARTING_RANK;
        }
        if (isTitle(columnName)) {
            return RankingColumnType.TITLE;
        }
        if (isName(columnName)) {
            return RankingColumnType.NAME;
        }
        if (isCountry(columnName)) {
            return RankingColumnType.COUNTRY;
        }
        if (isBundesland(columnName)) {
            return RankingColumnType.BUNDESLAND;
        }
        if (isElo(columnName)) {
            return RankingColumnType.ELO;
        }
        if (isScore(columnName)) {
            return RankingColumnType.SCORE;
        }
        if (isTieBreak1(columnName)) {
            return RankingColumnType.TIE_BREAK_1;
        }
        if (isTieBreak2(columnName)) {
            return RankingColumnType.TIE_BREAK_2;
        }
        if (isTieBreak3(columnName)) {
            return RankingColumnType.TIE_BREAK_3;
        }
        if (isType(columnName)) {
            return RankingColumnType.TYPE;
        }
        if (isSex(columnName)) {
            return RankingColumnType.SEX;
        }
        return RankingColumnType.IGNORE;
    }

    private static boolean isStartingRank(final String string) {
        return string.matches("snr|Snr|Nr\\.");
    }

    private static boolean isTitle(final String string) {
        return string.matches("");
    }

    private static boolean isName(final String string) {
        return string.matches("name|Name");
    }

    private static boolean isCountry(final String string) {
        return string.matches("land|Land");
    }

    private static boolean isBundesland(final String string) {
        return string.matches("bdld|Bdld");
    }

    private static boolean isElo(final String string) {
        return string.matches("Elo|elo|EloI|eloi");
    }

    private static boolean isScore(final String string) {
        return string.matches("pkt|Pkt\\. ");
    }

    private static boolean isTieBreak1(final String string) {
        return string.matches("wtg1|Wtg1");
    }

    private static boolean isTieBreak2(final String string) {
        return string.matches("wtg2|Wtg2");
    }

    private static boolean isTieBreak3(final String string) {
        return string.matches("wtg3|Wtg3");
    }

    private static boolean isType(final String string) {
        return string.matches("typ|Typ");
    }

    private static boolean isSex(final String string) {
        return string.matches("sex|Sex");
    }

    private enum RankingColumnType {
        STARTING_RANK, TITLE, NAME, COUNTRY, BUNDESLAND, ELO, SCORE, TIE_BREAK_1, TIE_BREAK_2, TIE_BREAK_3, TYPE, SEX, IGNORE
    }

}
