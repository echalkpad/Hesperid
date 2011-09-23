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
package ch.astina.hesperid.web.pages.admin.user;

import ch.astina.hesperid.dao.RoleDAO;
import ch.astina.hesperid.dao.UserDAO;
import ch.astina.hesperid.model.user.Role;
import ch.astina.hesperid.model.user.User;
import ch.astina.hesperid.web.services.users.UserService;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.annotation.Secured;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class ManageUser
{
    @Inject
    private UserDAO userDAO;
    @Inject
    private RoleDAO roleDAO;
    @Inject
    private UserService userService;
    @InjectComponent
    private Form form;
    @InjectComponent("password")
    private Field passwordField;
    @Property
    private User user;
    @Property
    private String password;

    @Secured({"ROLE_ADMIN"})
    void onActivate()
    {
        if (user == null) {
            user = new User();
        }
    }

    @Secured({"ROLE_ADMIN"})
    void onActivate(User user)
    {
        this.user = user;
    }

    Object onPassivate()
    {
        return (user == null || user.getId() == null) ? null : user;
    }

    @CommitAfter
    public Object onSuccess()
    {
        if (password != null && !password.isEmpty()) {
            userService.setUserPassword(user, password);
        }

        if (user.getRoles().isEmpty()) {
            Role adminRole = roleDAO.getRoleByName("ROLE_ADMIN");
            user.getRoles().add(adminRole);
        }

        userDAO.save(user);

        return UserIndex.class;
    }

    public void onValidateForm()
    {
        if (user.getId() == null && (password == null || password.isEmpty())) {
            form.recordError(passwordField, "Password required");
        }
    }
}
