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
package ch.astina.hesperid.web.components;

import ch.astina.hesperid.util.feedback.FeedbackManager;
import ch.astina.hesperid.util.feedback.FeedbackMessage;
import ch.astina.hesperid.util.feedback.FeedbackType;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class Feedback
{
    @SessionState
    private FeedbackManager manager;
    @Property
    private FeedbackMessage message;
    private final Logger logger = LoggerFactory.getLogger(Feedback.class);

    public void add(FeedbackMessage message)
    {
        logger.info("[" + message.getType() + "] " + message.getMessage());
        manager.add(message);
    }

    public void add(String message, FeedbackType type)
    {
        add(new FeedbackMessage(message, type));
    }

    public void add(String message, String comment, FeedbackType type)
    {
        add(new FeedbackMessage(message, comment, type));
    }

    public void success(String message)
    {
        add(message, FeedbackType.SUCCESS);
    }

    public void success(String message, String comment)
    {
        add(message, comment, FeedbackType.SUCCESS);
    }

    public void warning(String message)
    {
        add(message, FeedbackType.WARNING);
    }

    public void warning(String message, String comment)
    {
        add(message, comment, FeedbackType.WARNING);
    }

    public void error(String message)
    {
        add(message, FeedbackType.ERROR);
    }

    public void error(String message, String comment)
    {
        add(message, comment, FeedbackType.ERROR);
    }

    public void info(String message)
    {
        add(message, FeedbackType.INFO);
    }

    public void info(String message, String comment)
    {
        add(message, comment, FeedbackType.INFO);
    }

    public List<FeedbackMessage> getMessages()
    {
        return manager.getMessages();
    }

    public boolean isEmpty()
    {
        return manager.hasMessages() == false;
    }

    public String getCssClass()
    {
        if (message == null) {
            throw new IllegalStateException("Not inside messages loop");
        }
        return "feedback-" + message.getType().toString().toLowerCase();
    }
}
