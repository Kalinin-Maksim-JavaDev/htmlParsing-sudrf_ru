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
import org.sudrf.parsing.strategies.CommonCoutr;
import org.sudrf.dao.Attributes;
import org.sudrf.dao.Court;
import org.sudrf.util.ExcelExportProvider;

/**
 *
 * @author Kalinin Maksim
 */
public class CommonCourts {

    private static final Logger LOGGER = Logger.getLogger(CommonCourts.class.getName());

    public static void main(String[] args) {
        List<String> urlOfSuds = Stream.of(
                //"kraevoy.alt"
                //"oblsud.amr"
                //"_oblsud.arh",
                "oblsud.ast",
                "oblsud.sam"
        /*       "oblsud.blg",
                "oblsud.blg",
                "oblsud.brj",
                "oblsud.wld",
                "oblsud.vol",
                "oblsud.vld",
                "oblsud.vrn",
                "2zovs.msk",
                "sankt-peterburgsky.spb",
                "oblsud.lpk",
                "vs.chn"*/
        )
                .map(st -> "http://%1%.sudrf.ru/modules.php?name=sud".replace("%1%", st))
                .collect(Collectors.toList());

        //ParserListUrl.parse(urlOfSuds, Demo::print);
        List<Court> courts = new CourtsHTMLPaser(CommonCoutr::parseDocument).parseCourts(urlOfSuds, CommonCourts::report, null);
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
