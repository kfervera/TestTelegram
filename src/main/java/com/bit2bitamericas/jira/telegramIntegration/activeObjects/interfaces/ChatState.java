package com.bit2bitamericas.jira.telegramIntegration.activeObjects.interfaces;

import net.java.ao.Entity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;

public interface ChatState extends Entity {

    @AutoIncrement
    @NotNull
    @PrimaryKey("ID")
    public int getID();
    String getChatId();
    @StringLength(value = StringLength.UNLIMITED)
    void setChatId(String chatId);
    String getBody();
    @StringLength(value = StringLength.UNLIMITED)
    void setBody(String body);


}
