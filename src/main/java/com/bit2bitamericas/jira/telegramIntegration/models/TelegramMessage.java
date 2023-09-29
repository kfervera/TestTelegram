package com.bit2bitamericas.jira.telegramIntegration.models;

public class TelegramMessage {
    private long message_id;
    private long date;
    private String text;

    public long getMessageId() {
        return message_id;
    }

    public void setMessageId(long message_id) {
        this.message_id = message_id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
