/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.application.urlSource;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.sudrf.application.urlSource.FileURLs.saveToExcelFile;
import org.sudrf.dao.Court;
import org.sudrf.parsing.CourtsHTMLPaser;
import org.sudrf.parsing.strategies.ArbitreCoutr;
import org.sudrf.util.URLCourt;

/**
 *
 * @author Kalinin Maksim
 */
public class ArbitreURLs {

    private static final String template = "http://arbitr.ru/as/subj/?id_ac=%1%";

    public static String parse(File src, File destFile, Function<Double, Boolean> progesssReport, StringBuilder errors) {

        List<URLCourt> urlOfSuds = IntStream.rangeClosed(1, 300).boxed()
                .filter(e -> ((1 < e && e < 121) || (201 < e && e < 204)))                
                .map(i -> new URLCourt(template.replace("%1%", i.toString()), -1))
                .collect(Collectors.toList());

        List<Court> courts = new CourtsHTMLPaser(ArbitreCoutr::parseDocument)
                .parseCourts(urlOfSuds, progesssReport, 1);

        Optional<File> file = saveToExcelFile(courts, destFile, "Арбитражные суды", errors);

        return file.isPresent() ? "Записан файл ".concat(file.get().getName()) : "Ошибка записи";
    }
}
