package com.kutugondrong.jsonkg;

/**
 * Created by hedy on 1/25/2016.
 */
public interface InternetConectionListener {
    void onStart();
    void onProgress(Integer... progress);
    void onConectionError(String error);
    void onDone(String result);
}
