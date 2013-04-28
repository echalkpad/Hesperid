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

import ch.astina.hesperid.dao.*;
import ch.astina.hesperid.dao.hibernate.*;
import ch.astina.hesperid.mails.SmtpCredentials;
import ch.astina.hesperid.model.internal.JiraSettings;
import ch.astina.hesperid.model.internal.MailServer;
import ch.astina.hesperid.util.HibernateFileConfigurer;
import ch.astina.hesperid.web.services.dbmigration.DbMigration;
import ch.astina.hesperid.web.services.dbmigration.impl.DbMigrationImpl;
import ch.astina.hesperid.web.services.failures.FailureReporter;
import ch.astina.hesperid.web.services.failures.FailureService;
import ch.astina.hesperid.web.services.failures.impl.FailureReporterImpl;
import ch.astina.hesperid.web.services.failures.impl.FailureServiceImpl;
import ch.astina.hesperid.web.services.impl.SystemHealthServiceImpl;
import ch.astina.hesperid.web.services.jira.JiraIssueService;
import ch.astina.hesperid.web.services.jira.impl.JiraIssueServiceImpl;
import ch.astina.hesperid.web.services.jobs.DbCleanupJob;
import ch.astina.hesperid.web.services.jobs.ExternalObserverJob;
import ch.astina.hesperid.web.services.jobs.FailureCheckerJob;
import ch.astina.hesperid.web.services.jobs.ObserverStatusCheckerJob;
import ch.astina.hesperid.web.services.jobs.impl.DbCleanupJobImpl;
import ch.astina.hesperid.web.services.jobs.impl.ExternalObserverJobImpl;
import ch.astina.hesperid.web.services.jobs.impl.FailureCheckerJobImpl;
import ch.astina.hesperid.web.services.jobs.impl.ObserverStatusCheckerJobImpl;
import ch.astina.hesperid.web.services.mail.MailerService;
import ch.astina.hesperid.web.services.mail.impl.MailerServiceImpl;
import ch.astina.hesperid.web.services.scheduler.SchedulerService;
import ch.astina.hesperid.web.services.springsecurity.SpringSecurityServices;
import ch.astina.hesperid.web.services.systemenvironment.SystemEnvironment;
import ch.astina.hesperid.web.services.systemenvironment.impl.SystemEnvironmentImpl;
import ch.astina.hesperid.web.services.version.Version;
import ch.astina.hesperid.web.services.version.impl.VersionImpl;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.hibernate.HibernateConfigurer;
import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationProvider;

import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
@SubModule({SitemapModule.class, SecurityModule.class})
public class AppModule
{   
    @Startup
    public static void initHesperid(SystemEnvironment systemEnvironment, Logger logger)
    {
        logger.info("Starting Hesperid with home directory: " + systemEnvironment.getApplicationHomeDirectoryPath());
        
        try {
            org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();

            Layout fileAppenderLayout = new PatternLayout("%d %p (%t) [%c{2}] - %m%n");

            RollingFileAppender rfa = new RollingFileAppender();
            rfa.setMaxFileSize("10240KB");
            rfa.setMaxBackupIndex(10);
            rfa.setLayout(fileAppenderLayout);
            rfa.setFile(systemEnvironment.getApplicationLogfilePath());
            rfa.setName("file");
            rfa.activateOptions();

            root.addAppender(rfa);
            
        } catch (Exception e) {
            logger.error("Cannot add file appender for logging",e);
        }    
    }
    
    public static void bind(ServiceBinder binder)
    {
        binder.bind(DbMigration.class, DbMigrationImpl.class);
        binder.bind(SystemEnvironment.class, SystemEnvironmentImpl.class);
        binder.bind(Version.class, VersionImpl.class);
        binder.bind(MailServerDAO.class, MailServerDAOHibernate.class);
        binder.bind(JiraSettingsDAO.class, JiraSettingsDAOHibernate.class);
        binder.bind(HqlDAO.class, HqlDAOHibernate.class);
        binder.bind(LocationDAO.class, LocationDAOHibernate.class);
        binder.bind(SystemDAO.class, SystemDAOHibernate.class);
        binder.bind(SoftwareLicenseDAO.class, SoftwareLicenseDAOHibernate.class);
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
        binder.bind(FailureReporter.class, FailureReporterImpl.class);
        binder.bind(ExternalObserverJob.class, ExternalObserverJobImpl.class);
        binder.bind(ObserverStatusCheckerJob.class, ObserverStatusCheckerJobImpl.class);
        binder.bind(FailureCheckerJob.class, FailureCheckerJobImpl.class);
	    binder.bind(DbCleanupJob.class, DbCleanupJobImpl.class);
        binder.bind(SchedulerService.class).scope(ScopeConstants.DEFAULT).eagerLoad();
	    binder.bind(SystemSettingsDAO.class, SystemSettingsDAOHibernate.class);
    }

    public static void contributeHibernateEntityPackageManager(Configuration<String> configuration)
    {
        configuration.add("ch.astina.hesperid.model");
    }

    public static void contributeHibernateSessionSource(
            @InjectService("SystemEnvironment") SystemEnvironment systemEnvironment,
            OrderedConfiguration<HibernateConfigurer> config)
    {
        config.add("FileConfiguration", new HibernateFileConfigurer(systemEnvironment));
    }

    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration)
    {
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
        configuration.add(SymbolConstants.PRODUCTION_MODE, "false");

        configuration.add("tapestry.default-cookie-max-age", "31536000");
    }

    public static MailerService buildMailerService(MailServerDAO mailServerDAO)
    {
        MailServer mailServer = mailServerDAO.getMailServerForId(1l); // XXX
        SmtpCredentials credentials = new SmtpCredentials(mailServer.getHost(),
                mailServer.getUserName(), mailServer.getPassword());
        return new MailerServiceImpl(credentials, new HashMap<String, String>());
    }

    public static JiraIssueService buildJiraIssueService(JiraSettingsDAO jiraSettingsDAO) throws URISyntaxException
    {
        JiraSettings jiraSettings = jiraSettingsDAO.findOne();

        return new JiraIssueServiceImpl(jiraSettings);
    }

    @Marker(SpringSecurityServices.class)
    public static void contributeProviderManager(
            OrderedConfiguration<AuthenticationProvider> configuration,
            @InjectService("DaoAuthenticationProvider") AuthenticationProvider daoAuthenticationProvider)
    {
        configuration.add("daoAuthenticationProvider", daoAuthenticationProvider);
    }

    public static void contributeIgnoredPathsFilter(Configuration<String> configuration)
    {
        configuration.add("/soap.*");
    }

    @Match({"*DAO", "*Job"})
    public static void adviseTransactions(HibernateTransactionAdvisor advisor,
            MethodAdviceReceiver receiver)
    {
        advisor.addTransactionCommitAdvice(receiver);
    }
}
