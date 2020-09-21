/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.parsing.strategies.magistrates;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.sudrf.dao.Attributes;
import org.sudrf.dao.Attribute;

/**
 *
 * @author Kalinin Maksim
 */
public class MoscowCourts extends Strategy {

    private static final String SELECTOR_ATTRTABLE = "#detail > tbody > tr:nth-child(3) > td > table > tbody";

    public static Attributes.List atrr(Document doc, StringBuilder title) {
        
        title.append(doc.title().replace(" :: mos-sud.ru", ""));
        
        Attributes.List sudAttributes = Attributes.List.create();

        /*
        <tbody>
            <tr>
               <td valign="top"><strong>Вышестоящий суд:</strong> </td>
               <td valign="top">Нагатинский (районный)</td>
            </tr>
            <tr>
               <td valign="top"><strong>Муниципальный район:</strong> </td>
               <td valign="top">Царицыно</td>
            </tr>
            <tr>
               <td valign="top"><strong>Судебный район:</strong> </td>
               <td valign="top">Нагатинский</td>
            </tr>
            <tr>
               <td valign="top"><strong>Адрес:</strong> </td>
               <td valign="top">115280, Автозаводская ул., д. 17, корп. 2</td>
            </tr>
            <tr>
               <td><b>E-mail:</b> </td>
               <td><a href="mailto://mirsud28@ums-mos.ru">mirsud28@ums-mos.ru</a></td>
            </tr>
            <tr>
               <td valign="top"><strong>ФИО Мирового судьи:</strong> </td>
               <td valign="top">Тужилкина Анастасия Алексеевна</td>
            </tr>
            <tr>
               <td valign="top"><strong>Телефон для справок:</strong> </td>
               <td valign="top">8-499-764-19-36</td>
            </tr>
            <tr>
               <td valign="top"><strong>Телефон судебного участка:</strong> </td>
               <td valign="top">8(495)675-84-48ф</td>
            </tr>
            <tr>
               <td valign="top"><strong>Режим работы:</strong> </td>
               <td valign="top">с понедельника по четверг с 9-00 до 18-00 в пятницу с 9-00 до 16-45</td>
            </tr>
            <tr>
               <td valign="top"><strong>Обед:</strong> </td>
               <td valign="top">с 13-00 до 13-45</td>
            </tr>
            <tr>
               <td valign="top"><strong>Выходные дни:</strong> </td>
               <td valign="top">суббота, воскресенье и праздничные дни</td>
            </tr>
            <tr>
               <td valign="top"><strong>График приема граждан:</strong> </td>
               <td valign="top">понедельник с 14-00 до 18-00, четверг с 9-00 до 13-00</td>
            </tr>
            <tr>
               <td valign="top"><strong>Территориальная подсудность:</strong> </td>
               <td valign="top">Судебный участок N 28 включает в себя территорию, граница которой проходит: по оси Кавказского бул., далее по оси Луганской ул., осям: полос отвода Курского и Павелецкого направлений МЖД до Кавказского бульвара.</td>
            </tr>
         </tbody>
         */
        for (Node node : doc.select(SELECTOR_ATTRTABLE).get(0).childNodes()) {

            if (node.toString().contains("Адрес")) {

                sudAttributes.add(Attribute.of("Адрес", attrValue(node, 1, 0).toString()).get());
            }
            if (node.toString().contains("Телефон")) {

                sudAttributes.add(Attribute.of("Телефон", attrValue(node, 1, 0).toString()).get());
            }
            if (node.toString().contains("E-mail")) {

                sudAttributes.add(Attribute.of("E-mail", attrValue(node, 1, 0).childNode(0).toString()).get());
            }
        }

        return sudAttributes;
    }

}
