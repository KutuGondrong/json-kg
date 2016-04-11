package com.kutugondrong.jsonkg;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by hedy on 1/27/2016.
 */
public class PostJsonInternet extends GetJsonInternet{

    private ArrayList<NameValuePair> pairs;


    public PostJsonInternet(InternetConectionListener connection) {
        super(connection);
    }

    public void addPair(String key, String value){
        if (pairs == null) {
            pairs = new ArrayList<NameValuePair>();
        }
        if (value!=null) {
            pairs.add(new BasicNameValuePair(key, value));
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String str = new String();
        try {
            if (strings[0].startsWith("https")) {
                HttpParams httpParameters = new BasicHttpParams();

                HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

                HttpClient httpclient = new DefaultHttpClient();

                SchemeRegistry registry = new SchemeRegistry();
                SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
                socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
                registry.register(new Scheme("https", socketFactory, 443));
                SingleClientConnManager mgr = new SingleClientConnManager(httpclient.getParams(), registry);
                DefaultHttpClient httpClient = new DefaultHttpClient(mgr, httpclient.getParams());

                // Set verifier
                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
                HttpPost httppost = new HttpPost(strings[0]);

                if(null != pairs)httppost.setEntity(new UrlEncodedFormEntity(pairs));

                int timeoutConnection = 120000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutConnection);
                httppost.setParams(httpParameters);

                for(int i=0;i<jsonHeader.getHeaders().size();i++){
                    httppost.addHeader(jsonHeader.getHeaders().get(i).getNameHeader(), jsonHeader.getHeaders().get(i).getResutHeader());
                }

                HttpResponse response = httpClient.execute(httppost);

                InputStream is = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                String line="0";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                str = sb.toString().trim();
            } else if (strings[0].startsWith("http")) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(strings[0]);

                if(null != pairs)httppost.setEntity(new UrlEncodedFormEntity(pairs));

                @SuppressWarnings("unused")
                int timeoutConnection = 120000;

                for(int i=0;i<jsonHeader.getHeaders().size();i++){
                    httppost.addHeader(jsonHeader.getHeaders().get(i).getNameHeader(),jsonHeader.getHeaders().get(i).getResutHeader());
                }

                HttpResponse response = httpclient.execute(httppost);

                InputStream is = response.getEntity().getContent();
//			int length = (int) response.getEntity().getContentLength();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                String line="0";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                str = sb.toString().trim();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

}
