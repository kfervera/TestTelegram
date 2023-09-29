package com.bit2bitamericas.jira.telegramIntegration.models;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class TelegramRestResourceModel {

    @XmlElement(name = "value")
    private String message;

    public TelegramRestResourceModel() {
    }

    public TelegramRestResourceModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}