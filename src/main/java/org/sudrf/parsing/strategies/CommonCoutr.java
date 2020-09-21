/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.parsing.strategies;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.sudrf.dao.Attribute;
import org.sudrf.dao.Attributes;
import org.sudrf.dao.Court;
import org.sudrf.dao.Courts;
import org.sudrf.parsing.strategies.magistrates.Strategy;

/**
 *
 * @author Kalinin Maksim
 */
public class CommonCoutr extends Strategy {

    private static final Logger LOGGER = Logger.getLogger(CommonCoutr.class.getName());

    static public void parseDocument(Document doc, Integer regionName, Map<Court.ID, Attributes.List> allSudsAttribute) {
        Elements sudlistBorder = doc.body().getElementsByClass("sudlistBorder");
        for (Element sudLine : sudlistBorder) {
            List<Node> ulSudInfo = sudLine.childNodes();
            String sudName
                    = //sudlistBorder
                    //mapSubtree
                    //mapSubtreeCont
                    //mapSubtreeToggle
                    sudLine.parentNode().parentNode() //sudlistBorder
                            .parentNode() //mapSubtree
                            .parentNode() //mapSubtreeCont
                            .childNode(0) //mapSubtreeToggle
                            .childNode(0).toString();
            /*
            <td class='mapSubtreeCont'><a class='mapSubtreeToggle' href='#'>Алтайский краевой суд</a>
            <table class='mapSubtree'>
            <tr>
            <td class='sudlistBorder'>
            <ul id='ulSudInfo'>
            <li>Адрес: 656043, г. Барнаул, пр-т. Ленина, д. 25</li>
            <li>Телефон: (3852) 63-72-90, 63-83-14 (ф.)</li>
            <li>E-mail: <a href='mailto:kraevoy.alt@sudrf.ru'>kraevoy.alt@sudrf.ru</a></li>
            <li>Официальный сайт: <a href='http://kraevoy.alt.sudrf.ru' target='_blank'>http://kraevoy.alt.sudrf.ru</a></li>
            </ul></td>
            </tr>
            </table></td>
             */
            for (Node node : ulSudInfo) {
                Attributes.List sudAttributes = node.childNodes()
                        .stream()
                        .filter(ch -> "li".equals(ch.nodeName()))
                        .collect(Attributes.List::create, CommonCoutr::append, Attributes.List::addAll);

                if (sudAttributes.isEmpty()) {
                    sudAttributes.add(Attribute.error("не распознано содержимое ".concat(doc.location())));
                } else {
                    sudAttributes.add(Attribute.name(sudName)); //Алтайский краевой суд
                    sudAttributes.add(Attribute.region(regionName));
                }
                sudAttributes.add(Attribute.type("Суд общей юрисдикции"));
                allSudsAttribute.put(Courts.ID.of(doc.location(), regionName, sudName), sudAttributes);
            }
        }
    }

    static public void append(Attributes.List list, Node node) {
        if (node.childNodes().size() == 1) {
            /*<li>Адрес: 656043, г. Барнаул, пр-т. Ленина, д. 25</li>*/
            list.add(
                    Attribute.of(node.childNode(0).toString().split(":")).get()
            );
            return;
        }
        /*<li>E-mail: <a href='mailto:kraevoy.alt@sudrf.ru'>kraevoy.alt@sudrf.ru</a></li>*/
        try {
            list.add(Attribute.of(node.childNode(0).toString(), node.childNode(1).childNode(0).toString()).get());
        } catch (IndexOutOfBoundsException e) {
            LOGGER.log(Level.INFO, "Could''t parse element {0}", node);
        } finally {
        }
    }
}
