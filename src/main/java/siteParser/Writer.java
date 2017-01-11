package siteParser;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.File;
import java.io.IOException;

/**
 * Created by user on 1/10/17.
 */
public class Writer {
    public static int rowIndex = 1;
    public static int columnIndex = 0;
    public static String parth = "src/main/java/reportFolder/report.xls";

    public static void createFile() throws IOException, WriteException {
        WritableWorkbook workbook = Workbook.createWorkbook(new File(parth));
        WritableSheet sheet = workbook.createSheet("First Sheet", 0);

        int widthInChars = 45;
        sheet.setColumnView(0, widthInChars);
        sheet.addCell(new Label(0, 0, "LINK ADDRESS "));
        sheet.addCell(new Label(1, 0, "COMPANY NAME "));
        sheet.addCell(new Label(2, 0, "WEB SITE "));
        sheet.addCell(new Label(3, 0, "SIZE "));
        sheet.addCell(new Label(4, 0, "INDUSTRY "));
        sheet.addCell(new Label(5, 0, "TYPE "));
        workbook.write();
        workbook.close();
    }

    public static void writeToFile(String s) throws IOException, BiffException, WriteException {
        Workbook workbook1 = Workbook.getWorkbook(new File(parth));
        WritableWorkbook copy = Workbook.createWorkbook(new File(parth), workbook1);
        WritableSheet sheet2 = copy.getSheet(0);
        int widthInChars = 45;
        sheet2.setColumnView(columnIndex, widthInChars);
        Label label2 = new Label(columnIndex, rowIndex, s);
        sheet2.addCell(label2);
        ++columnIndex;
        copy.write();
        copy.close();
    }
}
