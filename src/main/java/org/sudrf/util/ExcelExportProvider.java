/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.util;

import org.sudrf.dao.Attribute;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author Kalinin Maksim
 */
public class ExcelExportProvider {

    Sheet sheet;
    Workbook book;

    int rowsCounter = 0;

    private static final Logger LOGGER = Logger.getLogger(ExcelExportProvider.class.getName());

    public ExcelExportProvider(String name) {
        book = new HSSFWorkbook();
        sheet = book.createSheet(name);
    }

    public void saveTo(FileOutputStream outputStream) {
        sheet.autoSizeColumn(1);
        try {
            book.write(outputStream);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            try {
                book.close();
                outputStream.close();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    public void printRow(Iterator<Attribute> attrItr) {

        Row row = sheet.createRow(rowsCounter++);
        attrItr.forEachRemaining(attr -> {
            row.createCell(attr.getId()).setCellValue(attr.getValue());
        });
    }

    public void printHeader(Iterator<Map.Entry<String, Integer>> attributesNamesItterator) {

        Row row = sheet.createRow(rowsCounter++);

        attributesNamesItterator.forEachRemaining(attr -> {
            row.createCell(attr.getValue()).setCellValue(attr.getKey());
        });
    }
}
