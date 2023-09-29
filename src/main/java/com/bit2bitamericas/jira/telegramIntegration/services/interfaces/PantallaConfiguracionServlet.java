package com.bit2bitamericas.jira.telegramIntegration.services.interfaces;

import com.atlassian.jira.issue.fields.FieldException;
import com.bit2bitamericas.jira.telegramIntegration.settings.servlets.PantallaAdministradorSettings;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface PantallaConfiguracionServlet {
    String obtenerValorDeCampo(String nombre);

    void guardar(HttpServletRequest request);

    Map<String, Object> obtenerCampos(JiraService jiraService) throws FieldException;

    PantallaAdministradorSettings obtenerGlobalSettings();
}
