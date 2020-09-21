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
public class PiterCourts extends Strategy {

    private static final String SELECTOR_ATTRTABLE = "body > div.inner-wrapper > main > section.sector-info > div > div > div:nth-child(1) > div";

    public static Attributes.List atrr(Document doc, StringBuilder title) {

        title.append(doc.title());
        
        Attributes.List sudAttributes = Attributes.List.create();

        /*
        <div class="adress-info">
            <div class="row">
               <div class="col-lg-12">
                  <div class="adress-fact">
                     <span>Адрес фактический</span>
                     <p>190068, Санкт-Петербург, ул. Садовая, д. 55-57, литера А</p>
                  </div>
               </div>
            </div>
            <div class="row">
               <div class="col-lg-7">
                  <div class="row">
                     <div class="col-lg-12">
                        <div class="telfax">
                           <span>Телефон/факс</span>
                           <p>8 (812) 246-26-37</p>
                        </div>
                     </div>
                     <div class="col-lg-12">
                        <div class="reseption">
                           <span>Прием граждан</span>
                           <p>
                              Понедельник: с 14-00 до 17-00
                              <br>
                              Четверг: с 10-00 до 13-00
                           </p>
                        </div>
                     </div>
                  </div>
               </div>
               <div class="col-lg-5">
                  <div class="open-hours">
                     <span>Часы работы</span>
                     <table>
                        <tbody>
                           <tr>
                              <td>Понедельник-четверг: c 9-00 до 18-00</td>
                           </tr>
                           <tr>
                              <td>Пятница: до 17-00; Обед: с 13-00 до  14-00</td>
                           </tr>
                        </tbody>
                     </table>
                  </div>
               </div>
            </div>
            <div class="row">
               <div class="col-lg-6">
                  <div class="reseption">
                     <span>Электронный адрес</span>
                     <a class="link__mail" href="mailto:ss4@mirsud.spb.ru">
                     ss4@mirsud.spb.ru
                     </a>
                  </div>
               </div>
            </div>
            <div class="row">
               <div class="col-lg-6">
                  <a href="#" class="send-button" data-toggle="modal" data-target=".appeal-sector">Отправить обращение</a>
               </div>
            </div>
         </div>
         */
        for (Node node : doc.select(SELECTOR_ATTRTABLE).get(0).childNodes()) {

            if (node.toString().contains("Адрес фактический")) {

                sudAttributes.add(
                        Attribute.of("Адрес", attrValue(node, 1, 1, 3, 0).toString()).get());
            }
            if (node.toString().contains("Телефон")) {

                sudAttributes.add(
                        Attribute.of("Телефон", attrValue(node, 1, 1, 1, 1, 3, 0).toString()).get());
            }
            if (node.toString().contains("Электронный адрес")) {

                sudAttributes.add(
                        Attribute.of("E-mail", attrValue(node, 1, 1, 3, 0).toString()).get());
            }
        }

        return sudAttributes;

    }

}
