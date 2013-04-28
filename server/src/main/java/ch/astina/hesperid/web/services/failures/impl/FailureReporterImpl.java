package ch.astina.hesperid.web.services.failures.impl;

import ch.astina.hesperid.dao.UserDAO;
import ch.astina.hesperid.mails.FailureNotificationMail;
import ch.astina.hesperid.model.base.*;
import ch.astina.hesperid.model.user.User;
import ch.astina.hesperid.web.services.failures.FailureReporter;
import ch.astina.hesperid.web.services.jira.JiraIssue;
import ch.astina.hesperid.web.services.jira.JiraIssueService;
import ch.astina.hesperid.web.services.mail.MailerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;

public class FailureReporterImpl implements FailureReporter
{
    private MailerService mailerService;

    private JiraIssueService jiraIssueService;

    private UserDAO userDAO;

    private Logger logger = LoggerFactory.getLogger(FailureServiceImpl.class);

    public FailureReporterImpl(MailerService mailerService, JiraIssueService jiraIssueService, UserDAO userDAO)
    {
        this.mailerService = mailerService;
        this.jiraIssueService = jiraIssueService;
        this.userDAO = userDAO;
    }

    @Override
    public void reportFailure(FailureEscalation failureEscalation)
    {
        Failure failure = failureEscalation.getFailure();
        EscalationLevel escalationLevel = failureEscalation.getEscalationLevel();

        logger.info("Reporting failure: " + failure);
        logger.info("Escalation Level: " + escalationLevel);

        if (escalationLevel.getChannel().equals(EscalationChannel.EMAIL)) {
            logger.info("Username: " + escalationLevel.getUsername());
            User user = findUser(escalationLevel);
            FailureNotificationMail message = new FailureNotificationMail(failure, user);
            sendNotificationMail(message);
        } else if (escalationLevel.getChannel().equals(EscalationChannel.JIRA)) {
            logger.info("Project code: " + escalationLevel.getProjectCode());
            JiraIssue issue = new JiraIssue(failure, escalationLevel.getProjectCode());
            jiraIssueService.createIssue(issue);
            failureEscalation.setIssueKey(issue.getIssueKey());
        }
    }

    @Override
    public void reportResolution(FailureEscalation failureEscalation)
    {
        logger.info("Reporting resolution for escalation: " + failureEscalation.getId());

        EscalationLevel escalationLevel = failureEscalation.getEscalationLevel();

        if (escalationLevel.getChannel().equals(EscalationChannel.EMAIL)) {
            User user = findUser(escalationLevel);
            FailureNotificationMail message = new FailureNotificationMail(failureEscalation.getFailure(), user);
            sendNotificationMail(message);
        } else if (escalationLevel.getChannel().equals(EscalationChannel.JIRA)) {
            jiraIssueService.resolveIssue(new JiraIssue(failureEscalation.getIssueKey()));
        }
    }

    private User findUser(EscalationLevel level)
    {
        return userDAO.getUserByName(level.getUsername());
    }

    private void sendNotificationMail(FailureNotificationMail message)
    {
        try {
            mailerService.sendHtmlMail(message);
        } catch (MessagingException e) {
            logger.error(e.getMessage());
        }
    }
}
