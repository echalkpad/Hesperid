package ch.astina.hesperid.web.services.jira;

public interface JiraIssueService
{
    public void createIssue(JiraIssue issue);

    public void resolveIssue(JiraIssue issue);
}
