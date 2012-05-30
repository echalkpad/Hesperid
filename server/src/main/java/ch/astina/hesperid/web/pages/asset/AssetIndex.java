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

import ch.astina.hesperid.dao.AssetDAO;
import ch.astina.hesperid.model.base.Asset;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.access.annotation.Secured;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 118 $, $Date: 2011-09-21 16:33:28 +0200 (Mi, 21 Sep 2011) $
 */
@Secured({"ROLE_ADMIN"})
public class AssetIndex
{
    @Inject
    private AssetDAO assetDAO;

    @Property
    private Asset asset;

    public List<Asset> getAllAssets()
    {
        return assetDAO.getAllAssets();
    }

    public String getFormattedLastUpdatedObserverDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (asset.getLastUpdatedObserver() != null) {
            return sdf.format(asset.getLastUpdatedObserver());
        }

        return "";
    }

    public String getFormattedLastTickReceivedDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (asset.getLastTickReceived() != null) {
            return sdf.format(asset.getLastTickReceived());
        }

        return "";
    }
}
