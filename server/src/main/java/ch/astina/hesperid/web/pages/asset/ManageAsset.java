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
package ch.astina.hesperid.web.pages.asset;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.springframework.security.annotation.Secured;

import ch.astina.hesperid.dao.AssetDAO;
import ch.astina.hesperid.dao.EscalationDAO;
import ch.astina.hesperid.dao.LocationDAO;
import ch.astina.hesperid.dao.SystemDAO;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.EscalationScheme;
import ch.astina.hesperid.model.base.Location;
import ch.astina.hesperid.model.base.System;
import ch.astina.hesperid.util.GenericSelectModel;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class ManageAsset
{
    @Property
    private Asset asset;
    @SuppressWarnings("unused")
    @Property
    private GenericSelectModel<EscalationScheme> escalationSchemes;
    @SuppressWarnings("unused")
    @Property
    private GenericSelectModel<Location> locations;
    @SuppressWarnings("unused")
    @Property
    private GenericSelectModel<System> systems;
    @Inject
    private AssetDAO assetDAO;
    @Inject
    private EscalationDAO escalationDAO;
    @Inject
    private SystemDAO systemDAO;
    @Inject
    private LocationDAO locationDAO;
    @Inject
    private PropertyAccess propertyAccess;
    @Inject
    private PageRenderLinkSource linkSource;

    @Secured({"ROLE_ADMIN"})
    public void onActivate(Long assetId)
    {
        asset = assetDAO.getAssetForId(assetId);
    }

    public void onActivate()
    {
        if (asset == null) {
            asset = new Asset();
        }

        escalationSchemes = new GenericSelectModel<EscalationScheme>(
                escalationDAO.getEscalationSchemes(), EscalationScheme.class, "name", "id",
                propertyAccess);

        locations = new GenericSelectModel<Location>(
                locationDAO.getAllLocations(),
                Location.class, "name", "id", propertyAccess);

        systems = new GenericSelectModel<System>(systemDAO.getSystems(), System.class,
                "name", "id", propertyAccess);
    }

    public Long onPassivate()
    {
        if (asset != null) {
            return asset.getId();
        }
        return null;
    }

    @CommitAfter
    public Link onSuccess()
    {
        asset.setManaged(true);
        assetDAO.saveOrUpdateAsset(asset);

        return linkSource.createPageRenderLinkWithContext(AssetOverview.class, asset);
    }
}
