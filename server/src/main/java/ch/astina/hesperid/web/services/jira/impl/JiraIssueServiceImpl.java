package ch.astina.hesperid.web.services.jira.impl;

import ch.astina.hesperid.model.internal.JiraSettings;
import ch.astina.hesperid.web.services.jira.JiraIssue;
import ch.astina.hesperid.web.services.jira.JiraIssueService;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class JiraIssueServiceImpl implements JiraIssueService
{
    private JiraRestClient restClient;

    private JiraSettings jiraSettings;

    private Logger logger = LoggerFactory.getLogger(JiraIssueServiceImpl.class);

    public JiraIssueServiceImpl(JiraSettings jiraSettings) throws URISyntaxException
    {
        JiraRestClientFactory factory = new JerseyJiraRestClientFactory();
        URI jiraServerUri = new URI(jiraSettings.getUrl());

        this.restClient = factory.createWithBasicHttpAuthentication(
                jiraServerUri, jiraSettings.getUsername(), jiraSettings.getPassword());

        this.jiraSettings = jiraSettings;
    }

    @Override
    public void createIssue(final JiraIssue issue)
    {
        IssueInputBuilder builder = new IssueInputBuilder(issue.getProjectCode(), this.jiraSettings.getIssueTypeId());

        builder.setSummary(issue.getSummary());
        builder.setDescription(issue.getDescription());

        final NullProgressMonitor pm = new NullProgressMonitor();

        BasicIssue remoteIssue = restClient.getIssueClient().createIssue(builder.build(), pm);
        issue.setIssueKey(remoteIssue.getKey());

        logger.info("Created JIRA issue: " + remoteIssue.getKey());
    }

    @Override
    public void resolveIssue(JiraIssue issue)
    {
        if (issue.getIssueKey() == null) {
            return;
        }

        final NullProgressMonitor pm = new NullProgressMonitor();
        Issue remoteIssue = restClient.getIssueClient().getIssue(issue.getIssueKey(), pm);

        Iterable<Transition> transitions = restClient.getIssueClient().getTransitions(remoteIssue.getTransitionsUri(), pm);

        final Transition closeIssueTransition = getTransitionByName(transitions, jiraSettings.getResolutionTransitionName());
        if (closeIssueTransition == null) {
            logger.error("Cannot close JIRA issue: transition not available (\"" + jiraSettings.getResolutionTransitionName() + "\")");
            return;
        }

        Collection<FieldInput> fieldInputs = Arrays.asList(new FieldInput("resolution", ComplexIssueInputFieldValue.with("name", "Fixed")));
        final TransitionInput transitionInput = new TransitionInput(closeIssueTransition.getId(), fieldInputs, Comment.valueOf("Closed via Hesperid"));
        restClient.getIssueClient().transition(remoteIssue.getTransitionsUri(), transitionInput, pm);

        logger.info("Resolved JIRA issue: " + remoteIssue.getKey());
    }

    private static Transition getTransitionByName(Iterable<Transition> transitions, String transitionName)
    {
        for (Transition transition : transitions) {
            if (transition.getName().equals(transitionName)) {
                return transition;
            }
        }
        return null;
    }
}
