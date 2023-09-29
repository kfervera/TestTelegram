package com.bit2bitamericas.jira.telegramIntegration.services.interfaces;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.FieldException;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.parser.JqlParseException;
//import com.bit2bitamericas.jira.core.issue.SimpleIssue;

import java.util.List;
import java.util.Map;

public interface JiraService {
    /*List<CustomField> obtenerTodosLosCustomfields();

    List<SimpleIssue> obtenerSimpleIssuesPorJQL(String jql, String username) throws SearchException, JqlParseException;

    List<Field> obtenerTodosLosCampos() throws FieldException;

    void setearValorEnCustomField(SimpleIssue simpleIssue, String customFieldName, String value);

    void saveIssue(MutableIssue mutableIssue);

    MutableIssue obtenerMutableIssue(Issue issue);

    SimpleIssue getSimpleIssue(String issueKey);

    boolean issuePerteneceAJql(String jql, String username, MutableIssue issue) throws SearchException, JqlParseException;

    Map<String, String> obtenerIssueEvents();*/
    MutableIssue obtenerMutableIssuePorKey(String issueKey);
}
