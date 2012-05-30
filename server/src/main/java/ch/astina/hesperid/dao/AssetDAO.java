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
package ch.astina.hesperid.dao;

import ch.astina.hesperid.dao.hibernate.FilterGridDataSource;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.AssetContact;
import ch.astina.hesperid.model.base.AssetSoftwareLicense;
import ch.astina.hesperid.model.base.ClientHierarchy;
import ch.astina.hesperid.model.base.Location;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public interface AssetDAO
{
    public Asset getAssetForId(Long assetId);

    public Asset getAssetForAssetIdentifier(String assetIdentifier);

    public List<Asset> getAllAssets();

    public void saveOrUpdateAsset(Asset asset);

    public void deleteClientHierarchy(ClientHierarchy clientHierarchy);

    public void deleteAllClientHierarchies(Asset asset);

    public List<Asset> getAssetsByLocation(Location location);

    public void saveOrUpdateClientHierarchy(ClientHierarchy clientHierarchy);

    public void deleteAsset(Asset asset);

    public Asset getAssetForHost(String host);

    public List<Asset> getAllRelatedAssets(Asset asset);

    public abstract FilterGridDataSource getFilterGridDataSource();

    public void saveOrUpdateAssetContact(AssetContact assetContact);

    public void saveOrUpdateAssetSoftwareLicense(AssetSoftwareLicense assetSoftwareLicense);

    public AssetSoftwareLicense getAssetSoftwareLicenseForId(Long assetSoftwareLicenseId);

    public void deleteAssetSoftwareLicense(AssetSoftwareLicense assetSoftwareLicense);

    public AssetContact getAssetContactForId(Long assetContactId);

    public void deleteAssetContact(AssetContact ac);
}
