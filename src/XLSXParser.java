import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class XLSXParser {
    public static void main(String[] args) throws IOException {
        getPairings("https://chess-results.com/tnr507449.aspx?lan=0&zeilen=0&art=1&rd=7&turdet=YES&flag=30&prt=4&excel=2010");
    }

    public static void getPairings(String link) throws IOException {
        XSSFSheet worksheet = getWorksheet(improveLink(link));
        for (Iterator<Row> rowIterator = worksheet.rowIterator(); rowIterator.hasNext(); ) {
            Row row = rowIterator.next();
            for (Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext(); ) {
                Cell cell = cellIterator.next();
                if (cell.getCellType().equals(CellType.STRING)) {
                    System.out.print(cell.getStringCellValue());
                } else if (cell.getCellType().equals(CellType.NUMERIC)) {
                    System.out.print(cell.getNumericCellValue());
                }
                if (cellIterator.hasNext()) {
                    System.out.print(",");
                }
            }
            System.out.println();
        }
    }

    public static List<Participant> getParticipantsFromRanking(String link) throws IOException {
        List<Participant> participants = new ArrayList<>();
        XSSFSheet worksheet = getWorksheet(improveLink(link));
        boolean tableStarted = false;
        Map<Integer, String> relevantEntryIndices;
        for (Iterator<Row> rowIterator = worksheet.rowIterator(); rowIterator.hasNext(); ) {
            Row row = rowIterator.next();
            // Before the participant entries start check for the table header.
            if (!tableStarted) {
                if (isTableHeader(row)) {
                    // Indicate that the relevant data has been reached.
                    tableStarted = true;
                    relevantEntryIndices = getRelevantEntryIndices(row);
                }
                continue;
            }
            // Check for end of participant entries.
            if (!isParticipantEntry(row)) {
                break;
            }
            // Runs for each row containing participant data.
            participants.add(createParticipantFromRow(row, relevantEntryIndices));
        }
        return participants;
    }

    private static XSSFSheet getWorksheet(String link) throws IOException {
        return new XSSFWorkbook(new URL(link).openStream()).getSheetAt(0);
    }

    private static String improveLink(String link) {
        return link.replace("flag=30", "flag=NO").replaceFirst("turdet=[^&]*", "turdet=NO");
    }

    private static boolean isTableHeader(Row row) {
        Iterator<Cell> cellIterator = row.cellIterator();
        Cell cell = cellIterator.next();
        if (cell.getCellType().equals(CellType.STRING)) {
            return cell.getStringCellValue().matches("rg\\.|Rg\\.|Nr\\.");
        }
        return false;
    }

    private static boolean isParticipantEntry(Row row) {
        Iterator<Cell> cellIterator = row.cellIterator();
        Cell cell = cellIterator.next();
        return cell.getCellType().equals(CellType.NUMERIC);
    }

    private static Map<Integer, String> getRelevantEntryIndices(Row tableHeader) {
        Map<Integer, String> relevantEntryIndices = new HashMap<>();
        int currentColumn = 0;
        for (Iterator<Cell> cellIterator = tableHeader.cellIterator(); cellIterator.hasNext(); ) {
            Cell cell = cellIterator.next();
            String cellContent = cell.getStringCellValue();
            String columnType = getColumnType(cellContent);
            if (!columnType.equals("")) {
                relevantEntryIndices.put(currentColumn, columnType);
            }
            ++currentColumn;
        }
        return relevantEntryIndices;
    }

    private static Participant createParticipantFromRow(Row row, Map<Integer, String> relevantEntryIndices) {

    }

    private static String getColumnType(String columnName) {

    }

    private static boolean isStartingRank(String string) {
        return string.matches("snr|Snr|Nr\\.");
    }

    private static boolean isTitle(String string) {
        return string.matches("");
    }
}