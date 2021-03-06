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
package ch.astina.hesperid.web.services;

import ch.astina.hesperid.dao.RoleDAO;
import ch.astina.hesperid.dao.UserDAO;
import ch.astina.hesperid.dao.hibernate.HibernateRoleDAO;
import ch.astina.hesperid.dao.hibernate.HibernateUserDAO;
import ch.astina.hesperid.web.services.users.impl.MockUserServiceImpl;
import ch.astina.hesperid.web.services.springsecurity.LogoutService;
import ch.astina.hesperid.web.services.springsecurity.SpringSecurityServices;
import ch.astina.hesperid.web.services.springsecurity.internal.LogoutServiceImpl;
import ch.astina.hesperid.web.services.springsecurity.internal.SecurityChecker;
import ch.astina.hesperid.web.services.users.UserService;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.services.ComponentClassTransformWorker;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class TestSecurityModule extends SecurityModule
{
    public static void bind(final ServiceBinder binder)
    {
        binder.bind(LogoutService.class, LogoutServiceImpl.class).withMarker(
                SpringSecurityServices.class);

        binder.bind(AuthenticationTrustResolver.class, AuthenticationTrustResolverImpl.class).withMarker(SpringSecurityServices.class);

        binder.bind(UserDAO.class, HibernateUserDAO.class).withMarker(SpringSecurityServices.class);
        binder.bind(RoleDAO.class, HibernateRoleDAO.class).withMarker(SpringSecurityServices.class);
        
        //Mock out user service
        binder.bind(UserService.class, MockUserServiceImpl.class);
    }
    
    public static void contributeComponentClassTransformWorker(
            OrderedConfiguration<ComponentClassTransformWorker> configuration,
            SecurityChecker securityChecker)
    {
        //MOCK this out, will anyway not work without a proper Servlet context
    }
}
