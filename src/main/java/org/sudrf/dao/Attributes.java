/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jsoup.nodes.Node;

/**
 *
 * @author Kalinin Maksim
 */
public class Attributes {

    private static final Map<Integer, String> regionsCodes = new HashMap<Integer, String>();

    static {
        regionsCodes.put(1, "Республика Адыгея (Адыгея)");
        regionsCodes.put(2, "Республика Башкортостан");
        regionsCodes.put(3, "Республика Бурятия");
        regionsCodes.put(4, "Республика Алтай");
        regionsCodes.put(5, "Республика Дагестан");
        regionsCodes.put(6, "Республика Ингушетия");
        regionsCodes.put(7, "Кабардино-Балкарская Республика");
        regionsCodes.put(8, "Республика Калмыкия");
        regionsCodes.put(9, "Карачаево-Черкесская Республика");
        regionsCodes.put(10, "Республика Карелия");
        regionsCodes.put(11, "Республика Коми");
        regionsCodes.put(12, "Республика Марий Эл");
        regionsCodes.put(13, "Республика Мордовия");
        regionsCodes.put(14, "Республика Саха (Якутия)");
        regionsCodes.put(15, "Республика Северная Осетия-Алания");
        regionsCodes.put(16, "Республика Татарстан");
        regionsCodes.put(17, "Республика Тыва");
        regionsCodes.put(18, "Удмуртская Республика");
        regionsCodes.put(19, "Республика Хакасия");
        regionsCodes.put(20, "Чеченская Республика");
        regionsCodes.put(21, "Чувашская Республика-Чувашия");
        regionsCodes.put(22, "Алтайский край");
        regionsCodes.put(23, "Краснодарский край");
        regionsCodes.put(24, "Красноярский край");
        regionsCodes.put(25, "Приморский край");
        regionsCodes.put(26, "Ставропольский край");
        regionsCodes.put(27, "Хабаровский край");
        regionsCodes.put(28, "Амурская область");
        regionsCodes.put(29, "Архангельская область");
        regionsCodes.put(30, "Астраханская область");
        regionsCodes.put(31, "Белгородская область");
        regionsCodes.put(32, "Брянская область");
        regionsCodes.put(33, "Владимирская область");
        regionsCodes.put(34, "Волгоградская область");
        regionsCodes.put(35, "Вологодская область");
        regionsCodes.put(36, "Воронежская область");
        regionsCodes.put(37, "Ивановская область");
        regionsCodes.put(38, "Иркутская область");
        regionsCodes.put(39, "Калининградская область");
        regionsCodes.put(40, "Калужская область");
        regionsCodes.put(41, "Камчатский край");
        regionsCodes.put(42, "Кемеровская область");
        regionsCodes.put(43, "Кировская область");
        regionsCodes.put(44, "Костромская область");
        regionsCodes.put(45, "Курганская область");
        regionsCodes.put(46, "Курская область");
        regionsCodes.put(47, "Ленинградская область");
        regionsCodes.put(48, "Липецкая область");
        regionsCodes.put(49, "Магаданская область");
        regionsCodes.put(50, "Московская область");
        regionsCodes.put(51, "Мурманская область");
        regionsCodes.put(52, "Нижегородская область");
        regionsCodes.put(53, "Новгородская область");
        regionsCodes.put(54, "Новосибирская область");
        regionsCodes.put(55, "Омская область");
        regionsCodes.put(56, "Оренбургская область");
        regionsCodes.put(57, "Орловская область");
        regionsCodes.put(58, "Пензенская область");
        regionsCodes.put(59, "Пермский край");
        regionsCodes.put(60, "Псковская область");
        regionsCodes.put(61, "Ростовская область");
        regionsCodes.put(62, "Рязанская область");
        regionsCodes.put(63, "Самарская область");
        regionsCodes.put(64, "Саратовская область");
        regionsCodes.put(65, "Сахалинская область");
        regionsCodes.put(66, "Свердловская область");
        regionsCodes.put(67, "Смоленская область");
        regionsCodes.put(68, "Тамбовская область");
        regionsCodes.put(69, "Тверская область");
        regionsCodes.put(70, "Томская область");
        regionsCodes.put(71, "Тульская область");
        regionsCodes.put(72, "Тюменская область");
        regionsCodes.put(73, "Ульяновская область");
        regionsCodes.put(74, "Челябинская область");
        regionsCodes.put(75, "Забайкальский край");
        regionsCodes.put(76, "Ярославская область");
        regionsCodes.put(77, "Город Москва");
        regionsCodes.put(78, "Город Санкт-Петербург");
        regionsCodes.put(79, "Еврейская автономная область");
        regionsCodes.put(83, "Ненецкий автономный округ");
        regionsCodes.put(86, "Ханты-Мансийский автономный округ-Югра");
        regionsCodes.put(87, "Чукотский автономный округ");
        regionsCodes.put(89, "Ямало-Ненецкий автономный округ");
        regionsCodes.put(91, "Республика Крым");
        regionsCodes.put(92, "Севастополь");
        regionsCodes.put(99, "Иные территории, включая город и космодром Байконур");

        regionsCodes.put(9901, "Волго-Вятский округ");
        regionsCodes.put(9902, "Восточно-Сибирский округ");
        regionsCodes.put(9903, "Дальневосточный округ");
        regionsCodes.put(9904, "Еврейская автономная область");
        regionsCodes.put(9905, "Западно-Сибирский округ");
        regionsCodes.put(9906, "Московский округ");
        regionsCodes.put(9907, "Поволжский округ");
        regionsCodes.put(9908, "Республика Адыгея");
        regionsCodes.put(9909, "Северо-Западный округ");
        regionsCodes.put(9910, "Северо-Кавказский округ");
        regionsCodes.put(9911, "Уральский округ");
        regionsCodes.put(9912, "Центральный округ");
        regionsCodes.put(9914, "Город Севастополь");
    }

    public static String getRegionNameByCode(Integer regionCode) {

        String st = regionsCodes.get(regionCode);
        if (st != null) {
            return st;
        }
        return "unknow";
    }

    public static Integer getCodeByRegionName(String name) {
        return regionsCodes.entrySet()
                .stream()
                .filter(kv -> kv
                .getValue()
                .toLowerCase()
                .equals(name.toLowerCase()))
                .map(kv -> kv.getKey())
                .findFirst().orElse(-1);
    }

    // Suppresses default constructor, ensuring non-instantiability.
    private Attributes() {
    }

    public static void setAttributesOrder(String... names) {

        for (String name : names) {
            AttributeImpl.castAndAddNameId(name);
        }
    }

    public static Attribute create(String... keyValue) {
        return new AttributeImpl(keyValue);
    }

    public static Stream<String> asString(Map.Entry<String, Attributes.List> entry) {

        return Stream.of(entry.getKey(), entry.getValue().toString());
    }

    public interface List extends Collection<Attribute> {

        public static Attributes.List create() {
            return new Attributes.ListImpl();
        }

        public String getByName(String region);

    }

    public static Iterator<Map.Entry<String, Integer>> attributesNamesItterator() {
        return AttributeImpl.namesId.entrySet().iterator();
    }

    private static class ListImpl extends ArrayList<Attribute> implements List {

        public boolean add(Attribute e) {
            Optional<Attribute> c = stream()
                    .filter(a -> a.getName().equals(e.getName())).findFirst();

            if (c.isPresent()) {

                c.get().setValue(c.get().getValue().concat(", ").concat(e.getValue()));

                return true;
            }

            return super.add(e);
        }

        @Override
        public String toString() {
            return stream().map(Attribute::toString).collect(Collectors.joining("],[", "[", "]"));
        }

        @Override
        public String getByName(String name) {
            return stream().filter((t) -> {
                return name.equals(t.getName());
            }).findFirst().orElseThrow(() -> new NullPointerException()).getValue();
        }
    }

    private static class AttributeImpl implements Attribute {

        static private final Map<String, Integer> namesId = new HashMap<>();

        private final String name;
        private String value;

        private AttributeImpl(String... src) {
            this.name = validName(src[0]);
            this.value = src[1];

            addNameId(this.name);
        }

        static String validName(String name) {
            return name
                    .toLowerCase()
                    .replace(":", "")
                    .replace("-", "")
                    .replace("официальный", "")
                    .trim();

        }

        static private void castAndAddNameId(String name) {
            addNameId(validName(name));
        }

        static private void addNameId(String name) {
            String nameValid = validName(name);
            if (!namesId.containsKey(nameValid)) {
                synchronized (namesId) {
                    if (!namesId.containsKey(nameValid)) {
                        namesId.put(nameValid, namesId
                                .values()
                                .stream()
                                .max(Integer::compare).map(e -> e + 1)
                                .orElse(0));
                    }
                }
            }
        }

        @Override
        public int getId() {
            return namesId.get(name);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return name.concat(": ").concat(value);
        }
    }
}
