package com.bit2bitamericas.jira.telegramIntegration.utils.interfaces;

import com.atlassian.jira.user.ApplicationUser;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public interface Generic {
    String getEncodeUserAndPassword(String userAdmin, String password);

    double convertirHorasASegundos(double horas);

    double convertirSegundosAHoras(int segundos);

    List<String> parseStringAList(String string);

    String formatStringDateToJiraDateFormat(String stringDate) throws ParseException;

    String convertDateToString(Date date, String dateformat);

    Date convertStringToDate(String stringDate, String formato) throws ParseException;

    Locale getLocale();

    Locale getLocaleOfUser(ApplicationUser applicationUser);

    Locale getDefaultLocaleJira();

    void setearUsuarioComoUsuarioLogueado(String username);

    String encodeUrl(String value);

    String quitarDecimales(String numero);

    String obtenerUrlBaseDeJira(String api);

    String jsonObjectToString(JSONObject jsonObject);

    Object stringToClass(String string, Class targetClass);

    String objectToJson(Object object);

    JSONObject parseStringToJSONObject(String body);

    String obtenerJsonStringValueByKey(JSONObject json, String key);

    JSONObject obtenerRequestBody(HttpServletRequest request) throws IOException;

    URI obtenerUrl(HttpServletRequest request);

    String encodeFileToBase64Binary(File file) throws IOException;
}
