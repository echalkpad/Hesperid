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
package ch.astina.hesperid.web.pages.escalation;

import ch.astina.hesperid.dao.ContactDAO;
import ch.astina.hesperid.dao.EscalationDAO;
import ch.astina.hesperid.dao.UserDAO;
import ch.astina.hesperid.model.base.EscalationLevel;
import ch.astina.hesperid.model.base.EscalationScheme;
import ch.astina.hesperid.model.user.User;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.springframework.security.access.annotation.Secured;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class EscalationLevelAdd
{
    @Property
    private EscalationScheme escalationScheme;
    @Property
    private EscalationLevel escalationLevel;
    @Property
    private User user;
    @Inject
    private EscalationDAO escalationDAO;
    @Inject
    private ContactDAO contactDAO;
    @Inject
    private UserDAO userDAO;
    @Inject
    private PageRenderLinkSource linkSource;

    @Secured("ROLE_ADMIN")
    void onActivate(EscalationScheme escalationScheme)
    {
        this.escalationScheme = escalationScheme;
        if (escalationLevel == null) {
            escalationLevel = new EscalationLevel();
            escalationLevel.setLevel(getNextEscalationLevelNumber());
            escalationLevel.setTimeout(600);
        }
    }

    EscalationScheme onPassivate()
    {
        return escalationScheme;
    }

    @CommitAfter
    Link onSuccess()
    {
        escalationLevel.setEscalationScheme(escalationScheme);

        if (user != null) {
            escalationLevel.setUsername(user.getUsername());
        }

        escalationDAO.save(escalationLevel);

        return linkSource.createPageRenderLinkWithContext(EscalationView.class, escalationScheme);
    }

    public List<User> getUsers()
    {
        return userDAO.getAllUsers();
    }

    private int getNextEscalationLevelNumber()
    {
        int level = 0;
        for (EscalationLevel escalationLevel : escalationScheme.getEscalationLevels()) {
            level = escalationLevel.getLevel();
        }
        return level + 1;
    }
}
