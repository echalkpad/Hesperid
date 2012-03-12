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

import ch.astina.hesperid.dao.FailureDAO;
import ch.astina.hesperid.dao.UserDAO;
import ch.astina.hesperid.mails.FailureNotificationMail;
import ch.astina.hesperid.model.base.*;
import ch.astina.hesperid.web.services.failures.EscalationSpecification;
import ch.astina.hesperid.web.services.failures.FailureService;
import ch.astina.hesperid.web.services.mail.MailerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.util.Date;

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

    public FailureServiceImpl(FailureDAO failureDAO, MailerService mailerService, UserDAO userDAO)
    {
        this.failureDAO = failureDAO;
        this.mailerService = mailerService;
        this.userDAO = userDAO;
    }

    @Override
    public void report(Failure failure)
    {
        logger.info("Reporting failure: " + failure);

	    // Make sure that no failure for this observer problem already exists.
	    Failure existingFailure = failureDAO.getFailureByExample(failure);
	    if (existingFailure != null) {
		    failure = existingFailure;
	    } else {
		    failure.setDetected(new Date());
		    failure.setFailureStatus(FailureStatus.DETECTED);

		    failureDAO.save(failure);
	    }
    }

    @Override
    public void escalate(Failure failure)
    {
        logger.info("Escalating failure: " + failure);

        EscalationLevel level = EscalationSpecification.nextEscalationLevelFor(failure);

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
        logger.info("Escalation Level: " + failure.getEscalationLevel());
        logger.info("Username: " + failure.getEscalationLevel().getUsername());

        FailureNotificationMail message = new FailureNotificationMail(failure, userDAO.getUserByName(failure.getEscalationLevel().getUsername()));
        mailerService.sendHtmlMail(message);
    }

}
