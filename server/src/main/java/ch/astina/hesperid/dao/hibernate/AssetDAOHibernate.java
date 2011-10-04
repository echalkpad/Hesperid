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
package ch.astina.hesperid.dao.hibernate;

import ch.astina.hesperid.model.base.AssetContact;
import ch.astina.hesperid.model.base.AssetSoftwareLicense;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import ch.astina.hesperid.dao.AssetDAO;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.ClientHierarchy;
import ch.astina.hesperid.model.base.Location;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class AssetDAOHibernate implements AssetDAO
{
    private Session session;

    public AssetDAOHibernate(Session session)
    {
        this.session = session;
    }

    public Asset getAssetForId(Long asset)
    {
        return (Asset) session.get(Asset.class, asset);
    }

    @SuppressWarnings("unchecked")
    public List<Asset> getAllAssets()
    {
        return session.createCriteria(Asset.class).list();
    }

    public void saveOrUpdateAsset(Asset asset)
    {
        session.saveOrUpdate(asset);
    }

    public void deleteClientHierarchy(ClientHierarchy clientHierarchy)
    {
        if (clientHierarchy == null || clientHierarchy.getId() == null) {
            return;
        }

        SQLQuery query = session.createSQLQuery("DELETE FROM client_hierarchy WHERE id = ?");
        query.setLong(0, clientHierarchy.getId());

        query.executeUpdate();
    }

    public void deleteAllClientHierarchies(Asset asset)
    {
        if (asset == null || asset.getId() == null) {
            return;
        }

        SQLQuery query = session.createSQLQuery("DELETE FROM client_hierarchy WHERE first_asset = ? OR second_asset = ?");
        query.setLong(0, asset.getId());
        query.setLong(1, asset.getId());

        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public List<Asset> getAssetsByLocation(Location location)
    {
        return session.createCriteria(Asset.class).add(Restrictions.eq("location", location)).list();
    }

    public void saveOrUpdateClientHierarchy(ClientHierarchy clientHierarchy)
    {
        session.saveOrUpdate(clientHierarchy);
    }

    public void deleteAsset(Asset asset)
    {
        String sql = "DELETE FROM observer WHERE asset = ?";
        Query query = session.createSQLQuery(sql);
        query.setLong(0, asset.getId());
        query.executeUpdate();

        sql = "DELETE FROM client_hierarchy WHERE first_asset = ? OR second_asset = ?";
        query = session.createSQLQuery(sql);
        query.setLong(0, asset.getId());
        query.setLong(1, asset.getId());
        query.executeUpdate();

        deleteAllClientHierarchies(asset);
        session.delete(asset);
    }

    public Asset getAssetForHost(String host)
    {
        return (Asset) session.createCriteria(Asset.class).add(Restrictions.eq("host", host)).setMaxResults(1).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public Asset getAssetForAssetIdentifier(String assetIdentifier)
    {
        List<Asset> assets = session.createCriteria(Asset.class).add(Restrictions.eq("assetIdentifier", assetIdentifier)).list();

        if (assets.isEmpty()) {
            return null;
        }

        return assets.get(0);
    }

    @SuppressWarnings("unchecked")
    public List<Asset> getAllRelatedAssets(Asset asset)
    {
        List<Asset> assets = new ArrayList<Asset>();

        String sql = "SELECT a.* FROM client_hierarchy ch INNER JOIN asset a ON a.id = ch.first_asset  WHERE first_asset = ?";
        SQLQuery query = session.createSQLQuery(sql);
        query.setLong(0, asset.getId());
        query.addEntity(Asset.class);

        assets.addAll(query.list());

        sql = "SELECT a.* FROM client_hierarchy ch INNER JOIN asset a ON a.id = ch.first_asset  WHERE second_asset = ?";
        query = session.createSQLQuery(sql);
        query.setLong(0, asset.getId());
        query.addEntity(Asset.class);

        assets.addAll(query.list());

        return assets;
    }

    @Override
    public FilterGridDataSource getFilterGridDataSource()
    {
        return new FilterGridDataSource(session, Asset.class);
    }

    @Override
    public void saveOrUpdateAssetContact(AssetContact assetContact) 
    {
        session.saveOrUpdate(assetContact);
    }
    
    @Override
    public AssetContact getAssetContactForId(Long assetContactId) 
    {
        return (AssetContact) session.get(AssetContact.class, assetContactId);
    }

    @Override
    public void deleteAssetContact(AssetContact assetContact) 
    {
        session.delete(assetContact);
    }

    @Override
    public void saveOrUpdateAssetSoftwareLicense(AssetSoftwareLicense assetSoftwareLicense) 
    {
        session.saveOrUpdate(assetSoftwareLicense);
    }

    @Override
    public AssetSoftwareLicense getAssetSoftwareLicenseForId(Long assetSoftwareLicenseId) 
    {
        return (AssetSoftwareLicense) session.get(AssetSoftwareLicense.class, assetSoftwareLicenseId);
    }

    @Override
    public void deleteAssetSoftwareLicense(AssetSoftwareLicense assetSoftwareLicense) 
    {
        session.delete(assetSoftwareLicense);
    }
}
