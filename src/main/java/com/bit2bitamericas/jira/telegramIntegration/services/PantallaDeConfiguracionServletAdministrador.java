package com.bit2bitamericas.jira.telegramIntegration.services;

import com.atlassian.jira.issue.fields.FieldException;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.bit2bitamericas.jira.telegramIntegration.services.interfaces.JiraService;
import com.bit2bitamericas.jira.telegramIntegration.services.interfaces.PantallaConfiguracionServlet;
import com.bit2bitamericas.jira.telegramIntegration.settings.servlets.PantallaAdministradorSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class PantallaDeConfiguracionServletAdministrador implements PantallaConfiguracionServlet {
    private static final Logger log = LoggerFactory.getLogger(PantallaDeConfiguracionServletAdministrador.class);

    private PluginSettingsFactory pluginSettingsFactory;

    public PantallaDeConfiguracionServletAdministrador(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public void guardar(HttpServletRequest request) {
        log.debug("guardar pantalla de configuración global");
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        PantallaAdministradorSettings pantallaAdministradorSettings = new PantallaAdministradorSettings();
        setValoresASettings(request, pantallaAdministradorSettings);
        pantallaAdministradorSettings.mostrarDatosDePantallaDeConfiguracionEnLogDeJira();
        pluginSettings.put(PantallaAdministradorSettings.JIRA_TO_ERICSSON_GLOBAL_SETTINGS, pantallaAdministradorSettings.toJson());
    }

    private void setValoresASettings(HttpServletRequest request, PantallaAdministradorSettings pantallaAdministradorSettings) {
        pantallaAdministradorSettings.setTelegramApiToken(request.getParameter("telegramApiToken"));
        pantallaAdministradorSettings.setProjectKey(request.getParameter("projectKey"));

    }

    @Override
    public Map<String, Object> obtenerCampos(JiraService jiraService) throws FieldException {
        Map<String, Object> context = new HashMap<>();
        log.debug("obtener Campos");
        PantallaAdministradorSettings settings = obtenerGlobalSettings();
        //settings.setPassDeUsuarioSistemaExterno("");
        /*context.put("camposJira", jiraService.obtenerTodosLosCampos());
        context.put("issueEvents", jiraService.obtenerIssueEvents());*/
        context.put("settings", settings);
        settings.mostrarDatosDePantallaDeConfiguracionEnLogDeJira();
        return context;
    }

    @Override
    public PantallaAdministradorSettings obtenerGlobalSettings() {
        log.debug("obteniendo global settings ");
        Object globalSettings = obtenerValorDeCampo(PantallaAdministradorSettings.JIRA_TO_ERICSSON_GLOBAL_SETTINGS);
        log.debug("valor de globalSettings: {}", globalSettings);
        if (globalSettings != null && !globalSettings.toString().isEmpty())
            return PantallaAdministradorSettings.fromJson(String.valueOf(globalSettings));
        return new PantallaAdministradorSettings();
    }

    @Override
    public String obtenerValorDeCampo(String nombre) {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        Object fieldObject = pluginSettings.get(nombre);
        return (fieldObject == null) ? "" : fieldObject.toString();
    }

}
