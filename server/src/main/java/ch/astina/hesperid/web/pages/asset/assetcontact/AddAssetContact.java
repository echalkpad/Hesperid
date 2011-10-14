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
package ch.astina.hesperid.web.pages.asset.assetcontact;

import ch.astina.hesperid.dao.AssetDAO;
import ch.astina.hesperid.dao.ContactDAO;
import ch.astina.hesperid.dao.MesRoleDAO;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.AssetContact;
import ch.astina.hesperid.model.base.BusinessRole;
import ch.astina.hesperid.model.base.Contact;
import ch.astina.hesperid.util.GenericSelectModel;
import ch.astina.hesperid.web.pages.asset.AssetOverview;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.springframework.security.annotation.Secured;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class AddAssetContact 
{
    @Inject
    private AssetDAO assetDAO;
    
    @Inject
    private MesRoleDAO mesRoleDAO;
    
    @Inject
    private ContactDAO contactDAO;
    
    @Inject
    private PropertyAccess propertyAccess;
    
    @Property
    private Asset asset;
    
    @Persist
    @Property
    private GenericSelectModel<BusinessRole> businessRoles;
    
    @Persist
    @Property
    private GenericSelectModel<Contact> contacts;
    
    @Property
    private BusinessRole businessRole;
    
    @Property
    private Contact contact;
    
    @Inject
    private PageRenderLinkSource pageRenderLinkSource;
    
    @Secured({"ROLE_ADMIN"})
    public void onActivate(Long assetId)
    {
        asset = assetDAO.getAssetForId(assetId);
        
        businessRoles = new GenericSelectModel<BusinessRole>(mesRoleDAO.getAllMesRoles(), BusinessRole.class, "name", "id", propertyAccess);
        contacts = new GenericSelectModel<Contact>(contactDAO.getAllContacts(), Contact.class, "formattedName", "id", propertyAccess);
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
        AssetContact ac = new AssetContact();
        
        ac.setBusinessRole(businessRole);
        ac.setContact(contact);
        ac.setAsset(asset);
        
        assetDAO.saveOrUpdateAssetContact(ac);
        
        return pageRenderLinkSource.createPageRenderLinkWithContext(AssetOverview.class, asset);
    }
}
