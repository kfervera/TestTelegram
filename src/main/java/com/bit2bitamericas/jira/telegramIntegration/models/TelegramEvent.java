package com.bit2bitamericas.jira.telegramIntegration.models;

public class TelegramEvent {
    private long update_id;
    private TelegramMessage message;

    public long getUpdateId() {
        return update_id;
    }

    public void setUpdateId(long update_id) {
        this.update_id = update_id;
    }

    public TelegramMessage getMessage() {
        return message;
    }

    public void setMessage(TelegramMessage message) {
        this.message = message;
    }
}
