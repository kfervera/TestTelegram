package com.bit2bitamericas.jira.telegramIntegration.services;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.bit2bitamericas.jira.telegramIntegration.activeObjects.interfaces.ChatState;
import com.bit2bitamericas.jira.telegramIntegration.services.interfaces.ITelegramApiService;
import com.bit2bitamericas.jira.telegramIntegration.services.interfaces.PantallaConfiguracionServlet;
import com.bit2bitamericas.jira.telegramIntegration.settings.servlets.PantallaAdministradorSettings;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class TelegramApiService implements ITelegramApiService {
    private static final Logger log = LoggerFactory.getLogger(TelegramApiService.class);
    private final PantallaConfiguracionServlet pantallaConfiguracionServlet;
    private final ActiveObjects activeObjects;
    public TelegramApiService(ActiveObjects activeObjects,
                              PantallaConfiguracionServlet pantallaConfiguracionServlet) {
        this.pantallaConfiguracionServlet = pantallaConfiguracionServlet;
        this.activeObjects = activeObjects;
    }

    @Override
    public void sendMessage(Object chatId, String message) throws Exception{
        JSONObject body =  new JSONObject();
        body.put("chat_id", chatId);
        body.put("text", message);
        sendMessage(body);
    }

    @Override
    public void sendMessage(Object chatId, String message, List<String> buttons) throws Exception{
        JSONObject body =  new JSONObject();
        body.put("chat_id", chatId);
        body.put("text", message);
        if (!buttons.isEmpty()){
            body.put("reply_markup", mapButtons(buttons));
        }
        sendMessage(body);
    }

    @Override
    public void SetChatState(Object chatId, String key, String status) throws JSONException {
        final ChatState chatState = activeObjects.create(ChatState.class);
        JSONObject state = new JSONObject();
        state.put(key, status);
        chatState.setChatId(chatId.toString());
        chatState.setBody(state.toString());
        chatState.save();
    }

    @Override
    public void SetChatState(ChatState chatState, String key, String value) throws JSONException{
        JSONObject state = new JSONObject(chatState.getBody());
        state.put(key, value);
        chatState.setBody(state.toString());
        chatState.save();
    }

    @Override
    public void setNextStep(ChatState chatState, String status) throws JSONException{
        JSONObject state = new JSONObject(chatState.getBody());
        state.put("STATUS", status);
        chatState.setBody(state.toString());
        chatState.save();
    }

    private void sendMessage(JSONObject body) throws Exception
    {
        PantallaAdministradorSettings settings = pantallaConfiguracionServlet.obtenerGlobalSettings();
        String token = settings.getTelegramApiToken();
        if (token.isEmpty()) throw new Exception();
        String uri = "https://api.telegram.org/bot" + token + "/sendMessage";
        HttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(uri);
        StringEntity entity = new StringEntity(body.toString());
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(entity);
        client.execute(httpPost);
        HttpResponse response = client.execute(httpPost);
        HttpEntity httpEntity = response.getEntity();
        String apiOutput = EntityUtils.toString(httpEntity);
        log.error(apiOutput);
    }

    private JSONObject mapButtons(List<String> stringButtons) throws JSONException {
        JSONArray buttons = new JSONArray();
        for (String stringButton:stringButtons) {
            JSONObject button =  new JSONObject();
            button.put("text", stringButton);
            button.put("callback_data", stringButton.split(" ", 2)[0].toUpperCase());
            buttons.put(button);
        }
        JSONArray inlineKeyboard = new JSONArray();
        inlineKeyboard.put(buttons);
        JSONObject replyMarkup = new JSONObject();
        replyMarkup.put("inline_keyboard", inlineKeyboard);
        return replyMarkup;
    }
}