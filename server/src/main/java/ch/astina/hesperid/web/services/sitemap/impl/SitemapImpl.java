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
package ch.astina.hesperid.web.services.sitemap.impl;

import ch.astina.hesperid.web.services.sitemap.Page;
import ch.astina.hesperid.web.services.sitemap.PageProvider;
import ch.astina.hesperid.web.services.sitemap.Sitemap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class SitemapImpl implements Sitemap
{
    private String applicationName;
    private List<Page> firstLevelPages;

    public SitemapImpl(String applicationName,
            List<PageProvider> pageProviders)
    {
        this.applicationName = applicationName;
        this.firstLevelPages = new ArrayList<Page>();
        for (PageProvider pageProvider : pageProviders) {
            firstLevelPages.addAll(pageProvider.getFirstLevelPages());
        }
    }

    public String getApplicationName()
    {
        return this.applicationName;
    }

    public List<Page> getFirstLevelPages()
    {
        return firstLevelPages;
    }

    public Page getPageForPath(String path)
    {
        if (path.length() == 0) {
            return null;
        }

        // strip slash from beginning
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        // @todo make recursive
        for (Page page : getFirstLevelPages()) {
            if (page.getName().equals(path)) {
                return page;
            }
        }
        return null;
    }

    @Override
    public Page getParentPage(Page page)
    {
        int pos = page.getName().lastIndexOf('/');

        if (pos == -1) {
            return null;
        }

        String parentName = page.getName().substring(0, pos);
        return getPageForPath(parentName);
    }
}
