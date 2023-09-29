package com.bit2bitamericas.jira.telegramIntegration.servlets;

import com.atlassian.jira.issue.fields.FieldException;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.bit2bitamericas.jira.telegramIntegration.services.interfaces.JiraService;
import com.bit2bitamericas.jira.telegramIntegration.services.interfaces.PantallaConfiguracionServlet;
import com.bit2bitamericas.jira.telegramIntegration.utils.interfaces.Generic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class PantallaAdministradorSevlet extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(PantallaAdministradorSevlet.class);
    private UserManager userManager;
    private LoginUriProvider loginUriProvider;
    private TemplateRenderer renderer;
    private PantallaConfiguracionServlet pantallaConfiguracionServlet;
    private Generic genericUtils;
    private JiraService jiraService;

    public PantallaAdministradorSevlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer, PantallaConfiguracionServlet pantallaConfiguracionServlet, Generic genericUtils, JiraService jiraService) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.renderer = renderer;
        this.pantallaConfiguracionServlet = pantallaConfiguracionServlet;
        this.genericUtils = genericUtils;
        this.jiraService = jiraService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("do get pantalla de configuración");
        try {
            String userName = userManager.getRemoteUsername(request);
            if (!userManager.isAdmin(userName)) {
                redireccionarAInicioSesion(request, response);
            } else {
                response.setContentType("text/html;charset=utf-8");
                renderer.render("templates/TelegramIntegrationConfiguration.vm", getCampos(), response.getWriter());
            }
        } catch (Exception e) {
            log.error("do get pantalla de configuración", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.debug("do actualizarIncidenteEricsson pantalla de configuración");
        pantallaConfiguracionServlet.guardar(request);
        doGet(request, response);
    }

    private void redireccionarAInicioSesion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(loginUriProvider.getLoginUri(genericUtils.obtenerUrl(request)).toASCIIString());
    }

    public Map<String, Object> getCampos() throws FieldException {
        return pantallaConfiguracionServlet.obtenerCampos(jiraService);
    }
}