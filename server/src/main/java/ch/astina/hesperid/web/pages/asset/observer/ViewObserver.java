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
package ch.astina.hesperid.web.pages.asset.observer;

import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.global.GlobalConstants;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverParameter;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.HibernateGridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
@Secured({"ROLE_ADMIN"})
public class ViewObserver
{
    @PageActivationContext
    @Property
    private Observer observer;
    @Property
    private ObserverParameter observerParameter;
    @Inject
    private ObserverDAO observerDAO;
    private Logger logger = LoggerFactory.getLogger(ViewObserver.class);

    public String getObserverParameterDate()
    {
        return GlobalConstants.DATETIME_FORMAT.format(observerParameter.getUpdated());
    }

    public String getLastCheck()
    {
        if (observer.getLastCheck() == null) {
            return null;
        }
        return GlobalConstants.DATETIME_FORMAT.format(observer.getLastCheck());
    }

    public HibernateGridDataSource getObserverParameterGridDataSource()
    {
        return observerDAO.getObserverParameterGridDataSource(observer);
    }
}
