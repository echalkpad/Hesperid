package ch.astina.hesperid.web.services.jira;

import ch.astina.hesperid.model.base.Failure;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverParameter;
import ch.astina.hesperid.model.base.ObserverStrategy;

public class JiraIssue
{
    private String issueKey;

    private String projectCode;

    private String summary;

    private String description;

    public JiraIssue(String issueKey)
    {
        this.issueKey = issueKey;
    }

    public JiraIssue(String projectCode, String summary, String description)
    {
        this.projectCode = projectCode;
        this.summary = summary;
        this.description = description;
    }

    public JiraIssue(Failure failure, String projectCode)
    {
        Observer observer = failure.getObserver();

        ObserverStrategy observerStrategy = observer.getObserverStrategy();
        ObserverParameter observerParameter = failure.getObserverParameter();

        this.projectCode = projectCode;

        String parameterValue = (observerParameter == null ? "<null>" : observerParameter.getValue());

        summary = String.format("%s / %s error (%s)", failure.getAsset(), observer.getName(), parameterValue).trim();

        description = String.format("Asset: %s\nObserver: %s\nParameter: %s = %s\n",
                failure.getAsset(), observer.getName(), observerStrategy.getResultParameterName(),
                parameterValue);
    }

    public String getIssueKey()
    {
        return issueKey;
    }

    public void setIssueKey(String issueKey)
    {
        this.issueKey = issueKey;
    }

    public String getProjectCode()
    {
        return projectCode;
    }

    public String getSummary()
    {
        return summary;
    }

    public String getDescription()
    {
        return description;
    }
}
