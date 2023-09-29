package com.bit2bitamericas.jira.telegramIntegration.services.interfaces;

import com.atlassian.jira.util.json.JSONException;
import com.bit2bitamericas.jira.telegramIntegration.activeObjects.interfaces.ChatState;

import java.util.List;

public interface ITelegramApiService {
    void sendMessage(Object chatId, String message) throws Exception;
    void sendMessage(Object chatId, String message, List<String> buttons) throws Exception;
    void SetChatState(Object chatId, String key, String status) throws JSONException;
    void SetChatState(ChatState chatState, String key, String status) throws JSONException;
    void setNextStep(ChatState chatState, String status) throws JSONException;
}
