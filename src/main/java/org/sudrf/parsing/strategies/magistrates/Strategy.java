/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.parsing.strategies.magistrates;

import org.jsoup.nodes.Node;

/**
 *
 * @author Kalinin Maksim
 */
public abstract class Strategy {
    
    protected static Node attrValue(Node node, int... num) {
        Node res = node;
        for (int i : num) {
            res = res.childNode(i);
        }
        return res;
    }
    
}
