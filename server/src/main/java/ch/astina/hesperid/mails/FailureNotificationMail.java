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
package ch.astina.hesperid.mails;

import ch.astina.hesperid.model.base.Failure;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverParameter;
import ch.astina.hesperid.model.base.ObserverStrategy;
import ch.astina.hesperid.model.mail.HtmlMailMessage;
import ch.astina.hesperid.model.user.User;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.InternetAddress;
import java.io.StringWriter;
import java.util.Properties;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class FailureNotificationMail extends HtmlMailMessage
{
    private Logger logger = LoggerFactory.getLogger(FailureNotificationMail.class);

    public FailureNotificationMail(Failure failure, User user)
    {
        Observer observer = failure.getObserver();
        String emailAddress = user.getEmail();

        ObserverStrategy observerStrategy = observer.getObserverStrategy();
        ObserverParameter observerParameter = failure.getObserverParameter();

        VelocityEngine ve = new VelocityEngine();

        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class",
                org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader.class.getName());
        ve.init(props);

        Template template = ve.getTemplate("ch/astina/hesperid/mails/FailureNotificationMail.vm");

        VelocityContext context = new VelocityContext();
        context.put("failure", failure);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        // XXX do better
        String altBody = "Asset: " + failure.getAsset() + "\n" + "Observer: " + observer.getName()
                + "\n" + "Parameter: " + observerStrategy.getResultParameterName() + " = "
                + (observerParameter == null ? "<null>" : observerParameter.getValue()) + "\n";

        setSubject(failure.getFailureStatus().name() + ": " + failure.getMessage() + " ("
                + failure.getId() + ")");
        setHtmlBody(writer.toString());
        setAltBody(altBody);

        try {
            setSender(new InternetAddress("notice@hesperid.org", "Monitoring")); // XXX
            addToRecipient(new InternetAddress(emailAddress));
        } catch (Exception e) {
            logger.error("Error while creating notification mail",e);
            throw new RuntimeException(e);
        }
    }
}
