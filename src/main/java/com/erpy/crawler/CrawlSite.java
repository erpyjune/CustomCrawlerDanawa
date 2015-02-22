package com.erpy.crawler;

import com.erpy.main.IndexingMain;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by baeonejune on 14. 11. 30..
 */
public class CrawlSite {
    private String crawlUrl;
    private String crawlData;
    private String crawlEncoding="UTF-8"; // UTF-8, EUC-KR
    private int reponseCode;
    private int socketTimeout=1000;
    private int connectionTimeout=1000;
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36";
    private static final String REFERER = "http://www.google.com/";
    private static final String CONNECTION = "keep-alive";

    private static Logger logger = Logger.getLogger(CrawlSite.class.getName());


    /*
        set method.
     */
    public void setCrawlUrl(String url) {
        this.crawlUrl = url;
    }
    public void setCrawlEncode(String type) {
        this.crawlEncoding = type;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /*
            get method.
         */
    public String getCrawlData() {
        return this.crawlData;
    }

    public void setCrawlData(String crawlData) {
        this.crawlData = crawlData;
    }

    public String HttpCrawlGetMethod1() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(this.crawlUrl);
        HttpEntity entity = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line=null;

        httpGet.addHeader("User-Agent", USER_AGENT);
        httpGet.addHeader("Referer", REFERER);
        httpGet.addHeader("Connection", CONNECTION);

        CloseableHttpResponse response = httpClient.execute(httpGet);
        System.out.println(response.getStatusLine());

        try {
            entity = response.getEntity();
            if (entity != null) {
                br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), this.crawlEncoding));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
        } finally {
            if(br!=null) br.close();
            response.close();
        }

        EntityUtils.consume(entity);

        if (sb.length() <= 0) {
            this.crawlData = "";
        } else {
            this.crawlData = sb.toString();
        }

        return this.crawlData;
    }


    public String HttpCrawlGetMethod2() throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(this.crawlUrl);

        request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("Referer", REFERER);
        request.addHeader("Connection", CONNECTION);

        HttpResponse response = client.execute(request);

        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), this.crawlEncoding));

        String line;
        StringBuilder result = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        if (result.length() <= 0) {
            this.crawlData = "";
        } else {
            this.crawlData = result.toString();
        }

        rd.close();

        return this.crawlData;
    }


    public int HttpCrawlGetDataTimeout() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectionTimeout)
                .build();

        HttpGet httpGet = new HttpGet(this.crawlUrl);
        httpGet.addHeader("User-Agent", USER_AGENT);
        httpGet.addHeader("Referer", REFERER);
        httpGet.addHeader("Connection", CONNECTION);

        httpGet.setConfig(requestConfig);
        CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpGet);

        try {
            //HttpEntity httpEntity = closeableHttpResponse.getEntity();
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(closeableHttpResponse.getEntity().getContent(), this.crawlEncoding));

            String line;
            StringBuffer result = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                result.append(line);
                result.append("\n");
            }

            if (result.length() <= 0) {
                this.crawlData = "";
            } else {
                this.crawlData = result.toString();
            }
        } finally {
            closeableHttpResponse.close();
        }

        return closeableHttpResponse.getStatusLine().getStatusCode();
    }


    public void HttpCrawlPostMethod() throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(crawlUrl);
        // add header
        httpPost.addHeader("User-Agent", USER_AGENT);
        httpPost.addHeader("Referer", REFERER);
//        post.setHeader("aaa", "ddd");
//        post.setHeader("User-Agent", this.USER_AGENT);
//        post.setHeader("Referer", this.REFERER);
//        post.setHeader("Content-Length:", "100");

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("mode", "categorymain"));
        urlParameters.add(new BasicNameValuePair("categoryid", "94201"));
        urlParameters.add(new BasicNameValuePair("startnum", "41"));
        urlParameters.add(new BasicNameValuePair("endnum", "200"));

        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        urlParameters.size();

        HttpResponse response = client.execute(httpPost);

        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), this.crawlEncoding));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        if (result.length() <= 0) {
            this.crawlData = "";
        } else {
            this.crawlData = result.toString();
        }

        rd.close();
    }


    public void HttpPostGetData() throws Exception {
        String jsonQueryString = "{\n" +
                "\t\"query\" : {\n" +
                "    \t\"multi_match\": {\n" +
                "        \t\"query\":                \"nike\",\n" +
                "        \t\"type\":                 \"best_fields\", \n" +
                "        \t\"fields\":               [ \"product_name^2\", \"brand_name^1\", \"keyword^1\" ]\n" +
                "    \t}\n" +
                "    }\n" +
                "}";
        HttpPost httpPost = new HttpPost(crawlUrl);
        httpPost.addHeader("User-Agent", USER_AGENT);
        httpPost.addHeader("Referer", REFERER);
        httpPost.setConfig(RequestConfig.custom().
                setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectionTimeout)
                .build());

//        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//        urlParameters.add(new BasicNameValuePair("mode", "categorymain"));
//        urlParameters.add(new BasicNameValuePair("categoryid", "94201"));
//        urlParameters.add(new BasicNameValuePair("startnum", "41"));
//        urlParameters.add(new BasicNameValuePair("endnum", "200"));
//        urlParameters.size();



        logger.info("jsonquery : " + jsonQueryString);
        StringEntity stringEntity = new StringEntity(jsonQueryString);
        httpPost.setEntity(stringEntity);
//        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpPost);

        logger.info("repose code : " + response.getStatusLine().getStatusCode());

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), this.crawlEncoding));

        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        if (result.length() <= 0) {
            this.crawlData = "";
        } else {
            this.crawlData = result.toString();
        }

        bufferedReader.close();
    }



    public int HttpXPUT() throws IOException {

        //String data = "{\"title\" : \"good morning\", \"name\" : \"erpy\", \"date\" : \"20141015\", \"id\" : 123}";
        int responseReturnCode;
        URL url = new URL(crawlUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        //httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setConnectTimeout(5000);
        httpConn.setReadTimeout(5000);
        httpConn.setRequestMethod("POST"); // PUT, DELETE, POST, GET

        OutputStreamWriter osw = new OutputStreamWriter(httpConn.getOutputStream());
        osw.write(crawlData);
        osw.flush();
        osw.close();

        //System.out.println("HTTP Response Code : " + httpConn.getResponseCode());
        responseReturnCode = httpConn.getResponseCode();
        httpConn.disconnect();

        return responseReturnCode;
    }

    public int HttpXGET() throws IOException {
        int responseReturnCode;
        URL url = new URL(this.crawlUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        //httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setConnectTimeout(5000);
        httpConn.setReadTimeout(5000);
        httpConn.setRequestMethod("GET"); // PUT, DELETE, POST, GET

        OutputStreamWriter osw = new OutputStreamWriter(httpConn.getOutputStream());
        osw.write(this.crawlData);
        osw.flush();
        osw.close();

        //System.out.println("HTTP Response Code : " + httpConn.getResponseCode());
        responseReturnCode = httpConn.getResponseCode();
        httpConn.disconnect();

        return responseReturnCode;
    }


    public void HttpXPut3() throws IOException {

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(this.crawlUrl);
        String body = "{\"title\" : \"good morning\", \"name\" : \"erpy\", \"date\" : \"20141015\", \"id\" : 123}";

        // add header
        System.out.println("url:" + this.crawlUrl);
        System.out.println("length :" + String.valueOf(body.length()));


//        post.addHeader("User-Agent", this.USER_AGENT);
//        post.addHeader("Referer", this.REFERER);
//        post.addHeader("Content-Length:", String.valueOf(body.length()));
//        post.setHeader("aaa", "ddd");
//        post.setHeader("User-Agent", this.USER_AGENT);
//        post.setHeader("Referer", this.REFERER);
//        post.setHeader("Content-Length:", "100");

//        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//        urlParameters.add(new BasicNameValuePair("title", "good morning"));
//        urlParameters.add(new BasicNameValuePair("name", "erpy"));
//        urlParameters.add(new BasicNameValuePair("date", "20141230"));
//        urlParameters.add(new BasicNameValuePair("id", "312"));

        //post.setEntity(new UrlEncodedFormEntity(urlParameters));

        post.setEntity(new StringEntity(body));

        HttpResponse response = client.execute(post);

        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), this.crawlEncoding));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        if (result.length() <= 0) {
            this.crawlData = "";
        } else {
            this.crawlData = result.toString();
        }

        rd.close();
    }


    public void HttpCrawlGetBuilder() throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(this.crawlUrl);

        // add request header
        httpGet.addHeader("User-Agent", USER_AGENT);
        HttpResponse httpResponse = httpClient.execute(httpGet);

        System.out.println("Response Code : "
                + httpResponse.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(httpResponse.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
    }
}
