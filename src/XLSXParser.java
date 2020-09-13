import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

public class XLSXParser {
    public static void main(String[] args) throws IOException {
        getPairings("https://chess-results.com/tnr507449.aspx?lan=0&zeilen=0&turdet=YES&flag=30&prt=4&excel=2010");
    }

    public static void getPairings(String link) throws IOException {
        XSSFSheet worksheet = getWorksheet(improveLink(link));
        for (Iterator<Row> rowIterator = worksheet.rowIterator(); rowIterator.hasNext(); ) {
            Row row = rowIterator.next();
            for (Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext(); ) {
                Cell cell = cellIterator.next();
                if (cell.getCellType().equals(CellType.STRING)) {
                    System.out.print(cell.getStringCellValue() + ",");
                } else if (cell.getCellType().equals(CellType.NUMERIC)) {
                    System.out.print(cell.getNumericCellValue() + ",");
                }
            }
            System.out.println();
        }
    }

    public static void getRanking(String link){}

    private static XSSFSheet getWorksheet(String link) throws IOException {
        return new XSSFWorkbook(new URL(link).openStream()).getSheetAt(0);
    }

    private static String improveLink(String link) {
        return link.replace("flag=30", "flag=NO").replaceFirst("turdet=[^&]", "turdet=NO");
    }
}