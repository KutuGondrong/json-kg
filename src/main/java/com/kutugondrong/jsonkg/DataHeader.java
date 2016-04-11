package com.kutugondrong.jsonkg;

/**
 * Created by hedy on 1/26/2016.
 */
public class DataHeader {

    private String nameHeader;
    private String resutHeader;

    public DataHeader(String nameHeader, String resutHeader) {
        this.nameHeader = nameHeader;
        this.resutHeader = resutHeader;
    }

    public String getNameHeader() {
        return nameHeader;
    }

    public void setNameHeader(String nameHeader) {
        this.nameHeader = nameHeader;
    }

    public String getResutHeader() {
        return resutHeader;
    }

    public void setResutHeader(String resutHeader) {
        this.resutHeader = resutHeader;
    }
}
