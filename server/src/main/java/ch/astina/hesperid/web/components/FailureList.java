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

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.criterion.Restrictions;

import ch.astina.hesperid.dao.FailureDAO;
import ch.astina.hesperid.dao.hibernate.FilterGridDataSource;
import ch.astina.hesperid.global.GlobalConstants;
import ch.astina.hesperid.model.base.Failure;
import ch.astina.hesperid.model.base.FailureStatus;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.user.User;
import ch.astina.hesperid.web.services.failures.FailureService;
import ch.astina.hesperid.web.services.users.UserService;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
@Import(stylesheet = {"context:styles/failurelist.css"})
public class FailureList {

    @Parameter(allowNull = true)
    @Property
    private Observer observer;
    @Inject
    private FailureDAO failureDAO;
    @Inject
    private FailureService failureService;
    @Inject
    private UserService userService;
    @Property
    private Failure failure;
    @Property
    @Persist
    private Boolean filterUnresolved;
    @Property
    @Persist
    private Boolean filterMine;

    void setupRender()
    {
        if (filterUnresolved == null) {
            filterUnresolved = true;
        }
        if (filterMine == null) {
            filterMine = false;
        }
    }

    public FilterGridDataSource getFailures()
    {
        FilterGridDataSource dataSource = failureDAO.getFailureFilterGridDataSource();

        if (observer != null) {
            dataSource.addFilter(Restrictions.eq("observer", observer));
        }
        if (filterUnresolved == true) {
            dataSource.addFilter(Restrictions.ne("failureStatus", FailureStatus.RESOLVED));
        }
        if (filterMine == true) {
            User user = userService.getCurrentUser();
            if (user != null) {
                dataSource.addAlias("escalationLevel");
                dataSource.addFilter(Restrictions.eq("escalationLevel.username", user.getUsername()));
            }
        }

        return dataSource;
    }

    @CommitAfter
    void onActionFromAcknowledge(Failure failure)
    {
        if (failure.isStatusDetected()) {
            failureService.acknowledge(failure);
        }
    }

    @CommitAfter
    void onActionFromResolve(Failure failure)
    {
        if (failure.isStatusAcknowledged()) {
            failureService.resolve(failure);
        }
    }

    public String getRowClass()
    {
        return String.format("status-%s", failure.getFailureStatus().name().toLowerCase());
    }

    public String getDetectedDate()
    {
        return GlobalConstants.DATETIME_FORMAT.format(failure.getDetected());
    }

    public String getEscalatedDate()
    {
        if (failure.getEscalated() == null) {
            return null;
        }
        return GlobalConstants.DATETIME_FORMAT.format(failure.getEscalated());
    }
}
