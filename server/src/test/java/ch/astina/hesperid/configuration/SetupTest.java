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
package ch.astina.hesperid.configuration;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 109 $, $Date: 2011-09-21 16:11:17 +0200 (Mi, 21 Sep 2011) $
 */
public class SetupTest
{
    public static String url;
    public static String username;
    public static String password;

    @BeforeSuite
    @Parameters({"local-db-url", "local-db-username", "local-db-password"})
    public void prepareTestSuit(String url, String username, String password)
    {
        SetupTest.url = url;
        SetupTest.username = username;
        SetupTest.password = password;
    }
}
