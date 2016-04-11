package com.kutugondrong.jsonkg;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Created by hedy on 1/25/2016.
 */
public class GetJsonInternet extends AsyncTask<String,Integer,String> {

    public static final String ERROR_INTERNAL = "Server error";
    public static final String ERROR_TIMEOUT = "Connection timeout";
    public static final String ERROR_CONNECT = "Error connecting to internet";


    private InternetConectionListener connectionListener;
    protected JsonHeader jsonHeader;


    public GetJsonInternet(InternetConectionListener connection) {
        this.connectionListener = connection;
        jsonHeader = new JsonHeader();
    }

    public void setJsonHeader(DataHeader... dataHeaders) {
        this.jsonHeader.setJsonJeader(dataHeaders);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        connectionListener.onStart();
    }

    @Override
    protected String doInBackground(String... strings) {
        String str = new String();
        try{
            URL url = new URL(strings[0]);
            if (strings[0].startsWith("https")) {
                System.out.println("start access: " + url);
                HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
//				        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                        return true;
                    }
                };
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setHostnameVerifier(hostnameVerifier);

                for(int i=0;i<jsonHeader.getHeaders().size();i++){
                    connection.setRequestProperty(jsonHeader.getHeaders().get(i).getNameHeader(),jsonHeader.getHeaders().get(i).getResutHeader());
                }

                connection.setConnectTimeout(20000);
                connection.setDoInput(true);
                connection.connect();
                int responseCode = connection.getResponseCode();
                System.out.println("response code: " + responseCode);
                switch (responseCode) {
                    case HttpURLConnection.HTTP_OK:
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                        StringBuilder strBuilder = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            strBuilder.append(line + "\n");
                        }

                        reader.close();
                        inputStream.close();

                        str = strBuilder.toString();
                        break;

                    case HttpURLConnection.HTTP_INTERNAL_ERROR:
                        str = "error:" + ERROR_INTERNAL;
                        break;

                    case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                        str = "error:" + ERROR_TIMEOUT;
                        break;
                }
            }else{
                System.out.println("start access: " + url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                for(int i=0;i<jsonHeader.getHeaders().size();i++){
                    connection.setRequestProperty(jsonHeader.getHeaders().get(i).getNameHeader(),jsonHeader.getHeaders().get(i).getResutHeader());
                }

                connection.setConnectTimeout(15000);

                connection.setDoInput(true);

                connection.connect();

                int responseCode = connection.getResponseCode();
                System.out.println("response code: " + responseCode);
                switch (responseCode) {
                    case HttpURLConnection.HTTP_OK:
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                        StringBuilder strBuilder = new StringBuilder();

                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            strBuilder.append(line + "\n");
                        }

                        reader.close();
                        inputStream.close();

                        str = strBuilder.toString();
                        break;

                    case HttpURLConnection.HTTP_INTERNAL_ERROR:
                        str = "error:" + ERROR_INTERNAL;
                        break;

                    case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                        str = "error:" + ERROR_TIMEOUT;
                        break;
                }
//                str = getStringJson(strings[0]);

            }
        }catch(IOException e){
            str = "error:" + ERROR_CONNECT;
        }
        return str;
    }

    //For Dev
    private String getStringJson(String url){
        InputStream inputStream = null;
        String result = "";
        System.out.println("start access: " + url);
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private  String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        connectionListener.onProgress(progress);

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.startsWith("error")){
            String errMsg = result.split(":")[1];
            connectionListener.onConectionError(errMsg);
        } else {
            System.out.println("internet access: " + result);
            connectionListener.onDone(result);
        }

    }

    //Example
//    GetJsonInternet json = new GetJsonInternet(new InternetConectionListener() {
//        @Override
//        public void onStart() {
//
//        }
//
//        @Override
//        public void onProgress(Integer progress) {
//            Toast.makeText(EventActivity.this, "Test"+progress,
//                    Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onConectionError(String error) {
//            Toast.makeText(EventActivity.this, error,
//                    Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onDone(String result) {
//            txt_date.setText(result);
//        }
//    });
//    json.execute("https://api.github.com/users/mralexgray/repos");
}
