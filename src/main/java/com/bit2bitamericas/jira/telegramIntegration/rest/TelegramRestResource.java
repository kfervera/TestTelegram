package com.bit2bitamericas.jira.telegramIntegration.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.user.UserManager;
import com.bit2bitamericas.jira.telegramIntegration.activeObjects.interfaces.ChatState;
import com.bit2bitamericas.jira.telegramIntegration.services.interfaces.ITelegramApiService;
import com.bit2bitamericas.jira.telegramIntegration.services.interfaces.JiraService;
import com.bit2bitamericas.jira.telegramIntegration.services.interfaces.PantallaConfiguracionServlet;
import com.bit2bitamericas.jira.telegramIntegration.settings.servlets.PantallaAdministradorSettings;
import net.java.ao.Query;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.util.json.JSONObject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import com.bit2bitamericas.jira.telegramIntegration.models.*;

@Path("/")
public class TelegramRestResource {
    private static final Logger log = LoggerFactory.getLogger(TelegramRestResource.class);
    private final ActiveObjects activeObjects;
    private final ITelegramApiService iTelegramApiService;
    private final PantallaConfiguracionServlet pantallaConfiguracionServlet;
    private final JiraService jiraService;
    private final UserManager userManager;
    public TelegramRestResource(ActiveObjects activeObjects,
                                ITelegramApiService iTelegramApiService,
                                PantallaConfiguracionServlet pantallaConfiguracionServlet,
                                JiraService jiraService, UserManager userManager) {
        this.activeObjects = activeObjects;
        this.iTelegramApiService = iTelegramApiService;
        this.pantallaConfiguracionServlet = pantallaConfiguracionServlet;
        this.jiraService = jiraService;
        this.userManager = userManager;

        int count = 0;
        boolean isInitialized = false;
        while (!isInitialized && count < 10) {
            try {
                activeObjects.moduleMetaData().awaitInitialization(10, TimeUnit.SECONDS);
                isInitialized = activeObjects.moduleMetaData().isInitialized();
            } catch (Exception ex) {
                log.error("INITALIZATION "+ count);
                count++;
            }
        }
    }

    @POST
    @Path("/action")
    @AnonymousAllowed
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response execute(HashMap<String, Object> body)
    {
        log.error("BODY: " + body.toString());
        try {
            HashMap<String, Object> chat = null;
            if(body.containsKey("callback_query"))
                chat = getChat((HashMap<String, Object>)body.get("callback_query"));
            else
                chat = getChat(body);
            ChatState state = getChatState(chat.get("id").toString());
            if(state == null)
                startInteraction(body);
            else {
                continueInteraction(body, state);
            }

        }catch (Exception e){
            HashMap<String, Object> chat = null;
            chat = getChat(body);
            ChatState state = getChatState(chat.get("id").toString());
            activeObjects.delete(state);
            log.error("ERROR EN REST API", e);
            log.error(e.getMessage());
        }
        return Response.ok(new TelegramRestResourceModel("Hello World")).build();
    }

    private HashMap<String, Object> getChat(HashMap<String, Object> requestBody){
        HashMap<String, Object> message = getMessage(requestBody);
        if (message == null) return null;
        if(!message.containsKey("chat"))
            return null;
        return  (HashMap<String, Object>) message.get("chat");
    }

    private HashMap<String, Object> getMessage(HashMap<String, Object> requestBody) {
        if(!requestBody.containsKey("message"))
            return null;
        HashMap<String, Object> message = (HashMap<String, Object>) requestBody.get("message");
        return message;
    }

    private ChatState getChatState(String chatId){
        String columns = "ID,BODY,STATUS,CHAT_ID";
        ChatState[] items = activeObjects.find(ChatState.class, Query.select(columns).where("CHAT_ID = ?", chatId).order("ID DESC").limit(1));
        if(items.length == 0)
            return null;
        return items[0];
    }

    private void startInteraction(HashMap<String, Object> requestBody) throws Exception{
        HashMap<String, Object> chat = getChat(requestBody);
        iTelegramApiService.sendMessage(chat.get("id"),
                "Hola " + chat.get("first_name") + ", en que puedo ayudarte?",
                Collections.singletonList("Consultar Issue"));
        iTelegramApiService.SetChatState(chat.get("id"),"STATUS", "INIT");
    }

    private void continueInteraction(HashMap<String, Object> requestBody, ChatState chatState) throws Exception{
        JSONObject state = new JSONObject(chatState.getBody());


        if(state.getString("STATUS").equals("INIT")) {
            HashMap<String, Object> chat = getChat((HashMap<String, Object>)requestBody.get("callback_query"));
            String data = ((HashMap<String, Object>)requestBody.get("callback_query")).get("data").toString();
            if(Objects.equals(data, "delete --force")){
                activeObjects.delete(chatState);
                return;
            }
            if(Objects.equals(data, "CONSULTAR")){
                iTelegramApiService.sendMessage(chat.get("id"), "De acuerdo");
                iTelegramApiService.sendMessage(chat.get("id"), "Primero permiteme verificar tus datos por favor");
                iTelegramApiService.sendMessage(chat.get("id"), "Por favor ingresa tu usuario de Jira");
                iTelegramApiService.setNextStep(chatState, "CONSULTAR_GETUSUARIO");
            }else {
                iTelegramApiService.sendMessage(chat.get("id"), "No es una opcion valida");
                activeObjects.delete(chatState);
            }
        }else {
            HashMap<String, Object> chat = getChat(requestBody);
            String data = getMessage(requestBody).get("text").toString();
            if(Objects.equals(data, "delete --force")){
                activeObjects.delete(chatState);
                return;
            }
            switch (state.getString("STATUS")){
                case "CONSULTAR_GETUSUARIO":
                    iTelegramApiService.SetChatState(chatState, "USER", data);
                    iTelegramApiService.setNextStep(chatState, "CONSULTAR_GETPASS");
                    iTelegramApiService.sendMessage(chat.get("id"), "Ahora ingresa tu password");
                    break;
                case "CONSULTAR_GETPASS":
                    iTelegramApiService.sendMessage(chat.get("id"), "Validando");
                    if (userManager.authenticate(state.getString("USER"), data)){
                        iTelegramApiService.SetChatState(chatState, "USER", data);
                        iTelegramApiService.setNextStep(chatState, "CONSULTAR_GETISSUE");
                        iTelegramApiService.sendMessage(chat.get("id"), "Ingresa el key issue que deseas consultar");
                    }else {
                        iTelegramApiService.sendMessage(chat.get("id"), "Lo siento, no hemos podido validar tu acceso");
                        activeObjects.delete(chatState);
                    }
                    break;
                case "CONSULTAR_GETISSUE":
                    PantallaAdministradorSettings settings = pantallaConfiguracionServlet.obtenerGlobalSettings();
                    String projectKey = settings.getProjectKey();
                    if(data.toLowerCase().contains(projectKey.toLowerCase())){
                        iTelegramApiService.sendMessage(chat.get("id"), "Buscando");
                        log.error(data);
                        MutableIssue issue = jiraService.obtenerMutableIssuePorKey(data);

                        if(issue == null){
                            iTelegramApiService.sendMessage(chat.get("id"), "Lo siento, no pude encontrar el ticket");
                            activeObjects.delete(chatState);
                            break;
                        }
                        ApplicationUser assignee = issue.getAssignee();
                        String asignado = "(Unassigned)";
                        if(assignee != null) asignado = assignee.getDisplayName() + " (" + assignee.getUsername() +  ")";
                        Comment lastComment = ComponentAccessor.getCommentManager().getLastComment(issue);
                        String comment = "";
                        if(lastComment != null) comment = lastComment.getBody();
                        String sla = getSla(issue);
                        String result = "Ticket:           " + issue.getKey() + "\n" +
                                        "Summary:     " + issue.getSummary()+ "\n" +
                                        "Estado:          " + issue.getStatus().getName()+ "\n" +
                                        "Asignado:     " + asignado +  "\n" +
                                        "SLA:               " + sla + "\n" +
                                        "Comentario:\n" + comment;
                        iTelegramApiService.sendMessage(chat.get("id"), result);
                    }else
                        iTelegramApiService.sendMessage(chat.get("id"), "Lo siento, no pude encontrar el ticket");
                    activeObjects.delete(chatState);
                    break;
            }
        }
    }

    private static String getSla(Issue issue) throws IOException, JSONException {
        String uri = "http://localhost:8080/rest/servicedeskapi/request/"+issue.getId()+"/sla/1";
        HttpClient client = HttpClients.createDefault();
        HttpGet httpPost = new HttpGet(uri);
        httpPost.setHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
        HttpResponse response = client.execute(httpPost);

        String json_string = EntityUtils.toString(response.getEntity());
        long jsonValue = new JSONObject(json_string).getJSONObject("ongoingCycle")
                .getJSONObject("remainingTime").getLong("millis");
        String symbol = "";
        if(jsonValue < 0) symbol="-";
        long slaValue = Math.abs(jsonValue);
        long minutes = (int) (slaValue / 60000);
        if(minutes < 60) return symbol + minutes + "m";
        long hours = minutes / 60;
        if(hours < 8) {
            long remainingMinutes = minutes - (hours * 60);
            String minutesString = "";
            if(remainingMinutes>0) minutesString = remainingMinutes + "m";
            return symbol + hours + "h " + minutesString;
        }
        long days = hours / 8;
        long remainingHours = hours - (days * 8);
        String hoursString = "";
        if (remainingHours > 0) hoursString = remainingHours + "h";
        return symbol + days + "d " + hoursString;
    }
}
