/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.parsing.strategies;

import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.sudrf.dao.Attribute;
import org.sudrf.dao.Attributes;
import org.sudrf.dao.Court;
import org.sudrf.dao.Courts;
import org.sudrf.parsing.strategies.magistrates.Strategy;

/**
 *
 * @author Kalinin Maksim
 */
public class ArbitreCoutr extends Strategy {

    private static final String REGIONNAME_SELECTOR = "body > table:nth-child(3) > tbody > tr > td:nth-child(3) > table > tbody > tr > td > table:nth-child(3) > tbody > tr > td:nth-child(2) > h1";

    public static void parseDocument(Document doc, Integer regionCode, Map<Court.ID, Attributes.List> allSudsAttribute) {
        Attributes.List sudAttributes = Attributes.List.create();

        LinkedList<Element> linkedList = new LinkedList<>(doc.select("td"));

        String courtsIndex = "";
        for (Element e : linkedList) {
            final int nextEIndex = 1 + linkedList.indexOf(e);
            if (nextEIndex == linkedList.size()) {
                break;
            }
            final int eIndex = linkedList.indexOf(e);
            String attrname = linkedList.get(eIndex).childNode(0).toString();
            boolean indexValue = false;
            String textValue = null;
            switch (attrname) {
                case "Индекс (код) суда:":
                    indexValue = true;
                case "Адрес:":
                case "Телефон:":
                case "E-mail:":
                case "Сайт:": {
                    textValue = linkedList.get(nextEIndex).childNodes()
                            .stream()
                            .filter(n -> n.childNodeSize() > 0 || n instanceof TextNode)
                            .map(n -> n.childNodeSize() > 0 ? n.childNode(0).toString() : n.toString())
                            .collect(Collectors.joining(" "));
                }
                default: {
                    if (textValue != null) {
                        if (indexValue) {
                            courtsIndex = textValue;
                            break;
                        }
                        sudAttributes.add(
                                Attribute.of(attrname.replace(":", ""), textValue).get());
                    }
                }
            }
        }
        String sudName = doc.getElementsByClass("as_header").text().replace("Арбитражный суд", "AC").concat(" (" + courtsIndex + ")");
        if (sudAttributes.isEmpty()) {
            sudAttributes.add(Attribute.error("не распознано содержимое ".concat(doc.location())));
        } else {
        }

        if (regionCode <= 0) {

            String regionName = doc.
                    select(REGIONNAME_SELECTOR).
                    get(0).
                    childNode(0).
                    toString();
            //Преобразуем
            //Арбитражный суд Ханты-Мансийского автономного округа - Югры
            //В именительный падеж
            //Ханты-Мансийский автономный округ - Югра
            String regionNameNominativeCase = regionName.toLowerCase()
                    .replace(" - ", "-")
                    .replace("санкт-петербурга и ленинградской области", "санкт-петербург")
                    .replace("чувашской республики-чувашии", "чувашская республика-чувашиия")
                    .replace("арбитражный суд ", "")
                    .replace("области", "область")
                    .replace("республики", "республика")
                    .replace("края", "край")
                    .replace("округа", "округ")
                    .replace("города", "город")
                    .replace("югры", "югра")
                    .replace("москвы", "москва")
                    .replace("ного", "ный")
                    .replace("ого ", "ий ")
                    .replace("ии ", "ия ")
                    .replace("ной ", "ная ")
                    .replace("кой ", "кая ");
            regionCode = Attributes.getCodeByRegionName(regionNameNominativeCase);

        }
        sudAttributes.add(Attribute.name(sudName));
        sudAttributes.add(Attribute.region(regionCode));
        sudAttributes.add(Attribute.type("Арбитражный суд"));
        allSudsAttribute.put(Courts.ID.of(doc.location(), regionCode,
                sudName),
                sudAttributes);

    }
}
