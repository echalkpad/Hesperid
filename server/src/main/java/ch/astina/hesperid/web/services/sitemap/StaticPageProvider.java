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
package ch.astina.hesperid.web.services.sitemap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 107 $, $Date: 2011-09-21 16:07:03 +0200 (Mi, 21 Sep 2011) $
 */
public class StaticPageProvider implements PageProvider
{
    private List<Page> firstLevelPages;

    public StaticPageProvider()
    {
        this.firstLevelPages = new ArrayList<Page>();
    }

    public void addFirstLevelPage(Page page)
    {
        this.firstLevelPages.add(page);
    }

    /**
     * @deprecated typo ...
     */
    public void addFistLevelPage(Page page)
    {
        addFirstLevelPage(page);
    }

    public List<Page> getFirstLevelPages()
    {
        return this.firstLevelPages;
    }
}
