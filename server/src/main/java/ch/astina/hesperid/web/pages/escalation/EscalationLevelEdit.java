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

import java.util.List;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.springframework.security.annotation.Secured;

import ch.astina.hesperid.dao.ContactDAO;
import ch.astina.hesperid.dao.EscalationDAO;
import ch.astina.hesperid.dao.UserDAO;
import ch.astina.hesperid.model.base.EscalationLevel;
import ch.astina.hesperid.model.user.User;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class EscalationLevelEdit
{
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
    void onActivate(EscalationLevel escalationLevel)
    {
        String username = escalationLevel.getUsername();
        if (username != null) {
            user = userDAO.getUserByName(username);
        }

        this.escalationLevel = escalationLevel;
    }

    EscalationLevel onPassivate()
    {
        return escalationLevel;
    }

    @CommitAfter
    Link onSuccess()
    {
        if (user == null) {
            escalationLevel.setUsername(null);
        } else {
            escalationLevel.setUsername(user.getUsername());
        }

        contactDAO.saveOrUpdateContact(escalationLevel.getContact());
        escalationDAO.save(escalationLevel);

        return linkSource.createPageRenderLinkWithContext(EscalationView.class,
                escalationLevel.getEscalationScheme());
    }

    public List<User> getUsers()
    {
        return userDAO.getAllUsers();
    }
}
