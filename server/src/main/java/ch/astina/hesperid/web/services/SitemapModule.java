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

import ch.astina.hesperid.web.services.sitemap.Page;
import ch.astina.hesperid.web.services.sitemap.PageProvider;
import ch.astina.hesperid.web.services.sitemap.Sitemap;
import ch.astina.hesperid.web.services.sitemap.StaticPageProvider;
import ch.astina.hesperid.web.services.sitemap.impl.SitemapImpl;
import org.apache.tapestry5.ioc.OrderedConfiguration;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class SitemapModule
{
    public static Sitemap buildSitemap(List<PageProvider> pageProviders)
    {
        return new SitemapImpl("HESPERID", pageProviders);
    }

    public static void contributeSitemap(OrderedConfiguration<PageProvider> configuration,
            PageProvider pageProvider)
    {
        configuration.add("main", pageProvider);
    }

    public static PageProvider build()
    {
        StaticPageProvider pageProvider = new StaticPageProvider();
        pageProvider.addFirstLevelPage(new Page("index", "Dashboard"));
        pageProvider.addFirstLevelPage(new Page("asset", "Assets"));
        pageProvider.addFirstLevelPage(new Page("contact", "Contacts"));
        pageProvider.addFirstLevelPage(new Page("system", "Systems"));
        pageProvider.addFirstLevelPage(new Page("location", "Locations"));
        pageProvider.addFirstLevelPage(new Page("escalation", "Escalation"));
        pageProvider.addFirstLevelPage(new Page("report", "Report & Exports"));

        Page adminPage = new Page("admin", "Admin");
        adminPage.addChildPage(new Page("admin/user", "Users"));
        adminPage.addChildPage(new Page("admin/businessrole", "Business Roles"));
        adminPage.addChildPage(new Page("admin/softwarelicense", "Software Licenses"));
        adminPage.addChildPage(new Page("admin/systemsettings", "System Settings"));
        adminPage.addChildPage(new Page("admin/report", "Reports"));
        adminPage.addChildPage(new Page("admin/observerstrategy", "Observer Strategies"));
        adminPage.addChildPage(new Page("admin/agentbundle", "Agent Software"));
        adminPage.addChildPage(new Page("admin/systemhealthlog", "System Health"));

        pageProvider.addFirstLevelPage(adminPage);

        return pageProvider;
    }
}
