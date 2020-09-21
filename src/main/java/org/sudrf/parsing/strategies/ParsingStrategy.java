/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.parsing.strategies;

import java.util.Map;
import org.jsoup.nodes.Document;
import org.sudrf.dao.Attributes;
import org.sudrf.dao.Court;

/**
 *
 * @author Kalinin Maksim
 */
public interface ParsingStrategy {

    void parseDocument(Document doc, Integer regionCode, Map<Court.ID, Attributes.List> allSudsAttribute);

}
