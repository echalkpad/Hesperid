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
package ch.astina.hesperid.web;

import ch.astina.hesperid.web.services.dbmigration.DbMigration;
import org.apache.tapestry5.test.PageTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 118 $, $Date: 2011-09-21 16:33:28 +0200 (Mi, 21 Sep 2011) $
 */
public class PageTesterFactory
{
    private static PageTester pageTester;
    private static Logger logger = LoggerFactory.getLogger(PageTesterFactory.class);

    public static PageTester getInstance()
    {
        if (pageTester != null) {
            return pageTester;
        }
        String appPackage = "ch.astina.hesperid.web";
        String appName = "TestApp";
        pageTester = new PageTester(appPackage, appName, "src/main/webapp");

        try {
            pageTester.getService(DbMigration.class).updateAllChangelogs();
        } catch (Exception e) {
            logger.error("Error updating test changelogs",e);
        }

        return pageTester;
    }
}
