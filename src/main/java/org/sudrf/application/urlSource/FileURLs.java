/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.application.urlSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import jdk.nashorn.internal.runtime.options.Option;
import org.sudrf.dao.Attribute;
import org.sudrf.parsing.strategies.CommonCoutr;
import org.sudrf.parsing.CourtsHTMLPaser;
import org.sudrf.dao.Attributes;
import org.sudrf.dao.Court;
import org.sudrf.util.ExcelExportProvider;
import org.sudrf.util.ExcelImportProvider;
import org.sudrf.util.URLCourt;

/**
 *
 * @author Kalinin Maksim
 */
public class FileURLs {

    private static final Logger LOGGER = Logger.getLogger(FileURLs.class.getName());

    public static String parse(File src, File dir, Function<Double, Boolean> progesssReport, StringBuilder errors) {

        ExcelImportProvider excelImport = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(src);
            excelImport = new ExcelImportProvider(fileInputStream);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            errors.append(ex.getMessage());
        }

        if (excelImport == null) {
            return "Excel не создан";
        } else {
            List<URLCourt> urlOfSuds = excelImport
                    .readToList()
                    .stream()
                    .map(st
                            -> new URLCourt("%1/modules.php?name=sud".replace("%1", st.get(2)),
                            Integer.valueOf(st.get(0))))
                    .collect(Collectors.toList());

            List<Court> courts = new CourtsHTMLPaser(CommonCoutr::parseDocument)
                    .parseCourts(urlOfSuds, progesssReport, 1)
                    .stream()
                    .filter(c -> !c.getId().getName().toLowerCase().contains("воен"))
                    .collect(Collectors.toList());
            Optional<File> file = saveToExcelFile(courts, dir, src.getName().replace(".xls", "").concat(" реквизиты"), errors);

            return file.isPresent() ? "Записан файл ".concat(file.get().getName()) : "Ошибка записи";
        }
    }

    public static Optional<File> saveToExcelFile(List<Court> courts, File fileDist, String fileName, StringBuilder errors) {

        File file = new File(fileDist.getAbsolutePath().concat("/").concat(fileName).concat(".xls"));
        FileOutputStream fileOutputStream = null;

        $save:
        {   
            try {

                fileOutputStream = new FileOutputStream(file.getAbsolutePath());
            } catch (FileNotFoundException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
                errors.append(ex.getMessage());
                break $save;
            }

            ExcelExportProvider provider = new ExcelExportProvider("SudsInfo");

            provider.printHeader(Attributes.attributesNamesItterator());

            courts.stream()
                    .sorted(Court::compareTo)
                    .forEach((t) -> {
                        provider.printRow(t.attributesIterator());
                    });

            provider.saveTo(fileOutputStream);
            return Optional.of(file);
        }
        
        return Optional.empty();
    }
}
