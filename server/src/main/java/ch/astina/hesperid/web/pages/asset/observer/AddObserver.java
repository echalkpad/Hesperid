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

import java.util.List;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.springframework.security.annotation.Secured;

import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.ObserverStrategy;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class AddObserver
{
    @Property
    private Asset asset;
    @Property
    private ObserverStrategy observerStrategy;
    @Inject
    private ObserverDAO observerDAO;
    @Inject
    private PageRenderLinkSource linkSource;

    @Secured({"ROLE_ADMIN"})
    void onActivate(Asset asset)
    {
        this.asset = asset;
    }

    Asset onPassivate()
    {
        return asset;
    }

    Link onSuccess()
    {
        return linkSource.createPageRenderLinkWithContext(ManageObserver.class, asset,
                observerStrategy);
    }

    public List<ObserverStrategy> getObserverStrategies()
    {
        return observerDAO.getObserverStrategies();
    }
}
