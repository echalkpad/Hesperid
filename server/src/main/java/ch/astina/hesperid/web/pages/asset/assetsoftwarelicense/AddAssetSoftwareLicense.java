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
package ch.astina.hesperid.web.pages.asset.assetsoftwarelicense;

import ch.astina.hesperid.dao.AssetDAO;
import ch.astina.hesperid.dao.SoftwareLicenseDAO;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.AssetSoftwareLicense;
import ch.astina.hesperid.model.base.SoftwareLicense;
import ch.astina.hesperid.util.GenericSelectModel;
import ch.astina.hesperid.web.pages.asset.AssetOverview;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.springframework.security.access.annotation.Secured;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class AddAssetSoftwareLicense 
{
    @Inject
    private AssetDAO assetDAO;
    
    @Inject
    private SoftwareLicenseDAO softwareLicenseDAO;
    
    @Inject
    private PropertyAccess propertyAccess;
    
    @Property
    private Asset asset;
    
    @Persist
    @Property
    private GenericSelectModel<SoftwareLicense> softwareLicenses;
    
    @Property
    private AssetSoftwareLicense assetSoftwareLicense;
    
    @Inject
    private PageRenderLinkSource pageRenderLinkSource;
    
    @Secured({"ROLE_ADMIN"})
    public void onActivate(Long assetId)
    {
        asset = assetDAO.getAssetForId(assetId);
        
        softwareLicenses = new GenericSelectModel<SoftwareLicense>(softwareLicenseDAO.getAllSoftwareLicenses(), SoftwareLicense.class, "name", "id", propertyAccess);
    }
    
    public void onActivate()
    {
        if (assetSoftwareLicense == null) {
            assetSoftwareLicense = new AssetSoftwareLicense();
        }
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
        assetSoftwareLicense.setAsset(asset);
        assetDAO.saveOrUpdateAssetSoftwareLicense(assetSoftwareLicense);
        
        return pageRenderLinkSource.createPageRenderLinkWithContext(AssetOverview.class, asset);
    }    
}
