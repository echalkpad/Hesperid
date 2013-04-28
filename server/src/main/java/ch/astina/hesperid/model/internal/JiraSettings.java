package ch.astina.hesperid.model.internal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class JiraSettings
{
    private Long id;

    private String url;

    private String username;

    private String password;

    private Long issueTypeId;

    private String resolutionTransitionName;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Long getIssueTypeId()
    {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId)
    {
        this.issueTypeId = issueTypeId;
    }

    public String getResolutionTransitionName()
    {
        return resolutionTransitionName;
    }

    public void setResolutionTransitionName(String resolutionTransitionName)
    {
        this.resolutionTransitionName = resolutionTransitionName;
    }
}
