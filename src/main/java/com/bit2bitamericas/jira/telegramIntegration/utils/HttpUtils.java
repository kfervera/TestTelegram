package com.bit2bitamericas.jira.telegramIntegration.utils;


import com.bit2bitamericas.jira.telegramIntegration.utils.interfaces.Http;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HttpUtils implements Http {
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    public HttpUtils() {
    }

    @Override
    public String encodeUrl(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            log.debug(ex.toString());
            return value;
        }
    }

    @Override
    public String obtenerHttpResponseString(HttpResponse response) throws IOException {
        log.debug("obtenerHttpResponseString");
        HttpEntity entity = response.getEntity();
        String stringResponse = EntityUtils.toString(entity);
        log.debug("httpResponse: {}", stringResponse);
        return stringResponse;
    }

    @Override
    public int obtenerStatusCode(HttpResponse response) throws IOException {
        log.debug("obtenerStatusCode");
        int statusCode = response.getStatusLine().getStatusCode();
        log.debug("statusCode: {}", statusCode);
        return statusCode;
    }

    @Override
    public StringBuilder buildResponseString(HttpResponse response) throws IOException {
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder responseString = new StringBuilder();
        String line;

        while ((line = bufReader.readLine()) != null) {
            responseString.append(line);
            responseString.append(System.lineSeparator());
        }
        return responseString;
    }
}
