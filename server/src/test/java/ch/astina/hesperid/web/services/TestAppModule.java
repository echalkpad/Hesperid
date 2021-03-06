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

import ch.astina.hesperid.configuration.HibernateSimpleConfigurer;
import ch.astina.hesperid.dao.*;
import ch.astina.hesperid.dao.hibernate.*;
import ch.astina.hesperid.web.services.dbmigration.DbMigration;
import ch.astina.hesperid.web.services.dbmigration.impl.DbMigrationImpl;
import ch.astina.hesperid.web.services.failures.FailureService;
import ch.astina.hesperid.web.services.failures.impl.FailureServiceImpl;
import ch.astina.hesperid.web.services.impl.SystemHealthServiceImpl;
import ch.astina.hesperid.web.services.jobs.DbCleanupJob;
import ch.astina.hesperid.web.services.jobs.ExternalObserverJob;
import ch.astina.hesperid.web.services.jobs.FailureCheckerJob;
import ch.astina.hesperid.web.services.jobs.ObserverStatusCheckerJob;
import ch.astina.hesperid.web.services.jobs.impl.DbCleanupJobImpl;
import ch.astina.hesperid.web.services.jobs.impl.ExternalObserverJobImpl;
import ch.astina.hesperid.web.services.jobs.impl.FailureCheckerJobImpl;
import ch.astina.hesperid.web.services.jobs.impl.ObserverStatusCheckerJobImpl;
import ch.astina.hesperid.web.services.scheduler.SchedulerService;
import ch.astina.hesperid.web.services.systemenvironment.SystemEnvironment;
import ch.astina.hesperid.web.services.systemenvironment.impl.MockSystemEnvironmentImpl;
import ch.astina.hesperid.web.services.version.Version;
import ch.astina.hesperid.web.services.version.impl.VersionImpl;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.hibernate.HibernateConfigurer;
import org.apache.tapestry5.ioc.*;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.*;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
@SubModule({TestSecurityModule.class, SitemapModule.class})
public class TestAppModule
{
    public static void bind(ServiceBinder binder)
    {
        binder.bind(DbMigration.class, DbMigrationImpl.class).eagerLoad();
        binder.bind(MailServerDAO.class, MailServerDAOHibernate.class);
        binder.bind(LocationDAO.class, LocationDAOHibernate.class);
        binder.bind(SystemDAO.class, SystemDAOHibernate.class);
        binder.bind(MesRoleDAO.class, MesRoleDAOHibernate.class);
        binder.bind(ContactDAO.class, ContactDAOHibernate.class);
        binder.bind(ReportDAO.class, ReportDAOHibernate.class);
        binder.bind(AssetDAO.class, AssetDAOHibernate.class);
        binder.bind(AgentBundleDAO.class, AgentBundleDAOHibernate.class);
        binder.bind(SystemHealthDAO.class, SystemHealthDAOHibernate.class);
        binder.bind(FailureDAO.class, FailureDAOHibernate.class);
        binder.bind(EscalationDAO.class, EscalationDAOHibernate.class);
        binder.bind(ObserverDAO.class, ObserverDAOHibernate.class);
        binder.bind(SystemHealthService.class, SystemHealthServiceImpl.class);
        binder.bind(FailureService.class, FailureServiceImpl.class);
        binder.bind(ExternalObserverJob.class, ExternalObserverJobImpl.class);
        binder.bind(ObserverStatusCheckerJob.class, ObserverStatusCheckerJobImpl.class);
        binder.bind(SystemEnvironment.class, MockSystemEnvironmentImpl.class);
        binder.bind(Version.class, VersionImpl.class);
	    binder.bind(SchedulerService.class).scope(ScopeConstants.DEFAULT).eagerLoad();
	    binder.bind(FailureCheckerJob.class, FailureCheckerJobImpl.class);
	    binder.bind(DbCleanupJob.class, DbCleanupJobImpl.class);
	    binder.bind(SystemSettingsDAO.class, SystemSettingsDAOHibernate.class);
    }

    //Mock a Servlet request...
    public static void contributeRequestHandler(OrderedConfiguration<RequestFilter> config, final RequestGlobals requestGlobals)
    {
        RequestFilter filter = new RequestFilter()
        {
            public boolean service(Request request, Response response, RequestHandler handler) throws IOException {
                requestGlobals.storeServletRequestResponse(Mockito.mock(HttpServletRequest.class), Mockito.mock(HttpServletResponse.class));
                return handler.service(request, response);
            }
        };
        config.add("EnsureNonNullHttpRequestAndResponse", filter, "before:*");
    }

    public static void contributeHibernateEntityPackageManager(Configuration<String> configuration)
    {
        configuration.add("ch.astina.hesperid.model");
    }

    public static void contributeHibernateSessionSource(
            @InjectService("SystemEnvironment") SystemEnvironment systemEnvironment,
            OrderedConfiguration<HibernateConfigurer> config)
    {
        config.add("FileConfiguration", new HibernateSimpleConfigurer());
    }

    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration)
    {
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en_GB");
        configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
        configuration.add("tapestry.default-cookie-max-age", "31536000");
    }
}
