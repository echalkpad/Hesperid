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

import ch.astina.hesperid.dao.AssetDAO;
import ch.astina.hesperid.dao.EscalationDAO;
import ch.astina.hesperid.dao.LocationDAO;
import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.dao.hibernate.FilterGridDataSource;
import ch.astina.hesperid.global.GlobalConstants;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.EscalationScheme;
import ch.astina.hesperid.model.base.Location;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.web.services.scheduler.SchedulerService;
import org.apache.commons.lang.time.DateUtils;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
@Import(stylesheet = {"context:styles/assetlist.css"})
public class AssetList
{

	private final Logger logger = LoggerFactory.getLogger(AssetList.class);

    @Inject
    private AssetDAO assetDAO;
	@Inject
	private ObserverDAO observerDAO;
    @Inject
    private LocationDAO locationDAO;
    @Inject
    private EscalationDAO escalationDAO;
    @Property
    private Asset asset;
    @SuppressWarnings("unused")
    @Property
    private Observer observer;
    @Parameter(allowNull = true)
    @Property
    private Location location;
    @Property
    @Persist
    private Location filterLocation;
    @Property
    @Persist
    private EscalationScheme filterEscalationScheme;
    @Property
    @Persist
    private Boolean filterManaged;
    private int i = 0;

	@Inject
	private SchedulerService schedulerService;

    void setupRender()
    {
        if (location != null) {
            filterLocation = location;
        }
    }

	@CommitAfter
	public void onActionFromDelete(Long id)
	{
		Asset asset = assetDAO.getAssetForId(id);

		try {
			schedulerService.stopObserverJobsFor(asset);
			assetDAO.deleteAsset(asset);
		} catch (Exception ex) {
			logger.error("Could not delete asset: " + asset.getAssetIdentifier(), ex);
		}
	}

    public FilterGridDataSource getAssets()
    {
        FilterGridDataSource dataSource = assetDAO.getFilterGridDataSource();

        if (filterLocation != null) {
            dataSource.addFilter(Restrictions.eq("location", filterLocation));
        }
        if (filterEscalationScheme != null) {
            dataSource.addFilter(Restrictions.eq("escalationScheme", filterEscalationScheme));
        }
        if (filterManaged != null && filterManaged == true) {
            dataSource.addFilter(Restrictions.eq("managed", true));
        }

        return dataSource;
    }

    public String getAssetStatus()
    {
        return String.format("%s-%s", isAssetOnline() ? "online" : "offline",
                asset.getErrorOccured() ? "failure" : "ok");
    }

    public String getRowClass()
    {
        i = 0;
        return String.format("status-%s", getAssetStatus());
    }

    private boolean isAssetOnline()
    {
        Date compdate = new Date();
        compdate = DateUtils.addSeconds(compdate, -65);
        return asset.getLastTickReceived() != null && asset.getLastTickReceived().after(compdate);
    }

    public String getLastTickReceived()
    {
        if (asset.getLastTickReceived() == null) {
            return null;
        }
        return GlobalConstants.DATETIME_FORMAT.format(asset.getLastTickReceived());
    }

    public String getObserversComma()
    {
        return ++i < asset.getObservers().size() ? ", " : "";
    }

    public List<Location> getLocations()
    {
        return locationDAO.getAllLocations();
    }

    public List<EscalationScheme> getEscalationSchemes()
    {
        return escalationDAO.getEscalationSchemes();
    }
}
