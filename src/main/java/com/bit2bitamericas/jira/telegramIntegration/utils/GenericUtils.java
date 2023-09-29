package com.bit2bitamericas.jira.telegramIntegration.utils;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.preferences.PreferenceKeys;
import com.bit2bitamericas.jira.telegramIntegration.utils.interfaces.Generic;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.LocaleUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GenericUtils implements Generic {
    private static final Logger log = LoggerFactory.getLogger(GenericUtils.class);

    @Override
    public String getEncodeUserAndPassword(String userAdmin, String password) {
        String username = userAdmin;
        String pass = password;
        Base64 base64 = new Base64();
        String user = String.format("%s:%s", username, pass);
        String encoded = base64.encodeToString(user.getBytes());
        return encoded.replaceAll("\\s+", "");
    }

    @Override
    public double convertirHorasASegundos(double horas) {
        return horas * 3600;
    }

    @Override
    public double convertirSegundosAHoras(int segundos) {
        return segundos / 3600.0;
    }

    @Override
    public List<String> parseStringAList(String string) {
        return new ArrayList<>(Arrays.asList(string.split(",")));
    }

    @Override
    public String formatStringDateToJiraDateFormat(String stringDate) throws ParseException {
        Date date = convertStringToDate(stringDate.substring(0, 10), "yyyy-MM-dd");
        String formatDateJira = ComponentAccessor.getApplicationProperties().getDefaultBackedString(APKeys.JIRA_LF_DATE_DMY);
        return convertDateToString(date, formatDateJira);
    }

    @Override
    public String convertDateToString(Date date, String dateformat) {
        return new SimpleDateFormat(dateformat, getLocale()).format(date);
    }

    @Override
    public Date convertStringToDate(String stringDate, String formato) throws ParseException {
        return new SimpleDateFormat(formato).parse(stringDate);
    }

    @Override
    public Locale getLocale() {
        ApplicationUser applicationUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        Locale locale = getLocaleOfUser(applicationUser);
        if (locale == null) {
            locale = getDefaultLocaleJira();
        }
        return locale;
    }

    @Override
    public Locale getLocaleOfUser(ApplicationUser applicationUser) {
        String userLocale = ComponentAccessor.getUserPreferencesManager().getExtendedPreferences(applicationUser).getString(PreferenceKeys.USER_LOCALE);
        return LocaleUtils.toLocale(userLocale);
    }

    @Override
    public Locale getDefaultLocaleJira() {
        String jiraDefaultLocale = ComponentAccessor.getApplicationProperties().getDefaultLocale().toString();
        return LocaleUtils.toLocale(jiraDefaultLocale);
    }

    @Override
    public void setearUsuarioComoUsuarioLogueado(String username) {
        JiraAuthenticationContext authContext = ComponentAccessor.getJiraAuthenticationContext();
        log.debug("impersonar a usuario de pantalla de configuracion: {}", username);
        ApplicationUser adminApplicationUser = ComponentAccessor.getUserUtil().getUserByName(username);
        authContext.setLoggedInUser(adminApplicationUser);
    }

    @Override
    public String encodeUrl(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            log.error("error encode value ", ex);
            return value;
        }
    }

    @Override
    public String quitarDecimales(String numero) {
        if (numero.contains(".")) {
            int i = numero.indexOf(".");
            numero = numero.substring(0, i);
        }
        return numero;
    }

    @Override
    public String obtenerUrlBaseDeJira(String api) {
        return ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + api;
    }

    @Override
    public String jsonObjectToString(JSONObject jsonObject) {
        log.debug("JSONObject to String");
        log.debug("JSONObject: {}", jsonObject);
        String json = new Gson().toJson(jsonObject);
        log.debug("String: {}", json);
        return json;
    }

    @Override
    public Object stringToClass(String string, Class targetClass) {
        log.debug("String: {} to: {}", string, targetClass);
        return new Gson().fromJson(string, targetClass);
    }

    @Override
    public String objectToJson(Object object) {
        return new Gson().toJson(object);
    }

    @Override
    public JSONObject parseStringToJSONObject(String json) {
        JSONObject jsonObject = new JSONObject();
        try {
            log.debug("json: {}", json);
            jsonObject = new JSONObject(json);
        } catch (Exception e) {
            log.error(String.format("NO es un json: %s", json), e);
        }
        return jsonObject;
    }

    @Override
    public String obtenerJsonStringValueByKey(JSONObject json, String key) {
        String value = json.getString(key);
        log.debug("key: {} value: {}", key, value);
        return value;
    }

    @Override
    public JSONObject obtenerRequestBody(HttpServletRequest request) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        return parseStringToJSONObject(body);
    }

    @Override
    public URI obtenerUrl(HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }

    @Override
    public String encodeFileToBase64Binary(File file) throws IOException {
        log.debug("encodeFileToBase64Binary");
        byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
        return new String(encoded, StandardCharsets.US_ASCII);
    }
}
