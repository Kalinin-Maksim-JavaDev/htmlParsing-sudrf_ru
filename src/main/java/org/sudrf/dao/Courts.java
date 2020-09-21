/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.dao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kalinin Maksim
 */
public class Courts extends ArrayList<Court> implements Iterable<Court> {

    private static final Map<String, Court.ID> base = new HashMap<>();

    private Courts() {
    }

    static public List<Court> createList() {
        return new Courts();
    }

    static Court create(Court.ID id, Attributes.List value) {

        return new CourtImpl(id).setAttributes(value);
    }

    public static class ID {

        public static Court.ID of(String url, Integer region, String name) {

            String key = url.concat(":").concat(region.toString()).concat(":").concat(name);
            Court.ID id = base.get(key);

            if (id != null) {
                return id;
            }

            return newID(key, region, name);
        }

        private static Court.ID newID(String key, Integer region, String name) {
            Court.ID id = createID(region, name);
            base.put(key, id);
            return id;
        }

        private static Court.ID createID(Integer region, String name) {
            return new IDimpl(region, name);
        }

    }

    private static class CourtImpl implements Court {

        private Court.ID id;
        private Attributes.List attriutes = Attributes.List.create();

        private CourtImpl(Court.ID id) {
            this.id = id;
            attriutes.add(Attributes.create("Регион", Attributes.getRegionNameByCode(id.getRegion())));
            attriutes.add(Attributes.create("Название", id.getName()));
        }

        @Override
        public Court.ID getId() {
            return id;
        }

        private Court setAttributes(Attributes.List list) {
            attriutes.addAll(list);
            return this;
        }

        @Override
        public Iterator<Attribute> attributesIterator() {
            return attriutes.iterator();
        }

        @Override
        public String toString() {
            return attriutes.toString();
        }

        @Override
        public int compareTo(Court court) {
            return Comparator
                    .comparing(Court::getId)
                    .compare(this, court);
        }
    }

    private static class IDimpl implements Court.ID {

        Integer regionCode;
        String name;

        private IDimpl(Integer regionCode, String name) {
            this.regionCode = regionCode;
            this.name = name;
        }

        @Override
        public Integer getRegion() {
            return regionCode;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int compareTo(Court.ID id) {

            return Comparator
                    .comparing(Court.ID::getRegion)
                    .thenComparing(Court.ID::getName)
                    .compare(this, id);
        }
    }
}
