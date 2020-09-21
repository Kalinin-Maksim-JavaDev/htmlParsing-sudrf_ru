/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author Kalinin Maksim
 */
public class ExcelImportProvider {

    Workbook book;

    int rowsCounter = 0;

    public ExcelImportProvider(FileInputStream inputStream) throws IOException {
        book = new HSSFWorkbook(inputStream);
    }

    public List<List<String>> readToList() {

        Sheet sheet = book.getSheetAt(0);

        List<List<String>> data = new ArrayList<>();

        for (Row row : sheet) {
            List<String> stringCellValue = new ArrayList<>();

            for (Cell cell : row) {

                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        stringCellValue.add(String.valueOf(Double.valueOf(cell.getNumericCellValue()).intValue()));
                        break;
                    case Cell.CELL_TYPE_STRING:
                        stringCellValue.add(cell.getStringCellValue());
                        break;
                }
            }
            data.add(stringCellValue);
        }
        return data;
    }
}
