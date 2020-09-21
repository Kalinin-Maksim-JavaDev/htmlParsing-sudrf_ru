/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.util;

/**
 *
 * @author Kalinin Maksim
 */
public class URLCourt {

    private final String url;
    private final Integer regionCode;

    public URLCourt(String url, Integer regionCode) {
        this.url = url;
        this.regionCode = regionCode;
    }

    public String getUrl() {
        return url;
    }

    public Integer getRegionCode() {
        return regionCode;
    }

    @Override
    public String toString() {
        return "URLCourt{" + "url=" + url + ", regionCode=" + regionCode + '}';
    }

}
