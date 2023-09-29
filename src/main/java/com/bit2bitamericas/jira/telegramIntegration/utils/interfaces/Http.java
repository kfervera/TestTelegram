package com.bit2bitamericas.jira.telegramIntegration.utils.interfaces;

import org.apache.http.HttpResponse;

import java.io.IOException;

public interface Http {
    String encodeUrl(String value);

    String obtenerHttpResponseString(HttpResponse response) throws IOException;

    int obtenerStatusCode(HttpResponse response) throws IOException;

    StringBuilder buildResponseString(HttpResponse response) throws IOException;
}
