package com.kutugondrong.jsonkg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hedy on 1/26/2016.
 */
public class JsonHeader {

    private List<DataHeader> dataHeaders;

    public JsonHeader() {
        this.dataHeaders = new ArrayList<DataHeader>();
    }

    public void setJsonJeader(DataHeader... dataHeaders){
        this.dataHeaders.clear();
        for(int i=0;i<dataHeaders.length;i++){
            this.dataHeaders.add(dataHeaders[i]);
        }
    }

    public List<DataHeader> getHeaders() {
        return dataHeaders;
    }
}
