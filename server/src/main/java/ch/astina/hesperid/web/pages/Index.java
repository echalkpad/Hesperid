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
package ch.astina.hesperid.web.pages;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Mixins;
import org.apache.tapestry5.corelib.components.Zone;
import org.springframework.security.access.annotation.Secured;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
@Secured({"ROLE_ADMIN"})
@Import(library = {"context:scripts/dashboard.js"})
public class Index
{
    @Component(parameters = {"PeriodicUpdate.event=refreshDashboard", "PeriodicUpdate.period=10"})
    @Mixins({"PeriodicUpdate"})
    private Zone dashboard;

    Object onRefreshDashboard()
    {
        return dashboard.getBody();
    }
}
