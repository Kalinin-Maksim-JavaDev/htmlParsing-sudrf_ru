/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.parsing.strategies.magistrates;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.sudrf.dao.Attributes;
import org.sudrf.dao.Attribute;

/**
 *
 * @author Kalinin Maksim
 */
public class PskovCourts extends Strategy {
    
    private static final String SELECTOR_NAME = "#container > div.content > table > tbody > tr > td:nth-child(2) > div.content_text > table > tbody > tr:nth-child(1) > td > h1";
    private static final String SELECTOR_ATTRTABLE = "#container > div.content > table > tbody > tr > td:nth-child(2) > div.content_text > table > tbody";
    
    public static Attributes.List atrr(Document doc, StringBuilder title) {
        
        title.append(doc.select(SELECTOR_NAME).get(0).childNode(0).toString());
        
        Attributes.List sudAttributes = Attributes.List.create();
        
        /*
        <tbody>
            <tr>
               <td>
                  <h1>Судебный участок № 1 Бежаницкого района</h1>
               </td>
            </tr>
            <tr></tr>
            <tr>
               <td>
                  <a href="/images/su_bejanicy.jpg" target="_blank"><img src="/images/su_bejanicy_small.jpg">
                  </a>
                  <br><strong>Мировой судья:</strong> Фомина Ольга Михайловна
               </td>
               <td rowspan="3"></td>
            </tr>
            <tr>
               <td><br><strong>Адрес:</strong> 182840, Бежаницы, ул. Советская, 12</td>
            </tr>
            <tr>
               <td><br><strong>Телефон:</strong> (81141) 2-23-69</td>
            </tr>
            <tr>
               <td><br><strong>E-mail:</strong> <a href="mailto:su1@mirsud.pskov.ru">su1@mirsud.pskov.ru</a></td>
            </tr>   
         </tbody>
        */
        for (Node node : doc.select(SELECTOR_ATTRTABLE).get(0).childNodes()) {

            if (node.toString().contains("Адрес")) {

                sudAttributes.add(
                        Attribute.of("Адрес", attrValue(node,1,2).toString()).get());
            }
            if (node.toString().contains("Телефон")) {

                sudAttributes.add(
                        Attribute.of("Телефон", attrValue(node,1,2).toString()).get());
            }
            if (node.toString().contains("E-mail")) {

                sudAttributes.add(
                        Attribute.of("E-mail", attrValue(node,1,3,0).toString()).get());
            }
        }

        return sudAttributes;

    }
    

}
