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

import ch.astina.hesperid.web.services.sitemap.NavigationHelper;
import ch.astina.hesperid.web.services.sitemap.Page;
import ch.astina.hesperid.web.services.sitemap.Sitemap;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class TwoLevelNavigation
{
    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String id;
    @Property
    private Page page;
    @Property
    private Page activeFirstLevelPage;
    @Inject
    private Sitemap sitemap;
    @Inject
    private ComponentResources resources;

    void setupRender()
    {
        for (Page page : getFirstLevelPages()) {
            if (isActivePage(page)) {
                this.activeFirstLevelPage = page;
            }
        }
    }

    public List<Page> getFirstLevelPages()
    {
        return sitemap.getFirstLevelPages();
    }

    public boolean isActivePage(Page page)
    {
        return NavigationHelper.isActivePage(page, resources.getPageName());
    }

    public String getCssClass()
    {
        if (isActivePage(page)) {
            return "active";
        }
        return null;
    }
}
