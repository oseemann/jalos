/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.oebs.jalos;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.oebs.jalos.handler.SubmitResponseObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Client {

    private CloseableHttpClient httpClient;
    private String serviceUrl;

    Client(String serviceUrl) {
        httpClient = HttpClients.createMinimal();
        this.serviceUrl = serviceUrl;
    }

    public HttpResponse xget(String url) throws IOException {
        HttpGet httpGet = new HttpGet(this.serviceUrl + url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        return response;
    }

    public String get(long id) throws IOException {
        HttpGet httpGet = new HttpGet(this.serviceUrl + "/a/" + id);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String ret = response.getLastHeader("Location").toString();
        EntityUtils.consume(entity);
        response.close();
        return ret;
    }

    public SubmitResponseObject submit(String postUrl) throws IOException {
        HttpPost httpPost = new HttpPost(this.serviceUrl + "/a/submit");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("url", postUrl));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();

        ObjectMapper mapper = new ObjectMapper();
        SubmitResponseObject sro = mapper.readValue(entity.getContent(), SubmitResponseObject.class);

        EntityUtils.consume(entity);
        response.close();
        return sro;
    }
}
