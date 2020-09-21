/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.demoHTMLParsing;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.*;
import org.sudrf.parsing.CourtsHTMLPaser;
import org.sudrf.parsing.strategies.ArbitreCoutr;
import org.sudrf.dao.Attributes;
import org.sudrf.dao.Court;
import org.sudrf.util.ExcelExportProvider;

/**
 *
 * @author Kalinin Maksim
 */
public class ArbitreCurts {

    private static final String template = "http://arbitr.ru/as/subj/?id_ac=%1%";

    private static final Logger LOGGER = Logger.getLogger(ArbitreCurts.class.getName());

    public static void main(String[] args) {
        List<String> urlOfSuds = IntStream.rangeClosed(1, 300).boxed()
                .filter(i -> (108 < i && i < 110) && (1 < i && i < 121) || (201 < i && i < 204))
                .map(i -> template.replace("%1%", i.toString())).collect(Collectors.toList());

        List<Court> courts = new CourtsHTMLPaser(ArbitreCoutr::parseDocument).parseCourts(urlOfSuds, ArbitreCurts::report, null);
        print(courts);
    }

    static void print(List<Court> courts) {
        courts.stream()
                .forEach(System.out::println);
    }

    static void saveToExcelFile(List<Court> courts) {

        FileOutputStream fileOutputStream = null;

        try {

            fileOutputStream = new FileOutputStream("C:/Work/temp/demo-sudsInfo.xls");

        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        if (fileOutputStream != null) {

            ExcelExportProvider provider = new ExcelExportProvider("SudsInfo");

            provider.printHeader(Attributes.attributesNamesItterator());

            courts.stream()
                    .sorted(Court::compareTo)
                    .forEach((t) -> {
                        provider.printRow(t.attributesIterator());
                    });

            provider.saveTo(fileOutputStream);
        }
    }

    private static Boolean report(Double t) {
        return Boolean.TRUE;
    }

}
