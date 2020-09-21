/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.parsing.strategies.magistrates;

import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.sudrf.dao.Attributes;
import org.sudrf.dao.Attribute;
import org.sudrf.dao.Court;
import org.sudrf.dao.Courts;

/**
 *
 * @author Kalinin Maksim
 */
public class MagistrateCoutr extends Strategy {

    public static void parseDocument(Document doc, Integer regionCode, Map<Court.ID, Attributes.List> allSudsAttribute) {
        Attributes.List sudAttributes = Attributes.List.create();

        StringBuilder title = new StringBuilder();

        switch (regionCode) {
            case 60: {
                sudAttributes.addAll(PskovCourts.atrr(doc, title));
                break;
            }
            case 77: {
                sudAttributes.addAll(MoscowCourts.atrr(doc, title));
                break;
            }
            case 78: {
                sudAttributes.addAll(PiterCourts.atrr(doc, title));
                break;
            }

            default: {
                
                title.append(doc.title());
                for (Element element : doc.getElementsByClass("info-block address-block")) {

                    /*
                <div class="info-block address-block">
                    <h2>Адрес</h2>
                    <p id="court_address">416170, Астраханская область, п. Володарский, пл. Октябрьская, д. 5</p>
                    <p>
                       <a href="http://files.msudrf.ru/69/images/20151228-085746_map.jpg" class="dashed map-enlarge">Схема проезда</a>
                       <span class="slash-separator">/</span>
                       <span class="dashed" id="yandex_link">Яндекс.Карта</span>
                       <script>
                          var coords = [];
                                                                                 coords.push({
                                         latitude   :  46.4064,
                                         longitude  :  48.54280090332031							});

                       </script>
                       <script src="/themes/2.0/js/yandex_map.js"></script>
                    </p>
                    <h2>Email</h2>
                    <p id="court_email"><a href="mailto:vol_su1@astranet.ru">vol_su1@astranet.ru</a></p>
                    <div class="sp10"></div>
                    <h2>Районный суд</h2>
                    <p>Жалобы на решения мирового судьи судебного участка рассматривает &laquo;<a href="http://volodarsky.ast.sudrf.ru" target="_blank" class="nowrap">Володарский районный суд</a>&raquo;.</p>
                 </div>
                     */
                    for (Node node : element.childNodes()) {

                        if (node.attributes().toString().contains("court_address")) {

                            sudAttributes.add(
                                    Attribute.of("Адрес", node.childNode(0).toString()).get());
                        }

                        if (node.attributes().toString().contains("court_email")) {

                            sudAttributes.add(
                                    Attribute.of("Email", node.childNode(0).childNode(0).toString()).get());
                        }

                    }

                }
            }
        }
        String sudName = title.toString();

        if (sudAttributes.isEmpty()) {
            sudAttributes.add(Attribute.error("не распознано содержимое ".concat(doc.location())));
        } else {
        }
        sudAttributes.add(Attribute.of("Сайт", doc.location()).get());
        sudAttributes.add(Attribute.type("Мировой суд"));
        allSudsAttribute.put(Courts.ID.of(doc.location(), regionCode, sudName), sudAttributes);

    }
}
