/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Kalinin Maksim
 */
public interface Attribute {

    public static Optional<Attribute> of(String... record) {
        if ((!record[1].isEmpty()) && (!record[0].isEmpty())) {
            return Optional.of(Attributes.create(record[0], record[1]));
        }
        return Optional.empty();
    }

    public static Attribute type(String region) {
        return Attributes.create(new String[]{"Тип", region});
    }

    public static Attribute region(String region) {
        return Attributes.create(new String[]{"Регион", region});
    }

    public static Attribute region(Integer regionCode) {
        String desc = Attributes.getRegionNameByCode(regionCode);
        return Attributes.create(new String[]{"Регион", desc != null ? desc : regionCode.toString()});
    }

    public static Attribute name(String region) {
        return Attributes.create(new String[]{"Название", region});
    }

    public static Attribute error(String commentary) {
        return Attributes.create(new String[]{"Error", commentary});
    }

    public int getId();

    public String getName();

    public String getValue();

    public void setValue(String value);

}
