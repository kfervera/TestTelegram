package com.bit2bitamericas.jira.telegramIntegration.settings.servlets;

import com.google.gson.Gson;
import com.opensymphony.workflow.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class PantallaAdministradorSettings {
    public static final String JIRA_TO_ERICSSON_GLOBAL_SETTINGS = "JiraToEricssonGlobalSettings";
    private static final Logger log = LoggerFactory.getLogger(PantallaAdministradorSettings.class);

    private String telegramApiToken;
    private String projectKey;

    public String getTelegramApiToken() {
        return telegramApiToken;
    }

    public void setTelegramApiToken(String telegramApiToken) {
        this.telegramApiToken = telegramApiToken;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public static PantallaAdministradorSettings fromJson(String json) {
        log.trace("fromJson: {}", json);
        return new Gson().fromJson(json, PantallaAdministradorSettings.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public void mostrarDatosDePantallaDeConfiguracionEnLogDeJira() {
        log.debug("Parametros de la clase {}", this.getClass().getCanonicalName());
        log.debug("mostrarDatosDePantallaDeConfiguracion: ");

        log.debug("telegramApiToken: {}", telegramApiToken);
        log.debug("projectKey: {}", projectKey);
    }

    private List<String> obtenerListaDeCampoObligatorios() {
        List<String> camposObligatorios = new ArrayList<>();
        camposObligatorios.add(telegramApiToken);
        camposObligatorios.add(projectKey);
        return camposObligatorios;
    }

    public void validarCamposObligatorios() throws InvalidInputException {
        List<String> listaDeCampoObligatorios = obtenerListaDeCampoObligatorios();
        boolean camposIncorrectos = listaDeCampoObligatorios.stream().anyMatch(campoObligatorio -> campoObligatorio == null || campoObligatorio.isEmpty());
        if (camposIncorrectos)
            throw new InvalidInputException("Pantalla de configuración global no tiene todos los campos obligatorios llenos");
    }

}
