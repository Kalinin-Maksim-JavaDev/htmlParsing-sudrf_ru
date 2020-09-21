/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.dao;

import java.util.Iterator;

/**
 *
 * @author Kalinin Maksim
 */
public interface Court extends Comparable<Court> {

    public interface ID extends Comparable<Court.ID> {

        public Integer getRegion();

        public String getName();

    }

    public static Court of(Court.ID id, Attributes.List value) {

        return Courts.create(id, value);
    }

    public Court.ID getId();

    public Iterator<Attribute> attributesIterator();
}
