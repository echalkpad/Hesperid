////////////////////////////////////////////////////////////////////////////////////////////////////
// Copyright 2011 Astina AG, Zurich
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
////////////////////////////////////////////////////////////////////////////////////////////////////
package ch.astina.hesperid.web.services.failures.impl;

import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.astina.hesperid.dao.FailureDAO;
import ch.astina.hesperid.dao.UserDAO;
import ch.astina.hesperid.mails.FailureNotificationMail;
import ch.astina.hesperid.model.base.EscalationLevel;
import ch.astina.hesperid.model.base.EscalationScheme;
import ch.astina.hesperid.model.base.Failure;
import ch.astina.hesperid.model.base.FailureEscalation;
import ch.astina.hesperid.model.base.FailureStatus;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.web.services.failures.FailureService;
import ch.astina.hesperid.web.services.mail.MailerService;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class FailureServiceImpl implements FailureService
{
    private FailureDAO failureDAO;
    private MailerService mailerService;
    private UserDAO userDAO;
    private Logger logger = LoggerFactory.getLogger(FailureServiceImpl.class);

    public FailureServiceImpl(FailureDAO failureDAO, MailerService mailerService)
    {
        this.failureDAO = failureDAO;
        this.mailerService = mailerService;
    }

    @Override
    public void report(Failure failure)
    {
        logger.info("Reporting failure: " + failure);

        // failure already known?
        Failure latest = getLatestUnresolvedFailure(failure);
        if (latest != null) {
            failure = latest;
        }

        // should not be necessary, but does not hurt either ...
        if (failure.isStatusAcknowledged() || failure.isStatusResolved()) {
            logger.info("Failure already acknowledged or resolved");
            return;
        }

        // first time this failure is reported?
        boolean firstTime = failure.getDetected() == null;
        if (firstTime) {
            failure.setDetected(new Date());
            failure.setFailureStatus(FailureStatus.DETECTED);
        }

        failureDAO.save(failure);

        // only escalate if this is a new failure. FailureCheckerJob takes care of further
        // escalation
        if (firstTime && needsEscalation(failure)) {
            escalate(failure);
        }
    }

    @Override
    public void escalate(Failure failure)
    {
        logger.info("Escalating failure: " + failure);

        EscalationLevel level = getNextEscalationLevel(failure);

        failure.setEscalationLevel(level);
        failure.setEscalated(new Date());

        FailureEscalation failureEscalation = new FailureEscalation();
        failureEscalation.setFailure(failure);
        failureEscalation.setEscalationLevel(failure.getEscalationLevel());
        failureEscalation.setEscalated(new Date());

        failureDAO.save(failure);
        failureDAO.save(failureEscalation);

        try {
            notifyContact(failure);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean needsEscalation(Failure failure)
    {
        if (failure.getAsset().getEscalationScheme() == null) {
            logger.info("Asset " + failure.getAsset() + " has no escalation scheme");
            return false;
        }

        if (!failure.isStatusDetected()) {
            return false;
        }

        EscalationLevel level = getNextEscalationLevel(failure);
        if (level == null) {
            return false;
        }

        EscalationLevel currentLevel = failure.getEscalationLevel();

        Date lastEscalation = failure.getEscalated();
        if (lastEscalation == null) {
            return true;
        }

        Date now = new Date();
        Long diff = now.getTime() - lastEscalation.getTime();
        return diff > (currentLevel.getTimeout() * 1000);
    }

    private Failure getLatestUnresolvedFailure(Failure failure)
    {
        List<Failure> unresolvedFailures = failureDAO.getUnresolvedFailures(failure.getObserver());
        int count = unresolvedFailures.size();
        return count == 0 ? null : unresolvedFailures.get(count - 1);
    }

    @Override
    public void acknowledge(Failure failure)
    {
        failure.setFailureStatus(FailureStatus.ACKNOWLEDGED);
        failure.setAcknowledged(new Date());
        failureDAO.save(failure);
    }

    @Override
    public void resolve(Failure failure)
    {
        failure.setFailureStatus(FailureStatus.RESOLVED);
        failure.setResolved(new Date());
        failureDAO.save(failure);

        for (FailureEscalation failureEscalation : failure.getFailureEscalations()) {
            FailureNotificationMail message = new FailureNotificationMail(failure, userDAO.getUserByName(failureEscalation.getEscalationLevel().getUsername()));
            try {
                mailerService.sendHtmlMail(message);
            } catch (MessagingException e) {
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public void autoresolve(Observer observer)
    {
        for (Failure failure : failureDAO.getUnresolvedFailures(observer)) {
            logger.info("Auto-resolving failure: " + failure);
            resolve(failure);
        }
    }

    private void notifyContact(Failure failure) throws MessagingException
    {
        logger.info("Notifying contact about failure: " + failure);

        FailureNotificationMail message = new FailureNotificationMail(failure, userDAO.getUserByName(failure.getEscalationLevel().getUsername()));
        mailerService.sendHtmlMail(message);
    }

    private EscalationLevel getNextEscalationLevel(Failure failure)
    {
        EscalationLevel currentLevel = failure.getEscalationLevel();
        EscalationScheme scheme = failure.getAsset().getEscalationScheme();
        List<EscalationLevel> levels = scheme.getEscalationLevels();

        for (EscalationLevel level : levels) {

            // no level set? use first level
            if (currentLevel == null) {
                return level;
            }

            // next level (levels should be ordered at this point)
            // {@link ch.astina.hesperid.model.base.EscalationScheme#getEscalationLevels()}
            if (level.getLevel() > currentLevel.getLevel()) {
                return level;
            }
        }

        return null;
    }
}
