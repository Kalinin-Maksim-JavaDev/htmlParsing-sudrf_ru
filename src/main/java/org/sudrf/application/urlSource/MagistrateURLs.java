/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.application.urlSource;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import static org.sudrf.application.urlSource.FileURLs.saveToExcelFile;
import org.sudrf.dao.Court;
import org.sudrf.parsing.CourtsHTMLPaser;
import org.sudrf.parsing.strategies.magistrates.MagistrateCoutr;
import static org.sudrf.util.SSLUtils.enableSSLSocket;
import org.sudrf.util.URLCourt;

/**
 *
 * @author Kalinin Maksim
 */
public class MagistrateURLs {

    private static final Logger LOGGER = getLogger(MagistrateURLs.class.getName());

    private static final String ALLURL = "https://sudrf.ru/index.php?id=300&act=go_ms_search&searchtype=ms&var=true&ms_type=ms&court_subj=%1%&ms_city=&ms_street=";

    public static String parse(File src, File destFile, Function<Double, Boolean> progesssReport, StringBuilder errors) {

        Map<Integer, List<String>> urlOfSuds = new HashMap<>();
        try {
            enableSSLSocket();
        } catch (RuntimeException | NoSuchAlgorithmException | KeyManagementException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            errors.append(ex.getMessage());
        }

        Map<Integer, String> regionsURL = IntStream
                .rangeClosed(1, 100)
                //.filter(e -> (e == 60))//Псков
                //.filter(e -> (e == 77))//Мск
                // .filter(e -> (e != 16))//Татарстан
                //.filter(e -> (e == 60) || (e == 77) || (e == 78))
                .boxed()
                .collect(
                        Collectors.toMap(
                                //https://sudrf.ru не правильные коды регионов:
                                code -> code == 2 ? 4 //04 Республика Алтай
                                        : code == 3 ? 2 //02 Республика Башкортостан
                                                : code == 4 ? 3 //03 Республика Бурятия
                                                        : code,
                                (Integer i) -> ALLURL.replace("%1%", String.format("%02d", i))
                        ));

        for (HashMap.Entry<Integer, String> url : regionsURL.entrySet()) {
            Document doc;
            try {
                doc = Jsoup.connect(url.getValue()).get();
                List<String> regionCurts = doc.getElementsByAttribute("href")
                        .stream()
                        .filter(e -> e.childNodeSize() > 0)
                        .map(e -> e.childNodes().get(0).toString())
                        .filter(st -> st.contains("http"))
                        .distinct()
                        //.limit(10)
                        .collect(Collectors.toList());
                urlOfSuds.put(url.getKey(), regionCurts);
            } catch (IOException ex) {
                Logger.getLogger(MagistrateURLs.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (urlOfSuds.isEmpty()) {
            return "Сайт не разобран: ".concat(ALLURL);
        }

        List<URLCourt> listOfURLCourt
                = urlOfSuds
                        .entrySet()
                        .stream()
                        .flatMap(kv
                                -> kv.getValue()
                                .stream()
                                .collect(ArrayList<URLCourt>::new, (list, e) -> list.add(new URLCourt(e, kv.getKey())), ArrayList::addAll)
                                .stream())
                        .collect(Collectors.toList());

        List<Court> courts = new CourtsHTMLPaser(MagistrateCoutr::parseDocument)
                .parseCourts(listOfURLCourt,
                        progesssReport,
                        24)//<-массив потоков
                .stream()
                .filter(c -> !c.getId().getName().toLowerCase().contains("ДОБРО ПОЖАЛОВАТЬ НА НАШ САЙТ!"))
                .collect(Collectors.toList());

        Optional<File> file = saveToExcelFile(courts, destFile, "Мировые суды", errors);
        return file.isPresent() ? "Записан файл ".concat(file.get().getName()) : "Ошибка записи";
    }
}
