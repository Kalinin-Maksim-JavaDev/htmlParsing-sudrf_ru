/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.parsing;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collector;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.sudrf.parsing.strategies.CommonCoutr;
import org.sudrf.parsing.strategies.ParsingStrategy;
import org.sudrf.dao.Attribute;
import org.sudrf.dao.Attributes;
import org.sudrf.dao.Court;
import org.sudrf.dao.Courts;
import org.sudrf.util.URLCourt;

/**
 *
 * @author Kalinin Maksim
 */
public class CourtsHTMLPaser extends CommonCoutr {

    private static final Logger LOGGER = getLogger(CourtsHTMLPaser.class.getName());

    private final ParsingStrategy strategy;

    public CourtsHTMLPaser(ParsingStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Court> parseCourts(List<String> urlOfSuds,
            Function<Double, Boolean> progesssReport,
            Void ignored) {

        List<URLCourt> list = urlOfSuds.stream()
                .map(e -> new URLCourt(e, -1)).collect(Collectors.toList());

        return parseCourts(list, progesssReport);
    }

    @Deprecated
    public List<Court> parseCourts(
            List<URLCourt> urlOfSuds,
            Function<Double, Boolean> progesssReport) throws IllegalArgumentException {
        return parseCourts(urlOfSuds, progesssReport, 0);

    }

    public List<Court> parseCourts(
            List<URLCourt> urlOfSuds,
            Function<Double, Boolean> progesssReport, int parallelism) throws IllegalArgumentException {

        Stream<URLCourt> stream = urlOfSuds.stream();

        if (parallelism > 0) {
            try {
                Parser parser = new Parser(stream.parallel(), urlOfSuds, progesssReport);
                if (parallelism == 1) {
                    return parser.parse();
                } else {
                    ForkJoinPool customThreadPool = new ForkJoinPool(parallelism);
                    return customThreadPool.submit(parser::parse).get();
                }
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return new Parser(stream, urlOfSuds, progesssReport).parse();
    }

    private class Parser implements Collector<URLCourt, Map<Court.ID, Attributes.List>, Map<Court.ID, Attributes.List>> {

        private Stream<URLCourt> stream;
        private Function<Double, Boolean> progesssReport;
        private AtomicLong counter = new AtomicLong();
        private int totalSize;

        public Parser(Stream<URLCourt> stream, List<URLCourt> urlOfSuds, Function<Double, Boolean> progesssReport) {
            this.stream = stream;
            this.totalSize = urlOfSuds.size();
            this.progesssReport = progesssReport;
        }

        private ArrayList<Court> parse() {
            return stream.collect(this)
                    .entrySet()
                    .stream()
                    .sorted((e1, e2)
                            -> e1.getKey().compareTo(e2.getKey())
                    ).collect(ArrayList<Court>::new,
                            (list, e) -> list.add(Court.of(e.getKey(), e.getValue())),
                            (ArrayList<Court> list, ArrayList<Court> neo) -> list.addAll(neo));
        }

        @Override
        public Supplier<Map<Court.ID, Attributes.List>> supplier() {
            return () -> {
                Map<Court.ID, Attributes.List> allSudsAttribute = new HashMap<>();
                Attributes.setAttributesOrder("Тип", "Регион", "Название", "Error");
                return allSudsAttribute;
            };
        }

        @Override
        public BiConsumer<Map<Court.ID, Attributes.List>, URLCourt> accumulator() {
            return (Map<Court.ID, Attributes.List> allSudsAttribute, URLCourt urlCourt) -> {

                if (progesssReport.apply(Double.valueOf(100 * counter.incrementAndGet() / totalSize))) {

                    String url = urlCourt.getUrl();
                    Integer regionCode = urlCourt.getRegionCode();
                    try {
                        Document doc = null;
                        int tryCounter = 10;
                        String errorMsg = null;
                        //synchronized (this) {
                        while ((doc == null) && (tryCounter > 0)) {
                            try {
                                doc = Jsoup.connect(url).timeout(20000).get();
                            } catch (IllegalArgumentException
                                    | UnknownHostException
                                    | SocketTimeoutException
                                    | ConnectException e) {
                                errorMsg = e.getMessage();
                                tryCounter = 0;
                            } catch (HttpStatusException e) {
                                if (e.getStatusCode() == 404) {
                                    errorMsg = e.getMessage();
                                    tryCounter = 0;
                                } else {
                                    errorMsg = e.getMessage();
                                    tryCounter--;
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException ex) {
                                        LOGGER.log(Level.SEVERE, null, ex);
                                    }
                                }
                            } catch (Exception e) {
                                errorMsg = e.getMessage();

                                if ((errorMsg.toLowerCase().contains("time"))
                                        && (errorMsg.toLowerCase().contains("out"))) {
                                    tryCounter = 0;
                                } else {

                                    tryCounter--;
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException ex) {
                                        LOGGER.log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }
                        if (doc == null) {
                            throw new IllegalStateException(errorMsg != null ? errorMsg : "?");
                        }
                        System.out.println("parsing... ".concat(doc.location()));
                        strategy.parseDocument(doc, regionCode, allSudsAttribute);

                    } 
                    catch (RuntimeException ex) {
                        LOGGER.info("Parsing is failed ".concat(url));
                        Attributes.List errorRow = Attributes.List.create();
                        errorRow.add(Attribute.name(url));
                        errorRow.add(Attribute.region(regionCode));
                        errorRow.add(Attribute.error("bad url:".concat(ex != null ? ex.getMessage() : "unknow run-time error")));
                        allSudsAttribute.merge(Courts.ID.of(url, regionCode, url), errorRow, (exist, neo) -> {
                            exist.addAll(neo);
                            return exist;
                        });

                    }
                }
            };
        }

        @Override
        public BinaryOperator<Map<Court.ID, Attributes.List>> combiner() {
            return (c, n) -> {
                c.putAll(n);
                return c;
            };

        }

        @Override
        public Set<Collector.Characteristics> characteristics() {
            return Arrays.asList(Collector.Characteristics.IDENTITY_FINISH).stream().collect(Collectors.toSet());
        }

        @Override
        public Function<Map<Court.ID, Attributes.List>, Map<Court.ID, Attributes.List>> finisher() {
            return Function.identity();
        }
    }

    public static interface Manager {

        String parse(File srcFile, File destFile, Function<Double, Boolean> reporter, StringBuilder errors);
    }

}
