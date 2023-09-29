package com.bit2bitamericas.jira.telegramIntegration.services;

import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
/*import com.bit2bitamericas.jira.core.issue.SimpleIssue;
import com.bit2bitamericas.jira.core.issue.SimpleIssueFactory;
import com.bit2bitamericas.jira.core.issue.impl.SimpleIssueFactoryImpl;
import com.bit2bitamericas.jira.jiraToEricssonIntegrator.servicios.interfaces.JiraService;
import com.bit2bitamericas.jira.jiraToEricssonIntegrator.servicios.interfaces.PantallaConfiguracionServlet;
import com.bit2bitamericas.jira.jiraToEricssonIntegrator.settings.servlets.PantallaAdministradorSettings;*/
import com.bit2bitamericas.jira.telegramIntegration.services.interfaces.JiraService;

public class JiraServiceGeneral implements JiraService {
    /*private static final Logger log = LoggerFactory.getLogger(JiraServiceGeneral.class);
    private SearchService searchService;
    private JqlQueryParser jqlQueryParser;
    private UserManager userManager;
    private PantallaConfiguracionServlet pantallaConfiguracionServlet;*/
    private IssueManager issueManager;

    public JiraServiceGeneral(IssueManager issueManager) {
        this.issueManager = issueManager;
    }

    /*private IssueIndexingService issueIndexingService;
    private FieldManager fieldManager;
    private SimpleIssueFactory simpleIssueFactory;*/
/*
    public JiraServiceGeneral(PantallaConfiguracionServlet pantallaConfiguracionServlet, FieldManager fieldManager, SimpleIssueFactory simpleIssueFactory) {
        this.pantallaConfiguracionServlet = pantallaConfiguracionServlet;
        this.fieldManager = fieldManager;
        this.simpleIssueFactory = simpleIssueFactory;
        this.searchService = ComponentAccessor.getComponent(SearchService.class);
        this.jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser.class);
        this.userManager = ComponentAccessor.getUserManager();
        this.issueManager = ComponentAccessor.getIssueManager();
        this.issueIndexingService = ComponentAccessor.getComponentOfType(IssueIndexingService.class);
    }

    @Override
    public List<CustomField> obtenerTodosLosCustomfields() {
        return ComponentAccessor.getCustomFieldManager().getCustomFieldObjects();
    }

    @Override
    public List<SimpleIssue> obtenerSimpleIssuesPorJQL(String jql, String username) throws SearchException, JqlParseException {
        SearchResults searchResults = ejecutarJQL(jql, username);
        return searchResults.getIssues()
                .stream()
                .map(o -> (new SimpleIssueFactoryImpl()).getIssue(o))
                .collect(Collectors.toList());
    }

    public SearchResults ejecutarJQL(String jql, String username) throws JqlParseException, SearchException {
        log.debug("ejecutar JQL: \"{}\" con el usuario: {}", jql, username);
        Query conditionQuery = jqlQueryParser.parseQuery(jql);
        ApplicationUser adminJiraUser = userManager.getUserByName(username);
        SearchResults resultadosDeFiltrado = searchService.search(adminJiraUser, conditionQuery, PagerFilter.getUnlimitedFilter());
        log.debug("La cantidad de isues encontrados es: {}", resultadosDeFiltrado.getTotal());
        return resultadosDeFiltrado;
    }

    @Override
    public List<Field> obtenerTodosLosCampos() throws FieldException {
        Set<NavigableField> availableFields = fieldManager.getAllAvailableNavigableFields();

        return availableFields.stream()
                .filter(field -> field != null)
                .map(field -> (Field) field)
                .sorted(Comparator.comparing(Field::getName))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void setearValorEnCustomField(SimpleIssue simpleIssue, String customFieldId, String value) {
        log.debug(String.format("Setear value: %s en el customfield: %s en el issue: %s", value, customFieldId, simpleIssue.getKey()));
        simpleIssue.setFieldValueById(customFieldId, value);
        saveIssue(simpleIssue.getMutableIssue());
    }

    @Override
    public void saveIssue(MutableIssue mutableIssue) {
        PantallaAdministradorSettings settings = pantallaConfiguracionServlet.obtenerGlobalSettings();
        ApplicationUser administrator = userManager.getUserByName(settings.getNombreDeUsuarioAdminJira());
        issueManager.updateIssue(administrator, mutableIssue, EventDispatchOption.DO_NOT_DISPATCH, false);
        reIndex(mutableIssue);
    }
*/
    @Override
    public MutableIssue obtenerMutableIssuePorKey(String issueKey) {
        return issueManager.getIssueByCurrentKey(issueKey);
    }
/*
    @Override
    public MutableIssue obtenerMutableIssue(Issue issue) {
        return issueManager.getIssueObject(issue.getId());
    }

    @Override
    public SimpleIssue getSimpleIssue(String issueKey) {
        MutableIssue mutableIssue = obtenerMutableIssuePorKey(issueKey);
        return simpleIssueFactory.getIssue(mutableIssue);
    }

    public void reIndex(MutableIssue issue) {
        try {
            List<Issue> issues = new ArrayList();
            issues.add(issue);
            issueIndexingService.reIndexIssueObjects(issues);
        } catch (IndexException ex) {
            log.error("Error indexing issues", ex);
        }
    }

    @Override
    public boolean issuePerteneceAJql(String jql, String username, MutableIssue issue) throws SearchException, JqlParseException {
        reIndex(issue);
        String newJql = String.format("%s AND issuekey = %s", jql, issue.getKey());
        SearchResults searchResults = ejecutarJQL(newJql, username);
        return searchResults.getIssues().size() == 1;
    }

    @Override
    public Map<String, String> obtenerIssueEvents() {
        Map<String, String> issueEvents = new HashMap<>();
        issueEvents.put(EventType.ISSUE_CREATED_ID.toString(), "Issue Created");
        issueEvents.put(EventType.ISSUE_UPDATED_ID.toString(), "Issue Updated");
        issueEvents.put(EventType.ISSUE_ASSIGNED_ID.toString(), "Issue Assigned");
        issueEvents.put(EventType.ISSUE_RESOLVED_ID.toString(), "Issue Resolved");
        issueEvents.put(EventType.ISSUE_CLOSED_ID.toString(), "Issue Closed");
        issueEvents.put(EventType.ISSUE_COMMENTED_ID.toString(), "Issue Commented");
        issueEvents.put(EventType.ISSUE_REOPENED_ID.toString(), "Issue Reopened");
        issueEvents.put(EventType.ISSUE_DELETED_ID.toString(), "Issue Deleted");
        issueEvents.put(EventType.ISSUE_MOVED_ID.toString(), "Issue Moved");
        issueEvents.put(EventType.ISSUE_WORKLOGGED_ID.toString(), "Issue Worklogged");
        issueEvents.put(EventType.ISSUE_WORKSTARTED_ID.toString(), "Issue Workstarted");
        issueEvents.put(EventType.ISSUE_WORKSTOPPED_ID.toString(), "Issue Workstopped");
        issueEvents.put(EventType.ISSUE_GENERICEVENT_ID.toString(), "Issue Generic event");
        issueEvents.put(EventType.ISSUE_COMMENT_EDITED_ID.toString(), "Issue Comment Edited");
        issueEvents.put(EventType.ISSUE_WORKLOG_UPDATED_ID.toString(), "Issue Worklog Updated");
        issueEvents.put(EventType.ISSUE_WORKLOG_DELETED_ID.toString(), "Issue Worklog Deleted");
        issueEvents.put(EventType.ISSUE_COMMENT_DELETED_ID.toString(), "Issue Comment Deleted");
        return issueEvents;
    }*/
}
